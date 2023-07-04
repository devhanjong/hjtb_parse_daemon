package kr.co.devhanjong.coin_parse_daemon.mapper;

import jakarta.annotation.Nullable;
import kr.co.devhanjong.coin_parse_daemon.dto.CoinQuoteDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CoinParseScheduleMapper {
    List<CoinQuoteDto> selectCoinQuote(@Nullable @Param("coinType") String coinType);
}
