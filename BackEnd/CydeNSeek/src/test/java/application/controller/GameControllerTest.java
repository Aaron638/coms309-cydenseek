package application.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import application.db.GameDB;
import application.db.GameUserDB;
import application.db.GeneralDB;
import application.db.UserDB;
import application.model.Game;
import application.model.GameUser;
import application.model.General;
import application.model.Stats;
import application.model.User;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class GameControllerTest {

	private static final String GAMEID = "12345";

	@Mock
	private GeneralDB generalDB;

	@Mock
	private UserDB userDB;

	@Mock
	private GameDB gameDB;

	@Mock
	private GameUserDB gameUserDB;

	@InjectMocks
	private GameController gameController = new GameController();

	private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

	@Test
	public void newGame() throws Exception {
		General row = new General();
		User user = new User();
		user.setUsername("John");
		row.setSession("abc-xyz-123");
		when(generalDB.findAll()).thenReturn(Stream.of(row).collect(Collectors.toList()));
		this.mockMvc.perform(post("/game/new")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\": \"abc-xyz-123\","
				+ "\"maxplayers\": 10,"
				+ "\"duration\": 5,"
				+ "\"mode\": 0,"
				+ "\"gperiod\": 10,"
				+ "\"hider\": true"
				+ "}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("John")));
	}

	@Test
	public void leaderboard() throws Exception {
		when(gameDB.findById(any(Integer.class))).thenReturn(Optional.of(new Game()));
		when(gameUserDB.findUsersByGame(any(Integer.class), any(Comparator.class))).thenReturn(Stream.of(buildGeneral()).collect(Collectors.toList()));
		this.mockMvc.perform(get("/game/" + GAMEID + "/leaderboard"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("John")));
	}

	@Test
	public void users() throws Exception {
		when(gameDB.findById(any(Integer.class))).thenReturn(Optional.of(new Game()));
		when(gameUserDB.findUsersByGame(any(Integer.class), any(Comparator.class))).thenReturn(Stream.of(buildGeneral()).collect(Collectors.toList()));
		this.mockMvc.perform(get("/game/" + GAMEID + "/users"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("John")));
	}

	private static General buildGeneral() {
		General general = new General();
		User user = new User();
		user.setUsername("John");
		Stats stats = new Stats();
		stats.setGPHider(5);
		stats.setGPSeeker(10);
		stats.setGWHider(2);
		stats.setGWSeeker(3);
		stats.setTotDistance(45);
		stats.setTotTime(39);
		GameUser gameUser = new GameUser();
		gameUser.setFound(false);
		gameUser.setIsHider(true);
		return general;
	}
}