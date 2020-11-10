package com.example.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class EventsGetter {
	//odpal URLREADER
	//wyszukaj eventów
	//dodawaj eventy do Mapy
	public static void getMonthEvents() throws IOException {

		Document doc = Jsoup.connect("http://localhost:8080/?year=2020&month=03").get();
		Elements newsHeadlines = doc.select("a[href:not(\"javascript:void();\")]");
		for (Element headline : newsHeadlines) {
			System.out.println(headline);
		}

	}
}
