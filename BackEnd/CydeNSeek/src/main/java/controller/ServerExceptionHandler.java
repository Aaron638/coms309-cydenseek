package controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ServerExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> error(Exception e) {
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("error", new HashMap<String, Object>() {{				
				put("message", e.getMessage());
			}});
		}}, HttpStatus.BAD_REQUEST);
	}
}