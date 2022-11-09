package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.client.clientForTests;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.util.AdaptersAndFormat.gson;

@SpringBootTest
class FilmorateApplicationTests {

	private class BlankClass{};
	private class ClassWithName{
		private String name;
		ClassWithName(){
			name = "name";
		}
	};
	private class ClassWithNameAnd{
		private String name;
		private String description;
		private LocalDate releaseDate;
		private Integer duration;
		ClassWithNameAnd(){
			name = "name";
			description = "description";
			releaseDate = LocalDate.now();
			duration = 9;
		}
	};

	String path = "http://localhost:8080";
	clientForTests client = new clientForTests(path);
	ConfigurableApplicationContext ctx;
	User user1 = new User();
	User user2 = new User();
	User user3 = new User();
	Film film = new Film();
	@BeforeAll
	public static void setData() {
	}

	@BeforeEach
	public void setResources() {
		ctx = SpringApplication.run(FilmorateApplication.class, new String[0]);

		user1.setLogin("login");
		user1.setEmail("email@yandex");
		user1.setName("name");
		user1.setBirthday(LocalDate.of(1990,12,9));
		user1.setId(9);

		user2.setLogin("log in");
		user2.setEmail("email@yandex");
		user2.setName("name");
		user2.setBirthday(LocalDate.of(1990,12,9));
		user2.setId(9);

		user3.setLogin("login");
		user3.setEmail("email.yandex");
		user3.setName("name");
		user3.setBirthday(LocalDate.of(1990,12,9));
		user3.setId(9);

		film.setDescription("description");
		film.setDuration(90);
		film.setReleaseDate(LocalDate.of(1920,7,9));
		film.setId(8);
		film.setName("film");
	}

	@AfterEach
	public void freeResources() {
		SpringApplication.exit(ctx);
	}

	@Nested
	public class TestsFilms {
		@Test
		public void testStandardWork() {
			HttpResponse<String> resp = client.post("/films",gson.toJson(film));
			Film f = gson.fromJson(resp.body(), Film.class);
			assertEquals(f.getName(), film.getName());
			assertEquals(f.getDuration(), film.getDuration());
			assertEquals(f.getDescription(), film.getDescription());
		}

		@Test
		public void testPutWrongId() {
			HttpResponse<String> resp = client.put("/films",gson.toJson(film));
			assertEquals(resp.statusCode(), 404);
		}

		@Test
		public void testBlanckRequest() {
			HttpResponse<String> resp = client.put("/films",gson.toJson(new BlankClass()));
			assertEquals(resp.statusCode(), 404);
		}

		@Test
		public void testRequestWithNameAnd() {
			HttpResponse<String> resp = client.post("/films",gson.toJson(new ClassWithNameAnd()));
			Film f = gson.fromJson(resp.body(), Film.class);
			assertEquals(200, resp.statusCode());
			assertEquals("name", f.getName());
			assertEquals("description", f.getDescription());
		}

		@Test
		public void testRequestWithName() {
			HttpResponse<String> resp = client.post("/films",gson.toJson(new ClassWithName()));
			Film f = gson.fromJson(resp.body(), Film.class);
			assertEquals(500, resp.statusCode());
		}

	}

	@Nested
	public class TestsUsers {
		@Test
		public void testStandardWork() {
			final Gson gsonS = new GsonBuilder().create();
			HttpResponse<String> resp = client.post("/users",gson.toJson(user1));
			User f = gson.fromJson(resp.body(), User.class);
			assertEquals(f.getName(), user1.getName());
			assertEquals(f.getEmail(), user1.getEmail());
			assertEquals(f.getLogin(), user1.getLogin());
		}

		@Test
		public void testPutWrongId() {
			HttpResponse<String> resp = client.put("/users",gson.toJson(user1));
			assertEquals(resp.statusCode(), 404);
		}

		@Test
		public void testBlankRequest() {
			HttpResponse<String> resp = client.put("/users",gson.toJson(new BlankClass()));
			//assertEquals(resp.statusCode(), 404);   //test without annotation login
			assertEquals(resp.statusCode(), 500);
		}

		@Test
		public void testWrongLogin() {
			HttpResponse<String> resp = client.post("/users",gson.toJson(user2));
			assertEquals(500, resp.statusCode());
			//String ss = resp.body();
			//assertEquals("\"500 : wrong login format\"", resp.body());
		}

		@Test
		public void testWrongEmail() {
			HttpResponse<String> resp = client.post("/users",gson.toJson(user3));
			assertEquals(400, resp.statusCode());
			String ss = resp.body();
			assertEquals("\"400 : wrong email format\"", resp.body());
		}
	}

	@Nested
	public class Tests {
		@Test
		void userWrongLogin() {
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();

			Set<ConstraintViolation<User>> violations = validator.validate(user2);
			assertFalse(violations.isEmpty());
			assertEquals(violations.size(), 1);
		}

		@Test
		void userCorrect() {
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			Validator validator = factory.getValidator();

			Set<ConstraintViolation<User>> violations = validator.validate(user1);
			assertTrue(violations.isEmpty());
			assertEquals(violations.size(), 0);
		}
	}

}
