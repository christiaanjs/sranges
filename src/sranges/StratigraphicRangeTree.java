package sranges;

import beast.core.Input;
import beast.evolution.tree.Tree;

import java.util.ArrayList;
import java.util.List;

public class StratigraphicRangeTree extends Tree {

    public Input<List<StratigraphicRange>> rangeInput = new Input<>("ranges",
            "Stratigraphic range information", new ArrayList<>());

    private List<StratigraphicRange> ranges;

    public void initAndValidate(){
        super.initAndValidate();
        initSRanges();
    }

    public void initSRanges(){
        if(rangeInput.get() != null){

        }
    }

    public List<StratigraphicRange> getStratigraphicRanges(){
        return ranges;
    }

}
