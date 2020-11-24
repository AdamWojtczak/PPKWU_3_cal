package com.example.demo;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
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
	public static String getMonthEvents(@RequestParam("year") String year, @RequestParam("month") String month) throws Exception {

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

		Document doc = Jsoup.connect(urlBuilder.toString()).get();
		Elements newsHeadlines = doc.select(".active");

		StringBuilder str = new StringBuilder();
		for (Element headline : newsHeadlines) {
			if(headline.attr("class").equals("active")) {
				String date = headline.text();
				Elements title = headline.getElementsByTag("p");
				String href = headline.attr("href");
				if( !href.contains("javascript:void();")) {
					str.append(date + ": " + title.text() + " " + href + "\n");
				}
			}
		}

		System.out.println("aaaaaaaaaaaaa");
		return str.toString();
	}

	@RequestMapping(method = RequestMethod.GET, path = "/cal")
	public static String getCal(@RequestParam("year") String year, @RequestParam("month") String month, HttpServletResponse response) throws Exception {

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

		Document doc = Jsoup.connect(urlBuilder.toString()).get();
		Elements newsHeadlines = doc.select(".active");

		StringBuilder str = new StringBuilder();
		for (Element headline : newsHeadlines) {
			if(headline.attr("class").equals("active")) {
				String date = headline.text();
				Elements title = headline.getElementsByTag("p");
				String href = headline.attr("href");
				if( !href.contains("javascript:void();")) {
					str.append(date + ": " + title.text() + " " + href + "\n");
				}
			}
		}



		FileInputStream fin = new FileInputStream("mycalendar.ics");

		CalendarBuilder builder = new CalendarBuilder();

		Calendar calendar = builder.build(fin);

		calendar.getProperties().add(new ProdId("-//AdamWojtczak//iCal4j 1.0/PL"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);



		try {
			InputStream inputStream = fin;
			response.setContentType("text/calendar;charset=utf-8");
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		System.out.println("aaaaaaaaaaaaa");
		return str.toString();
	}

	class ICalEvent {
		int day;
		String title;
		String eventURL;
	}

}