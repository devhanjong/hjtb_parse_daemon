package kr.co.devhanjong.coin_parse_daemon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.devhanjong.coin_parse_daemon.dto.StreamHogaDto;
import kr.co.devhanjong.coin_parse_daemon.dto.function.ValidateStreamHogaResponse;
import kr.co.devhanjong.coin_parse_daemon.dto.kafka.StreamRestartDto;
import kr.co.devhanjong.coin_parse_daemon.kafka.KafkaProducer;
import kr.co.devhanjong.coin_parse_daemon.memorydb.HogaMemoryDb;
import kr.co.devhanjong.coin_parse_daemon.model.binance.CandleStickResponseBinanceBase;
import kr.co.devhanjong.coin_parse_daemon.model.binance.DepthResponseBinance;
import kr.co.devhanjong.coin_parse_daemon.model.huobi.DepthResponseHuobi;
import kr.co.devhanjong.coin_parse_daemon.model.phemex.DepthResponsePhemex;
import kr.co.devhanjong.coin_parse_daemon.model.redis.RedisHogaDto;
import kr.co.devhanjong.coin_parse_daemon.model.redis.RedisHogaTimeDto;
import kr.co.devhanjong.coin_parse_daemon.submodule.monitoring_module.ExceptionMonitoringApiResponseUtil;
import kr.co.devhanjong.coin_parse_daemon.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static kr.co.devhanjong.coin_parse_daemon.config.Const.*;
import static kr.co.devhanjong.coin_parse_daemon.model.ApiResponse.mapper;


@Service
@Slf4j
@RequiredArgsConstructor
public class CoinCandleParseService {
    private final RedisUtil redisUtil;

    private final HogaMemoryDb hogaMemoryDb;

    private final KafkaProducer kafkaProducer;

    private final ExceptionMonitoringApiResponseUtil exceptionMonitoringApiResponseUtil;


    public ResponseEntity<?> streamCandleStickParse(String kafkaMessage){
        try {
//            log.info("message :: " + kafkaMessage);
            StreamHogaDto streamHogaDto = mapper.readValue(kafkaMessage, StreamHogaDto.class);

            String redisKey = streamHogaDto.getVaspSimpleName() + "::" + streamHogaDto.getMySymbol();
            LocalDateTime parseTime = LocalDateTime.now();
//            if (!checkStreamTime(streamHogaDto, parseTime)) {
//                log.warn("time delay !!");
//                return null;
//            }

            // 거래소별 bid, ask 형태 파싱
            try {
                messagePayloadParse(streamHogaDto);
            } catch (JsonProcessingException e) {
                return null;
            }

            // 레디스 core 거래소-mySymbol 업데이트 한다.
            updateRedisData(redisKey, streamHogaDto, kafkaMessage, parseTime);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (Exception e){
            log.error(ExceptionUtils.getStackTrace(e));
        }


        return null;
    }


    // 거래소별 bid, ask 추출
    private void messagePayloadParse(StreamHogaDto streamHogaDto) throws JsonProcessingException {
        String vaspSimpleName = streamHogaDto.getVaspSimpleName();
        String message = streamHogaDto.getMessage();

        if(vaspSimpleName.equalsIgnoreCase(BINANCE)){
            CandleStickResponseBinanceBase candleResponseBinance = mapper.readValue(message, CandleStickResponseBinanceBase.class);
            candleResponseBinance.getK().getC();
        }
        else if(vaspSimpleName.equalsIgnoreCase(PHEMEX)){
            DepthResponsePhemex depthResponsePhemex = mapper.readValue(message, DepthResponsePhemex.class);
            streamHogaDto.setBids(depthResponsePhemex.getBook().getBids());
            streamHogaDto.setAsks(depthResponsePhemex.getBook().getAsks());
        }
        else if(vaspSimpleName.equalsIgnoreCase(HUOBI)){
            DepthResponseHuobi depthResponseHuobi = mapper.readValue(message, DepthResponseHuobi.class);
            streamHogaDto.setBids(depthResponseHuobi.getTick().getBids());
            streamHogaDto.setAsks(depthResponseHuobi.getTick().getAsks());
        }

    }

    private void updateRedisData(String redisKey, StreamHogaDto streamHogaDto, String kafkaMessage, LocalDateTime parseTime) throws JsonProcessingException {
        String vaspSimpleName = streamHogaDto.getVaspSimpleName();
        String mySymbol = streamHogaDto.getMySymbol();

//        redisUtil.setRedisData(redisKeyTime, mapper.writeValueAsString(convertToRedisHogaTimeDto(streamHogaDto, parseTime)));

        // 그냥 단순 업데이트만 하는 vasp
        if(BINANCE.equalsIgnoreCase(vaspSimpleName)){
            redisUtil.setRedisData(redisKey, mapper.writeValueAsString(convertToRedisHogaDto(streamHogaDto, parseTime)));
        }
        // phemex 는 오더북 관리를 자체적으로해야함
        // memory db 로 관리하자
        else if(PHEMEX.equalsIgnoreCase(vaspSimpleName)){
            divide(streamHogaDto, 8);

            try {
                StreamHogaDto dbStreamHogaDto = hogaMemoryDb.getHoga(vaspSimpleName, mySymbol);

                int retryCount = 0;
                while (retryCount < 10){
                    if(validateSortNo(dbStreamHogaDto, streamHogaDto)) break;

                    log.warn("validate Sort fail retry :: " + retryCount);
                    retryCount++;
                    dbStreamHogaDto = hogaMemoryDb.getHoga(vaspSimpleName, mySymbol);

                    try {
                        TimeUnit.MILLISECONDS.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                if(retryCount >= 10){
                    exceptionMonitoringApiResponseUtil.insertMonitoring("orderbook", retryCount+"", "retry_cnt_fail", streamHogaDto.toString());
                    return;
                }

                hogaMemoryDb.lock(vaspSimpleName, mySymbol);

                if(kafkaMessage.contains("snapshot")){
                    log.info("snapshot insert");
                    hogaMemoryDb.addHoga(vaspSimpleName, mySymbol, streamHogaDto);
//                    log.info(mapper.writeValueAsString(streamHogaDto));
//                    exceptionMonitoringApiResponseUtil.insertMonitoring("orderbook", "phemex : " + streamHogaDto.getMySymbol(), "first : " + streamHogaDto.getSortNo(), streamHogaDto.toString());
                }
                else if(kafkaMessage.contains("incremental")){
                    List<List<BigDecimal>> bids = streamHogaDto.getBids();
                    List<List<BigDecimal>> asks = streamHogaDto.getAsks();

//                    StreamHogaDto dbStreamHogaDto = hogaMemoryDb.getHoga(vaspSimpleName, mySymbol);
//                    exceptionMonitoringApiResponseUtil.insertMonitoring("orderbook", "phemex : " + streamHogaDto.getMySymbol(), "in : " + streamHogaDto.getSortNo(), streamHogaDto.toString());

                    ValidateStreamHogaResponse validateStreamHogaResponse = validateStreamHoga(dbStreamHogaDto);
                    if(!validateStreamHogaResponse.isResult()){
                        log.warn("validate fail");
                        exceptionMonitoringApiResponseUtil.insertMonitoring("orderbook", "outFail", validateStreamHogaResponse.getReason().equalsIgnoreCase("streamHogaDto is null") ? "streamHogaDto is null" : dbStreamHogaDto.toString());
                        sendStremRestartRequest(streamHogaDto, validateStreamHogaResponse.getReason() + " :: " + kafkaMessage);
                        return;
                    }

//                log.info("oldHoga :: " + mapper.writeValueAsString(dbStreamHogaDto));

                    List<List<BigDecimal>> dbBids = dbStreamHogaDto.getBids();
                    List<List<BigDecimal>> dbAsks = dbStreamHogaDto.getAsks();
//                log.info("db bids :: " + dbBids.toString());
//                log.info("db asks :: " + dbAsks.toString());

                    streamHogaDto.setBids(updateDbHogaList(dbBids, bids, "bid"));
                    streamHogaDto.setAsks(updateDbHogaList(dbAsks, asks, "ask"));

//                    exceptionMonitoringApiResponseUtil.insertMonitoring("orderbook", "outSuccess", streamHogaDto.toString());
                    hogaMemoryDb.addHoga(vaspSimpleName, mySymbol, streamHogaDto);

//                log.info("newHoga :: " + mapper.writeValueAsString(streamHogaDto));
                }
                else {
                    throw new RuntimeException("PHEMEX Hoga parse error");
                }
            } finally {
                hogaMemoryDb.unlock(vaspSimpleName, mySymbol);
            }

            redisUtil.setRedisData(redisKey, mapper.writeValueAsString(convertToRedisHogaDto(streamHogaDto, parseTime)));
        }
        else if (HUOBI.equalsIgnoreCase(vaspSimpleName)) {
            redisUtil.setRedisData(redisKey, mapper.writeValueAsString(convertToRedisHogaDto(streamHogaDto, parseTime)));
        }
    }

    private List<List<BigDecimal>> updateDbHogaList(List<List<BigDecimal>> dbList, List<List<BigDecimal>> newList, String bidAsk){
        List<List<BigDecimal>> newUpdatedList = new ArrayList<>(dbList);

        List<BigDecimal> existingKeys = newUpdatedList.stream()
                .map(hoga -> hoga.get(0))
                .toList();

        newUpdatedList = newUpdatedList.stream()
                .map(oldHoga -> {
                    BigDecimal key = oldHoga.get(0);
                    List<BigDecimal> matchingNewHoga = newList.stream()
                            .filter(newHoga -> newHoga.get(0).equals(key))
                            .findFirst()
                            .orElse(null);
                    if (matchingNewHoga != null) {
                        BigDecimal value = matchingNewHoga.get(1);
                        return List.of(key, value);
                    }
                    return oldHoga;
                })
                .filter(bigDecimals -> bigDecimals.get(1).compareTo(BigDecimal.ZERO) != 0)
                .toList();

        List<List<BigDecimal>> newHogaToAdd = newList.stream()
                .filter(newHoga -> !existingKeys.contains(newHoga.get(0)))
                .toList();

        ArrayList<List<BigDecimal>> newUpdatedList2 = new ArrayList<>(List.copyOf(newUpdatedList));
        newUpdatedList2.addAll(newHogaToAdd);


        if("bid".equalsIgnoreCase(bidAsk)) newUpdatedList2.sort(Comparator.comparing((List<BigDecimal> hoga) -> hoga.get(0)).reversed());
        else if ("ask".equalsIgnoreCase(bidAsk)) newUpdatedList2.sort(Comparator.comparing((List<BigDecimal> hoga) -> hoga.get(0)));

        return newUpdatedList2;
    }

    private void divide(StreamHogaDto streamHogaDto, int unit){
        BigDecimal divideUnit = BigDecimal.TEN.pow(unit);

        List<List<BigDecimal>> bids = streamHogaDto.getBids().stream()
                .map(bid -> {
                    BigDecimal key = bid.get(0).divide(divideUnit,8, RoundingMode.DOWN).compareTo(BigDecimal.ZERO) > 0
                            ? bid.get(0).divide(divideUnit,8, RoundingMode.DOWN) : BigDecimal.ZERO;
                    BigDecimal value = bid.get(1).divide(divideUnit,8, RoundingMode.DOWN).compareTo(BigDecimal.ZERO) > 0
                            ? bid.get(1).divide(divideUnit,8, RoundingMode.DOWN) : BigDecimal.ZERO;
                    return Arrays.asList(key, value);
                })
                .collect(Collectors.toList());

        streamHogaDto.setBids(bids);

        List<List<BigDecimal>> asks = streamHogaDto.getAsks().stream()
                .map(ask -> {
                    BigDecimal key = ask.get(0).divide(divideUnit,8, RoundingMode.DOWN).compareTo(BigDecimal.ZERO) > 0
                            ? ask.get(0).divide(divideUnit,8, RoundingMode.DOWN) : BigDecimal.ZERO;
                    BigDecimal value = ask.get(1).divide(divideUnit,8, RoundingMode.DOWN).compareTo(BigDecimal.ZERO) > 0
                            ? ask.get(1).divide(divideUnit,8, RoundingMode.DOWN) : BigDecimal.ZERO;
                    return Arrays.asList(key, value);
                })
                .collect(Collectors.toList());

        streamHogaDto.setAsks(asks);
    }

    private RedisHogaTimeDto convertToRedisHogaTimeDto(StreamHogaDto streamHogaDto, LocalDateTime parseTime){
        return RedisHogaTimeDto.builder()
                .streamTime(streamHogaDto.getStreamTime())
                .parseTime(parseTime.toString())
                .build();
    }

    private RedisHogaDto convertToRedisHogaDto(StreamHogaDto streamHogaDto, LocalDateTime parseTime){
        return RedisHogaDto.builder()
                .streamTime(streamHogaDto.getStreamTime())
                .parseTime(parseTime.toString())
                .bids(streamHogaDto.getBids())
                .asks(streamHogaDto.getAsks())
                .build();
    }

    private boolean checkStreamTime(StreamHogaDto streamHogaDto, LocalDateTime parseTime){
        // streamTime 검증한다.
        LocalDateTime streamTime = LocalDateTime.parse(streamHogaDto.getStreamTime());

        Duration difference = Duration.between(streamTime, parseTime);
        boolean isWithinTimeLimit = difference.compareTo(Duration.ofMillis(100)) <= 0;

        // 오더북을 순차관리하는 거래소들은 time delay 와 관련없이 무조건 패스시켜야함 -> 차이가 너무 많이 벌어지면 경고등을 고려해야한다.
        if(PHEMEX.equalsIgnoreCase(streamHogaDto.getVaspSimpleName())){
            return true;
        }

        // 차이가 너무 많이 벌어지면 error 보낸다.

        return isWithinTimeLimit;
    }


    private ValidateStreamHogaResponse validateStreamHoga(StreamHogaDto streamHogaDto){
        if(streamHogaDto == null){
            return ValidateStreamHogaResponse.builder()
                    .result(false)
                    .reason("streamHogaDto is null")
                    .build();
        }

        // bid 1번과 ask 1번의 가격비교후 bid가 크면 잘못된 호가임
        if(streamHogaDto.getBids().get(0).get(0).compareTo(streamHogaDto.getAsks().get(0).get(0)) >= 0){
            return ValidateStreamHogaResponse.builder()
                    .result(false)
                    .reason("bid ask price validation fail :: " +
                            "bid : " + streamHogaDto.getBids().get(0).get(0).stripTrailingZeros().toPlainString() + " :: " +
                            "ask : " + streamHogaDto.getAsks().get(0).get(0).stripTrailingZeros().toPlainString())
                    .build();
        }

        // test용임
//        if(PHEMEX.equalsIgnoreCase(streamHogaDto.getVaspSimpleName())){
//            int ranInt = ThreadLocalRandom.current().nextInt(100);
//            if(ranInt < 2){
//                log.info("restart request !!");
//                return ValidateStreamHogaResponse.builder()
//                        .result(false)
//                        .reason("test case")
//                        .build();
//            }
//        }

        return ValidateStreamHogaResponse.builder()
                .result(true)
                .reason("success")
                .build();
    }

    private boolean validateSortNo(StreamHogaDto dbStreamHogaDto, StreamHogaDto streamHogaDto){
        Integer sortNo = streamHogaDto.getSortNo();

        if(dbStreamHogaDto== null && sortNo == 0){
            return true;
        }

        Integer dbSortNo = dbStreamHogaDto.getSortNo();

        // 스냅샷인 경우 통과
        if(sortNo == 0){
            return true;
        } else if (dbSortNo + 1 == sortNo) {
            return true;
        }

        return false;
    }
    private void sendStremRestartRequest(StreamHogaDto streamHogaDto, String reason){
        LocalDateTime restartRequestTime = LocalDateTime.now();

        // thread ip 는 필요하지않음
        StreamRestartDto streamRestartDto = StreamRestartDto.builder()
                .requestFrom("parseDaemon")
                .requestReason(reason)
                .vaspSimpleName(streamHogaDto.getVaspSimpleName())
                .mySymbol(streamHogaDto.getMySymbol())
                .requestTime(restartRequestTime.toString())
                .build();
        try {
            kafkaProducer.sendMessage(TOPIC_STREAM_DAEMON_RESTART, null, mapper.writeValueAsString(streamRestartDto));
        } catch (JsonProcessingException e) {
        }
    }

}
