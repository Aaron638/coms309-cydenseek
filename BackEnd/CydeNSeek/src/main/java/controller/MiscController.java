package controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MiscController {

	private static final Log LOG = LogFactory.getLog(MiscController.class);

	@RequestMapping("/")
	public ResponseEntity<Map<String, Object>> index() {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("about", "This is the index page. If you are seeing this, then it means the server is running and the endpoints are live.");
		}}, HttpStatus.OK);
	}

	@RequestMapping("/test")
	public ResponseEntity<Map<String, Object>> test() {
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