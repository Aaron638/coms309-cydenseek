package application.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import application.db.GeneralDB;
import application.db.StatsDB;
import application.db.UserDB;
import application.model.General;
import application.model.Stats;
import application.model.User;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class UserControllerTest {

	private static final String USERNAME = "user";
	private static final String USERSESSION = "user-session";

	@Mock
	private GeneralDB generalDB;

	@Mock
	private UserDB userDB;

	@Mock
	private StatsDB statsDB;

	@InjectMocks
	private UserController userController = new UserController();

	private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

	@Test
	public void getUser_failsWhenInvalidUsername() throws Exception {
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.empty());
		this.mockMvc.perform(get("/user/" + USERNAME))
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("not found")));
	}

	@Test
	public void getUser_succeeds() throws Exception {
		User user = new User();
		user.setUsername(USERNAME);
		user.setGeneralId(1);
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(user));
		General row = new General();
		row.setStatsId(1);
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		Stats stats = new Stats();
		stats.setGPHider(3);
		stats.setGPSeeker(6);
		stats.setGWHider(1);
		stats.setGWSeeker(3);
		stats.setTotDistance(5);
		stats.setTotTime(6);
		when(statsDB.findById(any(Integer.class))).thenReturn(Optional.of(stats));
		this.mockMvc.perform(get("/user/" + USERNAME))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("gphider")));
	}

	@Test
	public void updateUser_failsWhenSessionTokenMissing() throws Exception {
		this.mockMvc.perform(put("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("token")));
	}

	@Test
	public void updateUser_failsWhenUsernameInvalid() throws Exception {
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.empty());
		this.mockMvc.perform(put("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"" + USERSESSION + "\""
				+ "}")
		)
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("not found")));
	}

	@Test
	public void updateUser_failsWhenSessionTokenInvalid() throws Exception {
		User user = new User();
		user.setGeneralId(1);
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(user));
		General row = new General();
		row.setSession(USERSESSION);
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		this.mockMvc.perform(put("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"not-a-session\""
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("not found")));
	}

	@Test
	public void updateUser_succeeds() throws Exception {
		User user = new User();
		user.setGeneralId(1);
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(user));
		General row = new General();
		row.setSession(USERSESSION);
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		this.mockMvc.perform(put("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"" + USERSESSION + "\""
				+ "}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("{}")));
	}

	@Test
	public void newUser_failsWithoutPassword() throws Exception {
		this.mockMvc.perform(post("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("password")));
	}

	@Test
	public void newUser_failsWithTakenUsername() throws Exception {
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(new User()));
		this.mockMvc.perform(post("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"password\":\"mypassword\""
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("already taken")));
	}

	@Test
	public void newUser_succeeds() throws Exception {
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.empty());
		final General row = new General();
		row.setId(1);
		when(generalDB.saveAndFlush(any(General.class))).then(x -> {
			row.setSession(((General)x.getArgument(0)).getSession());
			return row;
		});
		when(generalDB.findAll()).thenReturn(Stream.of(row).collect(Collectors.toList()));
		User user = new User();
		user.setId(1);
		user.setGeneralId(1);
		when(userDB.findAll()).thenReturn(Stream.of(user).collect(Collectors.toList()));
		Stats stats = new Stats();
		stats.setId(1);
		stats.setGeneralId(1);
		when(statsDB.findAll()).thenReturn(Stream.of(stats).collect(Collectors.toList()));
		this.mockMvc.perform(post("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"password\":\"mypassword\""
				+ "}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("session")));
	}

	@Test
	public void authenticate_failsWhenPasswordMissing() throws Exception {
		this.mockMvc.perform(post("/user/" + USERNAME + "/auth")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{}")
			)
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("password")));
	}
	
	@Test
	public void authenticate_failsWhenInvalidUser() throws Exception {
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.empty());
		this.mockMvc.perform(post("/user/" + USERNAME + "/auth")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"password\":\"mypassword\""
				+ "}")
			)
		.andExpect(status().isNotFound())
		.andExpect(content().string(containsString("not found")));
	}

	@Test
	public void authenticate_failsWhenPasswordInvalid() throws Exception {
		User user = new User();
		user.setPassword("mypassword");
		user.setGeneralId(1);
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(user));
		this.mockMvc.perform(post("/user/" + USERNAME + "/auth")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"password\":\"notmypassword\""
				+ "}")
			)
		.andExpect(status().isBadRequest())
		.andExpect(content().string(containsString("password")));
	}
	
	@Test
	public void authenticate_succeeds() throws Exception {
		User user = new User();
		user.setPassword("mypassword");
		user.setGeneralId(1);
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(user));
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(new General()));
		this.mockMvc.perform(post("/user/" + USERNAME + "/auth")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"password\":\"mypassword\""
				+ "}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("session")));
	}

	@Test
	public void logout_failsWithoutSessionToken() throws Exception {
		this.mockMvc.perform(post("/user/" + USERNAME + "/logout")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("session token")));
	}

	@Test
	public void logout_failsWithInvalidUser() throws Exception {
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.empty());
		this.mockMvc.perform(post("/user/" + USERNAME + "/logout")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"abc-123-xyz\""
				+ "}")
		)
			.andExpect(status().isNotFound())
			.andExpect(content().string(containsString("not found")));
	}

	@Test
	public void logout_failsWithInvalidSessionToken() throws Exception {
		User u = new User();
		u.setGeneralId(1);
		General row = new General();
		row.setSession("");
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(u));
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		this.mockMvc.perform(post("/user/" + USERNAME + "/logout")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"abc-123-xyz\""
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("session token")));
	}

	@Test
	public void logout_succeeds() throws Exception {
		User u = new User();
		u.setGeneralId(1);
		General row = new General();
		row.setSession("abc-123-xyz");
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(u));
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		this.mockMvc.perform(post("/user/" + USERNAME + "/logout")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"abc-123-xyz\""
				+ "}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("")));
	}

	@Test
	public void deleteUser_failsWhenSessionTokenMissing() throws Exception {
		this.mockMvc.perform(delete("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("not found")));
	}

	@Test
	public void deleteUser_failsWhenPasswordMissing() throws Exception {
		this.mockMvc.perform(delete("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"user-session\""
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("Password")));
	}

	@Test
	public void deleteUser_failsWhenUsernameInvalid() throws Exception {
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.empty());
		this.mockMvc.perform(delete("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"user-session\","
				+ "\"password\":\"mypassword\""
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("not found")));
	}

	@Test
	public void deleteUser_failsWhenSessionTokenInvalid() throws Exception {
		User user = new User();
		user.setPassword("mypassword");
		user.setGeneralId(1);
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(user));
		General row = new General();
		row.setSession("not-user-session");
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		this.mockMvc.perform(delete("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"user-session\","
				+ "\"password\":\"mypassword\""
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("incorrect")));
	}

	@Test
	public void deleteUser_failsWhenPasswordInvalid() throws Exception {
		User user = new User();
		user.setPassword("notmypassword");
		user.setGeneralId(1);
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(user));
		General row = new General();
		row.setSession("user-session");
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		this.mockMvc.perform(delete("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"user-session\","
				+ "\"password\":\"mypassword\""
				+ "}")
		)
			.andExpect(status().isBadRequest())
			.andExpect(content().string(containsString("incorrect")));
	}

	@Test
	public void deleteUser_succeeds() throws Exception {
		User user = new User();
		user.setPassword("mypassword");
		user.setGeneralId(1);
		when(userDB.findUserByUsername(any(String.class))).thenReturn(Optional.of(user));
		General row = new General();
		row.setSession("user-session");
		when(generalDB.findById(any(Integer.class))).thenReturn(Optional.of(row));
		this.mockMvc.perform(delete("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{"
				+ "\"session\":\"user-session\","
				+ "\"password\":\"mypassword\""
				+ "}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("{}")));
	}
}