package sranges.speciation;

import beast.evolution.alignment.Taxon;
import beast.evolution.speciation.BirthDeathGernhard08Model;
import beast.evolution.speciation.SABirthDeathModel;
import beast.evolution.speciation.YuleModel;
import org.junit.Test;
import sranges.TestUtil;
import sranges.tree.StratigraphicRange;
import sranges.tree.StratigraphicRangeTree;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static sranges.speciation.StratigraphicRangeBirthDeathModelTest.*;

public class StratigraphicRangeBirthDeathModelSpecialCaseTest {
    @Test
    public void testSpecialCaseYuleToConstantRoot() {
        StratigraphicRangeTree tree = getUltrametricTree();

        YuleModel yuleModel = new YuleModel();
        yuleModel.setInputValue(yuleModel.birthDiffRateParameterInput.getName(), Double.toString(LAMBDA));
        yuleModel.setInputValue(yuleModel.originHeightParameterInput.getName(), Double.toString(ORIGIN));
        yuleModel.setInputValue(yuleModel.treeInput.getName(), tree);
        yuleModel.initAndValidate();

        StratigraphicRangeBirthDeathModel srModel = getSrModel(ORIGIN, LAMBDA, 0.0, 0.0, 1.0, 0.0, 1.0);
        srModel.setInputValue(srModel.treeInput.getName(), tree);
        srModel.initAndValidate();

        TestUtil.assertEqualToConstantWhenNodeHeightTweaked(yuleModel, srModel, tree, tree.getRoot());
    }

    @Test
    public void testSpecialCaseYuleToConstantNode() {
        StratigraphicRangeTree tree = getUltrametricTree();

        YuleModel yuleModel = new YuleModel();
        yuleModel.setInputValue(yuleModel.birthDiffRateParameterInput.getName(), Double.toString(LAMBDA));
        yuleModel.setInputValue(yuleModel.originHeightParameterInput.getName(), Double.toString(ORIGIN));
        yuleModel.setInputValue(yuleModel.treeInput.getName(), tree);
        yuleModel.initAndValidate();

        StratigraphicRangeBirthDeathModel srModel = getSrModel(ORIGIN, LAMBDA, 0.0, 0.0, 1.0, 0.0, 1.0);
        srModel.setInputValue(srModel.treeInput.getName(), tree);
        srModel.initAndValidate();

        TestUtil.assertEqualToConstantWhenNodeHeightTweaked(yuleModel, srModel, tree, tree.getSampledNodeById("A").getParent());
    }

    private BirthDeathGernhard08Model getBirthDeathModel() {
        BirthDeathGernhard08Model bdModel = new BirthDeathGernhard08Model();
        bdModel.setInputValue(bdModel.birthDiffRateParameterInput.getName(), Double.toString(LAMBDA - MU));
        bdModel.setInputValue(bdModel.relativeDeathRateParameterInput.getName(), Double.toString(MU / LAMBDA));
        bdModel.setInputValue(bdModel.sampleProbabilityInput.getName(), Double.toString(1.0));
        return bdModel;
    }

    private static StratigraphicRangeBirthDeathModel getSrModelBirthDeathSpecialCase() {
        return getSrModel(ORIGIN, LAMBDA, MU, 0.0, 1.0, 0.0, 1.0);
    }

    @Test
    public void testSpecialCaseBirthDeathToConstantRoot() {
        StratigraphicRangeTree tree = getUltrametricTree();

        StratigraphicRangeBirthDeathModel srModel = getSrModelBirthDeathSpecialCase();
        srModel.setInputValue(srModel.treeInput.getName(), tree);
        srModel.initAndValidate();

        BirthDeathGernhard08Model bdModel = getBirthDeathModel();
        bdModel.setInputValue(bdModel.treeInput.getName(), tree);
        bdModel.initAndValidate();

        TestUtil.assertEqualToConstantWhenNodeHeightTweaked(bdModel, srModel, tree, tree.getRoot());
    }

    @Test
    public void testSpecialCaseBirthDeathToConstantNode() {
        StratigraphicRangeTree tree = getUltrametricTree();

        StratigraphicRangeBirthDeathModel srModel = getSrModelBirthDeathSpecialCase();
        srModel.setInputValue(srModel.treeInput.getName(), tree);
        srModel.initAndValidate();

        BirthDeathGernhard08Model bdModel = getBirthDeathModel();
        bdModel.setInputValue(bdModel.treeInput.getName(), tree);
        bdModel.initAndValidate();

        TestUtil.assertEqualToConstantWhenNodeHeightTweaked(bdModel, srModel, tree, tree.getSampledNodeById("A").getParent());
    }

    private BirthDeathGernhard08Model getBirthDeathSamplingModel() {
        BirthDeathGernhard08Model bdModel = new BirthDeathGernhard08Model();
        bdModel.setInputValue(bdModel.birthDiffRateParameterInput.getName(), Double.toString(LAMBDA - MU));
        bdModel.setInputValue(bdModel.relativeDeathRateParameterInput.getName(), Double.toString(MU / LAMBDA));
        bdModel.setInputValue(bdModel.sampleProbabilityInput.getName(), Double.toString(RHO));
        return bdModel;
    }

    private static StratigraphicRangeBirthDeathModel getSrModelBirthDeathSamplingSpecialCase() {
        return getSrModel(ORIGIN, LAMBDA, MU, 0.0, RHO, 0.0, 1.0);
    }

    @Test
    public void testSpecialCaseBirthDeathSamplingToConstantRoot() {
        StratigraphicRangeTree tree = getUltrametricTree();

        StratigraphicRangeBirthDeathModel srModel = getSrModelBirthDeathSamplingSpecialCase();
        srModel.setInputValue(srModel.treeInput.getName(), tree);
        srModel.initAndValidate();

        BirthDeathGernhard08Model bdModel = getBirthDeathSamplingModel();
        bdModel.setInputValue(bdModel.treeInput.getName(), tree);
        bdModel.initAndValidate();

        TestUtil.assertEqualToConstantWhenNodeHeightTweaked(bdModel, srModel, tree, tree.getRoot());
    }

    @Test
    public void testSpecialCaseBirthDeathSamplingToConstantNode() {
        StratigraphicRangeTree tree = getUltrametricTree();

        StratigraphicRangeBirthDeathModel srModel = getSrModelBirthDeathSamplingSpecialCase();
        srModel.setInputValue(srModel.treeInput.getName(), tree);
        srModel.initAndValidate();

        BirthDeathGernhard08Model bdModel = getBirthDeathSamplingModel();
        bdModel.setInputValue(bdModel.treeInput.getName(), tree);
        bdModel.initAndValidate();

        TestUtil.assertEqualToConstantWhenNodeHeightTweaked(bdModel, srModel, tree, tree.getSampledNodeById("A").getParent());
    }

    private static StratigraphicRangeBirthDeathModel getSrModelFBDSpecialCase() {
        return getSrModel(ORIGIN, LAMBDA, MU, PSI, RHO, 0.0, 1.0);
    }


    @Test
    public void testSpecialCaseFBDEqualToConstantTip() {
        String newick = "(((A:0.5,(B:0.5,C:0.0):0.5):0.5,D:0.0):0.5,E:0.0)";
        Taxon aTaxon = new Taxon("A");
        Taxon bTaxon = new Taxon("B");
        Taxon cTaxon = new Taxon("C");
        Taxon dTaxon = new Taxon("D");
        Taxon eTaxon = new Taxon("E");

        StratigraphicRange ancestralRange = new StratigraphicRange(eTaxon, dTaxon);
        StratigraphicRange multiTipRange = new StratigraphicRange(cTaxon, bTaxon);
        StratigraphicRange singleTipRange = new StratigraphicRange(aTaxon);
        List<StratigraphicRange> ranges = Arrays.asList(ancestralRange, multiTipRange, singleTipRange);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        tree.setInputValue(tree.rangeInput.getName(), ranges);
        tree.initAndValidate();
        tree.getSampledNodeById("A").getParent().setSymmetric(true);

        StratigraphicRangeBirthDeathModel srModel = getSrModelFBDSpecialCase();
        srModel.setInputValue(srModel.treeInput.getName(), tree);
        srModel.initAndValidate();

        SABirthDeathModel fbdModel = new SABirthDeathModel();

        fbdModel.setInputValue(fbdModel.originInput.getName(), Double.toString(ORIGIN));
        fbdModel.setInputValue(fbdModel.birthRateInput.getName(), Double.toString(LAMBDA));
        fbdModel.setInputValue(fbdModel.deathRateInput.getName(), Double.toString(MU));
        fbdModel.setInputValue(fbdModel.samplingRateInput.getName(), Double.toString(PSI));
        fbdModel.setInputValue(fbdModel.rhoProbability.getName(), Double.toString(RHO));
        fbdModel.setInputValue(fbdModel.removalProbability.getName(), Double.toString(0.0));
        fbdModel.setInputValue(fbdModel.treeInput.getName(), tree);
        fbdModel.initAndValidate();

        TestUtil.assertEqualToConstantWhenNodeHeightTweaked(fbdModel, srModel, tree, tree.getSampledNodeById("A"));
    }

    @Test
    public void testSpecialCaseFBDEqualToConstantSpeciation() {
        String newick = "(((A:0.5,(B:0.5,C:0.0):0.5):0.5,D:0.0):0.5,E:0.0)";
        Taxon aTaxon = new Taxon("A");
        Taxon bTaxon = new Taxon("B");
        Taxon cTaxon = new Taxon("C");
        Taxon dTaxon = new Taxon("D");
        Taxon eTaxon = new Taxon("E");

        StratigraphicRange ancestralRange = new StratigraphicRange(eTaxon, dTaxon);
        StratigraphicRange multiTipRange = new StratigraphicRange(cTaxon, bTaxon);
        StratigraphicRange singleTipRange = new StratigraphicRange(aTaxon);
        List<StratigraphicRange> ranges = Arrays.asList(ancestralRange, multiTipRange, singleTipRange);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        tree.setInputValue(tree.rangeInput.getName(), ranges);
        tree.initAndValidate();
        tree.getSampledNodeById("A").getParent().setSymmetric(true);

        StratigraphicRangeBirthDeathModel srModel = getSrModelFBDSpecialCase();
        srModel.setInputValue(srModel.treeInput.getName(), tree);
        srModel.initAndValidate();

        SABirthDeathModel fbdModel = new SABirthDeathModel();

        fbdModel.setInputValue(fbdModel.originInput.getName(), Double.toString(ORIGIN));
        fbdModel.setInputValue(fbdModel.birthRateInput.getName(), Double.toString(LAMBDA));
        fbdModel.setInputValue(fbdModel.deathRateInput.getName(), Double.toString(MU));
        fbdModel.setInputValue(fbdModel.samplingRateInput.getName(), Double.toString(PSI));
        fbdModel.setInputValue(fbdModel.rhoProbability.getName(), Double.toString(RHO));
        fbdModel.setInputValue(fbdModel.removalProbability.getName(), Double.toString(0.0));
        fbdModel.setInputValue(fbdModel.treeInput.getName(), tree);
        fbdModel.initAndValidate();

        TestUtil.assertEqualToConstantWhenNodeHeightTweaked(fbdModel, srModel, tree, tree.getSampledNodeById("A").getParent());
    }
}
