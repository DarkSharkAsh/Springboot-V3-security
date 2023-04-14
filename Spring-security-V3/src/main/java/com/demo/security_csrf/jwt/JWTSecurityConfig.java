package com.demo.security_csrf.jwt;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

//@Configuration
//@EnableWebSecurity
public class JWTSecurityConfig {

	@Bean
	public SecurityFilterChain filterchain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(auth -> {
			auth.anyRequest().authenticated();// any request or url must be authenticated and by formlogin
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

		http.csrf().disable();// if not disabled we have to pass csrf token for every post and put in headers
		http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

		return http.build();

	}

	// public /global config for CORS
	@Bean
	public WebMvcConfigurer corsConfigerer() {
		return new WebMvcConfigurer() {

			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedMethods("*").allowedOrigins("http://localhost:3000");// all the urls ,
				// all the
				// methods from
				// localhost:3000
				// our rest api will acknowledge it
			}
		};
	}

	// inmemory user cred more then 1 , for only 1 we use/ store in
	// application.properties
	// @Bean
	// public UserDetailsService userDetailsService() {
	// UserDetails user =
	// User.withUsername("ashu").password("{noop}dummy").roles("USER").build();
	// UserDetails user1 =
	// User.withUsername("monty").password("{noop}dummy").roles("USER").build();
	//
	// UserDetails admin =
	// User.withUsername("sonu").password("{noop}dummy").roles("ADMIN").build();
	// UserDetails admin2 =
	// User.withUsername("chintu").password("{noop}dummy").roles("ADMIN").build();
	//
	//
	// //now we have 4 user cred which can login
	// return new InMemoryUserDetailsManager(user,admin,user1,admin2);
	// }

	// @GetMapping("/csrfToken")
	// public CsrfToken getCsrfToken(HttpServletRequest request)
	// {
	//
	// return (CsrfToken) request.getAttribute("_csrf");
	// }

	@Bean
	public DataSource datasource() {

		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
				.addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION).build();
	}

	@Bean
	public UserDetailsService userDetailsService(DataSource dataSource, BCryptPasswordEncoder encoder) {

		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

		UserDetails user = User.withUsername("ashu").
		// password("{noop}dummy")
				password("dummy").passwordEncoder(str -> bCryptPasswordEncoder().encode(str)).roles("USER").build();
		UserDetails admin = User.withUsername("sonu").
		// password("{noop}dummy").
				password("dummy").passwordEncoder(str -> bCryptPasswordEncoder().encode(str)).roles("ADMIN").build();

		jdbcUserDetailsManager.createUser(user);
		jdbcUserDetailsManager.createUser(admin);

		return jdbcUserDetailsManager;
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt); is encoder
	// and it need a decoder else error , so
	// to create a decoder we need to create a RSAKey pari using KeyPairGenerator
	// 1
	@Bean
	public KeyPair keyPair() throws NoSuchAlgorithmException {
		var keypair = KeyPairGenerator.getInstance("RSA");
		keypair.initialize(2048);
		return keypair.generateKeyPair();

	}
	// 2

	@Bean
	public RSAKey rsaKey(KeyPair keyPair) {

		return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic()).privateKey(keyPair.getPrivate())
				.keyID(UUID.randomUUID().toString()).build();
	}

	// 3
	@Bean
	public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
		var jwkSet = new JWKSet(rsaKey);
		var jWKSource = new JWKSource() {

			@Override
			public List get(JWKSelector jwkSelector, SecurityContext context) throws KeySourceException {
				// TODO Auto-generated method stub
				return jwkSelector.select(jwkSet);
			}
		};
		// or lambda and return JWKSource js= (jwkSelector,context) ->
		// jwkSelector.select(jwkSet);

		return jWKSource;
	}

	// 4 now adding decoder for
	// http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);

	@Bean
	public JwtDecoder jetDecoder(RSAKey rsaKey) throws JOSEException {

		return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
	}

	// Creating a JWT encoder and resource which will use a encoder and
	// authentuicate user and send a JWT token back
	// Creating a Encoder
	// 1
	
	@Bean
	public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource)
	{
		return new NimbusJwtEncoder(jwkSource);
	}
	
	
	
	
}
