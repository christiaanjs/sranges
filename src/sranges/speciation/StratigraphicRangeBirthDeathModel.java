package sranges.speciation;

import beast.core.Input;
import beast.core.parameter.RealParameter;
import beast.evolution.speciation.SABirthDeathModel;
import beast.evolution.tree.TreeInterface;
import sranges.tree.StratigraphicRangeTree;

import java.util.stream.DoubleStream;

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
        StratigraphicRangeTree srTree = (StratigraphicRangeTree) tree;

        double conditionContribution = Math.log(1 - p(origin)); // Condition on sampling

        double eventContribution = srTree.getNodeStream()
                .filter(n -> !n.isFake())
                .mapToDouble(n -> {
                    if(n.isPsiSample()){
                        return psi;
                    } else if(n.isRhoSample()){
                        return rho;
                    } else { // Speciation event
                        return lambda * (n.isSymmetric() ? beta : 1 - beta);
                    }
                })
                .map(x -> Math.log(x))
                .sum();

        double branchContribution = srTree.getNodeStream() // Node above each branch
                .filter(n -> !n.isFake())
                .mapToDouble(n -> {
                    if(n.isRoot()){
                        return log_q(origin) - log_q(n.getHeight()); // Root branch
                    } else {
                        return 0.0; // TODO
                    }
                })
                .sum();

        double rangeContribution = 0.0;

        double unobservedSpeciationContribution = 0.0;

        return conditionContribution +
                eventContribution +
                branchContribution +
                rangeContribution;
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
        return  4.0 / q(t, c1, c2);
    }

    protected double log_q(double t){
        return  Math.log(4.0) - log_q(t, c1, c2);
    }

    protected double p(double t){
        return 1.0  + (-(lambda - mu - psi) + c1 * (Math.exp(-c1 * t) * (1 - c2) - (1 + c2)) / (Math.exp(-c1 * t) * (1 - c2) + (1 + c2))) / (2 * lambda);
    }

    protected double log_p(double t){
        return Math.log(p(t));
    }

    protected double log_q_tilde_asym(double t){
        return 0.5 * (-t*(lambda + mu + psi) + log_q(t));
    }

    protected double q_tilde_asym(double t){
        return Math.sqrt(Math.exp(-t * (lambda + mu + psi)) * q(t));
    }

    protected double q_tilde(double t){
        return Math.exp(-(lambda_a + beta * (lambda + mu + psi)) * t) * Math.pow(q_tilde_asym(t), 1 - beta);
    }

    protected double log_q_tilde(double t){
        return -(lambda_a + beta*(lambda + mu + psi))*t + (1 - beta)*log_q_tilde_asym(t);
    }
}
