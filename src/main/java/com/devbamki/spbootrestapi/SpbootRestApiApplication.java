package com.devbamki.spbootrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing /** Auditing 사용을 위한 어노테이션 추가 */
@PropertySource(value = {"classpath:jdbc.properties"}) /** 다른 프로퍼티 파일 사용을 위한 property source 어노테이션 추가 */

public class SpbootRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpbootRestApiApplication.class, args);
	}

}
