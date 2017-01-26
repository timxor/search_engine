/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        JSONTreeWriter.java
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JSONTreeWriter
{

	/**
	 * The method write is responsible for writing out the data structure to a text file.
	 *
	 * @param data is the data structure to extract the data to the outputFile.
	 */
	public static String writeHtml(TreeMap<String, ArrayList<SearchResult>> data) throws IOException
	{
		StringBuilder writer = new StringBuilder("");
		writer.append("<pre>");
		writer.append("{\n");
		if(!data.isEmpty())
		{
			int i = 0;
			for (String queryWord : data.keySet())
			{
				i++;
				writeWordOpening2(queryWord, data.get(queryWord).isEmpty(), (data.keySet().size() != i), writer);

				if(!data.get(queryWord).isEmpty())
				{
					int j = 0;
					for (SearchResult file : data.get(queryWord))
					{
						j++;
						writeLevelThree2(file.getFileName(), file.getCount(), file.getPosition(), writer, (data.get(queryWord).size() != j), i != data.keySet().size());
					}
				} else
				{
					if(i == data.keySet().size())
					{
						writer.append("}\n");
					}
				}
			}
		}
		writer.append("</pre>");

		return writer.toString();
	}

	/**
	 * Writes out the first word opening.
	 */
	private static void writeWordOpening2(String word, boolean isEmpty, boolean useComma, StringBuilder writer) throws IOException
	{
		if(!isEmpty)
		{
			writer.append(indent(2) + quote(word) + ": [\n");
		} else
		{
			writer.append(indent(2) + quote(word) + ": [\n");
			writer.append(indent(2) + "]");
			if(useComma)
			{
				writer.append(",");
			}
			writer.append("\n");
		}
	}

	/**
	 * Writer for the third level in the nested index.
	 */
	private static void writeLevelThree2(String fileName, int count, int index, StringBuilder writer, boolean useComma, boolean outerComma) throws IOException
	{
		writer.append(indent(4) + "{" + System.lineSeparator());
		writer.append(indent(6) + quote("where") + ": " + quote(fileName) + "," + System.lineSeparator());
		writer.append(indent(6) + quote("count") + ": " + count + "," + System.lineSeparator());
		writer.append(indent(6) + quote("index") + ": " + index + System.lineSeparator());
		writer.append(indent(4) + "}");

		if(useComma)
		{
			writer.append(",");
			writer.append(System.lineSeparator());
		} else
		{
			if(outerComma)
			{
				writer.append(System.lineSeparator() + indent(2) + "],");
				writer.append(System.lineSeparator());
			} else
			{
				writer.append(System.lineSeparator() + indent(2) + "]");
				writer.append(System.lineSeparator());
				writer.append("}");
				writer.append(System.lineSeparator());
			}
		}
	}

	/**
	 * The method write is responsible for writing out the data structure to a text file.
	 *
	 * @param outputFile is the path representation for the file to output.
	 * @param data       is the data structure to extract the data to the outputFile.
	 */
	public static void writeSearchResults(String outputFile, TreeMap<String, ArrayList<SearchResult>> data) throws IOException
	{
		Charset charset = Charset.forName("UTF-8");
		Path output_file = Paths.get(outputFile).toAbsolutePath().normalize();

		try (BufferedWriter writer = Files.newBufferedWriter(output_file, charset))
		{
			writer.write("{" + System.lineSeparator());
			if(!data.isEmpty())
			{
				int i = 0;
				for (String queryWord : data.keySet())
				{
					i++;
					writeWordOpening(queryWord, data.get(queryWord).isEmpty(), (data.keySet().size() != i), writer);

					if(!data.get(queryWord).isEmpty())
					{
						int j = 0;
						for (SearchResult file : data.get(queryWord))
						{
							j++;
							writeLevelThree(file.getFileName(), file.getCount(), file.getPosition(), writer, (data.get(queryWord).size() != j), i != data.keySet().size());
						}
					} else
					{
						if(i == data.keySet().size())
						{
							writer.write("}" + System.lineSeparator());
						}
					}
				}
				writer.flush();
			}
		}
	}


	public static void writeSearchHistoryToHtml(TreeMap<String, ArrayList<SearchResult>> data, PrintWriter writer, String searchTime) throws IOException
	{
		writer.printf("<body class=\"container\">%n");
		writer.printf("<table class=\"table table-hover\">%n");
		String cellFormat = "\t<td><b>%s</b></td>%n";

		String message = "<font color=\"white\">Your search history: </font>";
		writer.printf(cellFormat, message);
		writer.printf(cellFormat, "Query's:");
		writer.printf("</tr>%n");

		String rowEntry = "\t<tr> <td> %s </td><td> <b> %s </b> </td> <tr> %n";

		if(!data.isEmpty())
		{
			int i = 0;
			for (String queryWord : data.keySet())
			{
				i++;
				writer.printf(rowEntry, i, quote(queryWord));
			}
			writer.printf("</table>%n");
		}
	}


	public static void writeSearchResultsToHtml(TreeMap<String, ArrayList<SearchResult>> data, PrintWriter writer, String searchTime) throws IOException
	{
		writer.printf("<body class=\"container\">%n");
		writer.printf("<table class=\"table table-hover\">%n");
		//		writer.printf("<tr style=\"background-color: white; a:link{color:#6f6; background-color:transparent; text-decoration:none}\">%n");
		writer.printf("</tr>%n");
		String rowEntry = "\t<tr> <td> %s </td><td> <b> %s </b> </td> <tr> %n";

		if(!data.isEmpty())
		{
			int i = 0;
			for (String queryWord : data.keySet())
			{
				i++;
				if(!data.get(queryWord).isEmpty())
				{
					int j = 0;

					// (0.77 seconds)

					int totalResults = data.get(queryWord).size();
					String totalResultsString = "\t\tFound " + totalResults + " results " + searchTime;
					writer.printf(rowEntry, "", totalResultsString);

					// if there are results
					if(totalResults > 0)
					{
						for (SearchResult file : data.get(queryWord))
						{
							j++;
							writeLevelThreeHtml(rowEntry, j, file.getFileName(), file.getCount(), file.getPosition(), writer, (data.get(queryWord).size() != j), i != data.keySet().size());
						}
					}
				} else // no results found for searchQuery
				{
					String noResultsString = "\t\tFound 0 results " + searchTime;
					writer.printf(rowEntry, "", noResultsString);
				}
			}
			writer.printf("</table>%n");
			// uses bootstrap: http://getbootstrap.com/
			writer.printf("<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js\"></script>%n");
			writer.printf(
					"<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\" integrity=\"sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa\" " +
							"crossorigin=\"anonymous\"></script>%n");
			writer.flush();
		}
	}

	/**
	 * Writer for the third level in the nested index.
	 */
	private static void writeLevelThreeHtml(String rowEntry, int j, String fileName, int count, int index, PrintWriter writer, boolean useComma, boolean outerComma) throws IOException
	{
		String url = "<a href=\"" + fileName + "\" > " + fileName + "</a>";
		writer.printf(rowEntry, j, url);
	}


	/**
	 * Writes out the first word opening.
	 */
	private static void writeWordOpening(String word, boolean isEmpty, boolean useComma, BufferedWriter writer) throws IOException
	{
		if(!isEmpty)
		{
			writer.write(indent(2) + quote(word) + ": [" + System.lineSeparator());
		} else
		{
			writer.write(indent(2) + quote(word) + ": [" + System.lineSeparator());
			writer.write(indent(2) + "]");
			if(useComma)
			{
				writer.write(",");
			}
			writer.write(System.lineSeparator());
		}
	}

	/**
	 * Writer for the third level in the nested index.
	 */
	private static void writeLevelThree(String fileName, int count, int index, BufferedWriter writer, boolean useComma, boolean outerComma) throws IOException
	{
		writer.write(indent(4) + "{" + System.lineSeparator());
		writer.write(indent(6) + quote("where") + ": " + quote(fileName) + "," + System.lineSeparator());
		writer.write(indent(6) + quote("count") + ": " + count + "," + System.lineSeparator());
		writer.write(indent(6) + quote("index") + ": " + index + System.lineSeparator());
		writer.write(indent(4) + "}");

		if(useComma)
		{
			writer.write(",");
			writer.write(System.lineSeparator());
		} else
		{
			if(outerComma)
			{
				writer.write(System.lineSeparator() + indent(2) + "],");
				writer.write(System.lineSeparator());
			} else
			{
				writer.write(System.lineSeparator() + indent(2) + "]");
				writer.write(System.lineSeparator());
				writer.write("}");
				writer.write(System.lineSeparator());
			}
		}
	}

	/**
	 * The method write is responsible for writing out the data structure to a text file.
	 *
	 * @param outputFile is the path representation for the file to output.
	 * @param data       is the data structure to extract the data to the outputFile.
	 */
	public static void toJSON(String outputFile, TreeMap<String, TreeMap<String, TreeSet<Integer>>> data) throws IOException
	{
		Charset charset = Charset.forName("UTF-8");
		Path output_file = Paths.get(outputFile).toAbsolutePath().normalize();

		try (BufferedWriter writer = Files.newBufferedWriter(output_file, charset))
		{
			writer.write("{" + System.lineSeparator());

			if(!data.isEmpty())
			{
				int count = 0;
				for (String word : data.keySet())
				{
					count++;
					TreeMap<String, TreeSet<Integer>> outer_set = data.get(word);
					writeWord(word, writer);
					writeNestedObject(writer, outer_set);

					if(data.keySet().size() != count)
					{
						writer.write(indent(2) + "}," + System.lineSeparator());
					} else
					{
						writer.write(indent(2) + "}" + System.lineSeparator());
					}
				}
				writer.write("}" + System.lineSeparator());
			}
			writer.flush();
		}
	}

	private static void writeWord(String word, BufferedWriter writer) throws IOException
	{
		writer.write(indent(2) + quote(word) + ": {" + System.lineSeparator());
	}

	private static void writeNestedObject(BufferedWriter writer, TreeMap<String, TreeSet<Integer>> elements) throws IOException
	{
		Inner_set_element_location inner_set_location = Inner_set_element_location.INIT;
		Outer_set_element_location outer_set_location = Outer_set_element_location.INIT;

		if(!elements.isEmpty())
		{
			// get a set of entries from TreeMap<String, TreeSet<Integer>>
			Set<Map.Entry<String, TreeSet<Integer>>> outer_set = elements.entrySet();
			int outer_set_index = 0;

			// display the set
			for (Map.Entry<String, TreeSet<Integer>> outer_element : outer_set)
			{
				// determine Outer_set_element_location
				outer_set_location = assign_outer_element_location(outer_set.size(), outer_set_location, outer_set_index);
				outer_set_index++;

				// print outer set element
				writer.write(print_outer_set_element(outer_element.getKey(), outer_set_location));

				if(!outer_element.getValue().isEmpty())
				{
					// get a set of entries from TreeSet<Integer>
					TreeSet<Integer> inner_set = outer_element.getValue();

					int inner_set_index = 0;

					// display the inner_set
					for (Integer inner_element : inner_set)
					{
						// determine Outer_set_element_location
						inner_set_location = assign_inner_element_location(inner_set.size(), inner_set_location, inner_set_index);
						inner_set_index++;

						// print inner set element
						writer.write(print_inner_set_element(inner_element.toString(), inner_set_location, outer_set_location));
					}
				} else
				{
					inner_set_location = Inner_set_element_location.EMPTY;
				}
			}
		}
	}

	private static String print_outer_set_element(String value, Outer_set_element_location outer_set_location)
	{
		String data = "";
		switch (outer_set_location)
		{
		case FIRST:
			data = indent2(2) + quote(value) + ": [" + System.lineSeparator();
			break;
		case MIDDLE:
			data = indent2(2) + quote(value) + ": [" + System.lineSeparator();
			break;
		case LAST:
			data = indent2(2) + quote(value) + ": [" + System.lineSeparator();
			break;
		}
		return data;
	}

	private static String print_inner_set_element(String value, Inner_set_element_location inner_set_location, Outer_set_element_location outer_set_location)
	{
		String data = "";
		switch (inner_set_location)
		{
		case FIRST:
			data = indent2(3) + value + "," + System.lineSeparator();
			break;
		case MIDDLE:
			data = indent2(3) + value + "," + System.lineSeparator();
			break;
		case LAST:
			data = indent2(3) + value + System.lineSeparator();
			break;
		}

		if(inner_set_location == Inner_set_element_location.LAST)
		{
			switch (outer_set_location)
			{
			case FIRST:
				data = data + indent2(2) + "]," + System.lineSeparator();
				break;
			case MIDDLE:
				data = data + indent2(2) + "]," + System.lineSeparator();
				break;
			case LAST:
				data = data + indent2(2) + "]" + System.lineSeparator();
				break;
			}
		}
		return data;
	}

	private static String indent2(int times)
	{
		String data = "";

		if(times == 1)
		{
			data = "\t";
		} else if(times == 2)
		{
			data = "\t\t";
		} else if(times == 3)
		{
			data = "\t\t\t";
		}

		return data;
	}


	private static Outer_set_element_location assign_outer_element_location(int outer_set_size, Outer_set_element_location outer_set_location, int outer_set_index)
	{
		if(outer_set_index == 0 && outer_set_size >= 2) // first
		{
			outer_set_location = Outer_set_element_location.FIRST;
		} else if(!(outer_set_index == outer_set_size - 1) && outer_set_index > 0 && outer_set_index < outer_set_size) // middle
		{
			outer_set_location = Outer_set_element_location.MIDDLE;
		} else if(outer_set_index == outer_set_size - 1)//last
		{
			outer_set_location = Outer_set_element_location.LAST;
		}

		return outer_set_location;
	}

	private static Inner_set_element_location assign_inner_element_location(int inner_set_size, Inner_set_element_location inner_set_location, int inner_set_index)
	{
		if(inner_set_index == 0 && inner_set_size == 0) // empty
		{
			inner_set_location = Inner_set_element_location.EMPTY;
		} else if(inner_set_index == 0 && inner_set_size >= 2) // first
		{
			inner_set_location = Inner_set_element_location.FIRST;
		} else if(!(inner_set_index == inner_set_size - 1) && inner_set_index > 0 && inner_set_index < inner_set_size) // middle
		{
			inner_set_location = Inner_set_element_location.MIDDLE;
		} else if(inner_set_index == inner_set_size - 1)//last
		{
			inner_set_location = Inner_set_element_location.LAST;
		}
		return inner_set_location;
	}

	enum Outer_set_element_location
	{
		FIRST, MIDDLE, LAST, INIT
	}

	enum Inner_set_element_location
	{
		FIRST, MIDDLE, LAST, INIT, EMPTY
	}

	/**
	 * The method indent is responsible for generating the number of tabs computed from the level.
	 *
	 * @param times the indentation level or the nested level.
	 */
	private static String indent(int times)
	{
		return times > 0 ? String.format("%" + (times * 2) + "s", " ") : "";
	}

	/**
	 * Helper method to quote text for output. This requires escaping the quotation mark " as \" for use in Strings. For
	 *
	 * @param text input to surround with quotation marks
	 * @return quoted text
	 */
	private static String quote(String text)
	{
		return "\"" + text + "\"";
	}
}
