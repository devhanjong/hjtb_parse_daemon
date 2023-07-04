package kr.co.devhanjong.coin_parse_daemon.controller;

import kr.co.devhanjong.coin_parse_daemon.service.CoinParseScheduleService;
import kr.co.devhanjong.coin_parse_daemon.service.CoinParseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CoinParseController {

    private final CoinParseScheduleService coinParseScheduleService;

    @Scheduled(initialDelay = 30000, fixedDelay = 5000)
    public void scheduledUpdateRunningTime(){
        coinParseScheduleService.scheduledUpdateRunningTime();
    }
}
