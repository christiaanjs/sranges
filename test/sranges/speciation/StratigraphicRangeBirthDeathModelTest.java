package sranges.speciation;

import beast.evolution.alignment.Taxon;
import beast.evolution.speciation.BirthDeathGernhard08Model;
import beast.evolution.speciation.YuleModel;
import org.junit.Test;
import sranges.TestUtil;
import sranges.tree.StratigraphicRange;
import sranges.tree.StratigraphicRangeTree;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class StratigraphicRangeBirthDeathModelTest {

    private static final double ORIGIN = 3.0;
    private static final double LAMBDA = 1.0;
    private static final double MU = 1.0;
    private static final double RHO = 0.7;
    private static final double PSI = 0.8;

    private StratigraphicRangeBirthDeathModel getSrModel(double origin, double lambda, double mu, double psi, double rho){
        StratigraphicRangeBirthDeathModel srModel = new StratigraphicRangeBirthDeathModel();
        srModel.setInputValue(srModel.originInput.getName(), Double.toString(origin));
        srModel.setInputValue(srModel.birthRateInput.getName(), Double.toString(lambda));
        srModel.setInputValue(srModel.deathRateInput.getName(), Double.toString(mu));
        srModel.setInputValue(srModel.samplingRateInput.getName(), Double.toString(psi));
        srModel.setInputValue(srModel.samplingProportionInput.getName(), Double.toString(rho));
        srModel.setInputValue(srModel.removalProbability.getName(), "0.0");
        return srModel;
    }

    private StratigraphicRangeTree getSingleSampleRangeTree(String newickFormat, String[] taxonIds){
        String newick = String.format(newickFormat, taxonIds);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        List<StratigraphicRange> ranges = Arrays.stream(taxonIds)
                .map(i -> new StratigraphicRange(new Taxon(i)))
                .collect(Collectors.toList());
        tree.setInputValue(tree.rangeInput.getName(), ranges);
        return tree;
    }

    private StratigraphicRangeTree getUltrametricTree(){
        String[] taxonIds = new String[]{"A", "B", "C"};
        String newickFormat = "((%s:1.0,%s:1.0):1.5, %s:2.5)";
        StratigraphicRangeTree tree = getSingleSampleRangeTree(newickFormat, taxonIds);
        return tree;
    }

    @Test
    public void testSpecialCaseYuleConditionOnOrigin(){
        StratigraphicRangeTree tree = getUltrametricTree();
        tree.initAndValidate();

        YuleModel yuleModel = new YuleModel();
        yuleModel.setInputValue(yuleModel.birthDiffRateParameterInput.getName(), Double.toString(LAMBDA));
        yuleModel.setInputValue(yuleModel.originHeightParameterInput.getName(), Double.toString(ORIGIN));
        yuleModel.setInputValue(yuleModel.treeInput.getName(), tree);
        yuleModel.initAndValidate();

        StratigraphicRangeBirthDeathModel srModel = getSrModel(ORIGIN, LAMBDA, 0.0, 0.0, 1.0);
        srModel.setInputValue(srModel.treeInput.getName(), tree);
        srModel.initAndValidate();

        assertEquals(yuleModel.calculateTreeLogLikelihood(tree), srModel.calculateTreeLogLikelihood(tree), TestUtil.EPSILON);
    }

    private BirthDeathGernhard08Model getBirthDeathSamplingModel(){
        BirthDeathGernhard08Model bdModel = new BirthDeathGernhard08Model();
        bdModel.setInputValue(bdModel.birthDiffRateParameterInput.getName(), Double.toString(LAMBDA - MU));
        bdModel.setInputValue(bdModel.relativeDeathRateParameterInput.getName(), Double.toString(MU / LAMBDA));
        bdModel.setInputValue(bdModel.sampleProbabilityInput.getName(), Double.toString(RHO));
        return bdModel;
    }

    private StratigraphicRangeBirthDeathModel getSrModelBirthDeathSamplingSpecialCase(){
        return getSrModel(ORIGIN, LAMBDA, MU, 0.0, RHO);
    }

    @Test
    public void testSpecialCaseBirthDeathSamplingConditionOnOrigin(){
        StratigraphicRangeTree tree = getUltrametricTree();
        tree.initAndValidate();

        StratigraphicRangeBirthDeathModel srModel = getSrModelBirthDeathSamplingSpecialCase();
        srModel.setInputValue(srModel.treeInput.getName(), tree);

        BirthDeathGernhard08Model bdModel = getBirthDeathSamplingModel();
        bdModel.setInputValue(bdModel.treeInput.getName(), tree);

        assertEquals(bdModel.calculateTreeLogLikelihood(tree), srModel.calculateTreeLogLikelihood(tree), TestUtil.EPSILON);
    }

    private static double qNotated(double t, double c1, double c2){
        return 4.0 * Math.exp(-c1 * t)/
                Math.pow(Math.exp(-c1 * t) * (1.0 - c2) + (1.0 + c2) , 2.0);
    }

    // Derivation uses q(t) = 4*exp(-c1*t)/(exp(-c1*t)*(1-c2)+(1+c2))^2
    // sampled-ancestor implementation uses reciprocal
    @Test
    public void testQReciprocalToNotated(){
        double t = 1.3;

        StratigraphicRangeBirthDeathModel model = getSrModelBirthDeathSamplingSpecialCase();
        StratigraphicRangeTree tree = getUltrametricTree();
        model.setInputValue(model.treeInput.getName(), tree);
        model.initAndValidate();
        model.updateParameters();

        double c1 = model.getC1();
        double c2 = model.getC2();

        assertEquals(1.0/qNotated(t, c1, c2), model.q(t), TestUtil.EPSILON);
    }

    // In case they are implemented separately
    @Test
    public void testLogP(){
        double t = 1.3;

        StratigraphicRangeBirthDeathModel model = getSrModelBirthDeathSamplingSpecialCase();
        StratigraphicRangeTree tree = getUltrametricTree();
        model.setInputValue(model.treeInput.getName(), tree);
        model.initAndValidate();
        model.updateParameters();

        assertEquals(Math.log(model.p(t)), model.log_p(t), TestUtil.EPSILON);
    }

    @Test
    public void testLogQTilde_asym(){
        double t = 1.3;

        StratigraphicRangeBirthDeathModel model = getSrModelBirthDeathSamplingSpecialCase();
        StratigraphicRangeTree tree = getUltrametricTree();
        model.setInputValue(model.treeInput.getName(), tree);
        model.initAndValidate();
        model.updateParameters();

        assertEquals(Math.log(model.q_tilde_asym(t)), model.log_q_tilde_asym(t), TestUtil.EPSILON);
    }

    private static double qTilde_asymNotated(double t, double lambda, double mu, double psi, double c1, double c2){
        return Math.sqrt(Math.exp(-t*(lambda + mu + psi))*qNotated(t, c1, c2));
    }

    @Test
    public void testQTilde_asymReciprocalToNotated(){
        double t = 1.3;

        StratigraphicRangeBirthDeathModel model = getSrModelBirthDeathSamplingSpecialCase();
        StratigraphicRangeTree tree = getUltrametricTree();
        model.setInputValue(model.treeInput.getName(), tree);
        model.initAndValidate();
        model.updateParameters();

        double c1 = model.getC1();
        double c2 = model.getC2();

        assertEquals(1.0/qTilde_asymNotated(t, c1, c2, PSI, LAMBDA, MU), model.q_tilde_asym(t), TestUtil.EPSILON);

    }

}