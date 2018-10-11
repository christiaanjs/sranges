package sranges;

import beast.core.Input;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StratigraphicRangeTree extends Tree {

    public Input<List<StratigraphicRange>> rangeInput = new Input<>("ranges",
            "Stratigraphic range information", new ArrayList<>(), Input.Validate.REQUIRED);

    private List<StratigraphicRange> ranges;

    public StratigraphicRangeTree() {
        setInputValue(nodeTypeInput.getName(), StratigraphicRangeNode.class.getName());
    }

    public void initAndValidate(){
        super.initAndValidate();
        initSRanges();
    }

    public void initSRanges(){
        if(rangeInput.get() != null){
            ranges = rangeInput.get();
            Map<String, Node> sampledNodes = getSampledNodesIdMap();
            for(StratigraphicRange range: ranges) {
                Node firstOccurrence = sampledNodes.remove(range.getFirstOccurrenceId());
                if(firstOccurrence != null){
                    // Traverse sampled ancestor then sibling
                    boolean rangeEnded = false;
                    Node next = firstOccurrence; // TODO: Confirm case when root is sampled ancestor
                    while(!rangeEnded){ // TODO: Better abstraction from sampled ancestor representation
                        range.appendNode(next);
                        rangeEnded = range.isSingleFossilRange() ||
                                (next.getID() != null && next.getID().equals(range.getLastOccurrenceId()));
                        if(next.isDirectAncestor()){
                            sampledNodes.remove(next.getID());
                            next = next.getParent().getNonDirectAncestorChild();
                        } else if(next.isFake()){
                            next = next.getDirectAncestorChild();
                        } else { // Speciation event or leaf
                            if(!rangeEnded){
                                throw new StratigraphicRangeException("Range ended before finding last occurrence");
                            } else { // Sampled leaf
                                sampledNodes.remove(next.getID());
                            }
                        }
                    }
                } else {
                    throw new StratigraphicRangeException(String.format("Missing sampled node with ID %s", range.getFirstOccurrenceId()));
                }
            }
            if(!sampledNodes.isEmpty()){
                throw new StratigraphicRangeException(String.format("%d sampled nodes not assigned to ranges", sampledNodes.size()));
            }
        } else {
            throw new StratigraphicRangeException("Stratigraphic range input must be specified");
        }
    }

    public List<StratigraphicRange> getStratigraphicRanges(){
        return ranges;
    }

    private Map<String, Node> getSampledNodesIdMap(){
        return Arrays.stream(getNodesAsArray())
                .filter(n -> n.isLeaf() || n.isDirectAncestor())
                .collect(Collectors.toMap(n -> n.getID(), Function.identity()));
    }



}
