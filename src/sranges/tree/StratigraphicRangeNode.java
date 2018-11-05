package sranges.tree;

import beast.evolution.tree.Node;

public class StratigraphicRangeNode extends Node {

    private StratigraphicRange range;
    private boolean symmetric;
    private double EPSILON = 1e-12;

    public StratigraphicRangeNode(){}

    public StratigraphicRangeTree getTree(){
        return (StratigraphicRangeTree) m_tree;
    }


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

    public boolean isSymmetric(){
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

    public StratigraphicRangeNode getParent(){
        return (StratigraphicRangeNode) super.getParent();
    }

    public StratigraphicRangeNode getDirectAncestorChild(){
        return (StratigraphicRangeNode) super.getDirectAncestorChild();
    }

    public boolean isRealRoot(){
        return getRealParent() == null;
    }


    public StratigraphicRangeNode getRealParent(){
        if(isRoot()){
            return null;
        } else {
            StratigraphicRangeNode parent = getParent();
            if(isDirectAncestor()){
                return parent.getParent();
            } else if(parent.isFake()){
                return parent.getDirectAncestorChild();
            } else {
                return parent;
            }
        }
    }

    public boolean endsRangeBranch(){
        return !isRealRoot() && !isFake() && range == getRealParent().range;
    }

    public boolean isRangeNode(){
        return range != null;
    }

    public boolean isRhoSample(){
        return isLeaf() && height < EPSILON;
    }

    public boolean isPsiSample(){
        return isLeaf() && !isRhoSample();
    }


    public StratigraphicRange getRange() {
        return range;
    }

    public void setRange(StratigraphicRange range){
        this.range = range;
    }
}
