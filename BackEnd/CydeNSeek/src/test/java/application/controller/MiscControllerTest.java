package application.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
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
import application.model.User;
import static org.hamcrest.Matchers.containsString;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class MiscControllerTest {
	
	@Mock
	private GameDB gdb;
	
	@Mock
	private UserDB udb;
	
	private List<User> users;

	@InjectMocks
	private MiscController miscController = new MiscController();

	private MockMvc mockMvc = MockMvcBuilders.standaloneSetup(miscController).build();
	
	@Before
	public void setup()
	{	
		users = new ArrayList<User>();
		User u = new User();
		u.setUsername("John");
		u.setSession("abc");
		u.setGphider(12);
		u.setGpseeker(10);
		u.setGwhider(5);
		u.setGwseeker(8);
		users.add(u);
	}
	@Ignore
	@Test
	public void index() throws Exception {
		this.mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}

	@Ignore
	@Test
	public void test() throws Exception {
		this.mockMvc.perform(get("/test"))
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}

	@Test
	public void leaderboard() throws Exception {
		when(udb.findAllUsersSorted(any(Comparator.class))).thenReturn(users);		
		this.mockMvc.perform(get("/leaderboard"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("John")))
			.andExpect(content().string(containsString("abc")));
	}

	@Ignore
	@Test
	public void users() throws Exception {
		when(udb.findAllUsersSorted(any(Comparator.class))).thenReturn(users);
		this.mockMvc.perform(get("/users"))
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}
}