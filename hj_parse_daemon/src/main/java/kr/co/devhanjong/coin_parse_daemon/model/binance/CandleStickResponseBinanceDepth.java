package kr.co.devhanjong.coin_parse_daemon.model.binance;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandleStickResponseBinanceDepth {
    private Long t;    // kline start time
    private Long T;    // kline close time
    private String s;  // symbol
    private String i;  // interval
    private Long f;  // first trade id
    private Long L;  // Last trade id
    private String o;  //  Open price
    private String c;  // Close price
    private String h;  //  High price
    private String l;  // Low price
    private String v;  // Base asset volume
    private Long n;  // Number of trades
    private Boolean x;  //  Is this kline closed?
    private String q;  // Quote asset volume
    private String V;  // Taker buy base asset volume
    private String Q;  // Taker buy quote asset volume
    private String B;  // ignore
}
