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
			case GuesstimatePackage.PERCENTAGE:
				return validatePercentage((Double)value, diagnostics, context);
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
		return validate_EveryDefaultConstraint(normalDistribution, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateLogNormalDistribution(LogNormalDistribution logNormalDistribution,
			DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(logNormalDistribution, diagnostics, context);
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
		if (uniformDistribution.getMax() < uniformDistribution.getMin()) {
			if (diagnostics != null) {
				diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, 0,
						"_UI_validateUniformDistribution_minMaxAreConsistentConstraint_diagnostic",
						new Object[] { "minMaxAreConsistent", getObjectLabel(uniformDistribution, context) },
						new Object[] { uniformDistribution, GuesstimatePackage.eINSTANCE.getUniformDistribution_Max() },
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
		return validate_EveryDefaultConstraint(betaDistribution, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateTriangularDistribution(TriangularDistribution triangularDistribution,
			DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(triangularDistribution, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateBinomialDistribution(BinomialDistribution binomialDistribution, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(binomialDistribution, diagnostics, context);
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
		return validate_EveryDefaultConstraint(sheet, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePoissonDistribution(PoissonDistribution poissonDistribution, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(poissonDistribution, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateExponentialDistribution(ExponentialDistribution exponentialDistribution,
			DiagnosticChain diagnostics, Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(exponentialDistribution, diagnostics, context);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validateGammaDistribution(GammaDistribution gammaDistribution, DiagnosticChain diagnostics,
			Map<Object, Object> context) {
		return validate_EveryDefaultConstraint(gammaDistribution, diagnostics, context);
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
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public boolean validatePercentage(Double percentage, DiagnosticChain diagnostics, Map<Object, Object> context) {
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
