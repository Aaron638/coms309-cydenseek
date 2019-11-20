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

import application.db.GeneralDB;
import application.db.StatsDB;
import application.db.UserDB;
import application.model.General;
import application.model.Stats;
import application.model.User;
import application.model.UserBody;

@RestController
@RequestMapping("/user")
public class UserController {

	private static final Log LOG = LogFactory.getLog(UserController.class);

	@Autowired
	private GeneralDB generalDB;

	@Autowired
	private UserDB userDB;

	@Autowired
	private StatsDB statsDB;

	/*
	 * GET /user/<username>
	 * 
	 * Mapping for getting user information
	 */
	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> getUser(@PathVariable("username") String username) {
		Optional<User> user = userDB.findUserByUsername(username);
		/* Checks if user not exists */
		if(!user.isPresent()) {
			return error("User not found", HttpStatus.NOT_FOUND);
		}
		Stats s = statsDB.findById(generalDB.findById(user.get().getGeneralId()).get().getStatsId()).get();
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("gwhider", s.getGWHider());
			put("gwseeker", s.getGWSeeker());
			put("gphider", s.getGPHider());
			put("gpseeker", s.getGPSeeker());
			put("totdistance", s.getTotDistance());
			put("tottime", s.getTotTime());
		}}, HttpStatus.OK);
	}

	/*
	 * PUT /user/<username>
	 * 
	 * Mapping for updating user information
	 * {
	 * 		"password": "mynewsecurepassword",
	 * 		"session": "abc-123-xyz",
	 * 		"gameId": 12345,
	 * 		"hider": true
	 * }
	 */
	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.PUT,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> updateUser(@PathVariable("username") String username, @RequestBody UserBody user) {
		/* Checks if session not present */
		if(user.getSession() == null) {
			return error("Session token not present", HttpStatus.BAD_REQUEST);
		}
		Optional<User> foundUser = userDB.findUserByUsername(username);
		/* Checks if user not exists */
		if(!foundUser.isPresent()) {
			return error("User not found", HttpStatus.NOT_FOUND);
		}
		User u = foundUser.get();
		/* Checks if session invalid */
		if(!generalDB.findById(u.getGeneralId()).get().getSession().equals(user.getSession())) {
			return error("Session token not found", HttpStatus.BAD_REQUEST);
		}
		/* Updates specified user properties */
		if(user.getPassword() != null) u.setPassword(user.getPassword());
		userDB.saveAndFlush(u);
		return new ResponseEntity<>(new HashMap<String, Object>() {{}}, HttpStatus.OK);
	}

	/*
	 * POST /user/<username>
	 * 
	 * Mapping for new user
	 * {
	 * 		"password": "mysecurepassword"
	 * }
	 */
	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> newUser(@PathVariable("username") final String username, @RequestBody final User user) {
		/* Checks if password not present */
		if(user.getPassword() == null) {
			return error("Must provide password when authenticating user", HttpStatus.BAD_REQUEST);
		}
		Optional<User> foundUser = userDB.findUserByUsername(username);
		if(foundUser.isPresent()) {
			return error("Username already taken.", HttpStatus.BAD_REQUEST);
		}
		General row = new General();
		user.setUsername(username);
		String session = UUID.randomUUID().toString();
		row.setSession(session);
		user.setDeveloper(false);
		/* Need to set password to hashed password and store salt */
		Stats stats = new Stats();
		stats.setGPHider(0);
		stats.setGPSeeker(0);
		stats.setGWHider(0);
		stats.setGWSeeker(0);
		stats.setTotDistance(0);
		stats.setTotTime(0);
		generalDB.saveAndFlush(row);
		General foundRow = generalDB.findAll().stream().filter(x -> x.getSession().equals(session)).findFirst().get();
		user.setGeneralId(foundRow.getId());
		stats.setGeneralId(foundRow.getId());
		userDB.saveAndFlush(user);
		statsDB.saveAndFlush(stats);
		foundRow.setUserId(userDB.findAll().stream().filter(x -> x.getGeneralId().equals(foundRow.getId())).findFirst().get().getId());
		foundRow.setStatsId(statsDB.findAll().stream().filter(x -> x.getGeneralId().equals(foundRow.getId())).findFirst().get().getId());
		generalDB.saveAndFlush(foundRow);
		LOG.info("Created new user \"" + username + "\".");
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("session", session);
		}}, HttpStatus.OK);
	}

	/*
	 * POST /user/<username>/auth
	 * 
	 * Mapping for authenticating user
	 * {
	 * 		"password": "mysecurepassword"
	 * }
	 */
	@RequestMapping(
		value = "/{username}/auth",
		method = RequestMethod.POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> authenticate(@PathVariable("username") String username, @RequestBody User user) {
		/* Checks if password not present */
		if(user.getPassword() == null) {
			return error("Must provide password when authenticating user", HttpStatus.BAD_REQUEST);
		}
		Optional<User> foundUser = userDB.findUserByUsername(username);
		/* Checks if user not exists */
		if(!foundUser.isPresent()) {
			return error("User not found.", HttpStatus.NOT_FOUND);
		}
		User u = foundUser.get();
		/* Checks if password not match */
		if(!u.getPassword().equals(user.getPassword())) {
			return error("The password was incorrect", HttpStatus.BAD_REQUEST);
		}
		/* Generates session token */
		General row = generalDB.findById(u.getGeneralId()).get();
		String session = UUID.randomUUID().toString();
		row.setSession(session);
		generalDB.saveAndFlush(row);
		LOG.info("Authenticated user \"" + username + "\".");
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("session", session);
		}}, HttpStatus.OK);
	}

	/*
	 * POST /user/<username>/logout
	 * 
	 * Mapping for logging out
	 * {
	 * 		"session": "abc-123-xyz"
	 * }
	 */
	@RequestMapping(
		value = "/{username}/logout",
		method = RequestMethod.POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> logout(@PathVariable("username") final String username, @RequestBody final UserBody body) {
		if(body.getSession() == null) {
			return error("Missing session token.", HttpStatus.BAD_REQUEST);
		}
		Optional<User> foundUser = userDB.findUserByUsername(username);
		if(!foundUser.isPresent()) {
			return error("User not found.", HttpStatus.NOT_FOUND);
		}
		General row = generalDB.findById(foundUser.get().getGeneralId()).get();
		if(!row.getSession().equals(body.getSession())) {
			return error("Invalid session token.", HttpStatus.BAD_REQUEST);
		}
		row.setSession("CLEARED");
		generalDB.saveAndFlush(row);
		return new ResponseEntity<>(new HashMap<String, Object>() {{}}, HttpStatus.OK);
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
	public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("username") String username, @RequestBody UserBody user) {
		/* Checks is session not present */
		if(user.getSession() == null) {
			return error("Session token not found", HttpStatus.BAD_REQUEST);
		}
		/* Checks if password not present */
		if(user.getPassword() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Password not present");
			}}, HttpStatus.BAD_REQUEST);
		}
		Optional<User> foundUser = userDB.findUserByUsername(username);
		/* Checks if user not exists */
		if(!foundUser.isPresent()) {
			return error("User not found", HttpStatus.BAD_REQUEST);
		}
		User u = foundUser.get();
		/* Checks if session or password invalid */
		if(!generalDB.findById(u.getGeneralId()).get().getSession().equals(user.getSession()) || !u.getPassword().equals(user.getPassword())) {
			return error("The password or session token was incorrect", HttpStatus.BAD_REQUEST);
		}
		/* Deletes user */
		userDB.delete(u);
		LOG.info("Deleted user \"" + username + "\".");
		return new ResponseEntity<>(new HashMap<String, Object>() {{}}, HttpStatus.OK);
	}

	public ResponseEntity<Map<String, Object>> error(String errorMessage, HttpStatus h) {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("error", true);
			put("message", errorMessage);
		}}, h);
	}
}