package com.shopperapp;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.shopperapp.dto.OrderItem;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopperAppApplicationTests {

	@Test
	public void contextLoads() {
	}
	public static void main(String[] args) {
		String data = "Sujith";
		String[] split = data.split("S");
		System.err.println(split.length);
		System.err.println(split[0]);
		System.err.println(split[1]);
		
	}
}
