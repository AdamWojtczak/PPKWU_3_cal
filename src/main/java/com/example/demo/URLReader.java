package com.example.demo;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.UidGenerator;
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
import java.util.ArrayList;
import java.util.List;

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
		Elements newsHeadlines = doc.select("td");

		List<ICalEvent> iCalEventList = new ArrayList<ICalEvent>();

		StringBuilder str = new StringBuilder();
		for (Element headline : newsHeadlines) {
			if(headline.attr("class").equals("active")) {
				String date = headline.text();
				Elements title = headline.getElementsByTag("p");
				String href = headline.attr("href");
				if(date.charAt(0) > '0' && date.charAt(0) < '9' && date.charAt(1) > '0' && date.charAt(1) < '9') {
					date = "0" + date;
				}
				String titleString = "";
				if(date.length() > 2 ) {
					titleString = date.substring(2);
					date = date.substring(0,2);
				}

				if( !href.contains("javascript:void();")) {
					str.append(date + ": " + title + " " + href + "\n");
				}
				if(!titleString.isEmpty()) {
					titleString = title.text();
				}
				iCalEventList.add(new ICalEvent(new Date(year.toString() + month.toString() + date.toString()), titleString, href));
			}
		}



		File fin = new File("mycalendar.ics");



		Calendar calendar = new Calendar();

		calendar.getProperties().add(new ProdId("-//AdamWojtczak//iCal4j 1.0/PL"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);

		UidGenerator ug = () -> new Uid("adres.synchronizacji@example.com");

		for(ICalEvent iCalEvent : iCalEventList) {
			VEvent vEvent = new VEvent(iCalEvent.day,iCalEvent.title);
			vEvent.getProperties().add(ug.generateUid());
			Url url = new Url();
			url.setValue(iCalEvent.eventURL);
			vEvent.getProperties().add(url);
			calendar.getComponents().add(vEvent);
		}

		try {
			InputStream inputStream = new FileInputStream(fileWriter);
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

	static class ICalEvent {
		Date day;
		String title;
		String eventURL;

		public ICalEvent(Date day, String title, String eventURL) {
			this.day = day;
			this.title = title;
			this.eventURL = eventURL;
		}
	}

}