package org.example.common;

/**
 * Enumeration of available sampling algorithms.
 * <p>
 * These algorithms are used to generate sets of valid feature configurations
 * based on different strategies.
 * </p>
 *
 * <ul>
 * <li>{@link #YASA} – YASA algorithm.</li>
 * <li>{@link #UNIFORM} – Uniform random sampling.</li>
 * <li>{@link #INCLING} – Incling algorithm.</li>
 * </ul>
 *
 * @see org.example.common.SamplingConfig
 */
public enum SamplingAlgorithm {
    YASA,
    UNIFORM,
    INCLING
}