package application.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.LocalTime;
import java.util.HashMap;
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
import application.db.GameUserDB;
import application.db.GeneralDB;
import application.db.StatsDB;
import application.db.UserDB;
import application.model.Game;
import application.model.GameBody;
import application.model.GameUser;
import application.model.General;
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

	@Autowired
	private GameUserDB gameUserDB;

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
		GameUser gu = new GameUser();
		gu.setIsHider(game.getHider());
		gu.setFound(false);
		General row = foundUser.get();
		gu.setGeneralId(row.getId());
		/* Creates and builds game */
		Game newGame = new Game();
		String session = UUID.randomUUID().toString();
		newGame.setSession(session);
		gu.setSession(session);
		newGame.setCreator(user.getUsername());
		newGame.setMaxplayers(game.getMaxplayers());
		newGame.setStartTime(LocalTime.now());
		newGame.setDuration(game.getDuration());
		newGame.setGperiod(game.getGperiod());
		gameDB.saveAndFlush(newGame);
		gu.setGameId(gameDB.findAll().stream().filter(x -> x.getCreator().equals(user.getUsername())).findFirst().get().getGameId());
		gameUserDB.saveAndFlush(gu);
		row.setGameUserId(gameUserDB.findAll().stream().filter(x -> x.getGeneralId().equals(row.getId())).findFirst().get().getId());
		generalDB.saveAndFlush(row);
		LOG.info(user.getUsername() + " created a new game.");
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("game", newGame);
		}}, HttpStatus.OK);
	}

	/*
	 * PUT /game/<gameId>
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
		value = "/{gameId}",
		method = RequestMethod.PUT,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> updateGame(@PathVariable("gameId") String gameId, @RequestBody GameBody game) {
		/* Checks if session not present */
		if(game.getSession() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Session token not present.");
			}}, HttpStatus.BAD_REQUEST);
		}
		Optional<Game> checkGame = gameDB.findAll().stream().filter(x -> x.getSession().equals(gameId)).findFirst();
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
	 * GET /game/<gameId>/leaderboard
	 * 
	 * Mapping for getting game leaderboard
	 */
	@RequestMapping(
		value = "/{gameId}/leaderboard",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> leaderboard(@PathVariable("gameId") String gameId) {
		Optional<Game> game = gameDB.findAll().stream().filter(x -> x.getSession().equals(gameId)).findFirst();
		/* Checks if game exists */
		if(!game.isPresent()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Game not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", gameUserDB
				.findUsersByGame(gameId, (x,y) -> (statsDB.findById(generalDB.findById(x.getGeneralId()).get().getStatsId()).get().getGWHider() + statsDB.findById(generalDB.findById(x.getGeneralId()).get().getStatsId()).get().getGWSeeker()) / (statsDB.findById(generalDB.findById(x.getGeneralId()).get().getStatsId()).get().getGPHider() + statsDB.findById(generalDB.findById(x.getGeneralId()).get().getStatsId()).get().getGPSeeker()) - (statsDB.findById(generalDB.findById(y.getGeneralId()).get().getStatsId()).get().getGWHider() + statsDB.findById(generalDB.findById(y.getGeneralId()).get().getStatsId()).get().getGWSeeker()) / (statsDB.findById(generalDB.findById(y.getGeneralId()).get().getStatsId()).get().getGPHider() + statsDB.findById(generalDB.findById(y.getGeneralId()).get().getStatsId()).get().getGPSeeker()))
				.stream().map(x -> new HashMap<String, Object>() {{
					put("username", userDB.findById(generalDB.findById(x.getGeneralId()).get().getUserId()).get().getUsername());
					put("hider", gameUserDB.findById(generalDB.findById(x.getGeneralId()).get().getGameUserId()).get().getIsHider());
					put("gwhider", statsDB.findById(generalDB.findById(x.getGeneralId()).get().getStatsId()).get().getGWHider());
					put("gwseeker", statsDB.findById(generalDB.findById(x.getGeneralId()).get().getStatsId()).get().getGWSeeker());
					put("gphider", statsDB.findById(generalDB.findById(x.getGeneralId()).get().getStatsId()).get().getGPHider());
					put("gpseeker", statsDB.findById(generalDB.findById(x.getGeneralId()).get().getStatsId()).get().getGPSeeker());
					put("totdistance", statsDB.findById(generalDB.findById(x.getGeneralId()).get().getStatsId()).get().getTotDistance());
					put("tottime", statsDB.findById(generalDB.findById(x.getGeneralId()).get().getStatsId()).get().getTotTime());
					put("found", gameUserDB.findById(generalDB.findById(x.getGeneralId()).get().getGameUserId()).get().getFound());
				}}).collect(Collectors.toList()));
		}}, HttpStatus.OK);
	}

	/*
	 * GET /game/<gameId>/users
	 * 
	 * Mapping for getting game users
	 */
	@RequestMapping(
		value = "/{gameId}/users",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> users(@PathVariable("gameId") String gameId) {
		Optional<Game> game = gameDB.findAll().stream().filter(x -> x.getSession().equals(gameId)).findFirst();
		/* Checks if game exists */
		if(!game.isPresent()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Game not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", gameUserDB
			.findUsersByGame(gameId, (x,y) -> userDB.findById(generalDB.findById(x.getGeneralId()).get().getUserId()).get().getUsername().compareTo(userDB.findById(generalDB.findById(y.getGeneralId()).get().getUserId()).get().getUsername()))
				.stream().map(x -> new HashMap<String, Object>() {{
					put("username", userDB.findById(generalDB.findById(x.getGeneralId()).get().getUserId()).get().getUsername());
					put("hider", gameUserDB.findById(generalDB.findById(x.getGeneralId()).get().getGameUserId()).get().getIsHider());
					put("found", gameUserDB.findById(generalDB.findById(x.getGeneralId()).get().getGameUserId()).get().getFound());
				}}).collect(Collectors.toList()));
		}}, HttpStatus.OK);
	}
}