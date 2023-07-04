package kr.co.devhanjong.coin_parse_daemon.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HogaMemoryDbKey {
    private String vaspSimpleName;
    private String mySymbol;
}
