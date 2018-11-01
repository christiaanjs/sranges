package sranges.tree;

import beast.evolution.alignment.Taxon;
import org.junit.Test;
import sranges.TestUtil;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class StratigraphicRangeTest {

    @Test
    public void testStratigraphicRangeGetStraightLineAncestorRangeRoot() {
        String newick = "((B:1.0,C:1.0):1.0,A:0.0)";
        Taxon aTaxon = new Taxon("A");
        Taxon bTaxon = new Taxon("B");
        Taxon cTaxon = new Taxon("C");
        StratigraphicRange multiRange = new StratigraphicRange(aTaxon, bTaxon);
        StratigraphicRange singleRange = new StratigraphicRange(cTaxon);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        tree.setInputValue(tree.rangeInput.getName(), Arrays.asList(multiRange, singleRange));
        tree.initAndValidate();
        assertNull(multiRange.getStraightLineAncestralRange());
    }

    @Test
    public void testStratigraphicRangeGetStraightLineAncestorAsymmetricRangeSpeciation() {
        String newick = "((B:1.0,(D:1.0,C:0.0):1.0):1.0,A:0.0)";
        Taxon aTaxon = new Taxon("A");
        Taxon bTaxon = new Taxon("B");
        Taxon cTaxon = new Taxon("C");
        Taxon dTaxon = new Taxon("D");
        StratigraphicRange baseRange = new StratigraphicRange(aTaxon, bTaxon);
        StratigraphicRange descendantRange = new StratigraphicRange(cTaxon, dTaxon);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        tree.setInputValue(tree.rangeInput.getName(), Arrays.asList(baseRange, descendantRange));
        tree.initAndValidate();
        assertNull(descendantRange.getStraightLineAncestralRange());
    }

    @Test
    public void testStratigraphicRangeGetStraightLineAncestorAsymmetricNonRangeSpeciationRight() {
        String newick = "((B:1.0,C:1.0):1.0,A:0.0)";
        Taxon aTaxon = new Taxon("A");
        Taxon bTaxon = new Taxon("B");
        Taxon cTaxon = new Taxon("C");
        StratigraphicRange aRange = new StratigraphicRange(aTaxon);
        StratigraphicRange bRange = new StratigraphicRange(bTaxon);
        StratigraphicRange cRange = new StratigraphicRange(cTaxon);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        tree.setInputValue(tree.rangeInput.getName(), Arrays.asList(aRange, bRange, cRange));
        tree.initAndValidate();
        assertNull(cRange.getStraightLineAncestralRange());
    }

    @Test
    public void testStratigraphicRangeGetStraightLineAncestorSymmetricSpeciation() {
        String newick = "((B:1.0,C:1.0):1.0,A:0.0)";
        Taxon aTaxon = new Taxon("A");
        Taxon bTaxon = new Taxon("B");
        Taxon cTaxon = new Taxon("C");
        StratigraphicRange aRange = new StratigraphicRange(aTaxon);
        StratigraphicRange bRange = new StratigraphicRange(bTaxon);
        StratigraphicRange cRange = new StratigraphicRange(cTaxon);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        tree.setInputValue(tree.rangeInput.getName(), Arrays.asList(aRange, bRange, cRange));
        tree.initAndValidate();
        tree.getSampledNodeById("B").getParent().setSymmetric(true);
        assertNull(bRange.getStraightLineAncestralRange());
    }

    @Test
    public void testStratigraphicRangeGetStraightLineAncestorDirect() {
        String newick = "(B:1.0,A:0.0)";
        Taxon aTaxon = new Taxon("A");
        Taxon bTaxon = new Taxon("B");
        StratigraphicRange aRange = new StratigraphicRange(aTaxon);
        StratigraphicRange bRange = new StratigraphicRange(bTaxon);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        tree.setInputValue(tree.rangeInput.getName(), Arrays.asList(aRange, bRange));
        tree.initAndValidate();
        assertEquals(aRange, bRange.getStraightLineAncestralRange());
    }

    @Test
    public void testStratigraphicRangeGetStraightLineAncestorAsymmetricNonRangeSpeciationLeft() {
        String newick = "((B:1.0,C:1.0):1.0,A:0.0)";
        Taxon aTaxon = new Taxon("A");
        Taxon bTaxon = new Taxon("B");
        Taxon cTaxon = new Taxon("C");
        StratigraphicRange aRange = new StratigraphicRange(aTaxon);
        StratigraphicRange bRange = new StratigraphicRange(bTaxon);
        StratigraphicRange cRange = new StratigraphicRange(cTaxon);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        tree.setInputValue(tree.rangeInput.getName(), Arrays.asList(aRange, bRange, cRange));
        tree.initAndValidate();
        assertEquals(aRange, bRange.getStraightLineAncestralRange());
    }

    @Test
    public void testStratigraphicRangeIsTipRangeTrue() {
         String newick = "(((A:1.0,B:0.0):1.0,C:0.0):1.0,D:0.0)";
        Taxon aTaxon = new Taxon("A");
        Taxon bTaxon = new Taxon("B");
        Taxon cTaxon = new Taxon("C");
        Taxon dTaxon = new Taxon("D");
        StratigraphicRange tipRange = new StratigraphicRange(bTaxon, aTaxon);
        StratigraphicRange nonTipRange = new StratigraphicRange(dTaxon, cTaxon);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        tree.setInputValue(tree.rangeInput.getName(), Arrays.asList(tipRange, nonTipRange));
        tree.initAndValidate();
        assertTrue(tipRange.isTipRange());
    }

    @Test
    public void testStratigraphicRangeIsTipRangeFalse(){
        String newick = "(((A:1.0,B:0.0):1.0,C:0.0):1.0,D:0.0)";
        Taxon aTaxon = new Taxon("A");
        Taxon bTaxon = new Taxon("B");
        Taxon cTaxon = new Taxon("C");
        Taxon dTaxon = new Taxon("D");
        StratigraphicRange tipRange = new StratigraphicRange(bTaxon, aTaxon);
        StratigraphicRange nonTipRange = new StratigraphicRange(dTaxon, cTaxon);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        tree.setInputValue(tree.rangeInput.getName(), Arrays.asList(tipRange, nonTipRange));
        tree.initAndValidate();
        assertFalse(nonTipRange.isTipRange());
    }
}