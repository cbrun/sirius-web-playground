package fr.obeo.dsl.guesstimate;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.EmpiricalDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import fr.obeo.dsl.guesstimate.simulation.SamplingSimulationAdapter;

public class GuesstimateUtils {

	private final Logger logger = LoggerFactory.getLogger(GuesstimateUtils.class);

	public void completeModel(Sheet s) {

		Stopwatch sampling = Stopwatch.createStarted();

		for (Variable d : new VariableServices().getVariablesInEvaluationOrder(s)) {
			if (d.getSettings() != null) {
				SamplingSimulationAdapter apacheBridge = SamplingSimulationAdapter.getOrCreate(d);
				apacheBridge.resetApacheStateFromSettings();
			}
		}

		sampling.stop();
		this.logger.info("sampling distributions : " + sampling.elapsed(TimeUnit.MILLISECONDS) + " ms."); //$NON-NLS-1$ //$NON-NLS-2$
		// histogramBins(op.getOut());
	}

	private String getDefinitionFromSample(Variable out) {
		String result = "?";
		SamplingSimulationAdapter sAdapt = SamplingSimulationAdapter.getOrCreate(out);
		double[] sample = sAdapt.getSampleAsDoubles();
		if (sample != null) {
			result = getDefinitionFromSample(sample);
		}
		return result;
	}

	public void histogramBins(Variable d) {
		final int BIN_COUNT = 14;
		System.out.println("~~~~~~~~~~~~~" + d.getName() + "~~~~~~~~~~~~~");
		EmpiricalDistribution distribution = new EmpiricalDistribution(BIN_COUNT);
		SamplingSimulationAdapter sample = SamplingSimulationAdapter.getOrCreate(d);
		double[] doubleSample = sample.getSampleAsDoubles();
		if (doubleSample != null) {
			distribution.load(doubleSample);
		}
		long max = 0;
		for (org.apache.commons.math3.stat.descriptive.SummaryStatistics stats : distribution.getBinStats()) {
			long number = stats.getN();
			if (number > max) {
				max = number;
			}
		}
		int NB_CHAR_LINE = 80;
		long nb_per_char = max / NB_CHAR_LINE;
		for (org.apache.commons.math3.stat.descriptive.SummaryStatistics stats : distribution.getBinStats()) {
			StringBuffer line = new StringBuffer();
			long number = stats.getN();
			for (long i = 0; i < number / nb_per_char; i++) {
				line.append('█');
			}
			System.out.println(stats.getMin() + "-" + stats.getMax() + " \t:" + line.toString() + " " + number);
		}

	}

	public static String getDefinitionFromSample(int[] sample) {
		if (sample.length > 0) {
			int min = sample[0];
			int max = sample[0];
			int sum = 0;
			for (int i : sample) {
				sum += i;
				if (i < min) {
					min = i;
				}
				if (i > max) {
					max = i;
				}
			}
			int average = sum / sample.length;
			return "from " + min + " to " + max + " average " + average;
		}
		return "0";
	}

	public static String getDefinitionFromSample(double[] sample) {
		DecimalFormat fmt = new DecimalFormat("#");
		if (sample.length > 0) {
			double min = sample[0];
			double max = sample[0];
			double sum = 0;
			for (double i : sample) {
				sum += i;
				if (i < min) {
					min = i;
				}
				if (i > max) {
					max = i;
				}
			}

			double average = sum / sample.length;
			return fmt.format(average) + "~" + fmt.format(max) + "~" + fmt.format(min);
		}
		return "0";
	}

	public static String getDefaultName(Sheet container) {
		int nbDist = container.getVariables().size();
		char letter = (char) ('A' - 1 + nbDist);
		return "" + letter;
	}

}
