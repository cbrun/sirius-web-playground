package fr.obeo.dsl.guesstimate;

import java.util.Map;
import java.util.Set;

import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fr.obeo.dsl.guesstimate.formula.ArithParser;
import fr.obeo.dsl.guesstimate.formula.ArithmeticVisitor;

public class OperationServices {

	public void setFormula(Operation op, String newValue) {
		Sheet s = getContainingSheet(op);
		Map<String, Variable> available = collectAccessibleVariables(s);
		if (op.getOutput() == null) {
			op.setOutput(GuesstimateFactory.eINSTANCE.createVariable());
		}
		if (op.getOutput().getName() == null) {
			op.getOutput().setName("A" + new Services().getDefaultName(s));
		}
		Set<String> varNames = collectVariableNames(op);
		if (op.getFormula() != null) {
			Set<Variable> referedTo = Sets.newLinkedHashSet();
			Set<Variable> toRemove = Sets.newLinkedHashSet();
			Set<Variable> toAdd = Sets.newLinkedHashSet();
			for (String varName : varNames) {
				varName = varName.trim();
				Variable found = available.get(varName);
				if (found != null) {
					referedTo.add(found);
					if (!op.getInputs().contains(found)) {
						toAdd.add(found);
					}
				}
			}
			for (Variable distribution : op.getInputs()) {
				if (!referedTo.contains(distribution)) {
					toRemove.add(distribution);
				}
			}
			op.getInputs().removeAll(toRemove);
			op.getInputs().addAll(toAdd);
		}
	}

	/**
	 * @param sheet
	 * @return
	 */
	public Map<String, Variable> collectAccessibleVariables(Sheet s) {
		Map<String, Variable> available = Maps.newLinkedHashMap();
		for (Variable d : s.getVariables()) {
			if (d.getName() != null) {
				available.put(d.getName(), d);
			}
		}
		return available;
	}

	private Sheet getContainingSheet(Operation op) {
		return (Sheet) op.eContainer();
	}

	private Set<String> collectVariableNames(Operation op) {
		Set<String> varNames = Sets.newLinkedHashSet();
		if (op.getFormula() != null) {
			String content = op.getFormula();

			Parser p = new ArithParser().createParser();
			Result r = p.parse(content);
			ArithmeticVisitor v = new ArithmeticVisitor() {
				@Override
				public Object caseString(String child) {
					varNames.add(child);
					return child;
				}
			};
			v.visit(r);
		}
		return varNames;
	}

}
