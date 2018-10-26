package sranges.tree;

import beast.core.BEASTObject;
import beast.core.Input;
import beast.evolution.alignment.Taxon;
import beast.evolution.tree.Node;

import java.util.LinkedList;
import java.util.List;

public class StratigraphicRange extends BEASTObject {

    public Input<Taxon> firstOccurrenceInput = new Input<Taxon>("firstOccurrence",
            "Taxon corresponding to earliest sample in the range",
            Input.Validate.REQUIRED);

    public Input<Taxon> lastOccurrenceInput = new Input<Taxon>("lastOccurrence",
            "Taxon corresponding to latest sample in the range");

    private Taxon firstOccurrence;
    private Taxon lastOccurrence;

    private List<StratigraphicRangeNode> nodes = new LinkedList<>();
    private List<StratigraphicRangeNode> sampledNodes = new LinkedList<>();

    public StratigraphicRange(Taxon firstOccurrence, Taxon lastOccurrence){
        firstOccurrenceInput.setValue(firstOccurrence, this);
        lastOccurrenceInput.setValue(lastOccurrence, this);
        initAndValidate();
    }

    public StratigraphicRange(Taxon firstOccurrence){
        this(firstOccurrence, null);
    }

    public StratigraphicRange(){}

    @Override
    public void initAndValidate() {
        firstOccurrence = firstOccurrenceInput.get();
        lastOccurrence = lastOccurrenceInput.get();
    }

    public boolean isSingleFossilRange(){ // Only call with inputs or after initialisation
        return (firstOccurrence == null && (sampledNodes.size() == 1)) || lastOccurrence == null;
    }

    public List<StratigraphicRangeNode> getSampledNodes(){
        return sampledNodes;
    }

    public List<StratigraphicRangeNode> getNodes() {
        return nodes;
    }

    public String getFirstOccurrenceId(){
        return firstOccurrence.getID();
    }

    public String getLastOccurrenceId(){
        return lastOccurrence != null ? lastOccurrence.getID() : null;
    }

    public void appendNode(Node node){
        if(node instanceof StratigraphicRangeNode){
            StratigraphicRangeNode srNode = (StratigraphicRangeNode) node;
            nodes.add(srNode);
            if(srNode.isDirectAncestor() || srNode.isLeaf()){
                sampledNodes.add(srNode);
            }
            srNode.setRange(this);
        } else {
            throw new IllegalArgumentException("Attempted to add a non-StratigraphicRangeNode to a range");
        }

    }


}
