package fr.obeo.dsl.guesstimate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Strings;

import fr.obeo.dsl.guesstimate.simulation.SamplingSimulationAdapter;

/**
 * The services class used by VSM.
 */
public class Services {

	public boolean hasValidationError(EObject any) {
		DiagnosticAttachAdapter adapter = DiagnosticAttachAdapter.get(any);
		if (adapter != null && adapter.getDiagnostic().getSeverity() == Diagnostic.ERROR) {
			return true;
		}
		return false;
	}

	public double percentFloat(EObject any, String typed) {
		typed = typed.replace("%", "");
		Double parsed = Double.valueOf(typed);
		if (parsed <= 100) {
			return Math.abs(parsed / 100);
		} else {
			return 1d;
		}
	}

	/**
	 * See
	 * http://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.sirius.doc%2Fdoc%2Findex.html&cp=24
	 * for documentation on how to write service methods.
	 */
	public String getDefaultName(EObject container) {
		int nbDist = ((Sheet) container).getVariables().size();
		char letter = (char) ('A' - 1 + nbDist);
		return "" + letter;
	}

	public EObject smartEdit(EObject cur, String input) {
		if (cur instanceof Operation) {
			Operation o = (Operation) cur;
			o.setFormula(Strings.emptyToNull(input));
		}

		String regex = "(?i)\\bfrom\\b\\s*(\\d+(\\.\\d+)?)\\s*\\bto\\b\\s*(\\d+(\\.\\d+)?)";

		if (cur instanceof Variable) {
			Variable var = (Variable) cur;
			VariableServices service = new VariableServices();
			input = input.trim();
			if (input.startsWith("=")) {
				input = input.substring("=".length());
				// Check if the input is a number (integer or float)
				if (input.matches("^\\d+(\\.\\d+)?$")) {
					service.setTypeOfDistribution(var, VariableType.FORMULA);
					service.setFormula((FormulaSetting) var.getDistribution(), input);
				}
				// Check if the input is a confidence interval (e.g., "40 to 70" or "from 50 to
				// 60")
				//
				// ^(\\d+\\.?\\d*)\\s*(to|from)\\s*(\\d+\\.?\\d*)$

				else if (input.matches(regex)) {
					String[] parts = input.split("\\s*(to|from)\\s*");
					double min = Double.parseDouble(parts[1]);
					double max = Double.parseDouble(parts[2]);
					double mean = (max + min) / 2;
					double stdDev = (max - mean) / 1.645;
					service.setTypeOfDistribution(var, VariableType.NORMAL);
					((NormalDistribution) var.getDistribution()).setMean(mean);
					((NormalDistribution) var.getDistribution()).setSd(stdDev);

				}
				// Check if the input is a proportion (e.g., "1 of 5" or "1 over 5")
				else if (input.matches("^(\\d+)\\s*(of|out of|over)\\s*(\\d+)$")) {
					String[] parts = input.split("\\s*(of|out of|over)\\s*");
					double hits = Double.parseDouble(parts[0]);
					double total = Double.parseDouble(parts[1]);
					service.setTypeOfDistribution(var, VariableType.BETA);
					// simulate(`beta(${2 * hits},${2 * (total - hits)})`, [], n);
					((BetaDistribution) var.getDistribution()).setAlpha(2 * hits);
					((BetaDistribution) var.getDistribution()).setBeta(2 * (total - hits));

				} else {
					service.setTypeOfDistribution(var, VariableType.FORMULA);
					((FormulaSetting) var.getDistribution()).setFormula(input);

				}
			}
			// Check if the input is a valid variable name
			else if (input.matches("^[a-zA-Z_$][a-zA-Z_$0-9]*$")) {
				var.setName(input);
			} else if (input.startsWith(":")) {
				var.setDocumentation(input.substring(":".length()));
			}
			// If none of the above, treat the input as documentation
			else {
				var.setDocumentation(input);
			}
//			if (typedText.contains("to") || typedText.contains("of")) {
//				Double from = null;
//				Double to = null;
//				for (String word : Splitter.on(' ').split(typedText)) {
//					Double v = Doubles.tryParse(word.trim());
//					if (v != null) {
//						if (from == null) {
//							from = v;
//						} else if (to == null) {
//							to = v;
//						}
//					}
//				}
//				if (from != null) {
//					if (to != null) {
//						d.setLower(from);
//						d.setUpper(to);
//					} else {
//						d.setLower(from);
//						d.setUpper(from);
//					}
//				}
//				if (d.getType() != VariableType.NORMAL && typedText.contains("to")) {
//					d.setType(VariableType.NORMAL);
//				}
//				if (typedText.contains("of")) {
//					d.setType(VariableType.BINOMIAL);
//				}
//			} else {
//				Double from = null;
//				for (String word : Splitter.on(' ').split(typedText)) {
//					Double v = Doubles.tryParse(word.trim());
//					if (v != null) {
//						if (from == null) {
//							from = v;
//						}
//					}
//				}
//				d.setLower(from);
//				d.setUpper(from);
//
//			}
		}
		return cur;
	}

	final int BIN_COUNT = 14;

	public List<String> getDensityKeys(EObject d) {
		DecimalFormat fmt = new DecimalFormat("#");
		List<String> r = new ArrayList<>();
		EmpiricalDistribution distribution = new EmpiricalDistribution(BIN_COUNT);
		SamplingSimulationAdapter sample = SamplingSimulationAdapter.getOrCreate(d);
		double[] doubleSample = sample.getSampleAsDoubles();
		if (doubleSample != null) {
			distribution.load(doubleSample);
		}
		for (org.apache.commons.math3.stat.descriptive.SummaryStatistics stats : distribution.getBinStats()) {
			double min = stats.getMin();
			if (!Double.isNaN(min)) {
				r.add(fmt.format(min));
			} else {
				r.add("");
			}
		}
		return r;
	}

	public List<Number> getDensityValues(EObject d) {
		List<Number> r = new ArrayList<>();
		EmpiricalDistribution distribution = new EmpiricalDistribution(BIN_COUNT);
		SamplingSimulationAdapter sample = SamplingSimulationAdapter.getOrCreate(d);
		double[] doubleSample = sample.getSampleAsDoubles();
		if (doubleSample != null) {
			distribution.load(doubleSample);
		}
		for (org.apache.commons.math3.stat.descriptive.SummaryStatistics stats : distribution.getBinStats()) {
			long number = stats.getN();
			r.add(number);
		}

		return r;
	}

	public StatisticalSummary summary(EObject d) {
		EmpiricalDistribution distribution = new EmpiricalDistribution(BIN_COUNT);
		SamplingSimulationAdapter sample = SamplingSimulationAdapter.getOrCreate(d);
		double[] doubleSample = sample.getSampleAsDoubles();
		if (doubleSample == null) {
			doubleSample = new double[0];
		}
		distribution.load(doubleSample);
		return distribution.getSampleStats();
	}

	public String max(StatisticalSummary s) {
		if (s != null) {
			return Double.valueOf(s.getMax()).toString();
		}
		return "";
	}

	public String min(StatisticalSummary s) {
		if (s != null) {
			return Double.valueOf(s.getMin()).toString();
		}
		return "";
	}

	public String sum(StatisticalSummary s) {
		if (s != null) {

			return Double.valueOf(s.getSum()).toString();
		}
		return "";
	}

	public String mean(StatisticalSummary s) {
		if (s != null) {
			return Double.valueOf(s.getMean()).toString();
		}
		return "";
	}

	public String n(StatisticalSummary s) {
		if (s != null) {
			return Double.valueOf(s.getN()).toString();
		}
		return "";
	}

	public String sd(StatisticalSummary s) {
		if (s != null) {
			return Double.valueOf(s.getStandardDeviation()).toString();
		}
		return "";
	}

	public String var(StatisticalSummary s) {
		if (s != null) {
			return Double.valueOf(s.getVariance()).toString();
		}
		return "";
	}

}
