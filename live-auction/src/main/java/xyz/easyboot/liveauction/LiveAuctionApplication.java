package xyz.easyboot.liveauction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class LiveAuctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiveAuctionApplication.class, args);
	}

}
