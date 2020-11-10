package com.example.demo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.*;
import java.io.*;

@RestController
public class URLReader {

	@RequestMapping(method = RequestMethod.GET)
	public static String printURL( @RequestParam("url") String url) throws Exception {

		URL oracle = new URL(url);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(oracle.openStream()));

		String inputLine;
		StringBuilder stringBuilder = new StringBuilder();
		while ((inputLine = in.readLine()) != null)
			stringBuilder.append(inputLine);
		in.close();

		return stringBuilder.toString();
	}
}