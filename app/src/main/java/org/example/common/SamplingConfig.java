package org.example.common;

/**
 * Configuration holder for specifying parameters used in feature
 * configuration sampling.
 * <p>
 * This class is used to define which {@link SamplingAlgorithm} to apply, along
 * with
 * its corresponding parameters:
 * <ul>
 * <li><b>t</b> – The t-value where selectable.</li>
 * <li><b>numberOfConfigurations</b> – The number of configurations to generate
 * (used by uniform sampling).</li>
 * </ul>
 */
public class SamplingConfig {
    private SamplingAlgorithm samplingAlgorithm;
    private int t;
    private int numberOfConfigurations;

    /**
     * Creates a new {@code SamplingConfig} with the specified algorithm and t-value
     *
     * @param samplingAlgorithm the sampling algorithm to use (e.g., YASA, UNIFORM,
     *                          INCLING)
     * @param t                 the t-wise value for coverage (e.g., 2 for pairwise
     *                          sampling)
     */
    public SamplingConfig(SamplingAlgorithm samplingAlgorithm, int t) {
        this.samplingAlgorithm = samplingAlgorithm;
        this.t = t;
    }

    public SamplingAlgorithm getSamplingAlgorithm() {
        return samplingAlgorithm;
    }

    public void setSamplingAlgorithm(SamplingAlgorithm samplingAlgorithm) {
        this.samplingAlgorithm = samplingAlgorithm;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getNumberOfConfigurations() {
        return numberOfConfigurations;
    }

    public void setNumberOfConfigurations(int numberOfConfigurations) {
        this.numberOfConfigurations = numberOfConfigurations;
    }
}
