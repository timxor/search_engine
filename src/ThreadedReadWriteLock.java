/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        ReadWriteLock.java
 * Created:      =        11/6/16
 * Author:       =        Tim Siwula <tcsiwula@gmail.com>
 * University:   =        University of San Francisco
 * Class:        =        CS 212: Software Development
 * License:      =        GPLv2
 * Version:      =        0.001
 * ==============================================================================
 */

/**
 * A simple custom lock that allows simultaneously read operations, but
 * disallows simultaneously write and read/write operations.
 * <p>
 * Does not implement any form or priority to read or write operations. The
 * first thread that acquires the appropriate lock should be allowed to
 * continue.
 */
public class ThreadedReadWriteLock
{
	private int readers;
	private int writers;

	/**
	 * Initializes a multi-reader single-writer lock.
	 */
	public ThreadedReadWriteLock()
	{
		readers = 0;
		writers = 0;
	}

	/**
	 * Will wait until there are no active writers in the system, and then will
	 * increase the number of active readers.
	 */
	public synchronized void lockReadOnly()
	{
		while (writers > 0)
		{
			try
			{
				this.wait();
			} catch (InterruptedException ex)
			{
				Thread.currentThread().interrupt();
			}
		}
		readers++;
	}

	/**
	 * Will decrease the number of active readers, and notify any waiting
	 * threads if necessary.
	 */
	public synchronized void unlockReadOnly()
	{
		readers--;
		if(readers <= 0)
		{
			this.notifyAll();
		}
	}

	/**
	 * Will wait until there are no active readers or writers in the system, and
	 * then will increase the number of active writers.
	 */
	public synchronized void lockReadWrite()
	{
		while (readers > 0 || writers > 0)
		{
			try
			{
				this.wait();
			} catch (InterruptedException e)
			{
				System.err.println("Warning: Read-write-lock encountered an " + "exception while calling lockReadWrite().");
			}
		}
		writers++;
	}

	/**
	 * Will decrease the number of active writers, and notify any waiting
	 * threads if necessary.
	 */
	public synchronized void unlockReadWrite()
	{
		writers--;
		this.notifyAll();
	}
}
