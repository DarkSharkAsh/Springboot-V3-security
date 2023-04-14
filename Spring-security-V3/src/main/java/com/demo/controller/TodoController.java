package com.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController {

private Logger logger = LoggerFactory.getLogger(TodoController.class);
	
	private static List<Todo> list= List.of(new Todo(1,"Learn Aws"),new Todo(2,"Learn Python"),new Todo(4,"Learn Java"),new Todo(3,"Learn Kotlin"));

	@GetMapping("/todos")
	public List getAll()
	{
		logger.info("all todos ");

		return list;
	}

	@GetMapping("/todos/{name}")
	public Todo getTodos(@PathVariable("name")String name)
	{
		logger.info(name+"  todos ");
		return list.stream().filter(
				t -> (t.name().equalsIgnoreCase(name))).findFirst().get();
	}

	@GetMapping("/todo/{id}")
	public Todo getTodos(@PathVariable("id")int id)
	{
		logger.info(id+"  todos ");
		return list.stream().filter(
				 t -> (t.id()==id)).findAny().get();
	}
	
	
	@PostMapping("/todos")
	public Todo addTods(@RequestBody Todo todo )
	{
		logger.info(todo+"  todos ");
		System.err.println(todo);
		list.add(todo);
		return todo;
				 
	}
	

	@GetMapping("/user")
	public String user()
	{
		

		return "USER roles only";
	}
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/user/{id}/us")
	public String userbuId(@PathVariable("id")int  id)
	{
		

		return "USER roles only";
	}
	
	@GetMapping("/user/{name}")
	public String userbyName(@PathVariable("name") String name)
	{
		

		return name+"USER roles only";
	}

	

	@GetMapping("/admin")
	public String admin()
	{
		

		return "ADMIN roles only";
	}
	
	@GetMapping("/admin/{name}")
	public String adminbyName(@PathVariable("name") String name)
	{
		

		return name+"ADMIN roles only";
	}
	
	
}


record Todo(int id, String name) {} 