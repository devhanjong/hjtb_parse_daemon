package kr.co.devhanjong.coin_parse_daemon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamHogaDto {
    private String streamTime;
    private String vaspSimpleName;
    private String mySymbol;
    private String message;
    private Integer sortNo;
    private List<List<BigDecimal>> bids;
    private List<List<BigDecimal>> asks;
}
