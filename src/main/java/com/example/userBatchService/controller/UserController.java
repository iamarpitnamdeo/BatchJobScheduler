package com.example.userBatchService.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.userBatchService.model.BatchUser;
import com.example.userBatchService.service.UserService;

@RestController
public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/user",method=RequestMethod.POST)
	public String addUsers() {
		LOGGER.info("Adding 1000 users to DB");
		for(int i=0;i<1000;i++) {
			BatchUser user = new BatchUser();
			user.setUserName("User "+i);
			user.setUserEmail("user"+i+"@gmail.com");
			userService.addUser(user);
		}
		return "Users added successfully";
	}
	
	@RequestMapping(value="/user/delete",method=RequestMethod.DELETE)
	public String deleteUsers() {
		LOGGER.info("Called batch to delete 10 users");
	    userService.deleteUsersInBatch(10); // Delete 10 users per batch
	    return "Users deleted successfully";
	}

}
