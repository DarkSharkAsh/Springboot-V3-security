package com.demo.security_csrf;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class SpringSecurityPlayResource {

	@GetMapping("/csrfToken")
	public CsrfToken getCsrfToken(HttpServletRequest request)
	{
		
		return (CsrfToken) request.getAttribute("_csrf");
	}
	
}
