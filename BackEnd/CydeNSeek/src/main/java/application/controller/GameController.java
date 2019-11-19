package application.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
import application.db.GeneralDB;
import application.db.StatsDB;
import application.db.UserDB;
import application.model.Game;
import application.model.GameBody;
import application.model.GameUser;
import application.model.General;
import application.model.Stats;
import application.model.User;

@RestController
@RequestMapping("/game")
public class GameController {

	private static final Log LOG = LogFactory.getLog(GameController.class);

	@Autowired
	private GeneralDB generalDB;

	@Autowired
	private GameDB gameDB;

	@Autowired
	private UserDB userDB;

	@Autowired
	private StatsDB statsDB;

	/*
	 * POST /game/new
	 * 
	 * Mapping for creating new game
	 * Request Body:
	 * {
	 * 		"session": "abc-123-xyz",
	 * 		"radius": 10,
	 * 		"maxplayers": 10,
	 * 		"startTime": "20:00:00",
	 * 		"duration": 10,
	 * 		"mode": 0,
	 * 		"gperiod": 10,
	 * 		"creator": "user",
	 * 		"hider": true
	 * }
	 */
	@RequestMapping(
		value = "/new",
		method = RequestMethod.POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> newGame(@RequestBody GameBody game) {
		/* Checks if session not present */
		if(game.getSession() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Session token not present.");
			}}, HttpStatus.BAD_REQUEST);
		}
		/* Checks if maxplayers not specified */
		if(game.getMaxplayers() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Maxplayers is missing.");
			}}, HttpStatus.BAD_REQUEST);
		}
		/* Checks if duration not specified */
		if(game.getDuration() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Duration is missing.");
			}}, HttpStatus.BAD_REQUEST);
		}
		/* Checks if mode not specified */
		if(game.getMode() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Mode is missing.");
			}}, HttpStatus.BAD_REQUEST);
		}
		/* Checks if grace period not specified */
		if(game.getGperiod() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Grace period is missing.");
			}}, HttpStatus.BAD_REQUEST);
		}
		/* Checks if hider not specified */
		if(game.getHider() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Must specify if hider or not.");
			}}, HttpStatus.BAD_REQUEST);
		}
		Optional<General> foundUser = generalDB.findAll().stream().filter(x -> x.getSession().equals(game.getSession())).findFirst();
		/* Checks if user with token exists */
		if(!foundUser.isPresent()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Invalid session token.");
			}}, HttpStatus.BAD_REQUEST);
		}
		User user = userDB.findById(foundUser.get().getUserId()).get();
		General row = foundUser.get();
		/* Creates and builds game */
		Game newGame = new Game();
		final UUID session = UUID.randomUUID();
		newGame.setSession(session);
		newGame.setCreator(user.getUsername());
		newGame.setMaxplayers(game.getMaxplayers());
		newGame.setStartTime(LocalTime.now());
		newGame.setDuration(game.getDuration());
		newGame.setGperiod(game.getGperiod());
		gameDB.saveAndFlush(newGame);
		generalDB.saveAndFlush(row);
		LOG.info(user.getUsername() + " created a new game.");
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("game", newGame);
		}}, HttpStatus.OK);
	}

	/*
	 * PUT /game/<gameSession>
	 * 
	 * Mapping for updating game
	 * Request Body:
	 * {
	 * 		"session": "abc-123-xyz",
	 * 		"radius": 10,
	 * 		"maxplayers": 10,
	 * 		"startTime": "20:00:00",
	 * 		"duration": 10,
	 * 		"mode": 0,
	 * 		"gperiod": 10,
	 * 		"creator": "user",
	 * 		"hider": true
	 * }
	 */
	@RequestMapping(
		value = "/{gameSession}",
		method = RequestMethod.PUT,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> updateGame(@PathVariable("gameSession") final String session, @RequestBody GameBody game) {
		final UUID gameSession = UUID.fromString(session);
		/* Checks if session not present */
		if(game.getSession() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Session token not present.");
			}}, HttpStatus.BAD_REQUEST);
		}
		Optional<Game> checkGame = gameDB.findAll().stream().filter(x -> x.getSession().compareTo(gameSession) == 0).findFirst();
		/* Checks if game exists */
		if(!checkGame.isPresent()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Game not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		Game foundGame = checkGame.get();
		/* Checks if user created (and owns) game */
		if(!generalDB.findById(userDB.findUserByUsername(foundGame.getCreator()).get().getGeneralId()).get().getSession().equals(game.getSession())) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Cannot change game created by someone else.");
			}}, HttpStatus.FORBIDDEN);
		}
		/* Updates game with specified properties */
		if(game.getMaxplayers() != null) foundGame.setMaxplayers(game.getMaxplayers());
		if(game.getDuration() != null) {
			foundGame.setDuration(game.getDuration());
		}
		if(game.getGperiod() != null) foundGame.setGperiod(game.getGperiod());
		gameDB.saveAndFlush(foundGame);
		return new ResponseEntity<>(new HashMap<String, Object>() {{}}, HttpStatus.OK);
	}

	/*
	 * GET /game/<gameSession>/leaderboard
	 * 
	 * Mapping for getting game leaderboard
	 */
	@RequestMapping(
		value = "/{gameSession}/leaderboard",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> leaderboard(@PathVariable("gameSession") final String session) {
		final UUID gameSession = UUID.fromString(session);
		Optional<Game> game = gameDB.findAll().stream().filter(x -> x.getSession().compareTo(gameSession) == 0).findFirst();
		/* Checks if game exists */
		if(!game.isPresent()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Game not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		List<GameUser> gameusers = ServerWebSocketHandler.gameusers.entrySet().stream().filter(x -> gameSession.compareTo(x.getValue().getGameSession()) == 0).map(x -> x.getValue()).collect(Collectors.toList());
		final Map<Integer, General> rows = generalDB.findAll().stream().collect(Collectors.toMap(x->x.getId(), x->x));
		final Map<Integer, String> users = userDB.findAll().stream().collect(Collectors.toMap(x->rows.get(x.getGeneralId()).getStatsId(), x->x.getUsername()));
		final Map<String, Stats> userStats = statsDB.findAll().stream().collect(Collectors.toMap(x->users.get(x.getId()), x->x));
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", gameusers.stream().sorted((x,y) -> {
				Stats statsX = userStats.get(x.getUsername());
				Stats statsY = userStats.get(y.getUsername());
				return (statsX.getGWHider() + statsX.getGWSeeker()) / (statsX.getGPHider() + statsX.getGPSeeker()) - (statsY.getGWHider() + statsY.getGWSeeker()) / (statsY.getGPHider() + statsY.getGPSeeker());
			}).map(x -> {
				Stats stats = userStats.get(x.getUsername());
				return new HashMap<String, Object>() {{
					put("username", x);
					put("hider", x.getHider());
					put("found", x.getFound());
					put("gwhider", stats.getGWHider());
					put("gwseeker", stats.getGWSeeker());
					put("gphider", stats.getGPHider());
					put("gpseeker", stats.getGPSeeker());
					put("totdistance", stats.getTotDistance());
					put("tottime", stats.getTotTime());
				}};
			}).collect(Collectors.toList()));
		}}, HttpStatus.OK);
	}

	/*
	 * GET /game/<gameSession>/users
	 * 
	 * Mapping for getting game users
	 */
	@RequestMapping(
		value = "/{gameSession}/users",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> users(@PathVariable("gameSession") final String session) {
		final UUID gameSession = UUID.fromString(session);
		Optional<Game> game = gameDB.findAll().stream().filter(x -> x.getSession().compareTo(gameSession) == 0).findFirst();
		/* Checks if game exists */
		if(!game.isPresent()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Game not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		List<GameUser> gameusers = ServerWebSocketHandler.gameusers.entrySet().stream().filter(x -> gameSession.compareTo(x.getValue().getGameSession()) == 0).map(x -> x.getValue()).collect(Collectors.toList());
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", gameusers.stream().sorted((x,y) -> x.getUsername().compareTo(y.getUsername())).map(x -> {
				return new HashMap<String, Object>() {{
					put("username", x.getUsername());
					put("hider", x.getHider());
					put("found", x.getFound());
				}};
			}).collect(Collectors.toList()));
		}}, HttpStatus.OK);
	}
}