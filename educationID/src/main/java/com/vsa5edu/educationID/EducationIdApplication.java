package com.vsa5edu.educationID;

import com.vsa5edu.educationID.database.DTO.TrustedService;
import com.vsa5edu.educationID.database.DatabaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@SpringBootApplication(scanBasePackages={
		"com.vsa5edu.educationID.controllers"
		,"com.vsa5edu.educationID.services"
		, "com.vsa5edu.educationID.database"
		,"com.vsa5edu.educationID.redis"
		,"com.vsa5edu.educationID.filters"
		,"com.vsa5edu.educationID.mongodb"})
@EntityScan("com.vsa5edu.educationID.database.DTO")
@EnableMongoRepositories(basePackages = "com.vsa5edu.educationID.mongodb")
public class EducationIdApplication {
	public static void main(String[] args) {
		SpringApplication.run(EducationIdApplication.class, args);
	}

	//CORS!!!
	@Bean
	public WebMvcConfigurer corsConfigure(@Autowired DatabaseContext sqlcontext){
		return new WebMvcConfigurer() {
			public void addCorsMappings(CorsRegistry registry) {
				CorsRegistration registration = registry.addMapping("/**");
				registration.allowedMethods("GET", "POST", "PUT", "DELETE");
				registration.allowedHeaders("*");
				registration.exposedHeaders("Access-Control-Allow-Origin");
				registration.exposedHeaders("Access-Control-Allow-Credentials");
				registration.exposedHeaders("Authorization");
				registration.allowCredentials(true);

				String[] domains = sqlcontext.streamOf(TrustedService.class)
						.map(e->e.domainName)
						.toArray(String[]::new);
				registration.allowedOrigins(domains);
			}
		};
	}
}
