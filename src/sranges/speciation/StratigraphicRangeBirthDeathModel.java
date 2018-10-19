package sranges.speciation;

import beast.core.Input;
import beast.core.parameter.RealParameter;
import beast.evolution.speciation.SABirthDeathModel;
import beast.evolution.tree.TreeInterface;

public class StratigraphicRangeBirthDeathModel extends SABirthDeathModel {

    private static final double BETA_DEFAULT = 0.0;
    private static final double LAMBDA_A_DEFAULT = 0.0;

    private double beta;
    private double lambda_a;

    public Input<RealParameter> symmetricSpeciationProbabilityInput = new Input<>("symmetricSpeciationProbability",
            "The probability that a speciation event is symmetric");

    public Input<RealParameter> anageneticSpeciationRateInput = new Input<>("anageneticSpeciationRate",
            "The rate of anagenetic speciation");

    private static double coalesceInput(Input<RealParameter> input, double defaultValue){
        return input.get() != null ? input.get().getValue() : defaultValue;
    }

    protected void updateParameters(){
        super.updateParameters();
        beta = coalesceInput(symmetricSpeciationProbabilityInput, BETA_DEFAULT);
        lambda_a = coalesceInput(anageneticSpeciationRateInput, LAMBDA_A_DEFAULT);
    }

    @Override
    public double calculateTreeLogLikelihood(TreeInterface tree){
        updateParameters();
        throw new RuntimeException("Not implemented");
    }

    double getC1(){
        return c1;
    }

    double getC2(){
        return c2;
    }

    /*
     * Expose internal constant for testing
     */
    protected double q(double t){
        return q(t, c1, c2);
    }

    protected double log_q(double t){
        return log_q(t, c1, c2);
    }

    protected double p(double t){
        return 1.0  + (-(lambda - mu - psi) + c1 * (Math.exp(-c1 * t) * (1 - c2) - (1 + c2)) / (Math.exp(-c1 * t) * (1 - c2) + (1 + c2))) / (2 * lambda);
    }

    protected double log_p(double t){
        return Math.log(p(t));
    }

    protected double log_q_tilde_asym(double t){
        return 0.5 * (t*(lambda + mu + psi) - log_q(t));
    }

    protected double q_tilde_asym(double t){
        return Math.sqrt(Math.exp(t * (lambda + mu + psi)) * q(t));
    }

}
