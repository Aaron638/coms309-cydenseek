package application.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerErrorController implements ErrorController {

	private static final Log LOG = LogFactory.getLog(ServerErrorController.class);

	/* Handles errors */
	@RequestMapping("/error")
	public ResponseEntity<Map<String, Object>> error() {
		LOG.error("Error mapping utilized.");
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("error", new HashMap<String, Object>() {{
				put("message", "There was an issue with your request.");				
			}});
		}}, HttpStatus.BAD_REQUEST);
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}
}