package controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringBootTest
public class ServerErrorControllerTest {

	private ServerErrorController serverErrorController = new ServerErrorController();

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(serverErrorController).build();
	}

	@Ignore
	@Test
	public void error() throws Exception {
		this.mockMvc.perform(get("/error"))
			.andExpect(status().isBadRequest())
			.andExpect(content().string(""));
	}
}