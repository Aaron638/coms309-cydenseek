package application.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import application.db.GameDB;
import application.db.UserDB;
import application.model.Game;
import application.model.User;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class GameControllerTest {

	private static final String GAMEID = "12345";

	@Mock
	private GameDB gameDB;

	@Mock
	private UserDB userDB;

	@InjectMocks
	private GameController gameController = new GameController();

	private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

	@Test
	public void newGame_successfullySavesGame() throws Exception {
		List<User> users = new ArrayList<>();
		User user = new User();
		user.setUsername("abc");
		user.setSession("abc-123-xyz");
		users.add(user);
		when(userDB.findAll()).thenReturn(users);
		List<Game> games = new ArrayList<>();
		Game game = new Game();
		game.setId(0);
		game.setCreator("abc");
		games.add(game);
		when(gameDB.findAll()).thenReturn(games);
		when(gameDB.saveAndFlush(any(Game.class))).thenReturn(null);
		when(userDB.saveAndFlush(any(User.class))).thenReturn(null);
		this.mockMvc.perform(post("/game/new")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"abc-123-xyz\","
				+ "\"radius\":5,"
				+ "\"maxplayers\":10,"
				+ "\"duration\":10,"
				+ "\"mode\":0,"
				+ "\"gperiod\":5,"
				+ "\"hider\":true"
				+ "}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("game")));
	}

	@Ignore
	@Test
	public void leaderboard() throws Exception {
		this.mockMvc.perform(get("/game/" + GAMEID + "/leaderboard"))
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}

	@Ignore
	@Test
	public void users() throws Exception {
		this.mockMvc.perform(get("/game/" + GAMEID + "/users"))
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}
}