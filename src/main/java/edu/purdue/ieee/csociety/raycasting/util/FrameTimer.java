package edu.purdue.ieee.csociety.raycasting.util;

import edu.purdue.ieee.csociety.raycasting.Main;

import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

import static java.util.Objects.requireNonNull;

/**
 * A simple frame timer that calculates FPS and frame time
 */
public class FrameTimer {

    private long lastCalcTime;
    private final long sampleIntervalNanos;
    private final double sampleIntervalSec;
    private int numIntervalFramesElapsed;
    private int lastIntervalFps;
    private long lastIntervalDurationNanos;
    private long lastIntervalAvgFrameTimeNanos;
    private long frameStartNanos;
    private long lastFrameDurationNanos;
    private IntConsumer onEndIntervalFpsCallback;
    private LongConsumer onEndIntervalDurationNanosCallback;
    private LongConsumer onEndIntervalAvgFrameTimeNanosCallback;

    /**
     * Constructs a FrameTimer that samples at the given interval
     * @param sampleInterval The number of {@code timeUnit}s that should elapse before taking another sample
     * @param timeUnit The {@link TimeUnit} of the {@code sampleInterval}
     */
    public FrameTimer(long sampleInterval, TimeUnit timeUnit) {
        if (sampleInterval <= 0) {
            throw new IllegalArgumentException("Sample interval duration must be positive");
        }
        this.sampleIntervalNanos = timeUnit.toNanos(sampleInterval);
        sampleIntervalSec = sampleIntervalNanos / 1e9D;
        onEndIntervalAvgFrameTimeNanosCallback = l -> {};
        onEndIntervalDurationNanosCallback = l -> {};
        onEndIntervalFpsCallback = i -> {};
        Main.LOGGER.debug("Setting up frame timer at an interval of {} ns ({} s)",
                sampleIntervalNanos, sampleIntervalSec);
        numIntervalFramesElapsed = 0;
        lastCalcTime = System.nanoTime();
        frameStartNanos = lastCalcTime;
        lastFrameDurationNanos = 1L;
        lastIntervalAvgFrameTimeNanos = 1L;
        lastIntervalDurationNanos = 1L;
        lastIntervalFps = 1;
    }

    /**
     * Sets the callback to be invoked upon the end of a sample interval given the average FPS over the interval
     * @param onEndIntervalFpsCallback
     */
    public void setOnEndIntervalFpsCallback(IntConsumer onEndIntervalFpsCallback) {
        this.onEndIntervalFpsCallback = requireNonNull(onEndIntervalFpsCallback);
    }

    /**
     * Sets the callback to be invoked upon the end of a sample interval given the real duration of the interval in nanos
     * @param onEndIntervalDurationNanosCallback
     */
    public void setOnEndIntervalDurationNanosCallback(LongConsumer onEndIntervalDurationNanosCallback) {
        this.onEndIntervalDurationNanosCallback = requireNonNull(onEndIntervalDurationNanosCallback);
    }

    /**
     * Sets the callback to be invoked upon the end of a sample interval given the average frame time in nanos
     * @param onEndIntervalAvgFrameTimeNanosCallback
     */
    public void setOnEndIntervalAvgFrameTimeNanosCallback(LongConsumer onEndIntervalAvgFrameTimeNanosCallback) {
        this.onEndIntervalAvgFrameTimeNanosCallback = requireNonNull(onEndIntervalAvgFrameTimeNanosCallback);
    }

    /**
     * To be called at the start of a frame
     */
    public void start() {
        frameStartNanos = System.nanoTime();
        numIntervalFramesElapsed++;
    }

    /**
     * To be called at the end of a frame
     */
    public void end() {
        long endTime = System.nanoTime();
        long diff = endTime - lastCalcTime;
        lastFrameDurationNanos = endTime - frameStartNanos;
        if (diff > sampleIntervalNanos) {
            lastIntervalFps = (int) (numIntervalFramesElapsed / sampleIntervalSec);
            lastIntervalDurationNanos = diff;
            lastIntervalAvgFrameTimeNanos = diff / numIntervalFramesElapsed;
            lastCalcTime = endTime;
            numIntervalFramesElapsed = 0;
            onEndIntervalFpsCallback.accept(lastIntervalFps);
            onEndIntervalDurationNanosCallback.accept(lastIntervalDurationNanos);
            onEndIntervalAvgFrameTimeNanosCallback.accept(lastIntervalAvgFrameTimeNanos);
        }
    }

    /**
     * Gets the average FPS of the last measured interval
     * @return
     */
    public int getLastIntervalFps() {
        return lastIntervalFps;
    }

    /**
     * Gets the real amount of time the last measured interval took, in nanos
     * @return
     */
    public long getLastIntervalDurationNanos() {
        return lastIntervalDurationNanos;
    }

    /**
     * Gets the average frame time of the last measured interval, in nanos
     * @return
     */
    public long getLastIntervalAvgFrameTimeNanos() {
        return lastIntervalAvgFrameTimeNanos;
    }

    /**
     * Gets the duration of the last frame, in nanos
     * @return
     */
    public long getLastFrameDurationNanos() {
        return lastFrameDurationNanos;
    }

    /**
     * Gets the last frame's instantaneous FPS
     * @return
     */
    public long getLastFrameInstantaneousFps() {
        return 1_000_000_000L / lastFrameDurationNanos;
    }
}
