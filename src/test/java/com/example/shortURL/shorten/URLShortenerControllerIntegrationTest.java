package com.example.shortURL.shorten;

import static org.junit.Assert.assertEquals;

import java.util.List;

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

import com.example.shortURL.model.URLEntity;
import com.example.shortURL.model.URLEntityRepository;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:test.properties")
public class URLShortenerControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private URLEntityRepository urlEntityRepository;

	@Test
	public void testShortenURL_whenEmptyRequestParameter_thenDefaultJSONisReceived() throws Exception {
		JSONObject body = new JSONObject();
		body.put("url", "");
		body.put("custom_alias", null);

		MvcResult result =  mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();
		
		JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertEquals(json.get("ERROR_CODE"), "000");
		assertEquals(json.get("DESCRIPTION"), "No input was specified, please inform the URL you wish to shorten.");
	}
	
	@Test
	public void testShortenURL_whenDataStoreEmpty_thenCreatesURLEntity() throws Exception
	{
		JSONObject body = new JSONObject();
		body.put("url", "http://bemobi.com");
		body.put("custom_alias", "bemobi");

		mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();
		
		URLEntity urlEntity = urlEntityRepository.findByOriginalURLOrAlias("http://bemobi.com", null);
		assertEquals("http://bemobi.com", urlEntity.getOriginalURL());
		assertEquals("bemobi", urlEntity.getAlias());
	}
	
	@Test
	public void testShortenURL_whenMultipleShortenRequests_thenShouldCreateOnlyOneURLEntity() throws Exception
	{
		JSONObject body = new JSONObject();
		body.put("url", "http://bemobi.com");
		body.put("custom_alias", "bemobi");

		mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();

		mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();
		
		List<URLEntity> urlEntities = urlEntityRepository.findAll();
		
		assertEquals(1, urlEntities.size());
		assertEquals("http://bemobi.com", urlEntities.get(0).getOriginalURL());
		assertEquals("bemobi", urlEntities.get(0).getAlias());
	}
	
	@Test
	public void testShortenURL_whenMultipleShortenRequests_thenShouldIncrementTimesRequested() throws Exception
	{
		JSONObject body = new JSONObject();
		body.put("url", "http://bemobi.com");

		mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();

		mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();
		
		List<URLEntity> urlEntities = urlEntityRepository.findAll();
		
		assertEquals(Long.valueOf(2), urlEntities.get(0).getTimesRequested());
	}
	
	@Test
	public void testShortenURL_whenCustomAliasAlreadyExistsForDifferentURL_thenShouldReturnError() throws Exception
	{
		JSONObject body = new JSONObject();
		body.put("url", "http://bemobi1.com");
		body.put("custom_alias", "bemobi");

		mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();

		body.put("url", "http://bemobi2.com");
		body.put("custom_alias", "bemobi");
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();
		
		JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertEquals("001", json.get("ERROR_CODE"));
		assertEquals("Custom alias already in use for a different URL, please use a different one.", json.get("DESCRIPTION"));
	}

	@Test
	public void testShortenURL_whenCustomAliasAlreadyExistsForURLAndNoNewCustomAliasRequested_thenShouldReturnCustomAlias() throws Exception
	{
		JSONObject body = new JSONObject();
		body.put("url", "http://bemobi.com");
		body.put("custom_alias", "bemobi");

		mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();

		body.put("url", "http://bemobi.com");
		body.put("custom_alias", null);
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
							.post("/create")
							.contentType(MediaType.APPLICATION_JSON_VALUE)
							.content(body.toString()))
							.andReturn();
		
		JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertEquals("bemobi", json.get("ALIAS"));
	}
	
	@Test
	public void testShortenURL_whenURLRequestedWithoutAliasTwice_thenShouldIncrementTimesRequested() throws Exception
	{
		JSONObject body = new JSONObject();
		body.put("url", "http://bemobi.com");

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();

		JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		json = new JSONObject(json.get("STATISTICS").toString());
		assertEquals(1, json.get("TIMES_REQUESTED"));
		
		result = mockMvc.perform(MockMvcRequestBuilders
				.post("/create")
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(body.toString()))
				.andReturn();
		
		json = new JSONObject(result.getResponse().getContentAsString());
		json = new JSONObject(json.get("STATISTICS").toString());
		assertEquals(2, json.get("TIMES_REQUESTED"));
	}

}
