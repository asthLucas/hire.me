package com.example.shortURL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class URLRetrieverControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	public void testRetrieveURL_whenUsingPreviouslyShortenedURL_thenShouldReturnOriginalURL() throws Exception
	{
		JSONObject body = new JSONObject();
		body.put("url", "http://bemobi.com");
		body.put("custom_alias", null);

		MvcResult result =  mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();
		
		String alias = (String) new ObjectMapper().readValue(result.getResponse().getContentAsString(), HashMap.class).get("ALIAS");

		result = mockMvc.perform(MockMvcRequestBuilders
				.get("/u/".concat(alias)))
				.andReturn();
		
		
		final JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertThrows(JSONException.class, () -> json.get("ERROR_CODE"));
		assertNotNull(json.get("ALIAS"));
	}

	@Test
	public void testRetrieveURL_whenUsingAlias_thenShouldReturnOriginalURL() throws Exception
	{
		JSONObject body = new JSONObject();
		body.put("url", "http://bemobi.com");
		body.put("custom_alias", "bemobi");

		mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/u/bemobi"))
				.andReturn();

		final JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertThrows(JSONException.class, () -> json.get("ERROR_CODE"));
		assertNotNull(json.get("ALIAS"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testRetrieveURL_whenURLNotFound_thenShouldReturnError() throws Exception
	{
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/u/bemobi"))
				.andReturn();

		final HashMap<String, Object> json = new ObjectMapper().readValue(result.getResponse().getContentAsString(), HashMap.class);
		assertEquals("003", json.get("ERROR_CODE"));
		assertEquals("No URL found for the given identifier.", json.get("DESCRIPTION"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testRetrieveURL_whenURLNotFound_thenShouldReturnErrorResponse() throws Exception
	{
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/u/bemobi"))
				.andReturn();

		Map<String, Object> json = new ObjectMapper().readValue(result.getResponse().getContentAsString(), HashMap.class);
		assertEquals("003", json.get("ERROR_CODE"));
		assertEquals("No URL found for the given identifier.", json.get("DESCRIPTION"));
	}

	@Test
	public void testRetrieveURL_whenURLRetrieved_thenShouldRedirect() throws Exception
	{
		JSONObject body = new JSONObject();
		body.put("url", "http://bemobi.com");
		body.put("custom_alias", "bemobi");

		mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();
		
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/u/bemobi"))
				.andReturn();
		
		String headerLocation = result.getResponse().getHeader("Location");
		int responseStatus = result.getResponse().getStatus();

		assertEquals("http://bemobi.com", headerLocation);
		assertEquals(303, responseStatus);
	}
	
	
	@Test
	public void testRetrieveTop10URL_whenOnlyOneURLRequestedOnce_thenShouldReturnURL() throws Exception
	{
		JSONObject body = new JSONObject();
		body.put("url", "http://bemobi.com");

		mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();

		
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/top10"))
				.andReturn();
		
		JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		JSONArray jsonArray = (JSONArray) json.get("URLS");
		json = (JSONObject) jsonArray.get(0);
		
		assertEquals("4fe98b", json.get("ALIAS"));
	}
	
	@Test
	public void testRetrieveTop10URL_whenElevenURLsRequested_thenShouldReturnOnlyTopTenURL() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://google.com"))
				.andReturn();
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 2; j++) {
				mockMvc.perform(MockMvcRequestBuilders
						.get("/create?URL=http://bemobi".concat(Integer.toString(i)).concat(".com&CUSTOM_ALIAS=").concat(Integer.toString(i))))
						.andReturn();
			}
		}
		
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/top10"))
				.andReturn();

		JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		JSONArray jsonArray = (JSONArray) json.get("URLS");
		
		for (int i = 0; i < jsonArray.length(); i++) {
			int timesRequested = Integer.valueOf((Integer) (new JSONObject(jsonArray.getJSONObject(i).get("STATISTICS").toString()).get("TIMES_REQUESTED")));
			assertEquals(2, timesRequested);
		}
	}
	
}
