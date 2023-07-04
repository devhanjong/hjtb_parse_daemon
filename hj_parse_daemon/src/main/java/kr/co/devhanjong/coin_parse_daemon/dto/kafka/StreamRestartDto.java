package kr.co.devhanjong.coin_parse_daemon.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamRestartDto {
    private String requestFrom;
    private String requestReason;
    private String threadIp;
    private String vaspSimpleName;
    private String mySymbol;
    private String requestTime;
}
