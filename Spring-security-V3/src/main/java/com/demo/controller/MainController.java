package com.demo.controller;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class MainController {
	private Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@GetMapping("/test")
	public String demo()
	{
		
		String cap="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String small="abcdefghijklmnopqrstuvwxyz";
		String num="1234567890";
		String special="!#$%^&^&*()-+";
		
		Random random = new Random();
		char pass[]=new char[8];
		pass[0]=small.charAt(random.nextInt(small.length()));
		pass[1]=cap.charAt(random.nextInt(cap.length()));
		pass[2]=small.charAt(random.nextInt(small.length()));
		pass[3]=num.charAt(random.nextInt(num.length()));
		pass[4]=cap.charAt(random.nextInt(cap.length()));
		pass[5]=num.charAt(random.nextInt(num.length()));
		pass[6]=special.charAt(random.nextInt(special.length()));
		pass[7]=num.charAt(random.nextInt(num.length()));
		
		
		logger.info("Calling api");
		
		return pass.toString();
	}
	
}
