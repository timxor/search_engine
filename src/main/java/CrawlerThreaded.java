/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        CrawlerThreaded.java
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

public class CrawlerThreaded implements CrawlerInterface
{
	/**
	 * Stores the processed url list.
	 */
	private final HashSet<String> seenUrlList;

	/**
	 * A sweet logger for logging.
	 */
	private static final Logger logger = LogManager.getLogger(CrawlerThreaded.class);

	/**
	 * A diligent work queue that loves to work.
	 */
	private final ThreadedWorkQueue worker;

	/**
	 * Stores the index.
	 */
	private final IndexThreaded globalIndex;

	/**
	 * A superb IndexThreaded constructor.
	 *
	 * @param globalIndex A thread safe index.
	 * @param worker      A work queue for delegating work to threads.
	 */
	public CrawlerThreaded(InvertedIndex globalIndex, ThreadedWorkQueue worker)
	{
		this.globalIndex = (IndexThreaded) globalIndex;
		this.worker = worker;
		seenUrlList = new HashSet<>();
		logger.debug("CrawlerThreaded() instantiated.");
	}

	/**
	 * Starts the crawling from the seed url.
	 *
	 * @param seed The seed url to start the crawl with.
	 */
	public void startCrawl(String seed)
	{
		String baseURL = getBaseURL(seed);
		seenUrlList.add(seed);
		worker.execute(new WebCrawlerMinion(seed, baseURL));

		// tell threads to wait until all threads are done
		worker.finish();
	}

	/**
	 * Handles per-directory parsing. If a subdirectory is encountered, a new
	 * {@link IndexBuilderThreaded.TextFileMinion} is created to handle that subdirectory.
	 */
	public class WebCrawlerMinion implements Runnable
	{
		private final String baseURL, currentUrl;
		private final InvertedIndex localIndex;


		public WebCrawlerMinion(String currentUrl, String baseURL)
		{
			this.baseURL = baseURL;
			this.currentUrl = currentUrl;
			localIndex = new InvertedIndex();
		}

		@Override public void run()
		{
			crawl(currentUrl, baseURL, localIndex);
			globalIndex.allGather(localIndex);
		}
	}

	/**
	 * Crawl and extract all urls from a given url.
	 *
	 * @param currentUrl The url to crawl and get all of the urls from its page.
	 */
	private void crawl(String currentUrl, String baseURL, InvertedIndex localIndex)
	{
		// get the html
		String html = ParserHtml.fetchHTML(currentUrl, baseURL);

		// get the url links from this html page.
		ArrayList<String> urlList = ParserHtml.URLParser.getAllLinks(html);

		// clean and normalize all urls, return them to be added to the queue.
		urlList = ParserHtml.removeFragmentUrls(urlList, baseURL);

		synchronized (seenUrlList)
		{
			// ADD THE URLS TO THE LIST
			for (String s : urlList)
			{
				if(!seenUrlList.contains(s) && seenUrlList.size() < 50 && s != null)
				{
					seenUrlList.add(s);
					baseURL = getBaseURL(s);
					worker.execute(new WebCrawlerMinion(s, baseURL));
				}
			}
		}

		// ADD THE WORDS TO THE INDEX
		String text = ParserHtml.cleanHTML(html);
		Integer wordPosition = 1;

		for (String word : ParserHtml.parseWords(text))
		{
			localIndex.add(word, currentUrl, wordPosition);
			wordPosition++;
		}
	}

	/**
	 * Sets the base url path that other urls use when crawling.
	 *
	 * @param seed The seed url used to extracting the base url.
	 */
	private String getBaseURL(String seed)
	{
		try {
			URI uri = new URI(seed);
			return uri.getScheme() + "://" + uri.getHost() + uri.getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return "";
		//		String baseFromSeed = "";
		//		try
		//		{
		//			URL url = new URL(seed);
		//			baseFromSeed = url.getProtocol() + "://" + url.getHost() + url.getPath();
		//		} catch (MalformedURLException e)
		//		{
		//			e.printStackTrace();
		//		}
		//		return baseFromSeed;
	}
}
