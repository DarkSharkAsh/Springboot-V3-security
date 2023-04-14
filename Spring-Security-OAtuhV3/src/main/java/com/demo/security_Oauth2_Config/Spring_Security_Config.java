package com.demo.security_Oauth2_Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Spring_Security_Config {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.authorizeHttpRequests(auth -> {
			auth.anyRequest().authenticated();
		});
		httpSecurity.oauth2Login(Customizer.withDefaults());
		return httpSecurity.build();

	}

	
//	ClientRegistrationRepository we need to add this i.e its a Google client 
}
