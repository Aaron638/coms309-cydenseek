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
import application.model.Game;
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
	@RequestMapping(
		value = "/{username}",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> getUser(@PathVariable("username") String username) {
		User user = userDB.findUserByUsername(username);
		/*
		 * Checks if user not exists
		 */
		if(user == null) {
			return error("User not found", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("gwhider", user.getGwhider());
			put("gwseeker", user.getGwseeker());
			put("gphider", user.getGphider());
			put("gpseeker", user.getGpseeker());
			put("totdistance", user.getTotdistance());
			put("tottime", user.getTottime());
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
	public ResponseEntity<Map<String, Object>> updateUser(@PathVariable("username") String username, @RequestBody User user) {
		/*
		 * Checks if session not present
		 */
		if(user.getSession() == null) {
			return error("Session token not present", HttpStatus.BAD_REQUEST);
		}
		User foundUser = userDB.findUserByUsername(username);
		/*
		 * Checks if user not exists
		 */
		if(foundUser == null) {
			return error("User not found", HttpStatus.NOT_FOUND);
		}
		/*
		 * Checks if session invalid
		 */
		if(!foundUser.getSession().equals(user.getSession())) {
			return error("Session token not found", HttpStatus.BAD_REQUEST);
		}
		/*
		 * Updates specified user properties
		 */
		if(user.getPassword() != null) foundUser.setPassword(user.getPassword());
		if(user.getGameId() != null && !foundUser.getGameId().equals(user.getGameId())) {
			if(foundUser.getGameId() != 0) {
				Optional<Game> old = gameDB.findById(foundUser.getGameId());
				if(old.isPresent()) {
					Game oldGame = old.get();
					if(user.getHider()) oldGame.setHiders(oldGame.getHiders() - 1);
					else oldGame.setSeekers(oldGame.getSeekers() - 1);
					gameDB.saveAndFlush(oldGame);
				}
			}
			if(user.getGameId() != 0) {
				Optional<Game> newG = gameDB.findById(user.getGameId());
				if(!newG.isPresent()) {
					return error("Could not find game", HttpStatus.BAD_REQUEST);
				}
				Game newGame = newG.get();
				if(newGame.getHiders() + newGame.getSeekers() >= newGame.getMaxplayers()) {
					return error("Game is already full", HttpStatus.BAD_REQUEST);
				}
				if(user.getHider()) newGame.setHiders(newGame.getHiders() + 1);
				else newGame.setSeekers(newGame.getSeekers() + 1);
				gameDB.saveAndFlush(newGame);
			}
		}
		userDB.saveAndFlush(foundUser);
		return new ResponseEntity<>(new HashMap<String, Object>() {{}}, HttpStatus.OK);
	}

	/*
	 * POST /user/<username>
	 * 
	 * Mapping for authenticating user
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
	public ResponseEntity<Map<String, Object>> authenticate(@PathVariable("username") String username, @RequestBody User user) {
		/*
		 * Checks if password not present
		 */
		if(user.getPassword() == null) {
			return error("Must provide password when authenticating user", HttpStatus.BAD_REQUEST);
		}
		User foundUser = userDB.findUserByUsername(username);
		/*
		 * Checks if user not exists
		 */
		if(foundUser == null) {
			user.setUsername(username);
			String session = UUID.randomUUID().toString();
			user.setSession(session);
			user.setGameId(0);
			user.setDeveloper(false);
			user.setGphider(0);
			user.setGpseeker(0);
			user.setGwhider(0);
			user.setGwseeker(0);
			user.setFound(false);
			userDB.saveAndFlush(user);
			LOG.info("Created new user \"" + username + "\".");
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("session", session);
			}}, HttpStatus.OK);
		}
		/*
		 * Checks if password not match
		 */
		if(!foundUser.getPassword().equals(user.getPassword())) {
			return error("The password was incorrect", HttpStatus.BAD_REQUEST);
		}
		/*
		 * Generates session token
		 */
		String session = UUID.randomUUID().toString();
		foundUser.setSession(session);
		userDB.saveAndFlush(foundUser);
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
			return error("Session token not found", HttpStatus.BAD_REQUEST);
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
		User foundUser = userDB.findUserByUsername(username);
		/*
		 * Checks if user not exists
		 */
		if(foundUser == null) {
			return error("User not found", HttpStatus.BAD_REQUEST);
		}
		/*
		 * Checks if session or password invalid
		 */
		if(!foundUser.getSession().equals(user.getSession()) || !foundUser.getPassword().equals(user.getPassword())) {
			return error("The password or session token was incorrect", HttpStatus.BAD_REQUEST);
		}
		/*
		 * Deletes user
		 */
		userDB.delete(foundUser);
		LOG.info("Deleted user \"" + username + "\".");
		return new ResponseEntity<>(new HashMap<String, Object>() {{}}, HttpStatus.OK);
	}
	
	public ResponseEntity<Map<String, Object>> error(String errorMessage, HttpStatus h)
	{
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("error", true);
			put("message", errorMessage);
		}}, h);
	}
}
