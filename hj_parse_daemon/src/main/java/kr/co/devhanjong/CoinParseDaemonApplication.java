package kr.co.devhanjong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoinParseDaemonApplication {

    public static void main(String[] args) {
        SpringApplication.run(kr.co.devhanjong.CoinParseDaemonApplication.class, args);
    }

}
