package application.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import application.db.GameDB;
import application.db.UserDB;
import application.model.User;

@RestController
@RequestMapping("/user")
public class UserController {

	private static final Log LOG = LogFactory.getLog(UserController.class);

	@Autowired
	private UserDB userDB;

	@Autowired
	private GameDB gameDB;

	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> getUser(@PathVariable("username") String username) {
		User user = userDB.findUserByUsername(username);
		if(user == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "User not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("username", username);
			put("password", user.getPassword());
			put("session", user.getSession());
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.PUT,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> updateUser(@PathVariable("username") String username, @RequestBody User user) {
		User foundUser = userDB.findUserByUsername(username);
		if(foundUser == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "User not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		if(foundUser.getSession().equals(user.getSession())) {
			user.setPassword(foundUser.getPassword());
			userDB.saveAndFlush(user);
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("error", true);
			put("message", "Session token was incorrect.");
		}}, HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(
		value = "/{username}/location",
		method = RequestMethod.PUT,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> updateUserLocation(@PathVariable("username") String username, @RequestBody User user) {
		User foundUser = userDB.findUserByUsername(username);
		if(foundUser == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "User not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		if(foundUser.getSession().equals(user.getSession())) {
			foundUser.setLocation(user.getLocation());
			userDB.saveAndFlush(foundUser);
			LOG.info("Updated user \"" + username + "\" location.");
			return new ResponseEntity<>(new HashMap<String, Object>() {{}}, HttpStatus.OK);
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("error", true);
			put("message", "Session token was incorrect.");
		}}, HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> authenticate(@PathVariable("username") String username, @RequestBody User user) {
		User foundUser = userDB.findUserByUsername(username);
		if(foundUser == null) {
			user.setUsername(username);
			String session = UUID.randomUUID().toString();
			user.setSession(session);
			user.setGameId(0);
			user.setGphider(0);
			user.setGpseeker(0);
			user.setGwhider(0);
			user.setGwseeker(0);
			userDB.saveAndFlush(user);
			LOG.info("Created new user \"" + username + "\".");
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("session", session);
			}}, HttpStatus.OK);
		}
		if(foundUser.getPassword().equals(user.getPassword())) {
			String session = UUID.randomUUID().toString();
			foundUser.setSession(session);
			foundUser.setLocation(user.getLocation());
			userDB.saveAndFlush(foundUser);
			LOG.info("Authenticated user \"" + username + "\".");
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("session", session);
			}}, HttpStatus.OK);
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("error", true);
			put("message", "The password was incorrect.");
		}}, HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.DELETE,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("username") String username, @RequestBody User user) {
		User foundUser = userDB.findUserByUsername(username);
		if(user == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "User not found.");
			}}, HttpStatus.NOT_FOUND);
		} else if(foundUser.getSession().equals(user.getSession()) && foundUser.getPassword().equals(user.getPassword())) {
			userDB.delete(user);
			LOG.info("Deleted user \"" + username + "\".");
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("error", true);
			put("message", "The password or session token was incorrect.");
		}}, HttpStatus.BAD_REQUEST);
	}
}