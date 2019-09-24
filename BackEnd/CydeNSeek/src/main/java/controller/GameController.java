package controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import db.GameDB;
import db.User;
import db.UserDB;
import model.GameConfig;

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
	public ResponseEntity<Map<String, Object>> newGame(@RequestBody GameConfig game) {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("radius", game.getRadius());
			put("players", game.getPlayers());
			put("startTime", game.getStartTime());
			put("duration", game.getDuration());
			put("endTime", game.getEndTime());
			put("mode", game.getMode());
			put("gperiod", game.getGperiod());
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{gameId}/leaderboard",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> leaderboard(@PathVariable("gameId") int gameId) {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("gameId", gameId);
			put("users", userDB.findAll().stream().filter(x -> x.getGameId() == gameId).sorted((x,y) -> x.getScore() - y.getScore()).collect(Collectors.toList()));
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/{gameId}/users",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> users(@PathVariable("gameId") int gameId) {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("gameId", gameId);
			put("users", userDB.findAll().stream().filter(x -> x.getGameId() == gameId).collect(Collectors.toList()));
		}}, HttpStatus.OK);
	}
}