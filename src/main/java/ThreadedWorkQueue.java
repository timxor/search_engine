/*
 * =============================================================================
 * Production:   =        http://youtalky.com
 * Source:       =        https://github.com/usf-cs212-2016/project-tcsiwula
 * File:         =        WorkQueue.java
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
import java.util.LinkedList;

/**
 * A simple work queue implementation based on the IBM developerWorks article
 * by Brian Goetz. It is up to the user of this class to keep track of whether
 * there is any pending work remaining.
 *
 * @see <a href="http://www.ibm.com/developerworks/library/j-jtp0730/index.html">Java Theory and Practice: Thread Pools and Work Queues</a>
 */
public class ThreadedWorkQueue
{
	/**
	 * A sweet logger for logging.
	 */
	private static final Logger logger = LogManager.getLogger();

	private int pending;

	/**
	 * Pool of worker threads that will wait in the background until work is available.
	 */
	private final PoolWorker[] workers;

	/**
	 * Queue of pending work requests.
	 */
	private final LinkedList<Runnable> queue;

	/**
	 * Used to signal the queue should be shutdown.
	 */
	private volatile boolean shutdown;

	/**
	 * Starts a work queue with the specified number of threads.
	 *
	 * @param threads number of worker threads; should be greater than 1
	 */
	public ThreadedWorkQueue(int threads)
	{
		pending = 0;
		this.queue = new LinkedList<>();
		workers = new PoolWorker[threads];

		shutdown = false;

		// start the threads so they are waiting in the background
		for (int i = 0; i < threads; i++)
		{
			workers[i] = new PoolWorker();
			workers[i].start();
		}
		
		System.out.println(workers.length);
	}

	/**
	 * Adds a work request to the queue. A thread will process this request
	 * when available.
	 *
	 * @param r work request (in the form of a {@link Runnable} object)
	 */
	public void execute(Runnable r)
	{
		incrementPending();

		synchronized (queue)
		{
			queue.addLast(r);
			queue.notifyAll();
		}
	}

	/**
	 * Asks the queue to shutdown. Any unprocessed work will not be finished,
	 * but threads in-progress will not be interrupted.
	 */
	public void shutdown()
	{
		shutdown = true;

		synchronized (queue)
		{
			queue.notifyAll();
		}
	}

	/**
	 * Helper method, that helps a thread wait until all of the current
	 * work is done. This is useful for resetting the counters or shutting
	 * down the work queue.
	 */
	public synchronized void finish()
	{
		try
		{
			while (pending > 0)
			{
				this.wait();
			}
		} catch (InterruptedException e)
		{
			System.err.println("Warning: Work queue encountered an " + "exception while calling finish().");
		}
	}

	/**
	 * Indicates that we now have additional "pending" work to wait for. We
	 * need this since we can no longer call join() on the threads. (The
	 * threads keep running forever in the background.)
	 * <p>
	 * We made this a synchronized method in the outer class, since locking
	 * on the "this" object within an inner class does not work.
	 */
	private synchronized void incrementPending()
	{
		pending++;
		logger.debug("Pending is now {}", pending);
	}

	/**
	 * Indicates that we now have one less "pending" work, and will notify
	 * any waiting threads if we no longer have any more pending work left.
	 */
	private synchronized void decrementPending()
	{
		pending--;
		logger.debug("Pending is now {}", pending);

		if(pending <= 0)
		{
			this.notifyAll();
		}
	}

	/**
	 * Waits until work is available in the work queue. When work is found, will
	 * remove the work from the queue and run it. If a shutdown is detected,
	 * will exit instead of grabbing new work from the queue. These threads will
	 * continue running in the background until a shutdown is requested.
	 */
	private class PoolWorker extends Thread
	{
		@Override public void run()
		{
			Runnable r;

			while (true)
			{
				synchronized (queue)
				{
					while (queue.isEmpty() && !shutdown)
					{
						try
						{
							queue.wait();
						} catch (InterruptedException ex)
						{
							System.err.println("Warning: Work queue interrupted " + "while waiting.");
							Thread.currentThread().interrupt();
						}
					}

					// exit while for one of two reasons:
					// (a) queue has work, or (b) shutdown has been called

					if(shutdown)
					{
						break;
					} else
					{
						r = queue.removeFirst();
					}
				}

				// catch runtime exception
				try
				{
					r.run();
				} catch (RuntimeException ex)
				{
					// catch runtime exceptions to avoid leaking threads
					System.err.println("Warning: Work queue encountered an " + "exception while running.");
					ex.printStackTrace();
				}
				decrementPending();
			}
		}
	}
}