package application.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import application.db.GameDB;
import application.db.GeneralDB;
import application.db.StatsDB;
import application.db.UserDB;
import application.model.General;
import application.model.Stats;
import application.model.User;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class MiscControllerTest {

	@Mock
	private GeneralDB generalDB;

	@Mock
	private GameDB gameDB;

	@Mock
	private UserDB userDB;

	@Mock
	private StatsDB statsDB;

	private List<User> users;

	@InjectMocks
	private MiscController miscController = new MiscController();

	private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(miscController).build();

	@Before
	public void setup() {
		User u = new User();
		u.setGeneralId(1);
		Stats s = new Stats();
		u.setUsername("John");
		u.setDeveloper(true);
		s.setGPHider(12);
		s.setGPSeeker(10);
		s.setGWHider(5);
		s.setGWSeeker(8);
		s.setGeneralId(1);
		General row = new General();
		row.setUserId(1);
		row.setStatsId(1);
		row.setSession("abc-123-xyz");
		users = Stream.of(u).collect(Collectors.toList());
	}
	
	@Test
	public void index_succeeds() throws Exception {
		this.mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("")));
	}

	@Test
	public void test_succeeds() throws Exception {
		this.mockMvc.perform(get("/test"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("obj")));
	}

	@Test
	public void leaderboard_succeeds() throws Exception {
		General g = new General();
		g.setUserId(1);
		g.setStatsId(1);
		User u = new User();
		u.setUsername("Tom");
		u.setGeneralId(1);
		u.setId(1);
		Stats s = new Stats();
		s.setGPHider(12);
		s.setGPSeeker(10);
		s.setGWHider(5);
		s.setGWSeeker(8);
		s.setTotDistance(500);
		s.setTotTime(30);
		s.setGeneralId(1);
		s.setId(1);
		when(userDB.findAll()).thenReturn(Stream.of(u).collect(Collectors.toList()));
		when(statsDB.findAll()).thenReturn(Stream.of(s).collect(Collectors.toList()));
		when(generalDB.findAll()).thenReturn(Stream.of(g).collect(Collectors.toList()));
		this.mockMvc.perform(get("/leaderboard"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("users")));
	}

	@Test
	public void users_failsWhenSessionTokenInvalid() throws Exception {
		User user = new User();
		user.setGeneralId(1);
		when(userDB.findAll()).thenReturn(Stream.of(user).collect(Collectors.toList()));
		General row = new General();
		row.setSession("not-John");
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		this.mockMvc.perform(get("/users?session=abc-123-xyz"))
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("not found")));
	}

	@Test
	public void users_failsWhenNotDeveloper() throws Exception {
		User user = new User();
		user.setGeneralId(1);
		user.setDeveloper(false);
		when(userDB.findAll()).thenReturn(Stream.of(user).collect(Collectors.toList()));
		General row = new General();
		row.setSession("abc-123-xyz");
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		this.mockMvc.perform(get("/users?session=abc-123-xyz"))
			.andExpect(status().isUnauthorized())
			.andExpect(content().string(containsString("developer")));
	}

	@Test
	public void users_succeeds() throws Exception {
		General g = new General();
		g.setSession("abc-123-xyz");
		when(userDB.findAll()).thenReturn(users);
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(g));
		when(userDB.findAllUsersSorted(any(Comparator.class))).thenReturn(users);
		this.mockMvc.perform(get("/users?session=abc-123-xyz"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("John")));
	}

	@Test
	public void games_succeeds() throws Exception {
		this.mockMvc.perform(get("/games"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("games")));
	}
}