package sranges.tree;

import beast.core.Input;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            Map<String, StratigraphicRangeNode> sampledNodes = getSampledNodesIdMap();
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
                        } else if(next.isLeaf()){
                            if(!rangeEnded){
                                throw new StratigraphicRangeException("Range ended before finding last occurrence");
                            } else { // Sampled leaf
                                sampledNodes.remove(next.getID());
                            }
                        } else { // Range speciation event
                            next = next.getLeft();
                        }
                    }
                } else {
                    throw new StratigraphicRangeException(String.format("Missing sampled node with ID %s (possibly in two ranges)", range.getFirstOccurrenceId()));
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

    private Map<String, StratigraphicRangeNode> getSampledNodesIdMap(){
        return Arrays.stream(getNodesAsArray())
                .map(n -> (StratigraphicRangeNode) n)
                .filter(n -> n.isLeaf() || n.isDirectAncestor())
                .collect(Collectors.toMap(n -> n.getID(), Function.identity()));
    }

    public StratigraphicRangeNode getSampledNodeById(String id){
        return getSampledNodesIdMap().get(id);
    }

    public StratigraphicRangeNode getRoot(){
        return (StratigraphicRangeNode) root;
    }

    public Stream<StratigraphicRangeNode> getNodeStream(){
        return Arrays.stream(m_nodes).map(n -> (StratigraphicRangeNode) n);
    }

    public List<StratigraphicRangeNode> getNodeList() {
        return getNodeStream().collect(Collectors.toList());
    }
}
