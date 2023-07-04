package kr.co.devhanjong.coin_parse_daemon.model.huobi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepthResponseHuobi {
    private String ch;
    private String status;
    private String ts;
    private DepthResponseTick tick;
}
