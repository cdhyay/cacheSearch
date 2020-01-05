package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	/**
	 * 项目首页
	 * @return
	 */
	@RequestMapping("/")
	public String index() {
		System.out.println("index");
		return "index";
	}
}
