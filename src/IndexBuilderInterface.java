/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        IndexBuilderInterface.java
 * Created:      =        12/11/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The interface IndexBuilderInterface is responsible for providing static and universial methods used by single and multithreaded programs.
 *
 */
public interface IndexBuilderInterface
{
	void buildIndex(String directory);

	/**
	 * The helper method buildIndex is responsible for populating the data structure with the word, file and location data.
	 *
	 * @param file  This is a single file to process.
	 * @param index This is a triple nested inverted index data structure.
	 */
	static void buildIndex(Path file, InvertedIndex index) throws IOException
	{
		Charset utf8 = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(file, utf8))
		{
			Integer location = 1;
			String line;
			while ((line = reader.readLine()) != null)
			{
				String[] words = split(line);

				for (String s : words)
				{
					//System.out.println("buildIndex() index.add()");
					index.add(s, file.toString(), location);
					location++;
				}
			}
		}
	}

	/**
	 * The method split() splits all the words and returns them in an array.
	 *
	 * @param text This is the list of files to process.
	 */
	static String[] split(String text)
	{
		text = clean(text); // clean word

		if(text.isEmpty()) // check to see if there is any text and return array if empty
		{
			return new String[]{};
		} else
		{
			return text.split(SPLIT_REGEX); // put words into array and return array
		}
	}

	/**
	 * The method clean() processes all words and removes special characters, converts to lower case.
	 *
	 * @param word This is the word to clean.
	 */
	static String clean(String word)
	{
		word = word.toLowerCase();
		word = word.replaceAll(CLEAN_REGEX, ""); // replace special characters
		word = word.trim(); // remove white space
		return word;
	}

	/**
	 * Regular expression for removing special characters.
	 */
	static final String CLEAN_REGEX = "(?U)[^\\p{Alnum}\\p{Space}]+";

	/**
	 * Regular expression for splitting text into words by whitespace.
	 */
	static final String SPLIT_REGEX = "(?U)\\p{Space}+";
}
