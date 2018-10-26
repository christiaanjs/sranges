package sranges.tree;

import beast.evolution.alignment.Taxon;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import sranges.TestUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class StratigraphicRangeNodeTest {

    private static StratigraphicRangeTree getTree() {
        Taxon aTaxon = new Taxon("A");
        Taxon bTaxon = new Taxon("B");
        Taxon cTaxon = new Taxon("C");
        Taxon dTaxon = new Taxon("D");

        StratigraphicRange multiRange = new StratigraphicRange(bTaxon, aTaxon);
        StratigraphicRange cRange = new StratigraphicRange(cTaxon);
        StratigraphicRange dRange = new StratigraphicRange(dTaxon);
        List<StratigraphicRange> ranges = Arrays.asList(multiRange, cRange, dRange);

        String newick = "(((A:0.5,D:1.0):0.5,B:0.0):0.5,C:2.0)";
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        tree.setInputValue("ranges", ranges);
        tree.initAndValidate();
        return tree;
    }

    private static StratigraphicRangeNode getSpeciationNode(){
        return getTree().getRoot();
    }

    private static StratigraphicRangeNode getLeafNode(){
        return getTree().getSampledNodeById("C");
    }

    private static StratigraphicRangeNode getSANode(){
        return getTree().getSampledNodeById("B");
    }

    private static StratigraphicRangeNode getFakeNode(){
        return (StratigraphicRangeNode) getTree().getSampledNodeById("B").getParent();
    }


    private static StratigraphicRangeNode getRangeSpeciationNode(){
        return (StratigraphicRangeNode) getTree().getSampledNodeById("D").getParent();
    }

    @Test
    public void testSetSymmetricSpeciationSucceeds() {
        StratigraphicRangeNode node = getSpeciationNode();
        node.setSymmetric(true);
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testSetSymmetricLeafFails(){
        StratigraphicRangeNode node = getLeafNode();
        exceptionRule.expect(NodeSymmetryException.class);
        node.setSymmetric(true);
    }

    @Test
    public void testSetSymmetricSAFails(){
        StratigraphicRangeNode node = getSANode();
        exceptionRule.expect(NodeSymmetryException.class);
        node.setSymmetric(true);
    }

    @Test
    public void testSetSymmetricFakeFails(){
        StratigraphicRangeNode node = getFakeNode();
        exceptionRule.expect(NodeSymmetryException.class);
        node.setSymmetric(true);
    }

    @Test
    public void testSetSymmetricRangeSpeciationFails(){
        StratigraphicRangeNode node = getRangeSpeciationNode();
        exceptionRule.expect(NodeSymmetryException.class);
        node.setSymmetric(true);
    }

    @Test
    public void testGetSymmetricSpeciationSucceeds() {
        StratigraphicRangeNode node = getSpeciationNode();
        node.isSymmetric();
    }

    @Test
    public void testGetSymmetricLeafFails(){
        StratigraphicRangeNode node = getLeafNode();
        exceptionRule.expect(NodeSymmetryException.class);
        node.isSymmetric();
    }

    @Test
    public void testGetSymmetricSAFails(){
        StratigraphicRangeNode node = getSANode();
        exceptionRule.expect(NodeSymmetryException.class);
        node.isSymmetric();
    }

    @Test
    public void testGetSymmetricFakeFails(){
        StratigraphicRangeNode node = getFakeNode();
        exceptionRule.expect(NodeSymmetryException.class);
        node.isSymmetric();
    }

    @Test
    public void testGetSymmetricRangeSpeciationFails(){
        StratigraphicRangeNode node = getRangeSpeciationNode();
        exceptionRule.expect(NodeSymmetryException.class);
        node.isSymmetric();
    }

    @Test
    public void testGetRealParentDirectAncestor(){
        StratigraphicRangeNode rangeSpeciation = getRangeSpeciationNode();
        StratigraphicRangeNode b = rangeSpeciation.getTree().getSampledNodeById("B");
        assertEquals(b, rangeSpeciation.getRealParent());
    }

    @Test
    public void testGetRealParentRangeSpeciation(){
        StratigraphicRangeNode rangeSpeciation = getRangeSpeciationNode();
        StratigraphicRangeNode b = rangeSpeciation.getTree().getSampledNodeById("A");
        assertEquals(b.getRealParent(), rangeSpeciation);
    }

    @Test
    public void testGetRealParentSymmetricSpeciation(){
        StratigraphicRangeNode speciation = getSpeciationNode();
        StratigraphicRangeNode b = speciation.getTree().getSampledNodeById("B");
        assertEquals(b.getRealParent(), speciation);
    }

    @Test
    public void testGetRealParentRoot(){
        StratigraphicRangeNode speciation = getSpeciationNode();
        assertNull(speciation.getRealParent());
    }

}