/**
 */
package fr.obeo.dsl.guesstimate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Gamma Distribution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The gamma distribution is a continuous probability distribution defined by strictly positive shape (k) and scale (θ) parameters. It is useful for modeling waiting times with multiple stages, such as the time until the k-th event in a queuing process.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.GammaDistribution#getShape <em>Shape</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.GammaDistribution#getScale <em>Scale</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getGammaDistribution()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='parametersAreValid'"
 * @generated
 */
public interface GammaDistribution extends DistributionSetting {
	/**
	 * Returns the value of the '<em><b>Shape</b></em>' attribute.
	 * The default value is <code>"2"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Shape</em>' attribute.
	 * @see #setShape(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getGammaDistribution_Shape()
	 * @model default="2"
	 * @generated
	 */
	double getShape();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.GammaDistribution#getShape <em>Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Shape</em>' attribute.
	 * @see #getShape()
	 * @generated
	 */
	void setShape(double value);

	/**
	 * Returns the value of the '<em><b>Scale</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Scale</em>' attribute.
	 * @see #setScale(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getGammaDistribution_Scale()
	 * @model default="1"
	 * @generated
	 */
	double getScale();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.GammaDistribution#getScale <em>Scale</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Scale</em>' attribute.
	 * @see #getScale()
	 * @generated
	 */
	void setScale(double value);

} // GammaDistribution
