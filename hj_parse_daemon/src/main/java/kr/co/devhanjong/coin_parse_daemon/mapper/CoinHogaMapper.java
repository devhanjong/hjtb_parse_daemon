package kr.co.devhanjong.coin_parse_daemon.mapper;

import kr.co.devhanjong.coin_parse_daemon.dto.CoinHogaTempDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CoinHogaMapper {
    int insertCoinHoga(@Param("coinHogaTempDto") CoinHogaTempDto coinHogaTempDto);
}
