package fr.obeo.dsl.guesstimate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fr.obeo.dsl.guesstimate.formula.ArithParser;
import fr.obeo.dsl.guesstimate.formula.ArithmeticVisitor;
import fr.obeo.dsl.guesstimate.simulation.SamplingSimulationAdapter;

public class VariableServices {

	public Set<String> collectUnknownVariables(FormulaSetting op) {
		Set<String> unknowns = collectVariableNamesUsedInFormula(op);
		Sheet s = GuesstimateQueries.getParentSheet(op);
		if (s != null) {
			for (String var : collectAccessibleVariables(s).keySet()) {
				unknowns.remove(var);
			}
		}
		return unknowns;
	}

	public String setFormula(FormulaSetting op, String newValue) {
		Sheet s = GuesstimateQueries.getParentSheet(op);
		op.setFormula(newValue);
		Map<String, Variable> available = collectAccessibleVariables(s);
		Set<String> varNamesInFormula = collectVariableNamesUsedInFormula(op);
		if (op.getFormula() != null) {
			Set<Variable> referedTo = Sets.newLinkedHashSet();
			Set<Variable> toRemove = Sets.newLinkedHashSet();
			Set<Variable> toAdd = Sets.newLinkedHashSet();
			for (String varName : varNamesInFormula) {
				varName = varName.trim();
				Variable found = available.get(varName);
				if (found != null) {
					referedTo.add(found);
					if (!op.getInputs().contains(found)) {
						toAdd.add(found);
					}
				}
			}
			for (Variable currentlyReferenced : op.getInputs()) {
				if (!referedTo.contains(currentlyReferenced)) {
					toRemove.add(currentlyReferenced);
				}
			}
			op.getInputs().removeAll(toRemove);
			op.getInputs().addAll(toAdd);
		}
		if (op.eContainer() instanceof Variable) {
			SamplingSimulationAdapter simulatorBridge = SamplingSimulationAdapter.getOrCreate(op.eContainer());
			simulatorBridge.resetApacheStateFromSettings();
			simulatorBridge.resample();
		}
		s.resample();
		return newValue;
	}

	/**
	 * @param sheet
	 * @return
	 */
	private Map<String, Variable> collectAccessibleVariables(Sheet s) {
		Map<String, Variable> available = Maps.newLinkedHashMap();
		for (Variable d : s.getVariables()) {
			if (d.getName() != null) {
				available.put(d.getName().trim(), d);
			}
		}
		return available;
	}

	private Set<String> collectVariableNamesUsedInFormula(FormulaSetting op) {
		Set<String> varNames = Sets.newLinkedHashSet();
		if (op.getFormula() != null) {
			String content = op.getFormula();

			Parser p = new ArithParser().createParser();
			Result r = p.parse(content);
			ArithmeticVisitor v = new ArithmeticVisitor() {
				@Override
				public Object caseString(String child) {
					String child2 = child.trim();
					varNames.add(child2);
					return child2;
				}
			};
			v.visit(r);
		}
		return varNames;
	}

	public Variable setTypeOfDistribution(Variable v, VariableType type) {
		DistributionSetting setting = v.getDistribution();
		switch (type) {
		case BETA:
			setting = GuesstimateFactory.eINSTANCE.createBetaDistribution();
			break;
		case BINOMIAL:
			setting = GuesstimateFactory.eINSTANCE.createBinomialDistribution();
			break;
		case COMPUTED:
			setting = null;
			break;
		case EXPONENTIAL:
			setting = GuesstimateFactory.eINSTANCE.createExponentialDistribution();
			break;
		case GAMMA:
			setting = GuesstimateFactory.eINSTANCE.createGammaDistribution();
			break;
		case LOGNORMAL:
			setting = GuesstimateFactory.eINSTANCE.createLogNormalDistribution();
			break;
		case NORMAL:
			setting = GuesstimateFactory.eINSTANCE.createNormalDistribution();
			break;
		case POISSON:
			setting = GuesstimateFactory.eINSTANCE.createPoissonDistribution();
			break;
		case TRIANGULAR:
			setting = GuesstimateFactory.eINSTANCE.createTriangularDistribution();
			break;
		case UNIFORM:
			setting = GuesstimateFactory.eINSTANCE.createUniformDistribution();
			break;
		case FORMULA:
			setting = GuesstimateFactory.eINSTANCE.createFormulaSetting();
			break;
		default:
			break;
		}
		v.setType(type);
		v.setDistribution(setting);
		return v;
	}

	public String getGuideDocumentation(Variable v) {
		String doc = "No doc.";
		if (v.getDistribution() != null) {
			doc = EcoreUtil.getDocumentation(v.getDistribution().eClass());
		}
		return doc;
	}

	// we should be able to avoid that and just use AQL...
	public List<EEnumLiteral> getTypeEnumCandidates(Variable v) {
		List<EEnumLiteral> result = new ArrayList<>();
		for (EEnumLiteral eEnumLiteral : GuesstimatePackage.eINSTANCE.getVariableType().getELiterals()) {
			if (!eEnumLiteral.getLiteral().equals("computed")) {
				result.add(eEnumLiteral);
			}
		}
		return result;
	}

	public EEnumLiteral getTypeEnumValue(Variable v) {
		return GuesstimatePackage.eINSTANCE.getVariableType().getEEnumLiteralByLiteral(v.getType().getLiteral());
	}

	public VariableType setTypeEnumValue(Variable v, EEnumLiteral value) {
		return VariableType.get(value.getValue());
	}

}
