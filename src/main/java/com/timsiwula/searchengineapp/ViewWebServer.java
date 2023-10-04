/*
 =============================================================================
 Created:      =        12/13/16
 Project:      =        project-tcsiwula-master
 Package:      =        PACKAGE_NAME
 ==============================================================================
 Liscense:     =        GPLv2
 Version:      =        0.00x 
 ==============================================================================
 Production:   =        http://abc.com
 Source:       =        https://github.com/repo
 ==============================================================================
 Description   =        Awesome tool for server.
 ==============================================================================
 Author:       =        Tim Siwula <@tcsiwula> <tcsiwula@gmail.com>
 Class:        =        Computer Science xxx: Name
 School:       =        University of San Francisco
 ==============================================================================
 */
package com.timsiwula.searchengineapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.springframework.javapoet.ClassName;

import java.io.IOException;

//@SpringBootApplication
public class ViewWebServer
{
	public static InvertedIndex index;
	private final SearchResultBuilder searcher;
	private final CrawlerInterface crawler;
	private final CLIParser parser;
	private int portNumber;

	public ViewWebServer(InvertedIndex index, SearchResultBuilder searcher, CrawlerInterface crawler, CLIParser parser)
	{
		ViewWebServer.index = index;
		this.searcher = searcher;
		this.crawler = crawler;
		this.parser = parser;
	}
	
	public static void main(String[] args) throws Exception
	{
		// for testing without running from the driver
		CLIParser parser = new CLIParser(args);
		InvertedIndex index = new InvertedIndex();
		SearchResultBuilder searcher = new SearchResultBuilder(index);
		CrawlerInterface crawler = new Crawler(index);
		ViewWebServer webServer = new ViewWebServer(index, searcher, crawler, parser);
		webServer.setPortNumber(8080, 8080);
		webServer.startServer();
//		SpringApplication.run(ViewWebServer.class, args);
//		System.out.println("ViewWebServer.java.main()");
//		System.out.println("\n\napi server running at:  http://localhost:8080   \n\n");
	}
	
	public void setPortNumber(int proposedPortNumber, int defaultPortNumber)
	{
		this.portNumber = (proposedPortNumber < 1 || proposedPortNumber > 65535) ? defaultPortNumber : proposedPortNumber;
	}
	
	public void startServer()
	{
		// seed the database for testing
		// crawler.startCrawl("http://cs.usfca.edu/~cs212/wdghtml40/alist.html");
		// index.toJSON("index.json");

		// add static resource holders to web server
		// this indicates where web files are accessible on the file system
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase("/");
		resourceHandler.setDirectoriesListed(true);

		// only serve static resources in the "/images" context directory
		// this indicates where web files are accessible via the web server
		ContextHandler resourceContext = new ContextHandler("/");
		resourceContext.setHandler(resourceHandler);

		// type of handler that supports sessions
		ServletContextHandler servletContext = null;

		// turn on sessions and set context
		servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
		servletContext.setContextPath("/");
		String className = ClassName.class.getSimpleName();
	    servletContext.addServlet(className, "/");

		// default handler for favicon.ico requests
		DefaultHandler defaultHandler = new DefaultHandler();
		defaultHandler.setServeIcon(true);

		ContextHandler defaultContext = new ContextHandler("logo.svg");
		defaultContext.setHandler(defaultHandler);

		// setup handler order
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[]{defaultContext, servletContext});

		openWebBrowser();

		// setup jetty server
		Server server = new Server(portNumber);
		server.setHandler(handlers);
		try
		{
			server.start();
			server.join();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	public void openWebBrowser()
	{
		System.out.println("WebServer listening on: http://localhost:8080/");

		// auto launch chrome upon server start
		String openChromeCommand = "/usr/bin/open -a \"/Applications/Google Chrome.app\" 'http://localhost:8080/'";
		String[] args = new String[]{"/bin/bash", "-c", openChromeCommand};
		try
		{
			Process proc = new ProcessBuilder(args).start();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

	}
}