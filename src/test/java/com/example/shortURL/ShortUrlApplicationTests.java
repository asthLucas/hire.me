package com.example.shortURL;

import static org.junit.Assert.assertEquals;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

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
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class ShortUrlApplicationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private URLEntityRepository urlEntityRepository;
	
	@Test
	public void testShortenURL_whenEmptyRequestParameter_thenDefaultJSONisReceived() throws Exception {
			MvcResult result =  mockMvc.perform(MockMvcRequestBuilders
					.get("/create?URL="))
					.andReturn();
			
			JSONObject json = new JSONObject(result.getResponse().getContentAsString());
			assertEquals(json.get("ERROR_CODE"), "000");
			assertEquals(json.get("DESCRIPTION"), "No input was specified, please inform the URL you wish to shorten.");
	}
	
	@Test
	public void testShortenURL_whenDataStoreEmpty_thenCreatesURLEntity() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://bemobi.com&CUSTOM_ALIAS=bemobi"))
				.andReturn();
		
		URLEntity urlEntity = urlEntityRepository.findByOriginalURL("http://bemobi.com");
		assertEquals("http://bemobi.com", urlEntity.getOriginalURL());
		assertEquals("bemobi", urlEntity.getAlias());
	}
	
	@Test
	public void testShortenURL_whenMultipleShortenRequests_thenShouldCreateOnlyOneURLEntity() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://bemobi.com&CUSTOM_ALIAS=bemobi"))
				.andReturn();

		mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://bemobi.com&CUSTOM_ALIAS=bemobi"))
				.andReturn();

		List<URLEntity> urlEntities = urlEntityRepository.findAll();
		
		assertEquals(1, urlEntities.size());
		assertEquals("http://bemobi.com", urlEntities.get(0).getOriginalURL());
		assertEquals("bemobi", urlEntities.get(0).getAlias());
	}
	
	@Test
	public void testShortenURL_whenMultipleShortenRequests_thenShouldIncrementTimesRequested() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://bemobi.com&CUSTOM_ALIAS=bemobi"))
				.andReturn();
		
		mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://bemobi.com&CUSTOM_ALIAS=bemobi"))
				.andReturn();
		
		List<URLEntity> urlEntities = urlEntityRepository.findAll();
		
		assertEquals(Long.valueOf(2), urlEntities.get(0).getTimesRequested());
	}
	
	@Test
	public void testShortenURL_whenCustomAliasAlreadyExistsForDifferentURL_thenShouldReturnError() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://bemobi1.com&CUSTOM_ALIAS=bemobi"))
				.andReturn();
		
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://bemobi2.com&CUSTOM_ALIAS=bemobi"))
				.andReturn();
		
		JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertEquals("001", json.get("ERROR_CODE"));
		assertEquals("Custom alias already in use for a different URL, please use a different one.", json.get("DESCRIPTION"));
	}
}