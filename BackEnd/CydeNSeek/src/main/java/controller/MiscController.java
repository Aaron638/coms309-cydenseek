package controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import db.GameDB;
import db.UserDB;

@RestController
public class MiscController {

	private static final Log LOG = LogFactory.getLog(MiscController.class);

	@Autowired
	private UserDB userDB;

	@Autowired
	private GameDB gameDB;

	@RequestMapping(
		value = "/",
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> index() {
		LOG.info("Index page was visited.");
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("about", "This is the index page. If you are seeing this, then it means the server is running and the endpoints are live.");
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/test",
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> test() {
		LOG.info("Test mapping was utilized.");
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("key", "value");
			put("arr", new ArrayList<Object>());
			put("obj", new HashMap<String, Object>() {{
				put("key", "value");
			}});
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/leaderboard",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> leaderboard() {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", userDB.findAllUsersSorted((x,y) -> (x.getGwhider() + x.getGwseeker()) / (x.getGphider() + x.getGpseeker()) - (y.getGwhider() + y.getGwseeker()) / (y.getGphider() + y.getGpseeker())));
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/users",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> users() {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", userDB.findAllUsersSorted((x,y) -> x.getUsername().compareTo(y.getUsername())));
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/games",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> games() {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("games", gameDB.findAll());
		}}, HttpStatus.OK);
	}
}