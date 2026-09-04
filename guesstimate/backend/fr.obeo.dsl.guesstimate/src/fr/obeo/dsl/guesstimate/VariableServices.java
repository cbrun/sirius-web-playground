package fr.obeo.dsl.guesstimate;

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

	/**
	 * Returns the variables referenced by a formula variable.
	 *
	 * @param variable the formula variable
	 * @return the referenced variables, in first occurrence order
	 * @since 0.0.7
	 */
	public List<Variable> getReferencedVariables(Variable variable) {
		if (variable.getSettings() instanceof FormulaSetting formulaSetting) {
			Sheet sheet = GuesstimateQueries.getParentSheet(formulaSetting);
			if (sheet != null) {
				Map<String, Variable> available = collectAccessibleVariables(sheet);
				return collectVariableNamesUsedInFormula(formulaSetting).stream()
						.map(available::get)
						.filter(referencedVariable -> referencedVariable != null)
						.toList();
			}
		}
		return List.of();
	}

	/**
	 * @param sheet
	 * @return
	 */
	public Map<String, Variable> collectAccessibleVariables(Sheet s) {
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

	public String getGuideDocumentation(Variable v) {
		String doc = "No doc.";
		if (v.getSettings() != null) {
			doc = EcoreUtil.getDocumentation(v.getSettings().eClass());
		}
		return doc;
	}

	// we should be able to avoid that and just use AQL...
	public List<EEnumLiteral> getTypeEnumCandidates(Variable v) {
		return GuesstimatePackage.eINSTANCE.getVariableType().getELiterals();
	}

	public EEnumLiteral getTypeEnumValue(Variable v) {
		return v.getType() == null ? null : GuesstimatePackage.eINSTANCE.getVariableType().getEEnumLiteralByLiteral(v.getType().getLiteral());
	}

	public VariableType setTypeEnumValue(Variable v, EEnumLiteral value) {
		return VariableType.get(value.getValue());
	}

}
