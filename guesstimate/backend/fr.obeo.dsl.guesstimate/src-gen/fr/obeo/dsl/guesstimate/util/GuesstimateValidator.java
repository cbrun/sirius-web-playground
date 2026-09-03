/**
 */
package fr.obeo.dsl.guesstimate.util;

import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.petitparser.context.Result;
import org.petitparser.parser.Parser;

import com.google.common.base.Strings;

import fr.obeo.dsl.guesstimate.*;
import fr.obeo.dsl.guesstimate.BetaDistribution;
import fr.obeo.dsl.guesstimate.BinomialDistribution;
import fr.obeo.dsl.guesstimate.DistributionSetting;
import fr.obeo.dsl.guesstimate.ExponentialDistribution;
import fr.obeo.dsl.guesstimate.FormulaSetting;
import fr.obeo.dsl.guesstimate.GammaDistribution;
import fr.obeo.dsl.guesstimate.GuesstimateEMFPlugin;
import fr.obeo.dsl.guesstimate.GuesstimatePackage;
import fr.obeo.dsl.guesstimate.LogNormalDistribution;
import fr.obeo.dsl.guesstimate.NormalDistribution;
import fr.obeo.dsl.guesstimate.PoissonDistribution;
import fr.obeo.dsl.guesstimate.Sheet;
import fr.obeo.dsl.guesstimate.TriangularDistribution;
import fr.obeo.dsl.guesstimate.UniformDistribution;
import fr.obeo.dsl.guesstimate.Variable;
import fr.obeo.dsl.guesstimate.VariableServices;
import fr.obeo.dsl.guesstimate.VariableType;
import fr.obeo.dsl.guesstimate.formula.ArithParser;

/**
 * <!-- begin-user-doc --> The <b>Validator</b> for the model. <!-- end-user-doc
 * -->
 * 
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage
 * @generated
 */
public class GuesstimateValidator extends EObjectValidator {
	/**
	 * The cached model package
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public static final GuesstimateValidator INSTANCE = new GuesstimateValidator();

	/**
	 * A constant for the {@link org.eclipse.emf.common.util.Diagnostic#getSource() source} of diagnostic {@link org.eclipse.emf.common.util.Diagnostic#getCode() codes} from this package.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see org.eclipse.emf.common.util.Diagnostic#getSource()
	 * @see org.eclipse.emf.common.util.Diagnostic#getCode()
	 * @generated
	 */
	public static final String DIAGNOSTIC_SOURCE = "fr.obeo.dsl.guesstimate";

	/**
	 * A constant with a fixed name that can be used as the base value for
	 * additional hand written constants. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	private static final int GENERATED_DIAGNOSTIC_CODE_COUNT = 0;

	/**
	 * A constant with a fixed name that can be used as the base value for additional hand written constants in a derived class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static final int DIAGNOSTIC_CODE_COUNT = GENERATED_DIAGNOSTIC_CODE_COUNT;

	/**
	 * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	public GuesstimateValidator() {
		super();
	}

	/**
	 * Returns the package of this validator switch.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	@Override
	protected EPackage getEPackage() {
	  return GuesstimatePackage.eINSTANCE;
	}

	/**
	 * Calls <code>validateXXX</code> for the corresponding classifier of the model.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected boolean validate(int classifierID, Object value, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		switch (classifierID) {
			case GuesstimatePackage.VARIABLE:
				return validateVariable((Variable)value, diagnostics, context);
			case GuesstimatePackage.DISTRIBUTION_SETTING:
				return validateDistributionSetting((DistributionSetting)value, diagnostics, context);
			case GuesstimatePackage.NORMAL_DISTRIBUTION:
				return validateNormalDistribution((NormalDistribution)value, diagnostics, context);
			case GuesstimatePackage.LOG_NORMAL_DISTRIBUTION:
				return validateLogNormalDistribution((LogNormalDistribution)value, diagnostics, context);
			case GuesstimatePackage.UNIFORM_DISTRIBUTION:
				return validateUniformDistribution((UniformDistribution)value, diagnostics, context);
			case GuesstimatePackage.BETA_DISTRIBUTION:
				return validateBetaDistribution((BetaDistribution)value, diagnostics, context);
			case GuesstimatePackage.TRIANGULAR_DISTRIBUTION:
				return validateTriangularDistribution((TriangularDistribution)value, diagnostics, context);
			case GuesstimatePackage.BINOMIAL_DISTRIBUTION:
				return validateBinomialDistribution((BinomialDistribution)value, diagnostics, context);
			case GuesstimatePackage.FORMULA_SETTING:
				return validateFormulaSetting((FormulaSetting)value, diagnostics, context);
			case GuesstimatePackage.SHEET:
				return validateSheet((Sheet)value, diagnostics, context);
			case GuesstimatePackage.POISSON_DISTRIBUTION:
				return validatePoissonDistribution((PoissonDistribution)value, diagnostics, context);
			case GuesstimatePackage.EXPONENTIAL_DISTRIBUTION:
				return validateExponentialDistribution((ExponentialDistribution)value, diagnostics, context);
			case GuesstimatePackage.GAMMA_DISTRIBUTION:
				return validateGammaDistribution((GammaDistribution)value, diagnostics, context);
			case GuesstimatePackage.VARIABLE_TYPE:
				return validateVariableType((VariableType)value, diagnostics, context);
			case GuesstimatePackage.PROBABILITY:
				return validateProbability((Double)value, diagnostics, context);
			default:
				return true;
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateVariable(Variable variable, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!validate_NoCircularContainment(variable, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(variable, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(variable, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(variable, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(variable, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(variable, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(variable, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(variable, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(variable, diagnostics, context);
		if (result || diagnostics != null) result &= validateVariable_settingsAreValid(variable, diagnostics, context);
		if (result || diagnostics != null) result &= validateVariable_nameIsValid(variable, diagnostics, context);
		return result;
	}

	/**
	 * Validates the settingsAreValid constraint of '<em>Variable</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public boolean validateVariable_settingsAreValid(Variable variable, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		// TODO implement the constraint
		// -> specify the condition that violates the constraint
		// -> verify the diagnostic details, including severity, code, and message
		// Ensure that you remove @generated or mark it @generated NOT
		if (false) {
			if (diagnostics != null) {
				diagnostics.add
					(createDiagnostic
						(Diagnostic.ERROR,
						 DIAGNOSTIC_SOURCE,
						 0,
						 "_UI_GenericConstraint_diagnostic",
						 new Object[] { "settingsAreValid", getObjectLabel(variable, context) },
						 new Object[] { variable },
						 context));
			}
			return false;
		}
		return true;
	}

	/**
	 * Validates the nameIsValid constraint of '<em>Variable</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public boolean validateVariable_nameIsValid(Variable variable, DiagnosticChain diagnostics,
			Map<Object, Object> context) {

		if (Strings.isNullOrEmpty(variable.getName())) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_VariableConstraint_diagnostic_noname",
						new Object[] { "nameIsValid", getObjectLabel(variable, context) }, new Object[] { variable },
						context));
			}
			return false;
		}
		if (variable.getName().contains(" ")) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_VariableConstraint_diagnostic_nameinvalid",
						new Object[] { "nameIsValid", getObjectLabel(variable, context) }, new Object[] { variable },
						context));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateDistributionSetting(DistributionSetting distributionSetting, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(distributionSetting, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateNormalDistribution(NormalDistribution normalDistribution, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		if (!validate_NoCircularContainment(normalDistribution, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(normalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(normalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(normalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(normalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(normalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(normalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(normalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(normalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validateNormalDistribution_parametersAreValid(normalDistribution, diagnostics, context);
		return result;
	}

	/**
	 * Validates the parametersAreValid constraint of '<em>Normal Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean validateNormalDistribution_parametersAreValid(NormalDistribution normalDistribution, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!Double.isFinite(normalDistribution.getMean())
				|| !Double.isFinite(normalDistribution.getStandardDeviation())
				|| normalDistribution.getStandardDeviation() <= 0) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validateNormalDistribution_parametersAreValidConstraint_diagnostic",
						new Object[] { "parametersAreValid", getObjectLabel(normalDistribution, context) },
						new Object[] { normalDistribution, GuesstimatePackage.eINSTANCE.getNormalDistribution_Mean(),
								GuesstimatePackage.eINSTANCE.getNormalDistribution_StandardDeviation() }, context));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateLogNormalDistribution(LogNormalDistribution logNormalDistribution,
			DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!validate_NoCircularContainment(logNormalDistribution, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(logNormalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(logNormalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(logNormalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(logNormalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(logNormalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(logNormalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(logNormalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(logNormalDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validateLogNormalDistribution_parametersAreValid(logNormalDistribution, diagnostics, context);
		return result;
	}

	/**
	 * Validates the parametersAreValid constraint of '<em>Log Normal Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean validateLogNormalDistribution_parametersAreValid(LogNormalDistribution logNormalDistribution, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!Double.isFinite(logNormalDistribution.getLogMean())
				|| !Double.isFinite(logNormalDistribution.getLogStandardDeviation())
				|| logNormalDistribution.getLogStandardDeviation() <= 0) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validateLogNormalDistribution_parametersAreValidConstraint_diagnostic",
						new Object[] { "parametersAreValid", getObjectLabel(logNormalDistribution, context) },
						new Object[] { logNormalDistribution, GuesstimatePackage.eINSTANCE.getLogNormalDistribution_LogMean(),
								GuesstimatePackage.eINSTANCE.getLogNormalDistribution_LogStandardDeviation() }, context));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateUniformDistribution(UniformDistribution uniformDistribution, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		if (!validate_NoCircularContainment(uniformDistribution, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(uniformDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(uniformDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(uniformDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(uniformDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(uniformDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(uniformDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(uniformDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(uniformDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validateUniformDistribution_minMaxAreConsistent(uniformDistribution, diagnostics, context);
		return result;
	}

	/**
	 * Validates the minMaxAreConsistent constraint of '<em>Uniform
	 * Distribution</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public boolean validateUniformDistribution_minMaxAreConsistent(UniformDistribution uniformDistribution,
			DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!Double.isFinite(uniformDistribution.getMin()) || !Double.isFinite(uniformDistribution.getMax())
				|| uniformDistribution.getMin() >= uniformDistribution.getMax()) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validateUniformDistribution_minMaxAreConsistentConstraint_diagnostic",
						new Object[] { "minMaxAreConsistent", getObjectLabel(uniformDistribution, context) },
						new Object[] { uniformDistribution, GuesstimatePackage.eINSTANCE.getUniformDistribution_Min(),
								GuesstimatePackage.eINSTANCE.getUniformDistribution_Max() },
						context));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateBetaDistribution(BetaDistribution betaDistribution, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		if (!validate_NoCircularContainment(betaDistribution, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(betaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(betaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(betaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(betaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(betaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(betaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(betaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(betaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validateBetaDistribution_parametersAreValid(betaDistribution, diagnostics, context);
		return result;
	}

	/**
	 * Validates the parametersAreValid constraint of '<em>Beta Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean validateBetaDistribution_parametersAreValid(BetaDistribution betaDistribution, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!Double.isFinite(betaDistribution.getAlpha()) || betaDistribution.getAlpha() <= 0
				|| !Double.isFinite(betaDistribution.getBeta()) || betaDistribution.getBeta() <= 0) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validateBetaDistribution_parametersAreValidConstraint_diagnostic",
						new Object[] { "parametersAreValid", getObjectLabel(betaDistribution, context) },
						new Object[] { betaDistribution, GuesstimatePackage.eINSTANCE.getBetaDistribution_Alpha(),
								GuesstimatePackage.eINSTANCE.getBetaDistribution_Beta() }, context));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateTriangularDistribution(TriangularDistribution triangularDistribution,
			DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!validate_NoCircularContainment(triangularDistribution, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(triangularDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(triangularDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(triangularDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(triangularDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(triangularDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(triangularDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(triangularDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(triangularDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validateTriangularDistribution_parametersAreValid(triangularDistribution, diagnostics, context);
		return result;
	}

	/**
	 * Validates the parametersAreValid constraint of '<em>Triangular Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean validateTriangularDistribution_parametersAreValid(TriangularDistribution triangularDistribution, DiagnosticChain diagnostics, Map<Object, Object> context) {
		double min = triangularDistribution.getMin();
		double max = triangularDistribution.getMax();
		double mode = triangularDistribution.getMode();
		if (!Double.isFinite(min) || !Double.isFinite(max) || !Double.isFinite(mode)
				|| min >= max || mode < min || mode > max) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validateTriangularDistribution_parametersAreValidConstraint_diagnostic",
						new Object[] { "parametersAreValid", getObjectLabel(triangularDistribution, context) },
						new Object[] { triangularDistribution, GuesstimatePackage.eINSTANCE.getTriangularDistribution_Min(),
								GuesstimatePackage.eINSTANCE.getTriangularDistribution_Max(),
								GuesstimatePackage.eINSTANCE.getTriangularDistribution_Mode() }, context));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateBinomialDistribution(BinomialDistribution binomialDistribution, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		if (!validate_NoCircularContainment(binomialDistribution, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(binomialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(binomialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(binomialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(binomialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(binomialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(binomialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(binomialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(binomialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validateBinomialDistribution_trialsAreValid(binomialDistribution, diagnostics, context);
		return result;
	}

	/**
	 * Validates the trialsAreValid constraint of '<em>Binomial Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean validateBinomialDistribution_trialsAreValid(BinomialDistribution binomialDistribution, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (binomialDistribution.getTrials() < 0) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validateBinomialDistribution_trialsAreValidConstraint_diagnostic",
						new Object[] { "trialsAreValid", getObjectLabel(binomialDistribution, context) },
						new Object[] { binomialDistribution, GuesstimatePackage.eINSTANCE.getBinomialDistribution_Trials() },
						context));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateFormulaSetting(FormulaSetting formulaSetting, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		if (!validate_NoCircularContainment(formulaSetting, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(formulaSetting, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(formulaSetting, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(formulaSetting, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(formulaSetting, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(formulaSetting, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(formulaSetting, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(formulaSetting, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(formulaSetting, diagnostics, context);
		if (result || diagnostics != null) result &= validateFormulaSetting_unknownVariable(formulaSetting, diagnostics, context);
		if (result || diagnostics != null) result &= validateFormulaSetting_invalidSyntax(formulaSetting, diagnostics, context);
		return result;
	}

	/**
	 * Validates the unknownVariable constraint of '<em>Formula Setting</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public boolean validateFormulaSetting_unknownVariable(FormulaSetting formulaSetting, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		VariableServices service = new VariableServices();
		Set<String> unknownVars = service.collectUnknownVariables(formulaSetting);
		if (unknownVars.size() > 0) {
			for (String unknown : unknownVars) {
				if (diagnostics != null) {
					diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
							"_UI_validateFormulaSetting_unknownVariable",
							new Object[] { "unknownVariable", getObjectLabel(formulaSetting, context), unknown },
							new Object[] { formulaSetting, GuesstimatePackage.eINSTANCE.getFormulaSetting_Formula() },
							context));
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Validates the invalidSyntax constraint of '<em>Formula Setting</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public boolean validateFormulaSetting_invalidSyntax(FormulaSetting formulaSetting, DiagnosticChain diagnostics,
			Map<Object, Object> context) {

		if (formulaSetting.getFormula() != null) {
			Parser p = new ArithParser().createParser();
			Result r = p.parse(formulaSetting.getFormula());

			if (r.isFailure()) {
				if (diagnostics != null) {
					diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
							"_UI_validateFormulaSetting_invalidSyntax",
							new Object[] { "invalidSyntax", getObjectLabel(formulaSetting, context), r.getMessage() },
							new Object[] { formulaSetting, GuesstimatePackage.eINSTANCE.getFormulaSetting_Formula() },
							context));
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * Validates the settingsAreValid constraint of '<em>Variable</em>'. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	public boolean validateDistribution_settingsAreValid(Variable d, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		// if (diagnostics != null) {
		// diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
		// "_UI_GenericConstraint_diagnostic",
		// new Object[] { "settingsAreValid", getObjectLabel(d, context) }, new Object[]
		// { d }, context));
		// }
		// return false;
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateSheet(Sheet sheet, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!validate_NoCircularContainment(sheet, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(sheet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(sheet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(sheet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(sheet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(sheet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(sheet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(sheet, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(sheet, diagnostics, context);
		if (result || diagnostics != null) result &= validateSheet_sampleSizeIsPositive(sheet, diagnostics, context);
		return result;
	}

	/**
	 * Validates the sampleSizeIsPositive constraint of '<em>Sheet</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean validateSheet_sampleSizeIsPositive(Sheet sheet, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (sheet.getSampleSize() <= 0) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validateSheet_sampleSizeIsPositiveConstraint_diagnostic",
						new Object[] { "sampleSizeIsPositive", getObjectLabel(sheet, context) },
						new Object[] { sheet, GuesstimatePackage.eINSTANCE.getSheet_SampleSize() }, context));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePoissonDistribution(PoissonDistribution poissonDistribution, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		if (!validate_NoCircularContainment(poissonDistribution, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(poissonDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(poissonDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(poissonDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(poissonDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(poissonDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(poissonDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(poissonDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(poissonDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validatePoissonDistribution_meanIsPositive(poissonDistribution, diagnostics, context);
		return result;
	}

	/**
	 * Validates the meanIsPositive constraint of '<em>Poisson Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean validatePoissonDistribution_meanIsPositive(PoissonDistribution poissonDistribution, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!Double.isFinite(poissonDistribution.getMean()) || poissonDistribution.getMean() <= 0) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validatePoissonDistribution_meanIsPositiveConstraint_diagnostic",
						new Object[] { "meanIsPositive", getObjectLabel(poissonDistribution, context) },
						new Object[] { poissonDistribution, GuesstimatePackage.eINSTANCE.getPoissonDistribution_Mean() },
						context));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateExponentialDistribution(ExponentialDistribution exponentialDistribution,
			DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!validate_NoCircularContainment(exponentialDistribution, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(exponentialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(exponentialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(exponentialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(exponentialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(exponentialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(exponentialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(exponentialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(exponentialDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validateExponentialDistribution_meanIsPositive(exponentialDistribution, diagnostics, context);
		return result;
	}

	/**
	 * Validates the meanIsPositive constraint of '<em>Exponential Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean validateExponentialDistribution_meanIsPositive(ExponentialDistribution exponentialDistribution, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!Double.isFinite(exponentialDistribution.getMean()) || exponentialDistribution.getMean() <= 0) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validateExponentialDistribution_meanIsPositiveConstraint_diagnostic",
						new Object[] { "meanIsPositive", getObjectLabel(exponentialDistribution, context) },
						new Object[] { exponentialDistribution, GuesstimatePackage.eINSTANCE.getExponentialDistribution_Mean() },
						context));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateGammaDistribution(GammaDistribution gammaDistribution, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		if (!validate_NoCircularContainment(gammaDistribution, diagnostics, context)) return false;
		boolean result = validate_EveryMultiplicityConforms(gammaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryDataValueConforms(gammaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryReferenceIsContained(gammaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryBidirectionalReferenceIsPaired(gammaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryProxyResolves(gammaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_UniqueID(gammaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryKeyUnique(gammaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validate_EveryMapEntryUnique(gammaDistribution, diagnostics, context);
		if (result || diagnostics != null) result &= validateGammaDistribution_parametersAreValid(gammaDistribution, diagnostics, context);
		return result;
	}

	/**
	 * Validates the parametersAreValid constraint of '<em>Gamma Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean validateGammaDistribution_parametersAreValid(GammaDistribution gammaDistribution, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (!Double.isFinite(gammaDistribution.getShape()) || gammaDistribution.getShape() <= 0
				|| !Double.isFinite(gammaDistribution.getScale()) || gammaDistribution.getScale() <= 0) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validateGammaDistribution_parametersAreValidConstraint_diagnostic",
						new Object[] { "parametersAreValid", getObjectLabel(gammaDistribution, context) },
						new Object[] { gammaDistribution, GuesstimatePackage.eINSTANCE.getGammaDistribution_Shape(),
								GuesstimatePackage.eINSTANCE.getGammaDistribution_Scale() }, context));
			}
			return false;
		}
		return true;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateVariableType(VariableType variableType, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		return true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateProbability(Double probability, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = validateProbability_valueIsValid(probability, diagnostics, context);
		return result;
	}

	/**
	 * Validates the valueIsValid constraint of '<em>Probability</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean validateProbability_valueIsValid(Double probability, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (probability == null || !Double.isFinite(probability) || probability < 0 || probability > 1) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validateProbability_valueIsValidConstraint_diagnostic",
						new Object[] { "valueIsValid", getValueLabel(GuesstimatePackage.Literals.PROBABILITY, probability, context) },
						new Object[] { probability }, context));
			}
			return false;
		}
		return true;
	}

	/**
	 * Returns the resource locator that will be used to fetch messages for this validator's diagnostics.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return GuesstimateEMFPlugin.INSTANCE;
	}

} // GuesstimateValidator
