package kr.co.devhanjong.coin_parse_daemon.model.binance;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepthResponseBinance {
    private String lastUpdateId;
    private List<List<BigDecimal>> bids;
    private List<List<BigDecimal>> asks;
}
