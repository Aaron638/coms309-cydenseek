package application.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import application.db.GameDB;
import application.db.GeneralDB;
import application.db.StatsDB;
import application.db.UserDB;
import application.model.GameUser;
import application.model.General;
import application.model.Stats;
import application.model.User;

@RestController
public class MiscController {

	private static final Log LOG = LogFactory.getLog(MiscController.class);

	@Autowired
	private GeneralDB generalDB;

	@Autowired
	private UserDB userDB;

	@Autowired
	private StatsDB statsDB;

	@Autowired
	private GameDB gameDB;
	
	@Autowired
	private Optional<User> u;
	
	@Autowired
	private Optional<General> g;
	
	@Autowired
	private Optional<Stats> s;

	/*
	 * GET /
	 * 
	 * Mapping for index page
	 */
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

	/*
	 * GET /test
	 * 
	 * Mapping for test page
	 */
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

	/*
	 * GET /leaderboard
	 * 
	 * Mapping for getting global leaderboard
	 */
	
	@RequestMapping(
		value = "/leaderboard",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> leaderboard() {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", generalDB.findAll().stream()
				.sorted((x,y) -> (statsDB.findById(x.getStatsId()).get().getGWHider() + statsDB.findById(x.getStatsId()).get().getGWSeeker()) / (statsDB.findById(x.getStatsId()).get().getGPHider() + statsDB.findById(x.getStatsId()).get().getGPSeeker()) - (statsDB.findById(y.getStatsId()).get().getGWHider() + statsDB.findById(y.getStatsId()).get().getGWSeeker()) / (statsDB.findById(y.getStatsId()).get().getGPHider() + statsDB.findById(y.getStatsId()).get().getGPSeeker()))
				.map(x -> new HashMap<String, Object>() {{
					u = userDB.findById(x.getUserId());
					s = statsDB.findById(x.getStatsId());
					put("username", u.get().getUsername());
					put("gwhider", s.get().getGWHider());
					put("gwseeker", s.get().getGWSeeker());
					put("gphider", s.get().getGPHider());
					put("gpseeker", s.get().getGPSeeker());
					put("totdistance", s.get().getTotDistance());
					put("tottime", s.get().getTotTime());
				}})
				.collect(Collectors.toList()));
		}}, HttpStatus.OK);
	}

	/*
	 * GET /users
	 * 
	 * Mapping for getting global users
	 */
	@RequestMapping(
		value = "/users",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> users(@RequestParam("session") String session) {
		Optional<User> user = userDB.findAll().stream().filter(x -> generalDB.findById(x.getGeneralId()).get().getSession().equals(session)).findFirst();
		if(!user.isPresent()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "User not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		/* Checks if user is developer */
		if(!user.get().getDeveloper()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Only developers can get the list of users.");
			}}, HttpStatus.UNAUTHORIZED);
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", userDB.findAllUsersSorted((x,y) -> x.getUsername().compareTo(y.getUsername())));
		}}, HttpStatus.OK);
	}

	/*
	 * GET /games
	 * 
	 * Mapping for getting games
	 */
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