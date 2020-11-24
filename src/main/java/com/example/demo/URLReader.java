package com.example.demo;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
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
import java.io.*;
import java.net.URL;
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
				if(	Character.isDigit(date.charAt(0)) && !Character.isDigit(date.charAt(1))) {
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
				iCalEventList.add(new ICalEvent(date, year, month, titleString, href));
			}
		}


		Calendar calendar = new Calendar();

		calendar.getProperties().add(new ProdId("-//AdamWojtczak//iCal4j 1.0/PL"));
		calendar.getProperties().add(Version.VERSION_2_0);
		calendar.getProperties().add(CalScale.GREGORIAN);

		UidGenerator ug = () -> new Uid("adres.synchronizacji@example.com");

		for(ICalEvent iCalEvent : iCalEventList) {
			VEvent vEvent = new VEvent(new Date(iCalEvent.year+iCalEvent.month+iCalEvent.day),iCalEvent.title);
			vEvent.getProperties().add(ug.generateUid());
			Url url = new Url();
			url.setValue(iCalEvent.eventURL);
			vEvent.getProperties().add(url);
			calendar.getComponents().add(vEvent);
		}

		FileOutputStream fout = new FileOutputStream("mycalendar.ics");

		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(calendar, fout);

		try {
			InputStream inputStream = new FileInputStream(new File("mycalendar.ics"));
			response.setContentType("text/calendar;charset=utf-8");
			IOUtils.copy(inputStream, response.getOutputStream());
			response.flushBuffer();
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		return "";
	}

	static class ICalEvent {
		Date date;
		String day;
		String year;
		String month;
		String title;
		String eventURL;

		public ICalEvent(Date date, String title, String eventURL) {
			this.date = date;
			this.title = title;
			this.eventURL = eventURL;
		}

		public ICalEvent( String day, String year, String month, String title, String eventURL) {
			this.day = day;
			this.year = year;
			this.month = month;
			this.title = title;
			this.eventURL = eventURL;
		}

	}

}