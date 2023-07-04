package kr.co.devhanjong.coin_parse_daemon.model.binance;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandleStickResponseBinanceBase {
    private String e;  // event type
    private Long E;    // event time
    private String s;  // symbol
    private CandleStickResponseBinanceDepth k;
}
