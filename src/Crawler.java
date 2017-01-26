/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        WebCrawler.java
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Crawler implements CrawlerInterface
{
	/**
	 * Stores the urls to process.
	 */
	private final Queue<String> queue;

	/**
	 * Stores the processed url list.
	 */
	private final HashSet<String> processedUrlList;

	/**
	 * Stores the index.
	 */
	private final InvertedIndex index;

	/**
	 * Constructor creates a new instance of index for each object.
	 */
	public Crawler(InvertedIndex index)
	{
		this.index = index;
		queue = new LinkedList<>();
		processedUrlList = new HashSet<>();
	}

	/**
	 * Starts the crawling from the seed url.
	 *
	 * @param seed The seed url to start the crawl with.
	 */
	public void startCrawl(String seed)
	{
		String baseURL = CrawlerInterface.getBaseURL(seed);
		queue.add(seed);

		while (processedUrlList.size() < 50 && !queue.isEmpty())
		{
			crawl(queue.poll(), baseURL);
		}
	}

	/**
	 * Crawl and extract all urls from a given url.
	 *
	 * @param currentUrl The url to crawl and get all of the urls from its page.
	 */
	private void crawl(String currentUrl, String baseURL)
	{
		processedUrlList.add(currentUrl);

		// get the html
		String html = ParserHtml.fetchHTML(currentUrl, baseURL);

		// get the url links from this html page.
		ArrayList<String> urlList = ParserHtml.URLParser.getAllLinks(html);

		// clean and normalize all urls, return them to be added to the queue.
		urlList = ParserHtml.removeFragmentUrls(urlList, baseURL);

		// ADD THE URLS TO THE LIST
		urlList.stream().filter(s -> !processedUrlList.contains(s)).forEachOrdered(queue::offer);

		// ADD THE WORDS TO THE INDEX
		String text = ParserHtml.cleanHTML(html);
		Integer wordPosition = 1;

		for (String word : ParserHtml.parseWords(text))
		{
			index.add(word, currentUrl, wordPosition);
			wordPosition++;
		}
	}
}