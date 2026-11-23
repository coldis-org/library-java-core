package org.coldis.library.test.thread;

import java.nio.file.Files;
import java.nio.file.Path;

import org.coldis.library.thread.CpuLimits;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * {@link CpuLimits}: CPU detection on fake cgroup trees, pin-once, and the
 * resolution of limits. Every case builds its own instance; the process-wide
 * instance is never pinned here.
 */
@DisplayName("CPU limits")
public class CpuLimitsTest {

	/**
	 * Fake cgroup tree.
	 */
	@TempDir
	private Path cgroupRoot;

	/**
	 * Writes a cgroup file.
	 *
	 * @param  file      File (relative to the root).
	 * @param  content   Content.
	 * @throws Exception If the file cannot be written.
	 */
	private void write(
			final String file,
			final String content) throws Exception {
		final Path path = this.cgroupRoot.resolve(file);
		Files.createDirectories(path.getParent());
		Files.writeString(path, content + "\n");
	}

	/**
	 * Tests the fallback without a quota.
	 */
	@Test
	@DisplayName("Without a cgroup quota: the runtime's processors")
	public void testRuntimeFallback() {
		Assertions.assertEquals(16D, new CpuLimits(this.cgroupRoot, () -> 16).getCpus());
		Assertions.assertEquals(1D, new CpuLimits(this.cgroupRoot, () -> 0).getCpus());
	}

	/**
	 * Tests cgroup v2.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("cgroup v2: cpu.max quota / period, capped at the runtime's processors; 'max' is no quota")
	public void testCgroupV2() throws Exception {
		this.write("cpu.max", "208000 100000");
		Assertions.assertEquals(2.08D, new CpuLimits(this.cgroupRoot, () -> 16).getCpus(), 0.0001D);
		Assertions.assertEquals(2D, new CpuLimits(this.cgroupRoot, () -> 2).getCpus());
		this.write("cpu.max", "max 100000");
		Assertions.assertEquals(16D, new CpuLimits(this.cgroupRoot, () -> 16).getCpus());
	}

	/**
	 * Tests cgroup v1 and the v2 precedence.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("cgroup v1: cfs quota / period under the root or its cpu controllers; v2 wins; a quota of -1 is no quota")
	public void testCgroupV1() throws Exception {
		this.write("cpu,cpuacct/cpu.cfs_quota_us", "150000");
		this.write("cpu,cpuacct/cpu.cfs_period_us", "100000");
		Assertions.assertEquals(1.5D, new CpuLimits(this.cgroupRoot, () -> 16).getCpus());
		this.write("cpu.max", "300000 100000");
		Assertions.assertEquals(3D, new CpuLimits(this.cgroupRoot, () -> 16).getCpus());
		this.write("cpu.max", "max 100000");
		this.write("cpu,cpuacct/cpu.cfs_quota_us", "-1");
		Assertions.assertEquals(16D, new CpuLimits(this.cgroupRoot, () -> 16).getCpus());
	}

	/**
	 * Tests the detection happens once.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("The detected CPUs are read once and cached")
	public void testDetectedOnce() throws Exception {
		this.write("cpu.max", "200000 100000");
		final CpuLimits cpuLimits = new CpuLimits(this.cgroupRoot, () -> 16);
		Assertions.assertEquals(2D, cpuLimits.getCpus());
		this.write("cpu.max", "400000 100000");
		Assertions.assertEquals(2D, cpuLimits.getCpus());
	}

	/**
	 * Tests pin-once.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("Pin: wins over the cgroup; only the first positive value is kept")
	public void testPinOnce() throws Exception {
		this.write("cpu.max", "200000 100000");
		final CpuLimits cpuLimits = new CpuLimits(this.cgroupRoot, () -> 16);
		Assertions.assertNull(cpuLimits.getPinnedCpus());
		Assertions.assertFalse(cpuLimits.pin(0));
		Assertions.assertTrue(cpuLimits.pin(3.5));
		Assertions.assertEquals(3.5D, cpuLimits.getCpus());
		Assertions.assertTrue(cpuLimits.pin(3.5));
		Assertions.assertFalse(cpuLimits.pin(8));
		Assertions.assertEquals(3.5D, cpuLimits.getCpus());
		Assertions.assertEquals(3.5D, cpuLimits.getPinnedCpus());
	}

	/**
	 * Tests the resolution of limits.
	 *
	 * @throws Exception If the test fails.
	 */
	@Test
	@DisplayName("Resolve: absolute, then ratio, then default ratio, times the CPUs rounded up, at least 1")
	public void testResolve() throws Exception {
		this.write("cpu.max", "208000 100000");
		final CpuLimits cpuLimits = new CpuLimits(this.cgroupRoot, () -> 16);
		Assertions.assertEquals(5, cpuLimits.resolve(2D));
		Assertions.assertEquals(63, cpuLimits.resolve(30D));
		Assertions.assertEquals(1, cpuLimits.resolve(0.1D));
		Assertions.assertEquals(1, cpuLimits.resolve(0D));
		Assertions.assertEquals(7, cpuLimits.resolve(7, 2D, 3D));
		Assertions.assertEquals(5, cpuLimits.resolve(null, 2D, 3D));
		Assertions.assertEquals(7, cpuLimits.resolve(null, null, 3D));
		Assertions.assertEquals(5, cpuLimits.resolve(0, 2D, 3D));
		Assertions.assertNull(cpuLimits.resolve(null, 0D, 3D));
		Assertions.assertNull(cpuLimits.resolve(null, null, null));
		Assertions.assertEquals(8, CpuLimits.resolve(null, 2D, null, () -> 4D));
	}

	/**
	 * Tests the process-wide instance.
	 */
	@Test
	@DisplayName("The process-wide instance is shared and reports at least one CPU")
	public void testDefault() {
		Assertions.assertSame(CpuLimits.getDefault(), CpuLimits.getDefault());
		Assertions.assertTrue(CpuLimits.getDefault().getCpus() >= 1D);
	}

}
