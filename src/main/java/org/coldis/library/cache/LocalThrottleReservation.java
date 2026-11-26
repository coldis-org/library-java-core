package org.coldis.library.cache;

import java.time.LocalDateTime;

/**
 * A run acquired from a {@link LocalThrottle}, which {@link LocalThrottle#release(LocalThrottleReservation)}
 * gives back when the run did not happen (e.g. its transaction rolled back).
 *
 * @param key                  Key.
 * @param acquiredAt           When the run was acquired (its last run at).
 * @param nextRunAfter         When the key may run again.
 * @param previousLastRunAt    The key's last run at before this one.
 * @param previousNextRunAfter The key's next run after before this one.
 */
public record LocalThrottleReservation(
		String key,
		LocalDateTime acquiredAt,
		LocalDateTime nextRunAfter,
		LocalDateTime previousLastRunAt,
		LocalDateTime previousNextRunAfter) {}
