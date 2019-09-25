package application.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
import application.db.UserDB;
import application.model.Game;
import application.model.GameBody;

@RestController
@RequestMapping("/game")
public class GameController {

	private static final Log LOG = LogFactory.getLog(GameController.class);

	@Autowired
	private GameDB gameDB;

	@Autowired
	private UserDB userDB;

	@RequestMapping(
		value = "/new",
		method = RequestMethod.POST,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> newGame(@RequestBody GameBody game) {
		if(game.getSession() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Session token not present.");
			}}, HttpStatus.BAD_REQUEST);
		}
		if(!userDB.findUserByUsername(game.getUsername()).getSession().equals(game.getSession())) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Invalid session token for specified user.");
			}}, HttpStatus.BAD_REQUEST);
		}
		Game newGame = new Game();
		newGame.setPlayers(0);
		newGame.setCreator(game.getUsername());
		newGame.setRadius(game.getRadius());
		newGame.setMaxplayers(game.getMaxplayers());
		newGame.setStartTime(LocalTime.now());
		newGame.setDuration(game.getDuration());
		newGame.setEndTime(newGame.getStartTime().plusSeconds(newGame.getDuration()));
		newGame.setMode(game.getMode());
		newGame.setGperiod(game.getGperiod());
		gameDB.save(newGame);
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("game", game);
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{gameId}",
		method = RequestMethod.PUT,
		consumes = APPLICATION_JSON_VALUE,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> updateGame(@PathVariable("gameId") int gameId, @RequestBody GameBody game) {
		if(game.getSession() == null) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Session token not present.");
			}}, HttpStatus.BAD_REQUEST);
		}
		Optional<Game> checkGame = gameDB.findById(gameId);
		if(!checkGame.isPresent()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Game not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		Game foundGame = checkGame.get();
		if(!userDB.findUserByUsername(foundGame.getCreator()).getSession().equals(game.getSession())) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Cannot change game created by someone else.");
			}}, HttpStatus.FORBIDDEN);
		}
		if(game.getRadius() != null) foundGame.setRadius(game.getRadius());
		if(game.getMaxplayers() != null) foundGame.setMaxplayers(game.getMaxplayers());
		if(game.getDuration() != null) {
			foundGame.setDuration(game.getDuration());
			foundGame.setEndTime(foundGame.getStartTime().plusSeconds(game.getDuration()));
		}
		if(game.getMode() != null) foundGame.setMode(game.getMode());
		if(game.getGperiod() != null) foundGame.setGperiod(game.getGperiod());
		gameDB.save(foundGame);
		return new ResponseEntity<>(new HashMap<String, Object>() {{}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{gameId}/leaderboard",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> leaderboard(@PathVariable("gameId") int gameId) {
		Optional<Game> game = gameDB.findById(gameId);
		if(!game.isPresent()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Game not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", userDB
				.findUsersByGame(gameId, (x,y) -> (x.getGwhider() + x.getGwseeker()) / (x.getGphider() + x.getGpseeker()) - (y.getGwhider() + y.getGwseeker()) / (y.getGphider() + y.getGpseeker()))
				.stream().map(x -> new HashMap<String, Object>() {{
					put("username", x.getUsername());
					put("gwhider", x.getGwhider());
					put("gwseeker", x.getGwseeker());
					put("gphider", x.getGphider());
					put("gpseeker", x.getGpseeker());
					put("totdistance", x.getTotdistance());
					put("tottime", x.getTottime());
				}}).collect(Collectors.toList()));
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{gameId}/users",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> users(@PathVariable("gameId") int gameId) {
		Optional<Game> game = gameDB.findById(gameId);
		if(!game.isPresent()) {
			return new ResponseEntity<>(new HashMap<String, Object>() {{
				put("error", true);
				put("message", "Game not found.");
			}}, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", userDB
				.findUsersByGame(gameId, (x,y) -> x.getUsername().compareTo(y.getUsername()))
				.stream().map(x -> new HashMap<String, Object>() {{
					put("username", x.getUsername());
				}}).collect(Collectors.toList()));
		}}, HttpStatus.OK);
	}
}