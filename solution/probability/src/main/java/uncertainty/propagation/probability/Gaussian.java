package uncertainty.propagation.probability;

public final class Gaussian {
    private final double mean;
    private final double standard_deviation;

    public Gaussian(double mean, double standard_deviation) {
        this.mean = mean;
        this.standard_deviation = standard_deviation;
    }

    @Override
    public String toString() {
        return "Gaussian(mean=" + mean + ", std=" + standard_deviation + ")";
    }
}
