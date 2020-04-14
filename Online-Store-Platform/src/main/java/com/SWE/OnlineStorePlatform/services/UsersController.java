package com.SWE.OnlineStorePlatform.services;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.*;

import com.SWE.OnlineStorePlatform.models.*;


@RestController
public class UsersController {

	@Autowired
	private UserService service;
	@Autowired
	private UserRepo repo;
	@RequestMapping("/get-user-list")
	public List<User> getRegisteredUsers() {
		List<User> userList = service.listAll();
		return userList;
	}
	
	
	@RequestMapping("/register")
	public Boolean registerUser(HttpServletRequest request) {
		String emailPattern = "^([a-zA-Z0-9_\\.]+)@[a-zA-Z_]+?\\.[a-zA-Z]{2,4}$";
		String usernamePattern = "[a-zA-Z0-9_\\-\\.]";
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String pass = request.getParameter("password");
		String typeString = request.getParameter("type");
		int type = Integer.parseInt(typeString);
		User user;
		boolean isValid = username.matches(usernamePattern) || email.matches(emailPattern);
		
		switch(type) {
			case 0:{
				user = new Buyer(email, username, pass, Type.BUYER);
				break;
			}
			case 1:{
				user = new Owner(email, username, pass, Type.OWNER);
				break;
			}
			default:
				user = null;
				break;
		}
		
		if(!isValid || user == null)
			return false;
		else {
			service.save(user);
			return true;
		}
			
	}

	@RequestMapping("/login")
	public Boolean login(HttpServletRequest request) {
		String email_username = request.getParameter("email");
		String pass = request.getParameter("password");
		
		User user = repo.findByEmailOrUserName(email_username);
		
		if(user == null)
			return false;//"User Doen't exist.\n";
			if(user.getPassword().equals(pass))
				return true;//"User loged in successfully.\n";
		return false;//"Wrong email or password, Please try again.\n";
		
	}


}