package uncertainty.propagation.probability;

/**
 * Bernoulli distribution
 * Suitable for binary values
 *
 * We represent vale=1 with the true boolean anf value=0 with a false value
 *
 *
 * P(value = 1)= {@link Bernoulli#probability}
 * P(value = 0)= 1-{@link Bernoulli#probability}
 */
public final class Bernoulli {
    private final double probability; //[0,1]

    private Bernoulli(double probability) {
        this.probability = probability;
    }

    public static Bernoulli initBernoulli(double proba, boolean percentageRepr) {
        if(percentageRepr) {
            if(proba < 0 || proba > 100) {
                throw new RuntimeException("When percentageRepr is set to true, proba should be be in the range [0,100]");
            }
            return new Bernoulli(proba / 100);
        } else {
            if(proba < 0 || proba > 1) {
                throw new RuntimeException("When percentageRepr is set to false, proba should be be in the range [0,1]");
            }
            return new Bernoulli(proba);
        }
    }

    public double getConfidence(boolean value) {
        if(value) {
            return probability;
        }
        return 1-probability;
    }

    public double getConfidencePercRep(boolean value) {
        if(value) {
            return probability * 100;
        }
        return (1-probability)*100;
    }


    @Override
    public String toString() {
        return super.toString();
    }
}
