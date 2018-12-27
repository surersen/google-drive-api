package com.drive.quickstart.demoapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Controller
public class DemoApiApplication {

	@RequestMapping("get/hello")
	@ResponseBody
	public Map<String,String> getHello(){
		Map<String,String> map = new HashMap<>();
		map.put("id","HelloWorld");
		return map;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApiApplication.class, args);
	}

}

