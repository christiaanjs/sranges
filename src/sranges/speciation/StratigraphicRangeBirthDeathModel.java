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

    /**
     * Expose internal constant for testing
     * @param t
     * @return
     */
    protected double q(double t){
        return q(t, c1, c2);
    }
}
