/*
 * =============================================================================
 * Project:      =        project-tcsiwula-master
 * Package:      =        PACKAGE_NAME
 * Created:      =        12/11/16
 * Author:       =        Tim Siwula <tcsiwula@usfca.edu>
 * University:   =        University of San Francisco
 * Class:        =        Computer Science 345: Programming Languages
 * Liscense:     =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */
package com.timsiwula.searchengineapp;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public interface CrawlerInterface
{
	 void startCrawl(String seed);

	/**
	 * Sets the base url path that other urls use when crawling.
	 *
	 * @param seed The seed url used to extracting the base url.
	 */
	static String getBaseURL(String seed)
	{
		String baseFromSeed = "";
		try
		{
//			URL url = new URL(seed);
			URL url = new URI(seed).toURL();
			baseFromSeed = url.getProtocol() + "://" + url.getHost() + url.getPath();
		} catch (MalformedURLException e)
		{
			System.err.println("Error 1 getBaseURL(): Unable to get the base URL from the url: "+seed);
		} catch (URISyntaxException e) {
			System.err.println("Error 2 getBaseURL(): Unable to get the base URL from the url: "+seed);
			throw new RuntimeException(e);
        }
        return baseFromSeed;
	}
}
