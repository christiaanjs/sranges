package sranges.speciation;

import beast.evolution.speciation.SABirthDeathModel;
import beast.evolution.tree.TreeInterface;

public class StratigraphicRangeBirthDeathModel extends SABirthDeathModel {

    @Override
    public double calculateTreeLogLikelihood(TreeInterface tree){
        throw new RuntimeException("Not implemented");
    }
}
