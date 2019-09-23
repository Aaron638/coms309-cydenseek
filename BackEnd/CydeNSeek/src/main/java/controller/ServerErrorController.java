package controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServerErrorController implements ErrorController {

	@RequestMapping("/error")
	public ResponseEntity<Map<String, Object>> error() {
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