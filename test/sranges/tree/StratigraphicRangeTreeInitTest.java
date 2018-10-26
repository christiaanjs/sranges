package sranges.tree;

import beast.evolution.alignment.Taxon;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import sranges.TestUtil;
import sranges.tree.StratigraphicRange;
import sranges.tree.StratigraphicRangeException;
import sranges.tree.StratigraphicRangeNode;
import sranges.tree.StratigraphicRangeTree;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class StratigraphicRangeTreeInitTest {

    @Test
    public void testTaxonSetInit() {
        String[] taxonIds = new String[]{"A", "B", "C", "D"};
        String newick = String.format("(((%s:1.0,%s:0.0):0.5, %s:0.0):0.5,%s:1.5)", taxonIds);
        List<Taxon> taxa = Arrays.stream(taxonIds).map(i -> new Taxon(i)).collect(Collectors.toList());

        StratigraphicRange multiRange = new StratigraphicRange(taxa.get(2), taxa.get(0));

        StratigraphicRange singleRange = new StratigraphicRange(taxa.get(3));

        List<StratigraphicRange> ranges = Arrays.asList(multiRange, singleRange);

        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);

        tree.setInputValue("ranges", ranges);
        tree.initAndValidate();

        List<StratigraphicRange> initialisedRanges = tree.getStratigraphicRanges();
        Assert.assertEquals(2, initialisedRanges.size());

        List<StratigraphicRange> singleFossilRanges = initialisedRanges.stream()
                .filter(StratigraphicRange::isSingleFossilRange)
                .collect(Collectors.toList());

        List<StratigraphicRange> multiFossilRanges = initialisedRanges.stream()
                .filter(r -> !r.isSingleFossilRange())
                .collect(Collectors.toList());

        Assert.assertEquals(singleFossilRanges.size(), 1);
        Assert.assertEquals(multiFossilRanges.size(), 1);

        List<StratigraphicRangeNode> singleFossilRangeNodes = singleFossilRanges.get(0).getSampledNodes();
        Assert.assertEquals(1, singleFossilRangeNodes.size());
        Assert.assertEquals(singleFossilRangeNodes.get(0).getID(), taxonIds[3]);

        List<StratigraphicRangeNode> multiFossilRangeNodes = multiFossilRanges.get(0).getSampledNodes();
        Assert.assertEquals(3, multiFossilRangeNodes.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(multiFossilRangeNodes.get(i).getID(), taxonIds[2 - i]);
        }
    }

    @Test
    public void testTaxonSetInitRangeSpeciation(){
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

        List<StratigraphicRange> initialisedRanges = tree.getStratigraphicRanges();
        Assert.assertEquals(ranges.size(), initialisedRanges.size());
        long multiRangesCount = initialisedRanges.stream().filter(r -> !r.isSingleFossilRange()).count();
        Assert.assertEquals(1, multiRangesCount);
        Assert.assertTrue("A and D must be siblings", tree.getSampledNodeById(dTaxon.getID()).getParent() == tree.getSampledNodeById(aTaxon.getID()).getParent());
        Assert.assertTrue("The asymmetric event must be in the range", multiRange.getNodes().contains(tree.getSampledNodeById(dTaxon.getID()).getParent()));
    }

    @Test
    @Ignore("Possible future functionality")
    public void testNodeNameInit() {
        String[] taxonIds = new String[]{"A", "B", "C", "D"};

        String[] nodeIdentifiers = new String[taxonIds.length];
        String identifierFormat = "%s[&range=\"%s\"]";

        String multiRangeId = "MultiRange";
        String singleRangeId = "SingleRange";

        for (int i = 0; i < 3; i++) {
            nodeIdentifiers[i] = String.format(identifierFormat, taxonIds[i], multiRangeId);
        }

        nodeIdentifiers[3] = String.format(identifierFormat, taxonIds[3], singleRangeId);

        String newick = String.format("(((%s:1.0,%s:0.0):0.5, %s:0.0):0.5,%s:1.5)", nodeIdentifiers);

        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);

        tree.initAndValidate();

        List<StratigraphicRange> initialisedRanges = tree.getStratigraphicRanges();
        Assert.assertEquals(2, initialisedRanges.size());

        List<StratigraphicRange> singleFossilRanges = initialisedRanges.stream()
                .filter(StratigraphicRange::isSingleFossilRange)
                .collect(Collectors.toList());

        List<StratigraphicRange> multiFossilRanges = initialisedRanges.stream()
                .filter(r -> !r.isSingleFossilRange())
                .collect(Collectors.toList());

        Assert.assertEquals(singleFossilRanges.size(), 1);
        Assert.assertEquals(multiFossilRanges.size(), 1);

        List<StratigraphicRangeNode> singleFossilRangeNodes = singleFossilRanges.get(0).getSampledNodes();
        Assert.assertEquals(1, singleFossilRangeNodes.size());
        Assert.assertEquals(singleFossilRangeNodes.get(0), taxonIds[3]);

        List<StratigraphicRangeNode> multiFossilRangeNodes = multiFossilRanges.get(0).getSampledNodes();
        Assert.assertEquals(3, multiFossilRangeNodes.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(multiFossilRangeNodes.get(i).getID(), taxonIds[i]);
        }
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testInitWithoutRangesFails(){

        String[] taxonIds = new String[]{ "A", "B", "C", "D" };
        String newick = String.format("(((%s:1.0,%s:0.0):0.5, %s:0.0):0.5,%s:1.5)", taxonIds);

        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);

        exceptionRule.expect(StratigraphicRangeException.class);

        tree.initAndValidate();


    }

    @Test
    public void testTaxonSetInitNonLinearFails(){
        String[] taxonIds = new String[]{"A", "B", "C", "D"};
        String newick = String.format("(((%s:1.0,%s:0.0):0.5, %s:0.0):0.5,%s:1.5)", taxonIds);
        List<Taxon> taxa = Arrays.stream(taxonIds).map(i -> new Taxon(i)).collect(Collectors.toList());

        StratigraphicRange multiRange = new StratigraphicRange(taxa.get(3), taxa.get(0));

        StratigraphicRange singleRange = new StratigraphicRange(taxa.get(2));

        List<StratigraphicRange> ranges = Arrays.asList(multiRange, singleRange);

        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);

        tree.setInputValue("ranges", ranges);

        exceptionRule.expect(StratigraphicRangeException.class);

        tree.initAndValidate();
    }

    @Test
    public void testTaxonSetInitOverlappingRangeFails(){
        String[] taxonIds = new String[]{"A", "B", "C"};
        String newick = String.format("((%s:1.0,%s:0.0):0.5, %s:0.0)", taxonIds);
        List<Taxon> taxa = Arrays.stream(taxonIds).map(i -> new Taxon(i)).collect(Collectors.toList());

        StratigraphicRange multiRange = new StratigraphicRange(taxa.get(2), taxa.get(0));

        StratigraphicRange singleRange = new StratigraphicRange(taxa.get(1));

        List<StratigraphicRange> ranges = Arrays.asList(multiRange, singleRange);

        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);

        tree.setInputValue("ranges", ranges);

        exceptionRule.expect(StratigraphicRangeException.class);

        tree.initAndValidate();
    }

    @Test
    public void testTaxonSetInitMissingSampledNodeFails(){
        String[] taxonIds = new String[]{"A", "B", "C", "D"};
        String newick = String.format("(((%s:1.0,%s:0.0):0.5, %s:0.0):0.5,%s:1.5)", taxonIds);
        List<Taxon> taxa = Arrays.stream(taxonIds)
                .limit(3)
                .map(i -> new Taxon(i)).collect(Collectors.toList());

        StratigraphicRange multiRange = new StratigraphicRange(taxa.get(2), taxa.get(0));

        List<StratigraphicRange> ranges = Arrays.asList(multiRange);

        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);

        tree.setInputValue("ranges", ranges);

        exceptionRule.expect(StratigraphicRangeException.class);

        tree.initAndValidate();

    }

}