package org.coldis.library.cache;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.math3.stat.descriptive.AggregateSummaryStatistics;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;

/**
 * Statistics summary cache.
 */
public class LocalStatisticsSummaryCache {

	/** Cache lock. */
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	/** Statistics. */
	SummaryStatistics statistics = new SynchronizedSummaryStatistics();

	/**
	 * Cache
	 *
	 * /** Adds a value to the statistics.
	 *
	 * @param value Value to add.
	 */
	public void addValue(
			final double value) {
		try {
			this.lock.readLock().lock();
			this.statistics.addValue(value);
		}
		finally {
			this.lock.readLock().unlock();
		}
	}

	/**
	 * Merges and clears local statistics.
	 *
	 * @param  mainStatistics Main statistics to merge with.
	 * @return
	 * @return                Computed statistics.
	 */
	public StatisticalSummary mergeAndClear(
			final StatisticalSummary mainStatistics) {
		SummaryStatistics cachedStatistics = null;
		try {
			this.lock.writeLock().lock();
			cachedStatistics = this.statistics;
			this.statistics = new SynchronizedSummaryStatistics();
		}
		finally {
			this.lock.writeLock().unlock();
		}
		return AggregateSummaryStatistics.aggregate(List.of(mainStatistics, cachedStatistics.getSummary()));
	}

}
