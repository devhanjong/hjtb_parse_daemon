package kr.co.devhanjong.coin_parse_daemon.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CoinQuoteDto {
    private String coinType;
    private BigDecimal usdt;
    private BigDecimal price;
    private String regDate;
}
