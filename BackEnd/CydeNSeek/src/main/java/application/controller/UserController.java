package application.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

	/*
	 * GET /user/<username>
	 * 
	 * Mapping for getting user information
	 */
	
	/*
	 * PUT /user/<username>
	 * 
	 * Mapping for updating user information
	 * {
	 * 		"password": "mynewsecurepassword",
	 * 		"session": "abc-123-xyz",
	 * 		"gameId": 12345,
	 * 		"location": "",
	 * 		"hider": true
	 * }
	 */
	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.PUT,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> updateUser(@PathVariable("username") String username, @RequestBody User user) {
		/*
		 * Checks if session not present
		 */
		if(user.getSession() == null) {
			return createErrResEnt("Session token not present", HttpStatus.BAD_REQUEST);
		}
		Optional<User> foundUser = userDB.findUserByUsername(username);
		/*
		 * Checks if user not exists
		 */
		if(!foundUser.isPresent()) {
			return createErrResEnt("User not found", HttpStatus.NOT_FOUND);
		}
		User u = foundUser.get();
		/*
		 * Checks if session invalid
		 */
		if(!u.getSession().equals(user.getSession())) {
			return createErrResEnt("Session token not found", HttpStatus.BAD_REQUEST);
		}
		/*
		 * Updates specified user properties
		 */
		if(user.getPassword() != null) u.setPassword(user.getPassword());
		
		userDB.saveAndFlush(u);
		return new ResponseEntity<>(new HashMap<String, Object>() {{}}, HttpStatus.OK);
	}

	/*
	 * POST /user/<username>
	 * 
	 * Mapping for authenticating user
	 * {
	 * 		"password": "mysecurepassword",
	 * 		"location": ""
	 * }
	 */
	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> authenticate(@PathVariable("username") String username, @RequestBody User user) {
		/*
		 * Checks if password not present
		 */
		if(user.getPassword() == null) {
			return createErrResEnt("Must provide password when authenticating user", HttpStatus.BAD_REQUEST);
		}
		Optional<User> foundUser = userDB.findUserByUsername(username);
		/*
		 * Checks if user not exists
		 */
		if(!foundUser.isPresent()) {
			user.setUsername(username);
			String session = UUID.randomUUID().toString();
			user.setSession(session);
			user.setDeveloper(false);
			userDB.saveAndFlush(user);
			LOG.info("Created new user \"" + username + "\".");
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("session", session);
			}}, HttpStatus.OK);
		}
		User u = foundUser.get();
		/*
		 * Checks if password not match
		 */
		if(!u.getPassword().equals(user.getPassword())) {
			return createErrResEnt("The password was incorrect", HttpStatus.BAD_REQUEST);
		}
		/*
		 * Generates session token
		 */
		String session = UUID.randomUUID().toString();
		u.setSession(session);
		userDB.saveAndFlush(u);
		LOG.info("Authenticated user \"" + username + "\".");
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("session", session);
		}}, HttpStatus.OK);
	}

	/*
	 * DELETE /user/<username>
	 * 
	 * Mapping for deleting user
	 * {
	 * 		"session": "abc-123-xyz",
	 * 		"password": "mysecurepassword"
	 * }
	 */
	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.DELETE,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("username") String username, @RequestBody User user) {
		/*
		 * Checks is session not present
		 */
		if(user.getSession() == null) {
			return createErrResEnt("Session token not found", HttpStatus.BAD_REQUEST);
		}
		/*
		 * Checks if password not present
		 */
		if(user.getPassword() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Password not present");
			}}, HttpStatus.BAD_REQUEST);
		}
		Optional<User> foundUser = userDB.findUserByUsername(username);
		/*
		 * Checks if user not exists
		 */
		if(!foundUser.isPresent()) {
			return createErrResEnt("User not found", HttpStatus.BAD_REQUEST);
		}
		User u = foundUser.get();
		/*
		 * Checks if session or password invalid
		 */
		if(!u.getSession().equals(user.getSession()) || !u.getPassword().equals(user.getPassword())) {
			return createErrResEnt("The password or session token was incorrect", HttpStatus.BAD_REQUEST);
		}
		/*
		 * Deletes user
		 */
		userDB.delete(u);
		LOG.info("Deleted user \"" + username + "\".");
		return new ResponseEntity<>(new HashMap<String, Object>() {{}}, HttpStatus.OK);
	}
	
	public ResponseEntity<Map<String, Object>> createErrResEnt(String errorMessage, HttpStatus h)
	{
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("error", true);
			put("message", errorMessage);
		}}, h);
	}
}
