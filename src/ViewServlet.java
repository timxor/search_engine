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

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ViewServlet extends HttpServlet
{
	/**
	 * Stores the index.
	 */
	private static final SearchResultBuilder searcher = new SearchResultBuilder(ViewWebServer.index);

	/**
	 * Stores the index.
	 */
	private static final Crawler crawler = new Crawler(ViewWebServer.index);

	@Override protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		String dc = req.getParameter("discover");
		long startTimeAccountant = System.nanoTime();
		PrintWriter writer = res.getWriter();
		String searchQuery = getSearchQuery(req);

		if(req.getParameter("query") != null)
		{
			System.out.println("you clicked search!");
			writeSearchPageHeader(req, res, writer, searchQuery);
			writeSearchPageBody(req, res, writer, searchQuery, startTimeAccountant, false);
		}

		if(req.getParameter("history") != null)
		{
			System.out.println("you clicked search history!");
			writeSearchPageHeader(req, res, writer, searchQuery);
			writeSearchPageBody(req, res, writer, searchQuery, startTimeAccountant, true);
		}


		if(req.getParameter("discover") != null)
		{

			System.out.println("you clicked discover! arg = " + req.getParameter("discover"));

			if(dc.isEmpty())
			{
				System.out.println("dc is empty");
				writeLandingPageHeader(req, res, writer, searchQuery);
				writeLandingPageBody(req, res, writer, searchQuery, startTimeAccountant, false, true);

			} else
			{
				crawler.startCrawl(req.getParameter("discover"));
				ViewWebServer.index.toJSON("index.json");

				writeLandingPageHeader(req, res, writer, searchQuery);
				writeLandingPageBody(req, res, writer, searchQuery, startTimeAccountant, false, false);
			}
		}

		if(req.getParameter("query") == null && req.getParameter("discover") == null)
		{
			System.out.println("you arrived at the landing page!");
			writeLandingPageHeader(req, res, writer, searchQuery);
			writeLandingPageBody(req, res, writer, searchQuery, startTimeAccountant, false, false);
		}

		writeFooter(req, res, writer);
	}

	public static final String VISIT_DATE = "Visited";
	public static final String VISIT_COUNT = "Count";

	protected void setUpCookies(HttpServletRequest request, HttpServletResponse response, PrintWriter writer) throws IOException
	{
		Map<String, String> cookies = getCookieMap(request);

		String visitDate = cookies.get(VISIT_DATE);
		String visitCount = cookies.get(VISIT_COUNT);

		StringBuilder sb = new StringBuilder();
		// Update visit count as necessary and output information.
		if((visitDate == null) || (visitCount == null))
		{
			visitCount = "0";

			//			sb.append("You have never been to this webpage before! ");
			//			sb.append("Thank you for visiting.");

			//			writer.printf("You have never been to this webpage before! ");
			//			writer.printf("Thank you for visiting.");
		} else
		{
			visitCount = Integer.toString(Integer.parseInt(visitCount) + 1);

			sb.append("You have visited this website " + visitCount + " times. ");
			//writer.printf("You have visited this website %s times. ", visitCount);
			sb.append("Your last visit was on " + visitDate);

			//writer.printf("Your last visit was on %s.", visitDate);
		}

		sb.append("</p>%n");
		//writer.printf("</p>%n");

		// Checks if the browser indicates visits should not be tracked.
		// This is not a standard header!
		// Try this in Safari private browsing mode.
		if(request.getIntHeader("DNT") != 1)
		{
			response.addCookie(new Cookie("Visited", getLongDate()));
			response.addCookie(new Cookie("Count", visitCount));
		} else
		{
			clearCookies(request, response);
			//sb.append("\"<p>Your visits will not be tracked.</p>\"%n");

			//writer.printf("<p>Your visits will not be tracked.</p>");
		}


		sb.append("%n");
		sb.append("<p style=\"font-size: 13pt; font-style: italic; text-align: center;");
		sb.append("border-top: 1px solid #eeeeee; margin-bottom: 1ex;\">");

		sb.append(
				"Page <a href=\"" + request.getRequestURL() + request.getRequestURL() + "\">" + getShortDate() + " </a> generated" + " on" + getShortDate() + "  by thread " + Thread.currentThread()
						.getName());
		sb.append("</p>%n%n");

		writer.printf(sb.toString());
		//		writer.printf("%n");
		//		writer.printf("<p style=\"font-size: 13pt; font-style: italic; text-align: center;");
		//		writer.printf("border-top: 1px solid #eeeeee; margin-bottom: 1ex;\">");
		//
		//		writer.printf("Page <a href=\"%s\">%s</a> generated on %s by thread %s. ", request.getRequestURL(), request.getRequestURL(), getShortDate(), Thread.currentThread().getName());
		//
		//		writer.printf("</p>%n%n");
	}


	public static String getShortDate()
	{
		String format = "yyyy-MM-dd hh:mm a";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(Calendar.getInstance().getTime());
	}

	public void clearCookies(HttpServletRequest request, HttpServletResponse response)
	{

		Cookie[] cookies = request.getCookies();

		if(cookies != null)
		{
			for (Cookie cookie : cookies)
			{
				cookie.setValue(null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}

	public static String getLongDate()
	{
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}

	/**
	 * Gets the cookies form the HTTP request, and maps the cookie key to
	 * the cookie value.
	 *
	 * @param request - HTTP request from web server
	 * @return map from cookie key to cookie value
	 */
	public Map<String, String> getCookieMap(HttpServletRequest request)
	{
		HashMap<String, String> map = new HashMap<>();
		Cookie[] cookies = request.getCookies();

		if(cookies != null)
		{
			for (Cookie cookie : cookies)
			{
				map.put(cookie.getName(), cookie.getValue());
			}
		}

		return map;
	}

	/**
	 * Prepares the servlet response by writing header HTML to the web page.
	 *
	 * @param response
	 * @throws IOException
	 */
	protected void writeLandingPageHeader(HttpServletRequest request, HttpServletResponse response, PrintWriter writer, String searchQuery) throws IOException
	{
		writer.printf("<!DOCTYPE html>%n");
		writer.printf("<html lang=\"en\">%n");
		writer.printf("<head>%n");
		writer.printf("\t<meta charset=\"utf-8\">%n");
		writer.printf("\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">%n");
		writer.printf("\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");
		writer.printf("\t<title>YouTalky.com -- A next generation audio search engine</title>");
		writeCss(writer);
		//<!-- Image and text nav bar -->
		writer.printf("</head>%n%n");
	}

	protected void writeCss(PrintWriter writer)
	{

		writer.printf("\t\t\t<link rel=\"stylesheet\" type=\"text/css\" href=\"//maxcdn.bootstrapcdn" + ".com/bootstrap/3.3.4/css/bootstrap.min.css\"/>%n");
		writer.printf("\t<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min" + ".js\"></script>%n");
		//writer.printf("\t<script type=\"text/javascript\" src=\"https://www.dropbox
		// .com/s/7l2ej0o22j3nok5/main.css?dl=0\"></script>%n");

		// TODO CSS BODY
		writer.printf("<style>%n");
		writer.printf("\t\t .html position: relative; min-height: 100; %n");
		//writer.printf("\t\t a:visited {color:white; text-decoration:none} " + "%n");
		//writer.printf("\t\t a:url {color:black; text-decoration:none} %n");
		writer.printf("\t\t a:link{color:black;  text-decoration:none} %n");
		writer.printf("\t\t a:hover{color:black; text-decoration:underline}" + " %n");
		writer.printf("\t\tbody{margin-bottom: 60px; background: #5CC8FF; height: 100; font-family: " + "Times; }%n");

		writer.printf("\t\t .footer{" + "position: relative; bottom: 0; width: 100; height: 60px;" + "line-height: 60px; " + "background: #5CC8FF; color: white; " + "padding-bottom: 80px;}%n");
		writer.printf("\t\t .jumbotron{background: #5CC8FF; color: white; padding-bottom: 80px;} %n");


		writer.printf(
				".jumbotron .searchContainer\n" + "    {\n" + "        width: 500px;\n" + "       " + " height: 50px;\n" + "background: #5CC8FF;\n" + "border-color: red;\n" + "   " + "" + " }\n" +
						"\n" + "  .jumbotron .searchBox\n" + "  {\n" + "      width: 900px;\n" + "      height: 50px;\n" + "      " + "background: #5CC8FF;\n" + "      border-color: " + "white;\n" +
						"  }\n" + "  " + ".jumbotron .btn-default\n" + "    {\n" + "        margin-top: 20px;\n" + "        background: #845ac7;/* purple button */\n" + "        " + "border-color:"
						+ " #845ac7;/* purple button */\n" + "    }\n" + "    " + ".jumbotron .btn-default:hover\n" + "    {\n" + "        background: " + "#7646c1;\n" + "    }\n" + "  .jumbotron "
						+ ".btn-primary\n" + "  {\n" + "    " + "margin-top: 20px;\n" + "    background: #845ac7;    /* purple button " + "*/\n" + "    border-color: #845ac7;     /* " + "purple " +
						"button " + "*/\n" + "    " + "}\n" + "    .jumbotron .btn-primary:hover\n" + "    {\n" + "      " + "background: #7646c1;\n" + "      }\n" + "  .jumbotron p\n" + "  " +
						"{\n" + " " + "   " + "color: white;\n" + "    max-width: 75;\n" + "    margin: 1em auto " + "2em;\n" + "  }\n" + "  .navbar + .jumbotron\n" + "  {\n" + "    " +
						"margin-top:" + " -20px;\n" + "  }\n" + "    .jumbotron .lang-logo img\n" + "  " + "  {\n" + "      color: white;\n" + "      max-width: 100;\n" + "    }\n" + "  }");


		writer.printf(
				"@import url(http://fonts.googleapis.com/css?family=Cabin:400);\n" + "" + ".webdesigntuts-workshop {\n" + "\tbackground: #5CC8FF;\n" + "\theight: " + "100;\n" + "\tposition: " +
						"absolute;\n" + "\ttext-align: center;\n" + "\twidth: 100;\n" + "}\n" + ".webdesigntuts-workshop:before,\n" + "" + ".webdesigntuts-workshop:after {\n" + "\tcontent: '';\n" +
						"\tdisplay: " + "block;\n" + "\theight: 1px;\n" + "\tleft: 50;\n" + "\tmargin: 0 0 0 " + "-400px;\n" + "\tposition: absolute;\n" + "\twidth: 800px;\n" + "}\n" + "" + "" + ""
						+ ".webdesigntuts-workshop:before {\n" + "\tbackground: #444;\n" + "\tbackground: linear-gradient(left, #151515, #444, #151515);\n" + "\ttop:" + " 192px;\n" + "}\n" + "" + ""
						+ ".webdesigntuts-workshop:after {\n" + "\tbackground:" + " #5CC8FF;\n" + "\tbackground: linear-gradient(left, #151515, #5CC8FF, #151515);" + "\n" + "\ttop: 191px;\n" + "}\n"
						+ "" + ".webdesigntuts-workshop form {\n" + "\tbackground: #111;\n" + "\tbackground: linear-gradient(#1b1b1b, #111);" + "\n" + "\tborder: 1px solid #000;\n" +
						"\tborder-radius: 5px;" + "" + "\n" + "\tbox-shadow: inset 0 0 0 1px #272727;\n" + "\tdisplay: inline-block;\n" + "\tfont-size: 0px;\n" + "\tmargin: 150px auto 0;\n" +
						"\tpadding: 20px;" + "\n" + "\tposition: relative;\n" + "\tz-index: 1;\n" + "}\n" + "" + ".webdesigntuts-workshop input {\n" + "\tbackground: #222;\n" + "\tbackground: " +
						"linear-gradient(#333, #222);\n" + "\tborder: 1px solid " + "#444;\n" + "\tborder-radius: 5px 0 0 5px;\n" + "\tbox-shadow: 0 2px 0 " + "#000;\n" + "\tcolor: #888;\n" +
						"\tdisplay: block;\n" + "\tfloat: left;" + "\n" + "\tfont-family: 'Cabin', helvetica, arial, sans-serif;\n" + "\tfont-size: 13px;\n" + "\tfont-weight: 400;\n" + "\theight: "
						+ "40px;\n" + "\tmargin: 0;\n" + "\tpadding: " + "0" + " 10px;\n" + "\ttext-shadow: 0 -1px 0 #000;" + "\n" + "\twidth: 400px;\n" + "}\n" + ".ie .webdesigntuts-workshop input " +
						"" + "" + "" + "" + "" + "{\n" + "\tline-height: 40px;\n" + "}\n" + "" + ".webdesigntuts-workshop " + "input::-webkit-input-placeholder {\n" + "   color: #888;\n" + "}\n" +
						"" + "" + "" + "" + ".webdesigntuts-workshop input:-moz-placeholder {\n" + "   color: " + "#888;\n" + "}\n" + ".webdesigntuts-workshop input:focus {\n" + "\tanimation: glow "
						+ "800ms " + "ease-out " + "infinite alternate;\n" + "\tbackground: #222922;\n" + "\tbackground: " + "" + "linear-gradient(#333933, #222922);\n" + "\tborder-color: " + "#393;" +
						"" + "\n" + "\tbox-shadow: 0 0 " + "" + "" + "5px rgba(0,255,0,.2), inset 0 0 5px rgba(0," + "255,0,.1), 0 2px 0 #000;\n" + "\tcolor: #efe;\n" + "\toutline: none;\n" + "}\n"
						+ "" + ".webdesigntuts-workshop " + "input:focus::-webkit-input-placeholder " + "{\n" + "\tcolor: #efe;\n" + "}\n" + "" + "" + ".webdesigntuts-workshop " +
						"input:focus:-moz-placeholder {\n" + "\tcolor: #efe;" + "\n" + "}\n" + "" + ".webdesigntuts-workshop button {\n" + "\tbackground: #222;\n" + "\tbackground: linear-gradient" +
						"(#333, #222);\n" + "\tbox-sizing: " + "content-box;\n" + "\tborder: 1px solid #444;\n" + "\tborder-left-color: " + "#000;\n" + "\tborder-radius:" + " 0 5px 5px 0;\n" +
						"\tbox-shadow: 0 2px 0 " + "#000;\n" + "\tcolor: #fff;\n" + "\tdisplay: block;\n" + "\tfloat: left;" + "\n" + "\tfont-family: " + "'Cabin', helvetica, arial, " + "sans-serif;" +
						"" + "" + "\n" + "\tfont-size: 13px;\n" + "\tfont-weight: 400;\n" + "\theight: 40px;\n" + "\tline-height: 40px;\n" + "\tmargin: 0;\n" + "\tpadding: 0;\n" + "\tposition: " +
						"relative;" + "\n" + "\ttext-shadow: 0 -1px 0 #000;\n" + "\twidth: " + "80px;\n" + "}\n" + ".webdesigntuts-workshop button:hover,\n" + "" + "" + ".webdesigntuts-workshop " +
						"button:focus " + "{\n" + "\tbackground: #292929;\n" + "\tbackground: " + "linear-gradient(#393939, #292929);\n" + "\tcolor: #5f5;\n" + "\toutline: none;" + "\n" + "}\n" + ""
						+ ".webdesigntuts-workshop " + "button:active " + "{\n" + "\tbackground: " + "#292929;\n" + "\tbackground: linear-gradient" + "(#393939, #292929);\n" + "\tbox-shadow:" + " 0 " +
						"" + "" + "1px 0 #000, inset 1px 0 1px " + "#222;\n" + "\ttop: 1px;\n" + "}\n" + "@keyframes glow {\n" + "    0 {\n" + "\t\tborder-color: #393;\n" + "\t\tbox-shadow: 0 0 5px " +
						"rgba" + "(0," + "255,0,.2)," + " inset 0 0 5px rgba(0,255,0,.1)" + ", 0 2px 0 #000;\n" + "    }\n" + "    100 " + "{\n" + "\t\tborder-color: #6f6;\n" + "\t\tbox-shadow: 0 0 " +
						"20px rgba" + "(0," + "255,0,.6), inset 0 0 10px rgba(0,255,0,.4), 0 2px 0" + " #000;\n" + "  " + "" + "" + "  }\n" + "}");

		writer.printf("</style>%n");
	}

	protected void writeLandingPageBody(HttpServletRequest req, HttpServletResponse res, PrintWriter writer, String searchQuery, long startTimeAccountant, boolean showHistory,
			boolean discover) throws IOException
	{
		writer.printf("\t<body>%n");
		writer.printf("\t<div class=\"jumbotron text-center\">%n");
		String logoMessage = "<font color=\"white\"><h1>YouTalky</h1></font> </a>%n";
		writer.printf("\t<a href=\"http://localhost:8080/\">" + logoMessage);
		writer.printf("\t <p>Discover at your fingertips.</p>%n");

		if(!discover)
		{
			writeSearchBox(writer, searchQuery);
			writeSearchResults(req, res, writer, searchQuery, startTimeAccountant, showHistory);
		} else
		{
			writeDiscoverBox(writer, searchQuery);
		}
	}

	protected void writeSearchPageHeader(HttpServletRequest request, HttpServletResponse response, PrintWriter writer, String searchQuery) throws IOException
	{
		writer.printf("<!DOCTYPE html>%n");
		writer.printf("<html lang=\"en\">%n");
		writer.printf("<head>%n");
		writer.printf("\t<meta charset=\"utf-8\">%n");
		writer.printf("\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">%n");
		writer.printf("\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">%n");
		writer.printf("\t<title>YouTalky.com -- A next generation audio search engine</title>");
		writeCss(writer);

		String searchHistoryTab1 = "href=\"http://localhost:8080/?history=\">";
		String searchHistoryTab2 = "<font color=\"white\">Search History</font> %n";

		String discoverTab1 = "href=\"http://localhost:8080/?discover=\">";
		String discoverTab2 = "<font color=\"white\">Discover</font> %n";

		String navTabs = "<ul class=\"nav nav-tabs\">\n" + "  <li class=\"nav-item\">\n" + "<a class=\"nav-link active\" href=\"http://localhost:8080/\">Home</a>\n" + " </li>\n" + " <li " +
				"class=\"nav-item\">\n" +


				// Search History tab
				"<a class=\"nav-link\"" + searchHistoryTab1 + searchHistoryTab2 + "</a>\n" + "</li>\n" + "<li class=\"nav-item\">\n" +

				// Discover tab
				"<a class=\"nav-link\"" + discoverTab1 + discoverTab2 + "</a>\n" + "</li>\n" + "<li class=\"nav-item\">\n" +

				"</ul>";

		//				+ "<a class=\"nav-link\" href=\"#\">Link</a>\n"
		//				+ "</li>\n" + "<li" + "class=\"nav-item\">\n" + "<a class=\"nav-link disabled\" href=\"#\">Disabled</a>\n" + "  </li>\n" + "</ul>";
		writer.printf(navTabs);
		writer.printf("\t<nav class=\"navbar navbar-light bg-faded\">%n");
		writer.printf("\t<a class=\"navbar-brand\" href=\"http://localhost:8080/\">%n");
		writer.printf("\t<img src=\"https://simpleicons.org/icons/soundcloud.svg\" style=\"width:30 " + "height=30\" class=\"d-inline-block align-top\" alt=\"\"> ");


		writer.printf("YouTalky%n");
		writer.printf("</a>%n");
		// TODO ADD SEARCH BAR TO THE HEADER
		writeSearchBox(writer, searchQuery);
		writer.printf("\t </nav>%n");


		//<!-- Image and text nav bar -->
		writer.printf("</head>%n%n");
	}

	//https://twitter.com/realDonaldTrump
	protected void writeSearchPageBody(HttpServletRequest req, HttpServletResponse res, PrintWriter writer, String searchQuery, long startTimeAccountant, boolean showHistory) throws IOException
	{
		writer.printf("\t<body>%n");
		writer.printf("\t<div class=\"jumbotron text-center\">%n");
		writeSearchResults(req, res, writer, searchQuery, startTimeAccountant, showHistory);
	}

	protected void writeSearchBox(PrintWriter writer, String searchQuery) throws IOException
	{
		writer.printf("\t <div id=\"searchContainer\">%n");
		writer.printf("	\t<section class=\"d-inline-block align-top\" alt=\"\"> %n");
		writer.printf("\t <section class=\"webdesigntuts-workshop\">%n");
		writer.printf("\t <form action=\"/\"method=\"get\">%n");

		//System.out.println("request.getParameter(\"query\"); = "+ request.getParameter("query"));

		if(searchQuery != null)
		{
			writer.printf("\t <input type=\"text\" name=\"query\" value=\"" + searchQuery + "\">%n");
		} else
		{
			writer.printf("\t <input type=\"text\" name=\"query\" placeholder=\"What are you looking " + "for?\">%n");

		}
		writer.printf("\t <button>Search</button>%n");
		writer.printf("\t </form>%n");
		writer.printf("\t </section>%n");
		writer.printf("\t </section>%n");
		writer.printf("\t </div>%n");
	}

	protected void writeDiscoverBox(PrintWriter writer, String searchQuery) throws IOException
	{
		writer.printf("\t <div id=\"searchContainer\">%n");
		writer.printf("	\t<section class=\"d-inline-block align-top\" alt=\"\"> %n");
		writer.printf("\t <section class=\"webdesigntuts-workshop\">%n");
		writer.printf("\t <form action=\"/\" method=\"get\">%n");

		//System.out.println("request.getParameter(\"query\"); = "+ request.getParameter("query"));

		if(searchQuery != null)
		{
			writer.printf("\t <input type=\"text\" name=\"discover\" value=\"" + searchQuery + "\">%n");
		} else
		{
			writer.printf("\t <input type=\"text\" name=\"discover\" placeholder=\"Enter a website to discover!\">%n");

		}
		writer.printf("\t <button>Discover</button>%n");
		writer.printf("\t </form>%n");

		writer.printf("\t </section>%n");
		writer.printf("\t </section>%n");

		writer.printf("\t </div>%n");
	}

	protected void writeSearchResults(HttpServletRequest request, HttpServletResponse response, PrintWriter writer, String searchQuery, long startTimeAccountant,
			boolean showHistory) throws IOException
	{
		if(showHistory)
		{
			String searchTime = getPrettyTime(startTimeAccountant);
			//TODO WRITE SEARCH RESULTS
			searcher.writeSearchHistory(writer, searchTime);
		} else if(searchQuery != null && !searchQuery.isEmpty())
		{
			// perform partial search
			searcher.parseQueryFile("index.json", searchQuery, false);

			String searchTime = getPrettyTime(startTimeAccountant);

			//TODO WRITE SEARCH RESULTS
			searcher.writeSearchResultsToHtml(writer, searchTime);

			//			// for debug in console
			//			String results = searcher.writeToString();
			//			//System.out.println("results = " + results);
		}
	}

	protected void writeFooter(HttpServletRequest req, HttpServletResponse res, PrintWriter writer) throws IOException
	{
		writer.printf("\t<footer class=\"footer\">%n");
		writer.printf("\t<div class=\"container\">%n");

		//writer.printf("\t<span class=\"text-muted\">Place sticky footer content here.</span>%n");
		writer.printf("\t </div>%n");
		setUpCookies(req, res, writer);

		writer.printf("\t</footer>%n");
		writer.printf("\t%n");


		// close up body and html tags
		writer.printf("\t </body>%n");
		writer.printf("\t</html>%n");

		// finish up response
		res.setContentType("text/html");
		res.setStatus(HttpServletResponse.SC_OK);
		res.flushBuffer();
	}

	protected String getPrettyTime(long startTime)
	{
		long endTime = System.nanoTime();
		long duration = (endTime - startTime);
		long seconds = (duration / 1000) % 60;
		// formatedSeconds = (0.52 seconds)
		String formatedSeconds = String.format("(0.%d seconds)", seconds);
		//System.out.println("formatedSeconds = " + formatedSeconds);
		return formatedSeconds;
	}

	protected static String getSearchQuery(HttpServletRequest request)
	{
		String searchQuery = request.getParameter("query");
		return searchQuery;
	}
}