package controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import model.AuthUser;
import model.NewUser;
import model.User;

@RequestMapping("/user")
public class UserController {

	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> getUser(@PathVariable("username") String username) {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("username", username);
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.PUT,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> updateUser(@PathVariable("username") String username, @RequestBody User user) {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("session", user.getSession());
			put("password", user.getPassword());
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> newUser(@PathVariable("username") String username, @RequestBody NewUser user) {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("password", user.getPassword());
			put("group", user.getGroup());
			put("location", user.getLocation());
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{username}/auth",
		method = RequestMethod.POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> auth(@PathVariable("username") String username, @RequestBody AuthUser user) {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("password", user.getPassword());
			put("location", user.getLocation());
		}}, HttpStatus.OK);
	}
}