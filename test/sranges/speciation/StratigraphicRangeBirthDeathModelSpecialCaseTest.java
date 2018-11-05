package sranges.speciation;

import beast.evolution.speciation.BirthDeathGernhard08Model;
import beast.evolution.speciation.YuleModel;
import org.junit.Test;
import sranges.TestUtil;
import sranges.tree.StratigraphicRangeTree;

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
    public void testSpecialCaseFBD() {
        fail();
    }
}
