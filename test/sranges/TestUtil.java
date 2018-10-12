package sranges;

import beast.util.TreeParser;

public class TestUtil {
    public static StratigraphicRangeTree constructWithNewick(String newick){
        TreeParser parser = new TreeParser(newick);
        StratigraphicRangeTree tree = new StratigraphicRangeTree();
        parser.setInputValue("initial", tree);
        parser.initAndValidate();
        return tree;
    }
}
