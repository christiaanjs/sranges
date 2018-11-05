package sranges;

import beast.evolution.speciation.SpeciesTreeDistribution;
import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.util.TreeParser;
import sranges.tree.StratigraphicRangeTree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtil {

    public static final double EPSILON = 1e-8;
    private static final double DELTA = 0.2;

    public static StratigraphicRangeTree constructWithNewick(String newick){
        TreeParser parser = new TreeParser(newick);
        StratigraphicRangeTree tree = new StratigraphicRangeTree();
        parser.setInputValue("initial", tree);
        parser.initAndValidate();
        return tree;
    }

    public static void assertEqualToConstantWhenNodeHeightTweaked(SpeciesTreeDistribution expected, SpeciesTreeDistribution actual, Tree tree, Node node){
        double initExpected = expected.calculateTreeLogLikelihood(tree);
        double initActual = actual.calculateTreeLogLikelihood(tree);

        assertTrue(Double.isFinite(initActual));
        node.setHeight(node.getHeight() + DELTA);
        assertEquals(expected.calculateTreeLogLikelihood(tree) - initExpected, actual.calculateTreeLogLikelihood(tree) - initActual, EPSILON);
    }
}
