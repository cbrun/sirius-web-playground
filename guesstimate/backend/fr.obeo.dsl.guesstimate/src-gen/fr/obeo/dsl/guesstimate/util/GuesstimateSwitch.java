/**
 */
package fr.obeo.dsl.guesstimate.util;

import fr.obeo.dsl.guesstimate.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage
 * @generated
 */
public class GuesstimateSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static GuesstimatePackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GuesstimateSwitch() {
		if (modelPackage == null) {
			modelPackage = GuesstimatePackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
		case GuesstimatePackage.VARIABLE: {
			Variable variable = (Variable) theEObject;
			T result = caseVariable(variable);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.DISTRIBUTION_SETTING: {
			DistributionSetting distributionSetting = (DistributionSetting) theEObject;
			T result = caseDistributionSetting(distributionSetting);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.NORMAL_DISTRIBUTION: {
			NormalDistribution normalDistribution = (NormalDistribution) theEObject;
			T result = caseNormalDistribution(normalDistribution);
			if (result == null)
				result = caseDistributionSetting(normalDistribution);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.LOG_NORMAL_DISTRIBUTION: {
			LogNormalDistribution logNormalDistribution = (LogNormalDistribution) theEObject;
			T result = caseLogNormalDistribution(logNormalDistribution);
			if (result == null)
				result = caseDistributionSetting(logNormalDistribution);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.UNIFORM_DISTRIBUTION: {
			UniformDistribution uniformDistribution = (UniformDistribution) theEObject;
			T result = caseUniformDistribution(uniformDistribution);
			if (result == null)
				result = caseDistributionSetting(uniformDistribution);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.BETA_DISTRIBUTION: {
			BetaDistribution betaDistribution = (BetaDistribution) theEObject;
			T result = caseBetaDistribution(betaDistribution);
			if (result == null)
				result = caseDistributionSetting(betaDistribution);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.TRIANGULAR_DISTRIBUTION: {
			TriangularDistribution triangularDistribution = (TriangularDistribution) theEObject;
			T result = caseTriangularDistribution(triangularDistribution);
			if (result == null)
				result = caseDistributionSetting(triangularDistribution);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.BINOMIAL_DISTRIBUTION: {
			BinomialDistribution binomialDistribution = (BinomialDistribution) theEObject;
			T result = caseBinomialDistribution(binomialDistribution);
			if (result == null)
				result = caseDistributionSetting(binomialDistribution);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.FORMULA_SETTING: {
			FormulaSetting formulaSetting = (FormulaSetting) theEObject;
			T result = caseFormulaSetting(formulaSetting);
			if (result == null)
				result = caseDistributionSetting(formulaSetting);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.SHEET: {
			Sheet sheet = (Sheet) theEObject;
			T result = caseSheet(sheet);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.POISSON_DISTRIBUTION: {
			PoissonDistribution poissonDistribution = (PoissonDistribution) theEObject;
			T result = casePoissonDistribution(poissonDistribution);
			if (result == null)
				result = caseDistributionSetting(poissonDistribution);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.EXPONENTIAL_DISTRIBUTION: {
			ExponentialDistribution exponentialDistribution = (ExponentialDistribution) theEObject;
			T result = caseExponentialDistribution(exponentialDistribution);
			if (result == null)
				result = caseDistributionSetting(exponentialDistribution);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		case GuesstimatePackage.GAMMA_DISTRIBUTION: {
			GammaDistribution gammaDistribution = (GammaDistribution) theEObject;
			T result = caseGammaDistribution(gammaDistribution);
			if (result == null)
				result = caseDistributionSetting(gammaDistribution);
			if (result == null)
				result = defaultCase(theEObject);
			return result;
		}
		default:
			return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Variable</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Variable</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseVariable(Variable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Distribution Setting</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Distribution Setting</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseDistributionSetting(DistributionSetting object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Normal Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Normal Distribution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNormalDistribution(NormalDistribution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Log Normal Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Log Normal Distribution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseLogNormalDistribution(LogNormalDistribution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Uniform Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Uniform Distribution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUniformDistribution(UniformDistribution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Beta Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Beta Distribution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBetaDistribution(BetaDistribution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Triangular Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Triangular Distribution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseTriangularDistribution(TriangularDistribution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Binomial Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Binomial Distribution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBinomialDistribution(BinomialDistribution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Formula Setting</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Formula Setting</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseFormulaSetting(FormulaSetting object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sheet</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sheet</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSheet(Sheet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Poisson Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Poisson Distribution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T casePoissonDistribution(PoissonDistribution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Exponential Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Exponential Distribution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseExponentialDistribution(ExponentialDistribution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Gamma Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Gamma Distribution</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseGammaDistribution(GammaDistribution object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //GuesstimateSwitch
