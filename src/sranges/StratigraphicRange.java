package sranges;

import beast.core.BEASTObject;
import beast.core.Input;
import beast.evolution.alignment.Taxon;
import beast.evolution.tree.Node;

import java.util.List;

public class StratigraphicRange extends BEASTObject {

    public Input<Taxon> firstOccurrenceInput = new Input<Taxon>("firstOccurrence",
            "Taxon corresponding to earliest sample in the range");

    public Input<Taxon> lastOccurrenceInput = new Input<Taxon>("lastOccurrence",
            "Taxon corresponding to latest sample in the range");

    @Override
    public void initAndValidate() {

    }

    public boolean isSingleFossilRange(){
        return false;
    }

    public List<Node> getSampledNodes(){
        return null;
    }
}
