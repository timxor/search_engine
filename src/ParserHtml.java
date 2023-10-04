/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        HTMLParser.java
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserHtml
{
	/**
	 * Valid HTTP method types.
	 */
	public enum HTTP
	{
		GET
	}

	/**
	 * Regular expression for removing special characters.
	 */
	private static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

	/**
	 * Regular expression for splitting text into words by whitespace.
	 */
	private static final String SPLIT_REGEX = "(?U)\\p{Space}+";

	/**
	 * Version of HTTP used and supported.
	 */
	private static final String version = "HTTP/1.1";

	/**
	 * Port used by socket. For web servers, should be port 80.
	 */
	private static final int DEFAULT_PORT = 80;

	/**
	 * Parses the provided plain text (already cleaned of HTML tags) into
	 * individual words.
	 *
	 * @param text plain text without html tags
	 * @return list of parsed words
	 */
	public static String[] parseWords(String text)
	{
		text = text.replaceAll(CLEAN_REGEX, "").toLowerCase().trim();
		return text.split(SPLIT_REGEX);
	}

	/**
	 * Removes all style and script tags (and any text in between those tags),
	 * all HTML tags, and all HTML entities.
	 *
	 * @param html html code to parse
	 * @return plain text
	 */
	public static String cleanHTML(String html)
	{
		String text = html;
		text = stripElement("script", text);
		text = stripElement("style", text);
		text = stripTags(text);
		text = stripEntities(text);
		return text;
	}

	/**
	 * Fetches the webpage at the provided URL by opening a socket, sending an
	 * HTTP request, removing the headers, and returning the resulting HTML
	 * code.
	 *
	 * @param currentUrl webpage to download
	 * @param baseString absolute URL for currentUrl
	 * @return html code
	 */
	public static String fetchHTML(String currentUrl, String baseString)
	{
		URL target = null;
		URL base;
		try
		{
			base = new URL(baseString);
			target = new URL(base, currentUrl);
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		URI baseURI = null;

		try
		{
			assert target != null;
			baseURI = new URI(target.getProtocol(), target.getAuthority(), target.getPath(), target.getQuery(), null);
		} catch (URISyntaxException e)
		{
			e.printStackTrace();
		}

		String request = null;
		try
		{
			assert baseURI != null;
			request = craftHTTPRequest(baseURI.toURL());
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		}

		List<String> lines = null;
		try
		{
			lines = fetchLines(baseURI.toURL(), request);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		int start = 0;
		assert lines != null;
		int end = lines.size();

		// Determines start of HTML versus headers.
		while (!lines.get(start).trim().isEmpty() && start < end)
		{
			start++;
		}

		// Double-check this is an HTML file.
		Map<String, String> fields = parseHeaders(lines.subList(0, start + 1));
		String type = fields.get("Content-Type");

		if(type != null && type.toLowerCase().contains("html"))
		{
			return String.join(System.lineSeparator(), lines.subList(start + 1, end));
		}

		return null;
	}

	/**
	 * Removes everything between the element tags, and the element tags
	 * themselves.
	 *
	 * @param name name of the element to strip, like "style" or "script"
	 * @param html html code to parse
	 * @return html code without the element specified
	 */
	private static String stripElement(String name, String html)
	{
		html = html.replaceAll("(?msi)<" + name + "+.*?<\\/" + name + "\\s*>", "");
		return html;
	}

	/**
	 * Removes all HTML tags, which is essentially anything between the "<" and
	 * ">" symbols. The tag will be replaced by the empty string.
	 *
	 * @param html html code to parse
	 * @return text without any html tags
	 */
	private static String stripTags(String html)
	{
		html = html.replaceAll("\\<[^>]*>", "");
		return html;
	}

	/**
	 * Replaces all HTML entities in the text with an empty string. For example,
	 * "2010&ndash;2012" will become "20102012".
	 *
	 * @param html the text with html code being checked
	 * @return text with HTML entities replaced by an empty string
	 */
	private static String stripEntities(String html)
	{
		if(html.contains("& "))
		{
			return html;
		}
		html = html.replaceAll("(?msi)([&]+?.+?;)", "");
		return html;
	}

	/**
	 * Crafts a minimal HTTP/1.1 request for the provided method.
	 *
	 * @param url - url to fetch
	 * @return HTTP/1.1 request
	 */
	private static String craftHTTPRequest(URL url)
	{
		String host = url.getHost();
		String resource = url.getFile().isEmpty() ? "/" : url.getFile();

		// The specification is specific about where to use a new line
		// versus a carriage return!
		return String.format("%s %s %s\n" + "Host: %s\n" + "Connection: close\n" + "\r\n", HTTP.GET.name(), resource, version, host);
	}

	/**
	 * Will connect to the web server and fetch the URL using the HTTP request
	 * provided. It would be more efficient to operate on each line as returned
	 * instead of storing the entire result as a list.
	 *
	 * @param url     - url to fetch
	 * @param request - full HTTP request
	 * @return the lines read from the web server
	 * @throws IOException - throws if an exception is encountered.
	 */
	private static List<String> fetchLines(URL url, String request) throws IOException
	{
		ArrayList<String> lines = new ArrayList<>();
		int port = url.getPort() < 0 ? DEFAULT_PORT : url.getPort();

		try (Socket socket = new Socket(url.getHost(), port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(socket.getOutputStream()))
		{
			writer.println(request);
			writer.flush();

			String line;

			while ((line = reader.readLine()) != null)
			{
				lines.add(line);
			}
		}
		return lines;
	}

	/**
	 * Helper method that parses HTTP headers into a map where the key is the
	 * field name and the value is the field value. The status code will be
	 * stored under the key "Status".
	 *
	 * @param headers - HTTP/1.1 header lines
	 * @return field names mapped to values if the headers are properly
	 * formatted
	 */
	private static Map<String, String> parseHeaders(List<String> headers)
	{
		Map<String, String> fields = new HashMap<>();

		if(headers.size() > 0 && headers.get(0).startsWith(version))
		{
			fields.put("Status", headers.get(0).substring(version.length()).trim());

			for (String line : headers.subList(1, headers.size()))
			{
				String[] pair = line.split(":", 2);

				if(pair.length == 2)
				{
					fields.put(pair[0].trim(), pair[1].trim());
				}
			}
		}
		return fields;
	}

	/**
	 * Removes any fragment urls before they are added to the queue.
	 *
	 * @param dirtyUrls Urls that need to be normalized.
	 */
	public static ArrayList<String> removeFragmentUrls(ArrayList<String> dirtyUrls, String baseURL)
	{
		ArrayList<String> clean1 = new ArrayList<>();
		for (String url : dirtyUrls)
		{
			if(!url.contains("#"))
			{
				URL target;
				URL base;
				try
				{
					base = new URL(baseURL);
					target = new URL(base, url);
					clean1.add(target.toString());

				} catch (MalformedURLException e)
				{
					e.printStackTrace();
				}
			}
		}
		return clean1;
	}

	public static class URLParser
	{
		/**
		 * The regular expression used to parse the HTML for links.
		 */
		public static final String REGEX = "(?msi)(\\s*(<\\s*a[^>]*href\\s*=\\s*\\\"(([^\"])*)\\\"))";

		/**
		 * The group in the regular expression that captures the raw link.
		 */
		public static final int GROUP = 3;

		/**
		 * Parses the provided html for HTML links.
		 *
		 * @param html - valid HTML code, with quoted attributes and URL encoded links
		 * @return list of URLs found in HTML code
		 */
		public static ArrayList<String> getAllLinks(String html)
		{
			// list to store links
			ArrayList<String> links = new ArrayList<>();

			// compile string into regular expression
			Pattern p = Pattern.compile(REGEX);

			// match provided html against regular expression
			Matcher m = p.matcher(html);

			// loop through every match found in html
			while (m.find())
			{
				String url = m.group(GROUP);
				links.add(url);
			}
			return links;
		}
	}
}