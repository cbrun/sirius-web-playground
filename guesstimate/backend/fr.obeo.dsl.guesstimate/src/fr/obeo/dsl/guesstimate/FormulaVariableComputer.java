package fr.obeo.dsl.guesstimate;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import fr.obeo.dsl.guesstimate.formula.ArithParser;
import fr.obeo.dsl.guesstimate.formula.ArithmeticVisitor;
import fr.obeo.dsl.guesstimate.simulation.SamplingSimulationAdapter;

public class FormulaVariableComputer {

	private List<String> errors = new ArrayList<>();

	public void compute(Sheet s, Map<String, Variable> variables, Variable var) {
		if (var.getSettings() instanceof FormulaSetting) {
			FormulaSetting data = (FormulaSetting) var.getSettings();
			System.out.println("Computing  op " + data.getFormula() + " " + var.getName());

			if (data.getFormula() != null) {
				Parser p = new ArithParser().createParser();
				Result r = p.parse(data.getFormula());
				ArithmeticVisitor v = new ArithmeticVisitor() {

					@Override
					public Object caseString(String varName) {
						Variable referedVar = variables.get(varName);
						if (referedVar != null && referedVar != var) {
							return SamplingSimulationAdapter.getOrCreate(referedVar).getSampleAsDoubles();
						}
						errors.add("Could not find distribution: " + referedVar);
						return null;
					}

					@Override
					public Object caseNumber(Number child) {
						if (child instanceof Double) {
							double[] vals = new double[s.getSampleSize()];
							for (int i = 0; i < vals.length; i++) {
								vals[i] = Double.valueOf(child.doubleValue());
							}
							return vals;

						}
						if (child instanceof Integer) {
							double[] vals = new double[s.getSampleSize()];
							for (int i = 0; i < vals.length; i++) {
								vals[i] = Integer.valueOf(child.intValue());
							}
							return vals;
						}
						return child;
					}

					@Override
					public Object aggregate(List<Object> results) {
						Deque<double[]> stack = new ArrayDeque<>();
						int o = 0;
						char op = ' ';
						while (o < results.size()) {
							Object cur = results.get(o);
							if (cur instanceof double[]) {
								stack.push((double[]) cur);
							}
							if (cur instanceof Character) {
								op = ((Character) cur).charValue();
							}
							if (stack.size() == 2) {
								// beware op2 is the most recent one.
								double[] op2 = stack.pop();
								double[] op1 = stack.pop();
								double[] r = new double[s.getSampleSize()];
								switch (op) {
								case '+':
									for (int i = 0; i < r.length; i++) {
										r[i] = op1[i] + op2[i];
									}
									stack.push(r);
									break;
								case '-':
									for (int i = 0; i < r.length; i++) {
										r[i] = op1[i] - op2[i];
									}
									stack.push(r);
									break;
								case '/':
									for (int i = 0; i < r.length; i++) {
										r[i] = op1[i] / op2[i];
									}
									stack.push(r);
									break;
								case '*':
									for (int i = 0; i < r.length; i++) {
										r[i] = op1[i] * op2[i];
									}
									stack.push(r);
									break;
								}
							}

							o++;
						}
						// we are done

						if (stack.size() == 1) {
							return stack.pop();
						}
						return super.aggregate(results);
					}

				};
				if (r.isSuccess()) {
					Object table = v.visit(r);
					if (table instanceof double[]) {
						SamplingSimulationAdapter.getOrCreate(var).setSample((double[]) table);
//					op.getOutput().setDescription(GuesstimateUtils.getDefinitionFromSample((double[]) table));
					} else {
						System.err.println("Had no results for : " + data.getFormula());
					}
				} else {
					System.err.println("Error parsing : " + data.getFormula() + " error was :" + r.getMessage());
				}
			}
		}
	}
}
