package org.example.common;

public class SamplingConfig {
    private SamplingAlgorithm samplingAlgorithm;
    private int t;
    private int numberOfConfigurations;

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
