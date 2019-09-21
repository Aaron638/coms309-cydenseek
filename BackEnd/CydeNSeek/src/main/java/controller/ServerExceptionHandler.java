package controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ServerExceptionHandler {

	private static final Log LOG = LogFactory.getLog(ServerExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> error(Exception e) {
		LOG.error(e);
		return new ResponseEntity<>(new HashMap<String, Object>() {{
			put("error", new HashMap<String, Object>() {{				
				put("message", "An exception was thrown. Check the logs for more details.");
			}});
		}}, HttpStatus.BAD_REQUEST);
	}
}