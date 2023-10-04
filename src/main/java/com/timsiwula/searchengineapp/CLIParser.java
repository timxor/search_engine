/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        ArgumentParser.java
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */
package com.timsiwula.searchengineapp;

import java.util.HashMap;
import java.util.Map;

public class CLIParser
{
	/**
	 * Stores arguments in a map, where the key is a flag.
	 */
	private final Map<String, String> argumentMap;

	/**
	 * Constructor that parses args.
	 *
	 * @param args Args to process
	 * @see #parseArguments(String[])
	 */
	public CLIParser(String[] args)
	{
		parseArguments(args);
		argumentMap = new HashMap<>();
	}

	/**
	 * Parses command line arguments.
	 *
	 * @param args command-line arguments.
	 * @see #isFlag(String)
	 * @see #isNextArgument(int, int)
	 * @see #isNextArgumentAValue(int, String[])
	 */
	private void parseArguments(String[] args)
	{
		for (int i = 0; i < args.length; i++)
		{
			if(isFlag(args[i]))
			{
				if(!isNextArgument(i, args.length) || isNextArgumentAValue(i, args))
				{
					argumentMap.put(args[i], null);
				} else
				{
					argumentMap.put(args[i], args[i + 1]);
				}
			}
		}
	}

	/**
	 * Checks if the next argument is a value.
	 *
	 * @param current - Current index in the args array.
	 * @param args    - The args array list.
	 * @return True if the next argument is a value.
	 */
	private boolean isNextArgumentAValue(int current, String[] args)
	{
		return !isNextArgument(current, args.length) || !isValue(args[current + 1]);
	}

	/**
	 * Checks if there is a next argument
	 *
	 * @param current - Current index in the args array.
	 * @param max     - The size of the ars list.
	 * @return True if there is a next argument.
	 */
	private boolean isNextArgument(int current, int max)
	{
		return current + 1 < max;
	}

	/**
	 * Returns the value of a flag.
	 *
	 * @param flag Flag to check.
	 * @return Value of flag.
	 */
	public String getValue(String flag)
	{
		return argumentMap.get(flag);
	}

	/**
	 * Check if flag.
	 *
	 * @param arg Command-line argument.
	 * @return True if the argument is a flag.
	 */
	private static boolean isFlag(String arg)
	{
		return (arg.length() >= 2 && arg.charAt(0) == '-');
	}

	/**
	 * Checks if string is a value.
	 *
	 * @param arg command-line argument
	 * @return true if the argument is a value
	 */
	private static boolean isValue(String arg)
	{
		arg = arg.trim();
		return arg.length() >= 1 && arg.charAt(0) != '-';
	}

	/**
	 * Tests if the provided flag is stored in the map.
	 *
	 * @param flag Flag to check.
	 * @return True if flag exists.
	 */
	public boolean hasFlag(String flag)
	{
		return argumentMap.containsKey(flag);
	}

	/**
	 * Tests if the provided flag has a non-empty value.
	 *
	 * @param flag Flag to check.
	 * @return True if flag.
	 */
	private boolean hasValue(String flag)
	{
		return argumentMap.get(flag) != null;
	}

	/**
	 * Converts the argumentMap to a string.
	 *
	 * @see #isFlag(String)
	 * @see #isValue(String)
	 */
	@Override public String toString()
	{
		return argumentMap.toString();
	}

	/**
	 * Gets the String value from the flag if valid, else the default.
	 *
	 * @param flag         Flag to check.
	 * @param defaultValue Used if flag is invalid.
	 * @return String value.
	 */
	public String getValue(String flag, String defaultValue)
	{
		return hasValue(flag) ? getValue(flag) : defaultValue;
	}

	/**
	 * Gets the int value from the flag if valid, else the default.
	 *
	 * @param flag         Flag to check.
	 * @param defaultValue Used if flag is invalid.
	 * @return int value.
	 */
	public int getValueInt(String flag, int defaultValue)
	{
		int value;

		try
		{
			value = Integer.parseInt(getValue(flag));
		} catch (Exception e)
		{
			value = defaultValue;
		}

		return value;
	}
}
