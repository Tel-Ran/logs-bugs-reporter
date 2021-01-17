package telran.logs.bugs.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Date;

import javax.validation.Valid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@WebMvcTest(LogDtoTest.TestController.class) //what classes are tested
@ContextConfiguration(classes=LogDtoTest.TestController.class) //what classes will be in AC
public class LogDtoTest {
public static @RestController class TestController {
	static LogDto logDtoExp = new LogDto(new Date(), LogType.NO_EXCEPTION,
			"artifact", 0, "");
	@PostMapping("/")
	void testPost(@RequestBody @Valid LogDto logDto) {
		assertEquals(logDtoExp, logDto);
	}
}
ObjectMapper mapper = new ObjectMapper();
@Autowired
MockMvc mock;
@Test
void testPostRun() throws JsonProcessingException, Exception {
	assertEquals(200, mock.perform(post("/")
			.contentType(MediaType.APPLICATION_JSON)
			.content(mapper.writeValueAsString(TestController.logDtoExp)))
			.andReturn()
			.getResponse().getStatus());
}
}
