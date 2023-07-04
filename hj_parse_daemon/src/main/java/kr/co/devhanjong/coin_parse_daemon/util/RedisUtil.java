package kr.co.devhanjong.coin_parse_daemon.util;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("redisUtil")
public class RedisUtil {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public String getRedisData(String redisKey) {
        String data = "";
        try {
            if(redisTemplate.hasKey(redisKey)) {
                data = redisTemplate.opsForValue().get(redisKey);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    // Set Data
    public void setRedisData(String redisKey, String value) {
        try {
            redisTemplate.opsForValue().set(redisKey , value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }

    // Delete Data
    public void removeRedisData(String redisKey) {
        redisTemplate.delete(redisKey);
    }
}
