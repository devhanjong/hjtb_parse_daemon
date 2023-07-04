package kr.co.devhanjong.coin_parse_daemon.model.phemex;

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
public class DepthResponseResultBook {
    private List<List<BigDecimal>> asks;
    private List<List<BigDecimal>> bids;
}
