package com.SWE.OnlineStorePlatform.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import net.minidev.json.JSONObject;

import com.SWE.OnlineStorePlatform.models.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@RestController
public class UsersController {

	@Autowired
	private UserService service;

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

		switch (type) {
			case 0: {
				user = new Buyer(email, username, pass, Type.BUYER);
				break;
			}
			case 1: {
				user = new Owner(email, username, pass, Type.OWNER);
				break;
			}
			default:
				user = null;
				break;
		}

		if (!isValid || user == null) {
			return false;
		} else {
			service.save(user);
			{
				return true;
			}
		}

	}

	@RequestMapping("/login")
	@ResponseBody
	public ResponseEntity<String> login(HttpServletRequest request) {
		String email_username = request.getParameter("email_username");
		String pass = request.getParameter("password");

		User user = service.checkUserLogin(email_username);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok().headers(headers);
		if (user == null)
			return responseBuilder.build();
		if (user.getPassword().equals(pass))
			return new ResponseEntity<String>("", HttpStatus.OK);
		return responseBuilder.build();

	}

	private Token generateToken(String userID, String password) {
		String token = userID + "-" + password;
		int key = 512;
		String generated = "";
		for (int i = 0; i < (int) token.length(); ++i)
			generated += (char) token.charAt(i) ^ key;
		return new Token(generated, key);
	}

}
