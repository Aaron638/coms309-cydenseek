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
	public ResponseEntity<Map<String, Object>> newGame(@RequestBody final GameBody game) {
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
		/*
		if(game.getDuration() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Duration is missing.");
			}}, HttpStatus.BAD_REQUEST);
		}
		*/
		/* Checks if mode not specified */
		/*
		if(game.getMode() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Mode is missing.");
			}}, HttpStatus.BAD_REQUEST);
		}
		*/
		/* Checks if grace period not specified */
		/*
		if(game.getGperiod() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Grace period is missing.");
			}}, HttpStatus.BAD_REQUEST);
		}
		*/
		final Optional<General> foundUser = generalDB.findAll().stream().filter(x -> x.getSession().equals(game.getSession())).findFirst();
		/* Checks if user with token exists */
		if(!foundUser.isPresent()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Invalid session token.");
			}}, HttpStatus.BAD_REQUEST);
		}
		final General row = foundUser.get();
		final User user = userDB.findById(row.getUserId()).get();
		if(ServerWebSocketHandler.games.values().stream().anyMatch(g -> g.getCreator().equals(user.getUsername()))) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Already created game.");
			}}, HttpStatus.BAD_REQUEST);
		}
		/* Creates and builds game */
		final Game newGame = new Game();
		final String session = UUID.randomUUID().toString();
		newGame.setCreator(user.getUsername());
		newGame.setMaxplayers(game.getMaxplayers());
		newGame.setStartTime(LocalTime.now().plusMinutes(5/*game.getGperiod()*/));
		newGame.setDuration(10/*game.getDuration()*/);
		newGame.setGperiod(5/*game.getGperiod()*/);
		ServerWebSocketHandler.games.put(session, newGame);
		generalDB.saveAndFlush(row);
		LOG.info(user.getUsername() + " created a new game.");
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("session", session);
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
	public ResponseEntity<Map<String, Object>> updateGame(@PathVariable("gameSession") final String gameSession, @RequestBody final GameBody game) {
		/* Checks if session not present */
		if(game.getSession() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Session token not present.");
			}}, HttpStatus.BAD_REQUEST);
		}
		final Game foundGame = ServerWebSocketHandler.games.get(gameSession);
		/* Checks if game exists */
		if(foundGame == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Game not found.");
			}}, HttpStatus.NOT_FOUND);
		}
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
		ServerWebSocketHandler.games.put(gameSession, foundGame);
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
	public ResponseEntity<Map<String, Object>> leaderboard(@PathVariable("gameSession") final String gameSession) {
		final Game game = ServerWebSocketHandler.games.get(gameSession);
		/* Checks if game exists */
		if(game == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Game not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		final List<GameUser> gameusers = ServerWebSocketHandler.gameusers.values().stream().filter(x -> gameSession.equals(x.getGameSession())).collect(Collectors.toList());
		final Map<Integer, General> rows = generalDB.findAll().stream().collect(Collectors.toMap(x->x.getId(), x->x));
		final Map<Integer, String> users = userDB.findAll().stream().collect(Collectors.toMap(x->rows.get(x.getGeneralId()).getStatsId(), x->x.getUsername()));
		final Map<String, Stats> userStats = statsDB.findAll().stream().collect(Collectors.toMap(x->users.get(x.getId()), x->x));
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", gameusers.stream().sorted((x,y) -> {
				final Stats statsX = userStats.get(x.getUsername());
				final Stats statsY = userStats.get(y.getUsername());
				final Integer totalGamesX = statsX.getGPHider() + statsX.getGPSeeker();
				final Integer totalGamesY = statsY.getGPHider() + statsY.getGPSeeker();
				return (statsX.getGWHider() + statsX.getGWSeeker()) / (totalGamesX.equals(0) ? 1 : totalGamesX) - (statsY.getGWHider() + statsY.getGWSeeker()) / (totalGamesY.equals(0) ? 1 : totalGamesY);
			}).map(x -> {
				final Stats stats = userStats.get(x.getUsername());
				return new HashMap<String, Object>() {{
					put("username", x.getUsername());
					put("hider", x.isHider());
					put("found", x.isFound());
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
	public ResponseEntity<Map<String, Object>> users(@PathVariable("gameSession") final String gameSession) {
		final Game game = ServerWebSocketHandler.games.get(gameSession);
		/* Checks if game exists */
		if(game == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Game not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", ServerWebSocketHandler.gameusers.values().stream().filter(x -> gameSession.compareTo(x.getGameSession()) == 0).collect(Collectors.toList()).stream().sorted((x,y) -> x.getUsername().compareTo(y.getUsername())).map(x -> {
				return new HashMap<String, Object>() {{
					put("username", x.getUsername());
					put("hider", x.isHider());
					put("found", x.isFound());
				}};
			}).collect(Collectors.toList()));
		}}, HttpStatus.OK);
	}
}