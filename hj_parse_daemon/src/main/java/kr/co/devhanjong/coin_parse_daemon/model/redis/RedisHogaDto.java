package kr.co.devhanjong.coin_parse_daemon.model.redis;

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
public class RedisHogaDto {
    private String streamTime;
    private String parseTime;
    private List<List<BigDecimal>> bids;
    private List<List<BigDecimal>> asks;
}
