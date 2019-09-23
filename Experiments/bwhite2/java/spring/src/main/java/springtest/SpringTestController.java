package springtest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class SpringTestController {

	@RequestMapping("/")
	public String index() {
		return "This is index page.";
	}
}
