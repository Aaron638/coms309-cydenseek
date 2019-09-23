package controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
public class UserControllerTest {

	private static final String USERNAME = "user";

	@InjectMocks
	private UserController userController = new UserController();

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Ignore
	@Test
	public void getUser() throws Exception {
		this.mockMvc.perform(get("/user/" + USERNAME))
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}

	@Ignore
	@Test
	public void updateUser() throws Exception {
		this.mockMvc.perform(put("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}

	@Ignore
	@Test
	public void newUser() throws Exception {
		this.mockMvc.perform(post("/user/" + USERNAME)
			.contentType(APPLICATION_JSON_VALUE)
			.content("{}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}

	@Ignore
	@Test
	public void auth() throws Exception {
		this.mockMvc.perform(post("/user/" + USERNAME + "/auth")
			.contentType(APPLICATION_JSON_VALUE)
			.content("{}")
		)
			.andExpect(status().isOk())
			.andExpect(content().string(""));
	}
}