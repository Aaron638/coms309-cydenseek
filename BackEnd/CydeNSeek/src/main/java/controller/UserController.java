package controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import db.GameDB;
import db.UserDB;
import model.User;

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
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("session", user.getSession());
			put("password", user.getPassword());
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{username}/location",
		method = RequestMethod.PUT,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> updateUserLocation(@PathVariable("username") String username, @RequestBody User user) {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("session", user.getSession());
			put("location", user.getLocation());
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> authenticate(@PathVariable("username") String username, @RequestBody User user) {
		LOG.info("Authenticated user \"" + username + "\".");
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("username", username);
			put("password", user.getPassword());
			put("location", user.getLocation());
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.DELETE,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("username") String username, @RequestBody User user) {
		LOG.info("Deleted user \"" + username + "\".");
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("username", username);
			put("password", user.getPassword());
			put("session", user.getSession());
		}}, HttpStatus.OK);
	}
}