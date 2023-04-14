package com.demo.security_csrf;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class BasicAuthSecurityConfig {

	@Bean
	public SecurityFilterChain filterchain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(auth -> {
			auth.requestMatchers("/user/**").hasRole("USER")
			.requestMatchers("/admin/**").hasRole("ADMIN").
			
			anyRequest().authenticated();// any request or url must be authenticated and by formlogin
		});
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // disabling
		// Session
		// by making
		// restapi
		// stateless

		// http.formLogin();// disabling this will not ask usr and pass via form but
		// will ask usrname and pass in chrome ,but via postman wee need to provide in
		// auth

		http.httpBasic();

		http.headers().frameOptions().sameOrigin();

		http.csrf().disable();//if not disabled we have to pass csrf token for every post and put in headers 

		return http.build();

	}


	//public /global config for CORS
	@Bean
	public WebMvcConfigurer corsConfigerer() {
		return new WebMvcConfigurer() {

			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("*").allowedOrigins("http://localhost:3000");//all the urls , all the methods from localhost:3000
				//our rest api will acknowledge it
			}
		};
	}


	//inmemory user cred more then 1 , for only 1 we use/ store in application.properties
	//	@Bean
	//	public UserDetailsService userDetailsService() {
	//		UserDetails user = User.withUsername("ashu").password("{noop}dummy").roles("USER").build();
	//		UserDetails user1 = User.withUsername("monty").password("{noop}dummy").roles("USER").build();
	//
	//		UserDetails admin = User.withUsername("sonu").password("{noop}dummy").roles("ADMIN").build();
	//		UserDetails admin2 = User.withUsername("chintu").password("{noop}dummy").roles("ADMIN").build();
	//		
	//		
	//		//now we have 4 user cred which can login 
	//		return new InMemoryUserDetailsManager(user,admin,user1,admin2);
	//	}

	//	@GetMapping("/csrfToken")
	//	public CsrfToken getCsrfToken(HttpServletRequest request)
	//	{
	//		
	//		return (CsrfToken) request.getAttribute("_csrf");
	//	}


	@Bean
	public DataSource datasource()
	{


		return new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.H2)
				.addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
				.build();
	}  

	@Bean
	public UserDetailsService userDetailsService(DataSource dataSource,BCryptPasswordEncoder encoder ) {


		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

		UserDetails user = User.withUsername("ashu").
//				password("{noop}dummy")
				password("dummy").passwordEncoder(str -> bCryptPasswordEncoder().encode(str))
				.roles("USER").build();
		UserDetails admin = User.withUsername("sonu").
//				password("{noop}dummy").
				password("dummy").passwordEncoder(str -> bCryptPasswordEncoder().encode(str)).
				roles("ADMIN").build();


		jdbcUserDetailsManager.createUser(user);
		jdbcUserDetailsManager.createUser(admin);

		return jdbcUserDetailsManager;
	}
	
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}

}
