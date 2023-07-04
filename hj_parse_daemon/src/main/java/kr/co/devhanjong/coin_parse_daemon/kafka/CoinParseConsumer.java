package kr.co.devhanjong.coin_parse_daemon.kafka;

import kr.co.devhanjong.coin_parse_daemon.service.CoinCandleParseService;
import kr.co.devhanjong.coin_parse_daemon.service.CoinParseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static kr.co.devhanjong.coin_parse_daemon.config.Const.TOPIC_STREAM_CANDLE_STICK;
import static kr.co.devhanjong.coin_parse_daemon.config.Const.TOPIC_STREAM_HOGA;


@Component
@Slf4j
@RequiredArgsConstructor
public class CoinParseConsumer {


    private final CoinParseService coinParseService;
    private final CoinCandleParseService coinCandleParseService;

    /**
     * 메시지 파싱 처리
     * message ::
     */
    @KafkaListener(topics = TOPIC_STREAM_HOGA, concurrency = "100")
    public void streamHogaParse(String message){
        log.info("message :: {} ", message);
        coinParseService.streamHogaParse(message);
    }

    /**
     * 메시지 파싱 처리
     * message ::
     */
    @KafkaListener(topics = TOPIC_STREAM_CANDLE_STICK, concurrency = "20")
    public void streamCandleStickParse(String message){
        log.info("message :: {} ", message);
        coinCandleParseService.streamCandleStickParse(message);
    }
}
