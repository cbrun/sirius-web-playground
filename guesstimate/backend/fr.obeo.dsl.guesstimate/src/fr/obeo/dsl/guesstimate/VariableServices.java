package fr.obeo.dsl.guesstimate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import com.google.common.primitives.Doubles;

import fr.obeo.dsl.guesstimate.formula.ArithParser;

public class VariableServices {

	private static final Set<String> SUPPORTED_OPERATORS = Set.of("+", "-", "*", "/", "^");

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
	 * Returns the variables referenced through paths starting with the given operator.
	 *
	 * @param variable the formula variable
	 * @param operator the root operator
	 * @return the referenced variables, in first occurrence order
	 * @since 0.0.7
	 */
	public List<Variable> getReferencedVariablesByOperator(Variable variable, String operator) {
		if (operator != null && SUPPORTED_OPERATORS.contains(operator) && variable.getSettings() instanceof FormulaSetting formulaSetting) {
			Sheet sheet = GuesstimateQueries.getParentSheet(formulaSetting);
			if (sheet != null) {
				Map<String, Variable> available = this.collectAccessibleVariables(sheet);
				return this.collectVariableOperatorPaths(formulaSetting).entrySet().stream()
						.filter(entry -> entry.getValue().stream().anyMatch(path -> this.hasRootOperator(path, operator)))
						.map(Map.Entry::getKey)
						.map(available::get)
						.filter(referencedVariable -> referencedVariable != null)
						.toList();
			}
		}
		return List.of();
	}

	/**
	 * Returns the operator paths connecting a referenced variable to a formula.
	 *
	 * @param variable the formula variable
	 * @param referencedVariable the referenced variable
	 * @param operator the root operator used to select the paths
	 * @return the distinct paths separated by {@code |}, or an empty string
	 * @since 0.0.7
	 */
	public String getOperatorLabel(Variable variable, Variable referencedVariable, String operator) {
		if (operator != null && SUPPORTED_OPERATORS.contains(operator) && referencedVariable != null && referencedVariable.getName() != null
				&& variable.getSettings() instanceof FormulaSetting formulaSetting) {
			Set<String> paths = this.collectVariableOperatorPaths(formulaSetting)
					.getOrDefault(referencedVariable.getName().trim(), Set.of());
			return String.join(" | ", paths.stream()
					.filter(path -> this.hasRootOperator(path, operator))
					.toList());
		}
		return "";
	}

	/**
	 * Returns the variables in dependency-first evaluation order.
	 *
	 * @param sheet the sheet to order
	 * @return all variables in a stable evaluation order
	 * @since 0.0.7
	 */
	public List<Variable> getVariablesInEvaluationOrder(Sheet sheet) {
		List<Variable> orderedVariables = new ArrayList<>();
		Set<Variable> visiting = new HashSet<>();
		Set<Variable> visited = new HashSet<>();
		for (Variable variable : sheet.getVariables()) {
			this.addWithDependencies(variable, visiting, visited, orderedVariables);
		}
		return List.copyOf(orderedVariables);
	}

	/**
	 * Tests whether a formula belongs to or transitively depends on a cycle.
	 *
	 * @param formulaSetting the formula settings to test
	 * @return {@code true} if a dependency cycle is reachable
	 * @since 0.0.7
	 */
	public boolean hasCyclicDependency(FormulaSetting formulaSetting) {
		if (formulaSetting.eContainer() instanceof Variable variable) {
			return this.hasCyclicDependency(variable, new HashSet<>(), new HashSet<>());
		}
		return false;
	}

	private void addWithDependencies(Variable variable, Set<Variable> visiting, Set<Variable> visited,
			List<Variable> orderedVariables) {
		if (visited.contains(variable) || !visiting.add(variable)) {
			return;
		}
		for (Variable dependency : this.getReferencedVariables(variable)) {
			this.addWithDependencies(dependency, visiting, visited, orderedVariables);
		}
		visiting.remove(variable);
		visited.add(variable);
		orderedVariables.add(variable);
	}

	private boolean hasCyclicDependency(Variable variable, Set<Variable> visiting, Set<Variable> visited) {
		if (visiting.contains(variable)) {
			return true;
		}
		if (!visited.add(variable)) {
			return false;
		}
		visiting.add(variable);
		for (Variable dependency : this.getReferencedVariables(variable)) {
			if (this.hasCyclicDependency(dependency, visiting, visited)) {
				visiting.remove(variable);
				return true;
			}
		}
		visiting.remove(variable);
		return false;
	}

	/**
	 * @param sheet
	 * @return
	 */
	public Map<String, Variable> collectAccessibleVariables(Sheet s) {
		Map<String, Variable> available = new LinkedHashMap<>();
		for (Variable d : s.getVariables()) {
			if (d.getName() != null) {
				available.put(d.getName().trim(), d);
			}
		}
		return available;
	}

	private Set<String> collectVariableNamesUsedInFormula(FormulaSetting op) {
		return new LinkedHashSet<>(this.collectVariableOperatorPaths(op).keySet());
	}

	private Map<String, Set<String>> collectVariableOperatorPaths(FormulaSetting formulaSetting) {
		Map<String, Set<String>> paths = new LinkedHashMap<>();
		if (formulaSetting.getFormula() != null) {
			Parser parser = new ArithParser().createParser();
			Result result = parser.parse(formulaSetting.getFormula());
			if (result.isSuccess()) {
				this.collectVariableOperatorPaths(result.get(), "", paths);
			}
		}
		return paths;
	}

	private void collectVariableOperatorPaths(Object expression, String path, Map<String, Set<String>> paths) {
		if (expression instanceof String value && Doubles.tryParse(value) == null) {
			paths.computeIfAbsent(value.trim(), key -> new LinkedHashSet<>()).add(path.isEmpty() ? "+" : path);
		} else if (expression instanceof List<?> parts) {
			if (parts.size() == 3 && Character.valueOf('(').equals(parts.get(0)) && Character.valueOf(')').equals(parts.get(2))) {
				this.collectVariableOperatorPaths(parts.get(1), path, paths);
			} else if (parts.size() == 2 && Character.valueOf('-').equals(parts.get(0))) {
				this.collectVariableOperatorPaths(parts.get(1), this.appendOperator(path, "-"), paths);
			} else if (parts.size() == 3 && parts.get(1) instanceof Character operator) {
				String operatorValue = operator.toString();
				this.collectVariableOperatorPaths(parts.get(0), this.appendOperator(path, this.leftOperator(operatorValue)), paths);
				this.collectVariableOperatorPaths(parts.get(2), this.appendOperator(path, operatorValue), paths);
			}
		}
	}

	private String leftOperator(String operator) {
		return switch (operator) {
			case "+", "-" -> "+";
			case "*", "/" -> "*";
			case "^" -> "^";
			default -> operator;
		};
	}

	private String appendOperator(String path, String operator) {
		return path.isEmpty() ? operator : path + " " + operator;
	}

	private boolean hasRootOperator(String path, String operator) {
		return path.equals(operator) || path.startsWith(operator + " ");
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
