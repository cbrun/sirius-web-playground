package fr.obeo.dsl.guesstimate.simulation;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterators;

import fr.obeo.dsl.guesstimate.BetaDistribution;
import fr.obeo.dsl.guesstimate.BinomialDistribution;
import fr.obeo.dsl.guesstimate.ExponentialDistribution;
import fr.obeo.dsl.guesstimate.FormulaVariableComputer;
import fr.obeo.dsl.guesstimate.GammaDistribution;
import fr.obeo.dsl.guesstimate.GuesstimatePackage;
import fr.obeo.dsl.guesstimate.GuesstimateQueries;
import fr.obeo.dsl.guesstimate.LogNormalDistribution;
import fr.obeo.dsl.guesstimate.NormalDistribution;
import fr.obeo.dsl.guesstimate.OperationServices;
import fr.obeo.dsl.guesstimate.PoissonDistribution;
import fr.obeo.dsl.guesstimate.Sheet;
import fr.obeo.dsl.guesstimate.UniformDistribution;
import fr.obeo.dsl.guesstimate.Variable;
import fr.obeo.dsl.guesstimate.VariableType;
import fr.obeo.dsl.guesstimate.util.GuesstimateSwitch;

public class SamplingSimulationAdapter extends AdapterImpl {

	private int[] sample = null;
	private double[] doubleSample = null;
	private Object distributionSimulator = null;

	private void setDistributionSimulator(Object realOrIntegerDistributionFromApache) {
		this.distributionSimulator = realOrIntegerDistributionFromApache;
		sample = null;
		doubleSample = null;
	}

	@Override
	public void notifyChanged(Notification msg) {
		if (!msg.isTouch() && msg.getFeature() == GuesstimatePackage.eINSTANCE.getVariable_Distribution()) {
			resetApacheStateFromSettings();
		}
		resample();
	}

	/**
	 * 
	 */
	public void resample() {
		EObject target = (EObject) this.getTarget();
		Sheet parentSheet = GuesstimateQueries.getParentSheet(target);
		if (parentSheet != null) {
			if (distributionSimulator instanceof RealDistribution) {
				doubleSample = ((RealDistribution) distributionSimulator).sample(parentSheet.getSampleSize());
			} else if (distributionSimulator instanceof IntegerDistribution) {
				sample = ((IntegerDistribution) distributionSimulator).sample(parentSheet.getSampleSize());
			} else if (distributionSimulator instanceof FormulaVariableComputer) {
				OperationServices service = new OperationServices();
				var available = service.collectAccessibleVariables(parentSheet);
				((FormulaVariableComputer) distributionSimulator).compute(parentSheet, available, (Variable) target);
			}
		}
	}

	private void setTheEvaluationImplementation(final Variable d) {
		GuesstimateSwitch<Object> dispatcherForApacheDistributions = new GuesstimateSwitch<>() {

			@Override
			public Object caseUniformDistribution(UniformDistribution setting) {
				var apacheDist = new UniformRealDistribution(setting.getMin(), setting.getMax());
				return apacheDist;
			}

			@Override
			public Object casePoissonDistribution(PoissonDistribution setting) {
				var apacheDist = new org.apache.commons.math3.distribution.PoissonDistribution(setting.getP(),
						setting.getEpsilon());
				return apacheDist;
			}

			@Override
			public Object caseNormalDistribution(NormalDistribution setting) {
				var apacheDist = new org.apache.commons.math3.distribution.NormalDistribution(setting.getMean(),
						setting.getSd());
				return apacheDist;
			}

			@Override
			public Object caseTriangularDistribution(fr.obeo.dsl.guesstimate.TriangularDistribution setting) {
				var apacheDist = new TriangularDistribution(setting.getMin(), setting.getMode(), setting.getMax());
				return apacheDist;
			}

			@Override
			public Object caseGammaDistribution(GammaDistribution setting) {
				var apacheDist = new org.apache.commons.math3.distribution.GammaDistribution(setting.getShape(),
						setting.getScale());
				return apacheDist;
			}

			@Override
			public Object caseBetaDistribution(BetaDistribution setting) {
				var apacheDist = new org.apache.commons.math3.distribution.BetaDistribution(setting.getAlpha(),
						setting.getBeta());
				return apacheDist;
			}

			@Override
			public Object caseLogNormalDistribution(LogNormalDistribution setting) {
				var apacheDist = new org.apache.commons.math3.distribution.LogNormalDistribution(setting.getScale(),
						setting.getShape());
				return apacheDist;
			}

			@Override
			public Object caseExponentialDistribution(ExponentialDistribution setting) {
				var apacheDist = new org.apache.commons.math3.distribution.ExponentialDistribution(setting.getMean());
				return apacheDist;
			}

			@Override
			public Object caseBinomialDistribution(BinomialDistribution setting) {
				var apacheDist = new org.apache.commons.math3.distribution.BinomialDistribution(setting.getTrials(),
						setting.getP());
				return apacheDist;
			}

		};
		if (d.getType() == VariableType.FORMULA) {
			Object formulaImplementation = new FormulaVariableComputer();
			this.setDistributionSimulator(formulaImplementation);
		} else if (d.getDistribution() != null) {
			try {
				Object apacheImplementation = dispatcherForApacheDistributions.doSwitch(d.getDistribution());
				this.setDistributionSimulator(apacheImplementation);
			} catch (Exception e) {
				System.err.println("ERROR" + e.getMessage());
			}
		}
	}

	/**
	 * @param sample the sample to set
	 */
	public void setSample(int[] sample) {
		this.sample = sample;
	}

	/**
	 * @param sample the sample to set
	 */
	public void setSample(double[] sample) {
		this.doubleSample = sample;
	}

	/**
	 * @return the doubleSample
	 */
	public double[] getDoubleSample() {
		return doubleSample;
	}

	public static SamplingSimulationAdapter getOrCreate(EObject eObj) {
		Iterator<SamplingSimulationAdapter> it = Iterators.filter(eObj.eAdapters().iterator(),
				SamplingSimulationAdapter.class);
		if (it.hasNext()) {
			return it.next();
		}
		SamplingSimulationAdapter newOne = new SamplingSimulationAdapter();
		eObj.eAdapters().add(newOne);
		return newOne;
	}

	public double[] getSampleAsDoubles() {
		if (this.doubleSample != null) {
			return doubleSample;
		} else if (sample != null) {
			return Arrays.stream(sample).asDoubleStream().toArray();
		}
		return null;
	}

	public void resetApacheStateFromSettings() {
		Variable host = (Variable) this.getTarget();
		setTheEvaluationImplementation(host);
		resample();
	}
}
