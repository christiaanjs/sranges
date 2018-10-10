package sranges;

import beast.evolution.alignment.Taxon;
import beast.evolution.tree.Node;
import beast.util.TreeParser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

        StratigraphicRangeTree tree = new StratigraphicRangeTree();
        TreeParser parser = new TreeParser(newick);
        parser.setInputValue("initial", tree);
        parser.initAndValidate();

        tree.setInputValue("ranges", ranges);
        tree.initAndValidate();

        List<StratigraphicRange> initialisedRanges = tree.getStratigraphicRanges();
        assertEquals(2, initialisedRanges.size());

        List<StratigraphicRange> singleFossilRanges = initialisedRanges.stream()
                .filter(StratigraphicRange::isSingleFossilRange)
                .collect(Collectors.toList());

        List<StratigraphicRange> multiFossilRanges = initialisedRanges.stream()
                .filter(r -> !r.isSingleFossilRange())
                .collect(Collectors.toList());

        assertEquals(singleFossilRanges.size(), 1);
        assertEquals(multiFossilRanges.size(), 1);

        List<Node> singleFossilRangeNodes = singleFossilRanges.get(0).getSampledNodes();
        assertEquals(1, singleFossilRangeNodes.size());
        assertEquals(singleFossilRangeNodes.get(0).getID(), taxonIds[3]);

        List<Node> multiFossilRangeNodes = multiFossilRanges.get(0).getSampledNodes();
        assertEquals(3, multiFossilRangeNodes.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(multiFossilRangeNodes.get(i).getID(), taxonIds[2 - i]);
        }
    }

    @Test
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

        StratigraphicRangeTree tree = new StratigraphicRangeTree();
        TreeParser parser = new TreeParser(newick);
        parser.setInputValue("initial", tree);
        parser.initAndValidate();

        tree.initAndValidate();

        List<StratigraphicRange> initialisedRanges = tree.getStratigraphicRanges();
        assertEquals(2, initialisedRanges.size());

        List<StratigraphicRange> singleFossilRanges = initialisedRanges.stream()
                .filter(StratigraphicRange::isSingleFossilRange)
                .collect(Collectors.toList());

        List<StratigraphicRange> multiFossilRanges = initialisedRanges.stream()
                .filter(r -> !r.isSingleFossilRange())
                .collect(Collectors.toList());

        assertEquals(singleFossilRanges.size(), 1);
        assertEquals(multiFossilRanges.size(), 1);

        List<Node> singleFossilRangeNodes = singleFossilRanges.get(0).getSampledNodes();
        assertEquals(1, singleFossilRangeNodes.size());
        assertEquals(singleFossilRangeNodes.get(0), taxonIds[3]);

        List<Node> multiFossilRangeNodes = multiFossilRanges.get(0).getSampledNodes();
        assertEquals(3, multiFossilRangeNodes.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(multiFossilRangeNodes.get(i).getID(), taxonIds[i]);
        }
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void testInitWithoutRangesFails(){

        String[] taxonIds = new String[]{ "A", "B", "C", "D" };
        String newick = String.format("(((%s:1.0,%s:0.0):0.5, %s:0.0):0.5,%s:1.5)", taxonIds);

        StratigraphicRangeTree tree = new StratigraphicRangeTree();
        TreeParser parser = new TreeParser(newick);
        parser.setInputValue("initial", tree);
        parser.initAndValidate();

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

        StratigraphicRangeTree tree = new StratigraphicRangeTree();
        TreeParser parser = new TreeParser(newick);
        parser.setInputValue("initial", tree);
        parser.initAndValidate();

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

        StratigraphicRangeTree tree = new StratigraphicRangeTree();
        TreeParser parser = new TreeParser(newick);
        parser.setInputValue("initial", tree);
        parser.initAndValidate();

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

        StratigraphicRangeTree tree = new StratigraphicRangeTree();
        TreeParser parser = new TreeParser(newick);
        parser.setInputValue("initial", tree);
        parser.initAndValidate();

        tree.setInputValue("ranges", ranges);

        exceptionRule.expect(StratigraphicRangeException.class);

        tree.initAndValidate();

    }

}