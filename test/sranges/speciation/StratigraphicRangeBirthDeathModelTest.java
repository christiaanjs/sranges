package sranges.speciation;

import beast.evolution.alignment.Taxon;
import beast.evolution.speciation.BirthDeathGernhard08Model;
import beast.evolution.speciation.YuleModel;
import org.junit.Test;
import sranges.TestUtil;
import sranges.tree.StratigraphicRange;
import sranges.tree.StratigraphicRangeNode;
import sranges.tree.StratigraphicRangeTree;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class StratigraphicRangeBirthDeathModelTest {

    protected static final double ORIGIN = 3.0;
    protected static final double LAMBDA = 1.2;
    protected static final double MU = 0.2;
    protected static final double RHO = 0.7;
    protected static final double PSI = 1.1;
    protected static final double LAMBDA_A = 0.4;
    protected static final double BETA = 0.8;

    protected static StratigraphicRangeBirthDeathModel getSrModel(double origin, double lambda, double mu, double psi, double rho, double lambda_a, double beta) {
        StratigraphicRangeBirthDeathModel srModel = new StratigraphicRangeBirthDeathModel();
        srModel.setInputValue(srModel.originInput.getName(), Double.toString(origin));
        srModel.setInputValue(srModel.birthRateInput.getName(), Double.toString(lambda));
        srModel.setInputValue(srModel.deathRateInput.getName(), Double.toString(mu));
        srModel.setInputValue(srModel.samplingRateInput.getName(), Double.toString(psi));
        srModel.setInputValue(srModel.rhoProbability.getName(), Double.toString(rho));
        srModel.setInputValue(srModel.removalProbability.getName(), "0.0");
        srModel.setInputValue(srModel.anageneticSpeciationRateInput.getName(), Double.toString(lambda_a));
        srModel.setInputValue(srModel.symmetricSpeciationProbabilityInput.getName(), Double.toString(beta));
        return srModel;
    }

    protected static StratigraphicRangeTree getSingleSampleRangeTree(String newickFormat, String[] taxonIds) {
        String newick = String.format(newickFormat, taxonIds);
        StratigraphicRangeTree tree = TestUtil.constructWithNewick(newick);
        List<StratigraphicRange> ranges = Arrays.stream(taxonIds)
                .map(i -> new StratigraphicRange(new Taxon(i)))
                .collect(Collectors.toList());
        tree.setInputValue(tree.rangeInput.getName(), ranges);
        tree.initAndValidate();
        tree.getNodeStream().filter(StratigraphicRangeNode::possiblySymmetric).forEach(n  -> n.setSymmetric(true));
        return tree;
    }

    protected static StratigraphicRangeTree getUltrametricTree() {
        String[] taxonIds = new String[]{"A", "B", "C"};
        String newickFormat = "((%s:1.0,%s:1.0):1.5, %s:2.5)";
        StratigraphicRangeTree tree = getSingleSampleRangeTree(newickFormat, taxonIds);
        return tree;
    }


    // Variable value tests


    private static StratigraphicRangeBirthDeathModel getSrModelMixedMode() {
        return getSrModel(ORIGIN, LAMBDA, MU, PSI, RHO, LAMBDA_A, BETA);
    }

    private static double qNotated(double t, double c1, double c2) {
        return 4.0 * Math.exp(-c1 * t) /
                Math.pow(Math.exp(-c1 * t) * (1.0 - c2) + (1.0 + c2), 2.0);
    }

    // Derivation uses q(t) = 4*exp(-c1*t)/(exp(-c1*t)*(1-c2)+(1+c2))^2
    // sampled-ancestor implementation uses reciprocal
    @Test
    public void testQNotated() {
        double t = 1.3;

        StratigraphicRangeBirthDeathModel model = getSrModelMixedMode();
        StratigraphicRangeTree tree = getUltrametricTree();
        model.setInputValue(model.treeInput.getName(), tree);
        model.initAndValidate();
        model.updateParameters();

        double c1 = model.getC1();
        double c2 = model.getC2();

        assertEquals(qNotated(t, c1, c2), model.q(t), TestUtil.EPSILON);
    }

    @Test
    public void testLogP() {
        double t = 1.3;

        StratigraphicRangeBirthDeathModel model = getSrModelMixedMode();
        StratigraphicRangeTree tree = getUltrametricTree();
        model.setInputValue(model.treeInput.getName(), tree);
        model.initAndValidate();
        model.updateParameters();

        assertEquals(Math.log(model.p(t)), model.log_p(t), TestUtil.EPSILON);
    }

    @Test
    public void testLogQTilde_asym() {
        double t = 1.3;

        StratigraphicRangeBirthDeathModel model = getSrModelMixedMode();
        StratigraphicRangeTree tree = getUltrametricTree();
        model.setInputValue(model.treeInput.getName(), tree);
        model.initAndValidate();
        model.updateParameters();

        assertEquals(Math.log(model.q_tilde_asym(t)), model.log_q_tilde_asym(t), TestUtil.EPSILON);
    }

    private static double qTilde_asymNotated(double t, double c1, double c2, double lambda, double mu, double psi) {
        return Math.sqrt(Math.exp(-t * (lambda + mu + psi)) * qNotated(t, c1, c2));
    }

    @Test
    public void testQTilde_asymNotated() {
        double t = 1.3;

        StratigraphicRangeBirthDeathModel model = getSrModelMixedMode();
        StratigraphicRangeTree tree = getUltrametricTree();
        model.setInputValue(model.treeInput.getName(), tree);
        model.initAndValidate();
        model.updateParameters();

        double c1 = model.getC1();
        double c2 = model.getC2();

        assertEquals(qTilde_asymNotated(t, c1, c2, PSI, LAMBDA, MU), model.q_tilde_asym(t), TestUtil.EPSILON);

    }

    @Test
    public void testLogQTilde() {
        double t = 1.3;

        StratigraphicRangeBirthDeathModel model = getSrModelMixedMode();
        StratigraphicRangeTree tree = getUltrametricTree();
        model.setInputValue(model.treeInput.getName(), tree);
        model.initAndValidate();
        model.updateParameters();

        assertEquals(Math.log(model.q_tilde(t)), model.log_q_tilde(t), TestUtil.EPSILON);

    }

    private static double qTildeNotated(double t, double c1, double c2, double lambda, double mu, double psi, double lambda_a, double beta) {
        return Math.exp(-(lambda_a + beta * (lambda + mu + psi)) * t) * Math.pow(qTilde_asymNotated(t, c1, c2, lambda, mu, psi), 1 - beta);
    }

    @Test
    public void testQTildeNotated() {
        double t = 1.3;

        StratigraphicRangeBirthDeathModel model = getSrModelMixedMode();
        StratigraphicRangeTree tree = getUltrametricTree();
        model.setInputValue(model.treeInput.getName(), tree);
        model.initAndValidate();
        model.updateParameters();

        double c1 = model.getC1();
        double c2 = model.getC2();

        assertEquals(qTildeNotated(t, c1, c2, LAMBDA, MU, PSI, LAMBDA_A, BETA), model.q_tilde(t), TestUtil.EPSILON);

    }

    @Test
    public void testQTildeSpecialCaseAsym() {
        double t = 1.3;

        StratigraphicRangeBirthDeathModel model = getSrModel(ORIGIN, LAMBDA, MU, PSI, RHO, 0, 0.0);
        StratigraphicRangeTree tree = getUltrametricTree();
        model.setInputValue(model.treeInput.getName(), tree);
        model.initAndValidate();
        model.updateParameters();

        assertEquals(model.q_tilde_asym(t), model.q_tilde(t), TestUtil.EPSILON);

    }
}