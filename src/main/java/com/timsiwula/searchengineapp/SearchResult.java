/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        SearchResult.java
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */
package com.timsiwula.searchengineapp;

public class SearchResult implements Comparable<SearchResult>
{
	private final String fileName;
	private int position;
	private int count;

	/**
	 * Class constructor.
	 *
	 * @param fileName The value to assign fileName.
	 * @param count    The value to assign count.
	 * @param position The value to assign position.
	 */
	public SearchResult(String fileName, int count, int position)
	{
		this.fileName = fileName;
		this.count = count;
		this.position = position;
	}

	/**
	 * Returns the filename.
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * Returns the count.
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 * Returns the position.
	 */
	public int getPosition()
	{
		return position;
	}

	/**
	 * Update this search result count and index.
	 *
	 * @param count The value to update count.
	 * @param index The value to update index.
	 */
	public void update(int count, int index)
	{
		this.addCount(count);
		this.updatePosition(index);
	}

	/**
	 * Adds the count to this count.
	 *
	 * @param count The number to add to this count.
	 */
	private void addCount(int count)
	{
		this.count += count;
	}

	/**
	 * Updated the index if found before this location.
	 *
	 * @param otherIndex The value to update if found.
	 */
	private void updatePosition(int otherIndex)
	{
		if(otherIndex < this.getPosition())
		{
			this.position = otherIndex;
		}
	}

	/**
	 * Returns a string of this SearchResult.
	 */
	public String toString()
	{
		return "fileName: " + fileName + ", count: " + count + ", position: " + position;
	}

	/**
	 * Comparable that compares SearchResults objects.
	 */
	@Override public int compareTo(SearchResult o1)
	{
		int countCompare = Integer.compare(o1.getCount(), count);

		// compare by count
		if(countCompare == 0)
		{
			int compareIndex = Integer.compare(position, o1.getPosition());

			// compare by index
			if(compareIndex == 0)
			{
				//compare by fileName
				return fileName.compareTo(o1.getFileName());
			}
			return compareIndex;
		}
		return countCompare;
	}
}