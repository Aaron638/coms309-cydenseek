package application.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class GameControllerTest {

	private static final String GAMEID = "12345";

	@InjectMocks
	private GameController gameController = new GameController();

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
	}

	@Ignore
	@Test
	public void newGame() throws Exception {
		
		this.mockMvc.perform(post("/game/new")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{\"session\" : \"abc-xyz-123\"}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("session : abc-xyz-123")));
	}

	
	@Test
	public void leaderboard() throws Exception {
		this.mockMvc.perform(get("/game/" + GAMEID + "/leaderboard"))
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}

	
	@Test
	public void users() throws Exception {
		this.mockMvc.perform(get("/game/" + GAMEID + "/users"))
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}
}