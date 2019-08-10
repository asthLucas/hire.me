package com.example.shortURL;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ShortUrlApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void testShortenURL_whenEmptyRequestParameter_thenDefaultJSONisReceived() throws Exception {
			MvcResult result =  mockMvc.perform(MockMvcRequestBuilders
					.get("/create?u="))
					.andReturn();
			
			JSONObject json = new JSONObject(result.getResponse().getContentAsString());
			assertEquals(json.get("ERR_CODE"), "000");
			assertEquals(json.get("DESCRIPTION"), "No input was specified, please inform the URL you wish to shorten.");
	}
	
}
