package com.farmporject.backend;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public ApplicationRunner applicationRunner() {
		return args -> {
			System.out.println("\n" +
					"╔════════════════════════════════════════════════════════════╗\n" +
					"║                                                            ║\n" +
					"║     农业系统后端服务启动成功！                                ║\n" +
					"║     Backend Service Started Successfully!                  ║\n" +
					"║                                                            ║\n" +
					"║     服务地址: http://localhost:8081                         ║\n" +
					"║     API 文档: http://localhost:8081/api                     ║\n" +
					"║                                                            ║\n" +
					"╚════════════════════════════════════════════════════════════╝\n");
		};
	}

}
