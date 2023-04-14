# Springboot-V3-security

## dependency 
    <dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-test</artifactId>
			<scope>test</scope>
		</dependency>


## SC-1 
### No Security configurations 
![image](https://user-images.githubusercontent.com/105435085/224505227-4e8a9e8c-5962-4b14-b5db-bee8d940d774.png)


![image](https://user-images.githubusercontent.com/105435085/224505235-8650fb06-5ba3-49bb-801f-f05cd9f52f62.png)



## SC-2 

### Hard coded 

![image](https://user-images.githubusercontent.com/105435085/224505130-8f83207a-6f0a-4499-8cc8-6405b48c600d.png)


![image](https://user-images.githubusercontent.com/105435085/224505135-cdeeff12-6cea-4bb8-8ee7-460f7d3a7869.png)




## SC -3 

### Connecting with username and password from  inmemory DB

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


### SecurityFilterChain

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


