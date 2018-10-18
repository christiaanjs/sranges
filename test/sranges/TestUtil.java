package sranges;

import beast.util.TreeParser;
import sranges.tree.StratigraphicRangeTree;

public class TestUtil {

    public static final double EPSILON = 1e-8;

    public static StratigraphicRangeTree constructWithNewick(String newick){
        TreeParser parser = new TreeParser(newick);
        StratigraphicRangeTree tree = new StratigraphicRangeTree();
        parser.setInputValue("initial", tree);
        parser.initAndValidate();
        return tree;
    }
}
