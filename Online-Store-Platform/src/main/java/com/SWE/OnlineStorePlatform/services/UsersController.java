package com.SWE.OnlineStorePlatform.services;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.micrometer.core.ipc.http.HttpSender.Response;

import com.SWE.OnlineStorePlatform.models.*;


@RestController
public class UsersController {

	@Autowired
	private UserService service;
	@RequestMapping("/get-user-list")
	public List<User> getRegisteredUsers() {
		List<User> userList = service.listAll();
		return userList;
	}

	@GetMapping("/register/{type}")
	public ResponseEntity<User> registerTest(@PathVariable String type){
		User user = new Buyer(type, "username", "pass", Type.BUYER);;
		return ResponseEntity.ok(user);
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@PostMapping("/register")
	public Boolean registerUser(HttpServletRequest request) {
		String emailPattern = "^([a-zA-Z0-9_\\.]+)@[a-zA-Z_]+?\\.[a-zA-Z]{2,4}$";
		String usernamePattern = "[a-zA-Z0-9_\\-\\.]";
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String pass = request.getParameter("password");
		String typeString = request.getParameter("userType");
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
}
