/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        Driver.java
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */
package com.timsiwula.searchengineapp;

public class Driver
{
	/**
	 * Flag used to indicate the following value is the path to use when outputting the inverted index to a JSON file.
	 */
	private static final String INDEX_FLAG = "-index";

	/**
	 * Flag used to indicate the following value is a text file of search queries.
	 */
	private static final String DIRECTORY_FLAG = "-dir";

	/**
	 * Flag used to indicate the following value is a text file of search queries.
	 */
	private static final String QUERIES_FLAG = "-query";

	/**
	 * Flag used to indicate where to write the search results.
	 */
	private static final String RESULTS_FLAG = "-results";

	/**
	 * Flag used to indicate an exact search.
	 */
	private static final String EXACT_FLAG = "-exact";

	/**
	 * Flag used to indicate an crawler should search.
	 */
	private static final String CRAWL_FLAG = "-url";

	/**
	 * Flag used to indicate an crawler should search.
	 */
	private static final String THREAD_FLAG = "-multi";

	/**
	 * Flag used to indicate a port to use for the server.
	 */
	private static final String PORT_FLAG = "-port";

	/**
	 * Default to use when the value for the {@link #THREAD_FLAG} is missing.
	 */
	private static final int THREAD_DEFAULT = 5;

	/**
	 * Default to use when the value for the {@link #PORT_FLAG} is missing.
	 */
	private static final int PORT_DEFAULT = 8080;

	/**
	 * Default to use when the value for the {@link #INDEX_FLAG} is missing.
	 */
	private static final String INDEX_DEFAULT = "index.json";

	/**
	 * Default to use when the value for the {@link #RESULTS_FLAG} is missing.
	 */
	private static final String RESULTS_DEFAULT = "results.json";

	/**
	 * Parses the provided arguments and, if appropriate, will build an inverted index from a directory or seed URL,
	 * process search queries, configure multithreading, and launch a web server.
	 *
	 * @param args set of flag and value pairs
	 */
	public static void main(String[] args)
	{
		System.out.println("Driver.java.main()");
		// declare interface classes
		CLIParser parser = new CLIParser(args);
		ThreadedWorkQueue workers = null;
		InvertedIndex index;
		SearchResultBuilder searchBuilder;
		SearchResultBuilderInterface searcher;
		CrawlerInterface crawler;
		IndexBuilderInterface builder;
		ViewWebServer webServer;

		if(parser.hasFlag(Driver.THREAD_FLAG))    // if it is multi-threaded
		{
			// get number of threads and confirm
			int numThreads = parser.getValueInt(Driver.THREAD_FLAG, Driver.THREAD_DEFAULT);
			numThreads = numThreads < 1 ? Driver.THREAD_DEFAULT : numThreads;

			// define threaded classes
			IndexThreaded concurrentIndex = new IndexThreaded();

			// assign interface classes to threaded classes
			index = concurrentIndex;
			workers = new ThreadedWorkQueue(numThreads);
			searcher = new SearchResultBuilderThreaded(concurrentIndex, workers);
			crawler = new CrawlerThreaded(concurrentIndex, workers);
			builder = new IndexBuilderThreaded(concurrentIndex, workers);

		} else    // define single threaded classes
		{
			index = new InvertedIndex();
			searcher = new SearchResultBuilder(index);
			crawler = new Crawler(index);
			builder = new IndexBuilder(index);
		}

		if(parser.hasFlag(Driver.DIRECTORY_FLAG)) //Build index from files.
		{
			builder.buildIndex(parser.getValue(Driver.DIRECTORY_FLAG, Driver.RESULTS_DEFAULT));
		}

		if(parser.hasFlag(Driver.CRAWL_FLAG)) // Crawl here
		{
			crawler.startCrawl(parser.getValue(Driver.CRAWL_FLAG));
		}

		if(parser.hasFlag(Driver.INDEX_FLAG)) // Output results to file.
		{
			index.toJSON(parser.getValue(Driver.INDEX_FLAG, Driver.INDEX_DEFAULT));
		}

		if(parser.hasFlag(Driver.EXACT_FLAG)) // Perform Exact Search here.
		{
			searcher.parseQueryFile(parser.getValue(Driver.EXACT_FLAG, Driver.RESULTS_DEFAULT), true);
		}

		if(parser.hasFlag(QUERIES_FLAG)) // Perform Partial Search here
		{
			searcher.parseQueryFile(parser.getValue(Driver.QUERIES_FLAG, Driver.RESULTS_DEFAULT), false);
		}

		if(parser.hasFlag(RESULTS_FLAG)) // Output Results here
		{
			searcher.toJSON(parser.getValue(Driver.RESULTS_FLAG, Driver.RESULTS_DEFAULT));
		}

		if(parser.hasFlag(PORT_FLAG)) // webserver
		{
			// create server and start it
			webServer = new ViewWebServer(index, (SearchResultBuilder) searcher, crawler, parser);
			webServer.setPortNumber(parser.getValueInt(Driver.PORT_FLAG, Driver.PORT_DEFAULT), Driver.PORT_DEFAULT);
			webServer.startServer();
		}
		
		if(workers != null)    // if multithreaded
		{
			workers.shutdown();        // return resources back to the system.
		}
		
	}
}