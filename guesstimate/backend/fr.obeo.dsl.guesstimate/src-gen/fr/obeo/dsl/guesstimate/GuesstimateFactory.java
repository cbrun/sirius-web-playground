/**
 */
package fr.obeo.dsl.guesstimate;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage
 * @generated
 */
public interface GuesstimateFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GuesstimateFactory eINSTANCE = fr.obeo.dsl.guesstimate.impl.GuesstimateFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Variable</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Variable</em>'.
	 * @generated
	 */
	Variable createVariable();

	/**
	 * Returns a new object of class '<em>Normal Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Normal Distribution</em>'.
	 * @generated
	 */
	NormalDistribution createNormalDistribution();

	/**
	 * Returns a new object of class '<em>Log Normal Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Log Normal Distribution</em>'.
	 * @generated
	 */
	LogNormalDistribution createLogNormalDistribution();

	/**
	 * Returns a new object of class '<em>Uniform Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Uniform Distribution</em>'.
	 * @generated
	 */
	UniformDistribution createUniformDistribution();

	/**
	 * Returns a new object of class '<em>Beta Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Beta Distribution</em>'.
	 * @generated
	 */
	BetaDistribution createBetaDistribution();

	/**
	 * Returns a new object of class '<em>Triangular Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Triangular Distribution</em>'.
	 * @generated
	 */
	TriangularDistribution createTriangularDistribution();

	/**
	 * Returns a new object of class '<em>Binomial Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Binomial Distribution</em>'.
	 * @generated
	 */
	BinomialDistribution createBinomialDistribution();

	/**
	 * Returns a new object of class '<em>Formula Setting</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Formula Setting</em>'.
	 * @generated
	 */
	FormulaSetting createFormulaSetting();

	/**
	 * Returns a new object of class '<em>Sheet</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Sheet</em>'.
	 * @generated
	 */
	Sheet createSheet();

	/**
	 * Returns a new object of class '<em>Poisson Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Poisson Distribution</em>'.
	 * @generated
	 */
	PoissonDistribution createPoissonDistribution();

	/**
	 * Returns a new object of class '<em>Exponential Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Exponential Distribution</em>'.
	 * @generated
	 */
	ExponentialDistribution createExponentialDistribution();

	/**
	 * Returns a new object of class '<em>Gamma Distribution</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Gamma Distribution</em>'.
	 * @generated
	 */
	GammaDistribution createGammaDistribution();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	GuesstimatePackage getGuesstimatePackage();

} //GuesstimateFactory
