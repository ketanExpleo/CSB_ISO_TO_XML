package com.fss.aeps.test;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RestController
public class TestService {

	@PostMapping(path = "/acquirer/post", produces = MediaType.ALL_VALUE)
	public String test(@RequestBody String request) {
		return "hello "+request;
	}

	@GetMapping(path = "/acquirer/get", produces = MediaType.ALL_VALUE)
	public String testGet(@RequestParam("name") String name ) {
		return "hello "+name;
	}


	public static void main(String[] args) {
		String string = "04{011111161iXtUSFipkirdoKBHHu+CVKQtYVQiihkspuzzQSF639fg8Y0eH7oT/vEq2O12Dmm,A,0028bf26dee57125fa969797652a8d4b48a36a6a978e8c31ecb772a5018695b5,0100003000000210,2.0,20220913112740,1,1,0,0,2.5,df5bffab9001baf50e83059d18f359203299fda12e0adc5f7362b638cc77e156,df1f6dab559b4f8b2f4e08d6bff36af887d3f20021efb5bc64a2b2fe0406d984,df1f6dab559b4f8b2f4e08d6bff36af887d3f20021efb5bc64a2b2fe0406d984,23,NA,NA,NA,NA,NA,NA,NA,NA,NA,registered,ACPL.WIN.001,1.0.4,STARTEK.ACPL,FM220U,L0,NA}";
		System.out.println(string.substring(string.indexOf("{")+1, string.indexOf(",")));
	}
}