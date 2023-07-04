package kr.co.devhanjong.coin_parse_daemon.dto.function;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateStreamHogaResponse {
    private boolean result;
    private String reason;
}
