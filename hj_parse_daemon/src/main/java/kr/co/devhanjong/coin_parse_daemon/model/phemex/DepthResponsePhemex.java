package kr.co.devhanjong.coin_parse_daemon.model.phemex;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepthResponsePhemex {
    private DepthResponseResultBook book;
    private String depth;
    private String sequence;
    private String symbol;
    private String timestamp;
    private String type;
}
