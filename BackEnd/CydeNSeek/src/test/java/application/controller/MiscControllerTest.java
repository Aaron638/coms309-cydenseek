package application.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import application.db.GameDB;
import application.db.GeneralDB;
import application.db.UserDB;
import application.model.General;
import application.model.Stats;
import application.model.User;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class MiscControllerTest {
	
	@Mock
	private GameDB gdb;
	
	@Mock
	private UserDB udb;

	@Mock
	private GeneralDB gedb;
	
	private List<User> users;

	@InjectMocks
	private MiscController miscController = new MiscController();

	private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(miscController).build();
	
	@Before
	public void setup()
	{	
		User u = new User();
		Stats s = new Stats();
		u.setUsername("John");
		u.setSession("abc-123-xyz");
		u.setDeveloper(true);
		s.setGPHider(12);
		s.setGPSeeker(10);
		s.setGWHider(5);
		s.setGWSeeker(8);
		users = Stream.of(u).collect(Collectors.toList());
	}
	
	@Test
	public void index() throws Exception {
		this.mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("")));
	}

	@Test
	public void test() throws Exception {
		this.mockMvc.perform(get("/test"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("obj")));
	}

	@Test
	public void leaderboard() throws Exception {
		General g = new General();
		User u = new User();
		u.setUsername("Tom");
		g.setUser(u);
		Stats s = new Stats();
		s.setGPHider(12);
		s.setGPSeeker(10);
		s.setGWHider(5);
		s.setGWSeeker(8);
		s.setTotDistance(500);
		s.setTotTime(30);
		g.setStats(s);
		when(gedb.findAll()).thenReturn(Stream.of(g).collect(Collectors.toList()));
		this.mockMvc.perform(get("/leaderboard"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("users")));
	}

	@Test
	public void users() throws Exception {
		when(udb.findAll()).thenReturn(users);
		when(udb.findAllUsersSorted(any(Comparator.class))).thenReturn(users);
		this.mockMvc.perform(get("/users?session=abc-123-xyz"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("John")));
	}
	
	@Ignore
	@Test
	public void postUsers() throws Exception{
		User u = new User();
		u.setUsername("Bob");
		this.mockMvc.perform(post("/users")
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(asJsonString(u)))
	            .andExpect(status().isOk());
	}

	public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
	}
}
