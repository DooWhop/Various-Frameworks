package com.allinpal.twodfireservice.demo.controller;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
//import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.allinpal.twodfireservice.TwodfireServiceApplication;
import com.allinpal.twodfireservice.demo.domain.City;
import com.allinpal.twodfireservice.demo.mapper.DemoMapper;

import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TwodfireServiceApplication.class)
@WebAppConfiguration
public class DemoControllerTest {

	@Autowired
	private WebApplicationContext context;

	@MockBean
	public DemoMapper demoMapper;
	
	@MockBean
	private RestTemplate restTemplate;
	
	private MockMvc mvc;

	@Test
	public void testMapper() throws Exception {
		City city = new City();
		city.setCountry("China");
		city.setId(100001L);
		city.setName("Nanjing");
		city.setState("Jiangsu");
		given(this.demoMapper.findByState("CA")).willReturn(city);
		String creditCotractUrl = "http://localhost:8082/contract/createContract";
		JSONObject contractMap = new JSONObject();
		contractMap.put("useRecordNo", "test");
		given(this.restTemplate.postForObject(
				creditCotractUrl, contractMap, String.class)).willReturn("11111");
		MvcResult mvcResult = mvc
				.perform(MockMvcRequestBuilders.post("/demo/service").contentType(MediaType.ALL).content(""))
				.andReturn();
		MockHttpServletResponse  response = mvcResult.getResponse();
		assertEquals(200, response.getStatus());
		assertEquals(city.toString(),response.getContentAsString());
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		// mvc = MockMvcBuilders.standaloneSetup(new DemoController()).build();
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testHome() throws Exception {
		String url = "/demo/";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.ALL)).andReturn();
		assertEquals(200, mvcResult.getResponse().getStatus());
	}
	
	@Test
	public void testService() {
		// fail("Not yet implemented");
	}

	@Test
	public void testAddCity() {
		// fail("Not yet implemented");
	}

	@Test
	public void testFoo() {
		// fail("Not yet implemented");
	}

	@Test
	public void testOpen() {
		// fail("Not yet implemented");
	}

	@Test
	public void testUpload() {
		// fail("Not yet implemented");
	}

}
