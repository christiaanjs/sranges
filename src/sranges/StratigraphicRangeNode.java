package sranges;

import beast.evolution.tree.Node;

public class StratigraphicRangeNode extends Node {

    private StratigraphicRange range;
    private boolean symmetric;

    public StratigraphicRangeNode(){}


    public boolean possiblySymmetric(){
        return !(isFake() || isDirectAncestor() || isLeaf() || isRangeNode());
    }

    public void setSymmetric(boolean symmetric){
        if(possiblySymmetric()){
            this.symmetric = symmetric;
        } else {
            throw new NodeSymmetryException("Attempt to set symmetry of non-speciation node");
        }
    }

    public boolean getSymmetric(){
        if(possiblySymmetric()){
            return symmetric;
        } else {
            throw new NodeSymmetryException("Attempt to get symmetry of non-speciation node");
        }
    }

    public void setLeft(Node left){
        super.setLeft(left);
        left.setParent(this);
    }

    public void setRight(Node right){
        super.setRight(right); // TODO: Don't allow internal fake nodes to be detached from SA
        right.setParent(this);
    }

    public void setRange(StratigraphicRange range){
        this.range = range;
    }

    public boolean isRangeNode(){
        return range != null;
    }
}
