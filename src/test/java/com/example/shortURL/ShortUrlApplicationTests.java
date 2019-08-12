package com.example.shortURL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.json.JSONException;
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
		
		URLEntity urlEntity = urlEntityRepository.findByOriginalURLOrAlias("http://bemobi.com", null);
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

	@Test
	public void testShortenURL_whenCustomAliasAlreadyExistsForURL_thenShouldReturnError() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://bemobi.com"))
				.andReturn();
		
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://bemobi.com&CUSTOM_ALIAS=bemobi"))
				.andReturn();
		
		JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertEquals("002", json.get("ERROR_CODE"));
		assertEquals("This URL has been mapped already.", json.get("DESCRIPTION"));
	}
	
	@Test
	public void testRetrieveURL_whenUsingPreviouslyShortenedURL_thenShouldReturnOriginalURL() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://bemobi.com"))
				.andReturn();

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/find?URI=http://bemobi.com"))
				.andReturn();

		final JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertThrows(JSONException.class, () -> json.get("ERROR_CODE"));
		assertNotNull(json.get("ALIAS"));
	}

	@Test
	public void testRetrieveURL_whenUsingAlias_thenShouldReturnOriginalURL() throws Exception
	{
		mockMvc.perform(MockMvcRequestBuilders
				.get("/create?URL=http://bemobi.com&CUSTOM_ALIAS=bemobi"))
				.andReturn();

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/find?URI=bemobi"))
				.andReturn();

		final JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertThrows(JSONException.class, () -> json.get("ERROR_CODE"));
		assertNotNull(json.get("ALIAS"));
	}
	
	@Test
	public void testRetrieveURL_whenURLNotFound_thenShouldReturnError() throws Exception
	{
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders
				.get("/find?URI=http://bemobi.com"))
				.andReturn();

		final JSONObject json = new JSONObject(result.getResponse().getContentAsString());
		assertEquals("003", json.get("ERROR_CODE"));
		assertEquals("No URL found for the given identifier.", json.get("DESCRIPTION"));
	}

}