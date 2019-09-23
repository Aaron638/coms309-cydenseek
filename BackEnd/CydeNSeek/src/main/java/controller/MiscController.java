package controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MiscController {

	@RequestMapping(
		value = "/leaderboard",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> leaderboard() {
		List<Map<String, Object>> users = new ArrayList<>();
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", users);
		}}, HttpStatus.OK);
	}

	@RequestMapping(
		value = "/users",
		method = RequestMethod.GET,
		produces = APPLICATION_JSON_VALUE
	)
	public ResponseEntity<Map<String, Object>> users() {
		List<Map<String, Object>> users = new ArrayList<>();
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("users", users);
		}}, HttpStatus.OK);
	}
}