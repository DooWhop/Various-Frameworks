package com.allinpal.twodfireservice;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.allinpal.twodfireservice.idenverify.domain.BankCard;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;  
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;  
import static org.hamcrest.Matchers.*;

//这是JUnit的注解，通过这个注解让SpringRunner(SpringJUnit4ClassRunner)这个类提供Spring测试上下文。  
@RunWith(SpringRunner.class)  
//这是Spring Boot注解，为了进行集成测试，需要通过这个注解加载和配置Spring应用上下文  
@SpringBootTest(classes = TwodfireServiceApplication.class)  
@WebAppConfiguration 
public class TwodfireServiceApplicationTests {

	@Autowired  
    public WebApplicationContext context;  
      
    public MockMvc mockMvc; 
    
    @Before   
    public void setupMockMvc() throws Exception {   
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();   
    }  
    
    /*** 
     * 测试添加用户接口 
     * @throws Exception 
     */  
    /*@Test  
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
       // .andExpect(content().contentType(MediaType.ALL))  
        //使用jsonPath解析返回值，判断具体的内容  
        .andExpect(jsonPath("$.isSuccess", isA(String.class)))  
        //.andExpect(jsonPath("$.response", notNullValue()))  
       // .andExpect(jsonPath("$.response.code", is(Matchers.anyString())))
        ;  
    } */

}
