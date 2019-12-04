package application.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import application.db.GeneralDB;
import application.db.StatsDB;
import application.db.UserDB;
import application.model.Game;
import application.model.GameUser;
import application.model.General;
import application.model.Stats;
import application.model.User;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class GameControllerTest {

	private static final String GAMESESSION = "game-session";
	private static final String USERSESSION = "user-session";

	@Mock
	private GeneralDB generalDB;

	@Mock
	private UserDB userDB;

	@Mock
	private StatsDB statsDB;

	@InjectMocks
	private GameController gameController = new GameController();

	private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

	@Test
	public void newGame_fails_whenBodyEmpty_becauseSessionTokenMissing() throws Exception {
		this.mockMvc.perform(post("/game/new")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("token")));
	}

	@Test
	public void newGame_fails_whenMaxplayersMissing() throws Exception {
		this.mockMvc.perform(post("/game/new")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"\""
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Maxplayers")));
	}

	@Ignore
	@Test
	public void newGame_fails_whenDurationMissing() throws Exception {
		this.mockMvc.perform(post("/game/new")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"\","
				+ "\"maxplayers\":3"
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Duration")));
	}

	@Ignore
	@Test
	public void newGame_fails_whenModeMissing() throws Exception {
		this.mockMvc.perform(post("/game/new")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"\","
				+ "\"maxplayers\":3,"
				+ "\"duration\":10"
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Mode")));
	}

	@Ignore
	@Test
	public void newGame_fails_whenGracePeriodMissing() throws Exception {
		this.mockMvc.perform(post("/game/new")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"\","
				+ "\"maxplayers\":3,"
				+ "\"duration\":10,"
				+ "\"mode\":0"
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Grace period")));
	}

	@Ignore
	@Test
	public void newGame_fails_whenHiderNotSpecified() throws Exception {
		this.mockMvc.perform(post("/game/new")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"\","
				+ "\"maxplayers\":3,"
				+ "\"duration\":10,"
				+ "\"mode\":0,"
				+ "\"gperiod\":5"
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("hider")));
	}

	@Test
	public void newGame_fails_whenSessionTokenInvalid() throws Exception {
		this.mockMvc.perform(post("/game/new")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"\","
				+ "\"maxplayers\":3,"
				+ "\"duration\":10,"
				+ "\"mode\":0,"
				+ "\"gperiod\":5,"
				+ "\"hider\":true"
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Invalid session token")));
	}

	@Test
	public void newGame_succeeds() throws Exception {
		when(userDB.findById(any(Integer.class))).thenReturn(Optional.of(buildUser()));
		when(generalDB.findAll()).thenReturn(Stream.of(buildGeneral()).collect(Collectors.toList()));
		this.mockMvc.perform(post("/game/new")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\": \"" + USERSESSION + "\","
				+ "\"maxplayers\": 10,"
				+ "\"duration\": 5,"
				+ "\"mode\": 0,"
				+ "\"gperiod\": 10,"
				+ "\"hider\": true"
				+ "}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("session")));
	}

	@Test
	public void updateGame_failsWhenBodyEmpty_becauseSessionTokenMissing() throws Exception {
		this.mockMvc.perform(put("/game/" + GAMESESSION)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("token")));
	}

	@Test
	public void updateGame_failsWhenGameSessionInvalid() throws Exception {
		this.mockMvc.perform(put("/game/" + GAMESESSION)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"\""
				+ "}")
		)
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("not found")));
	}

	@Test
	public void updateGame_failsWhenSessionTokenInvalid() throws Exception {
		ServerWebSocketHandler.games.put(GAMESESSION, buildGame());
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(buildUser()));
		General row = new General();
		row.setSession("not-John");
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		this.mockMvc.perform(put("/game/" + GAMESESSION)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"\""
				+ "}")
		)
			.andExpect(status().isForbidden())
			.andExpect(content().string(containsString("someone else")));
	}

	@Test
	public void updateGame_succeeds() throws Exception {
		ServerWebSocketHandler.games.put(GAMESESSION, buildGame());
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(buildUser()));
		General row = new General();
		row.setSession(USERSESSION);
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		this.mockMvc.perform(put("/game/" + GAMESESSION)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"" + USERSESSION + "\""
				+ "}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("{}")));
	}

	@Test
	public void leaderboard_failsWhenGameSessionInvalid() throws Exception {
		this.mockMvc.perform(get("/game/" + UUID.randomUUID() + "/leaderboard"))
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("not found")));
	}

	@Test
	public void leaderboard_succeeds() throws Exception {
		ServerWebSocketHandler.gameusers.put(null, buildGameUser());
		when(generalDB.findAll()).thenReturn(Stream.of(buildGeneral()).collect(Collectors.toList()));
		when(userDB.findAll()).thenReturn(Stream.of(buildUser()).collect(Collectors.toList()));
		when(statsDB.findAll()).thenReturn(Stream.of(buildStats()).collect(Collectors.toList()));
		this.mockMvc.perform(get("/game/" + GAMESESSION + "/leaderboard"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("John")));
	}

	@Test
	public void users_failsWhenGameSessionInvalid() throws Exception {
		this.mockMvc.perform(get("/game/" + UUID.randomUUID() + "/users"))
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("not found")));
	}

	@Test
	public void users_succeeds() throws Exception {
		ServerWebSocketHandler.gameusers.put(null, buildGameUser());
		this.mockMvc.perform(get("/game/" + GAMESESSION + "/users"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("John")));
	}

	private static General buildGeneral() {
		General general = new General();
		general.setId(1);
		general.setSession(USERSESSION);
		general.setUserId(1);
		general.setStatsId(1);
		return general;
	}

	private static User buildUser() {		
		User user = new User();
		user.setUsername("John");
		user.setGeneralId(1);
		return user;
	}

	private static Stats buildStats() {
		Stats stats = new Stats();
		stats.setId(1);
		stats.setGPHider(5);
		stats.setGPSeeker(10);
		stats.setGWHider(2);
		stats.setGWSeeker(3);
		stats.setTotDistance(45.0);
		stats.setTotTime(39);
		stats.setGeneralId(1);
		return stats;
	}

	private static GameUser buildGameUser() {
		GameUser gu = new GameUser();
		gu.setUsername("John");
		gu.setGameSession(GAMESESSION);
		gu.setHider(true);
		gu.setFound(true);
		return gu;
	}

	private static Game buildGame() {
		Game game = new Game();
		game.setCreator("John");
		return game;
	}
}