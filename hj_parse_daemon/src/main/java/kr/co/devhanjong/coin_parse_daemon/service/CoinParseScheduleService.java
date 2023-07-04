package kr.co.devhanjong.coin_parse_daemon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.co.devhanjong.coin_parse_daemon.dto.StreamHogaDto;
import kr.co.devhanjong.coin_parse_daemon.mapper.CoinParseScheduleMapper;
import kr.co.devhanjong.coin_parse_daemon.memorydb.HogaMemoryDb;
import kr.co.devhanjong.coin_parse_daemon.model.binance.DepthResponseBinance;
import kr.co.devhanjong.coin_parse_daemon.model.phemex.DepthResponsePhemex;
import kr.co.devhanjong.coin_parse_daemon.model.redis.RedisHogaDto;
import kr.co.devhanjong.coin_parse_daemon.model.redis.RedisHogaTimeDto;
import kr.co.devhanjong.coin_parse_daemon.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static kr.co.devhanjong.coin_parse_daemon.config.Const.*;
import static kr.co.devhanjong.coin_parse_daemon.model.ApiResponse.mapper;


@Service
@Slf4j
@RequiredArgsConstructor
public class CoinParseScheduleService {
    private final RedisUtil redisUtil;

    public void scheduledUpdateRunningTime(){
        // 파싱 데몬자체가 모든 데이터를 파싱할수있기때문에 마켓이나 심볼 구별없이 그냥 타임 갱신만 하면된다.
        redisUtil.setRedisData(KEY_PARSE_RUNNING_TIME, LocalDateTime.now().toString());
    }

}
