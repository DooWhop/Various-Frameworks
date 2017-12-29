package com.allinpal.twodfireservice;

import static org.hamcrest.Matchers.isA;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.allinpal.twodfireservice.demo.domain.City;
import com.allinpal.twodfireservice.demo.mapper.DemoMapper;
import com.allinpal.twodfireservice.idenverify.domain.BankCard;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;  

public class Test1 extends TwodfireServiceApplicationTests {
	
	
	@MockBean
	public DemoMapper demoMapper;
	
	@Test
	public void testMapper() throws Exception {
		City city = new City();
		city.setCountry("China");
		city.setId(100001L);
		city.setName("Nanjing");
		city.setState("Jiangsu");
		given(this.demoMapper.findByState("CA")).willReturn(city);
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.post("/demo/service").contentType(MediaType.ALL).content(""))
				.andReturn();
		MockHttpServletResponse  response = mvcResult.getResponse();
		assertEquals(200, response.getStatus());
		assertEquals(city.toString(),response.getContentAsString());
	}
	
	
	/**controller集成测试
	 * @throws Exception
	 */
	@Test
    public void testController() throws Exception { 
    	
        BankCard bankCard = new BankCard();
        bankCard.setHpNo("18914728385");
        bankCard.setReqType("11");
        bankCard.setVerifyCode("1234");
        bankCard.setVerifyCodeId("1234567");
        ObjectMapper mapper = new ObjectMapper();  
        String uri = "/idenVerifyService/checkVerifyCode";       
		//调用接口，传入添加参数  
        mockMvc.perform(MockMvcRequestBuilders.post(uri)
        		.contentType(MediaType.APPLICATION_JSON_UTF8)  
                .content(mapper.writeValueAsString(bankCard)) )  
        //判断返回值，是否达到预期，测试示例中的返回值的结构如下{"isSuccess":true/false,"errorMessage":"*","response":object}  
        .andExpect(status().isOk())   
      //  .andExpect(content().contentType(MediaType.ALL))  
        //使用jsonPath解析返回值，判断具体的内容  
        .andExpect(jsonPath("$.isSuccess", isA(String.class)))  
      //  .andExpect(jsonPath("$.response", notNullValue()))  
       // .andExpect(jsonPath("$.response.code", is(Matchers.anyString())))
        ;  
    } 

}
