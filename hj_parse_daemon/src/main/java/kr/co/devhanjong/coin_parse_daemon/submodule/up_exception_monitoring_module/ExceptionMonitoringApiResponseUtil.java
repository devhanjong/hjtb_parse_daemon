package kr.co.devhanjong.coin_parse_daemon.submodule.monitoring_module;

import kr.co.devhanjong.monitoring_module.dto.MonitoringDto;
import kr.co.devhanjong.monitoring_module.service.MonitoringService;
import kr.co.devhanjong.monitoring_module.service.MonitoringServiceImplException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExceptionMonitoringApiResponseUtil {
    private final MonitoringService monitoringService;

    public ExceptionMonitoringApiResponseUtil(@Autowired MonitoringServiceImplException monitoringService) {
            this.monitoringService = monitoringService;
        }

        public void insertMonitoring(String level, String detail, String message){
        MonitoringDto.Insert monitoringInsert = MonitoringDto.Insert.builder()
                .appName("parse_daemon")
                .level(level)
                .status("N")
                .detail(detail)
                .message(message)
                .build();
        monitoringService.insertExceptionHistory(monitoringInsert);
    }

    public void insertMonitoring(String level, String type, String detail, String message){
        MonitoringDto.Insert monitoringInsert = MonitoringDto.Insert.builder()
                .appName("parse_daemon")
                .level(level)
                .status("N")
                .type(type)
                .detail(detail)
                .message(message)
                .build();
        monitoringService.insertExceptionHistory(monitoringInsert);
    }
}
