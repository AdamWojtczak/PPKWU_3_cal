package com.example.demo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.*;
import java.io.*;

@RestController
public class URLReader {

	@RequestMapping(method = RequestMethod.GET)
	public static String printURL( @RequestParam("year") String year, @RequestParam("month") String month) throws Exception {

		if (month.length() == 1) {
			if(!month.startsWith("0")) {
				month = "0" + month;
			}
		}
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("http://www.weeia.p.lodz.pl/pliki_strony_kontroler/kalendarz.php?rok=");
		urlBuilder.append(year);
		urlBuilder.append("&miesiac=");
		urlBuilder.append(month);
		urlBuilder.append("&lang=01");
		URL oracle = new URL(urlBuilder.toString());
		BufferedReader in = new BufferedReader(
				new InputStreamReader(oracle.openStream()));

		String inputLine;
		StringBuilder stringBuilder = new StringBuilder();
		while ((inputLine = in.readLine()) != null)
			stringBuilder.append(inputLine);
		in.close();

		return stringBuilder.toString();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/events")
	public static String getMonthEvents() throws Exception {

		Document doc = Jsoup.connect("http://localhost:8080/?year=2020&month=10").get();
		Elements newsHeadlines = doc.select(".active");

		StringBuilder str = new StringBuilder();
		for (Element headline : newsHeadlines) {
			str.append(headline.toString());
		}
		System.out.println("aaaaaaaaaaaaa");
		return str.toString();
	}
}