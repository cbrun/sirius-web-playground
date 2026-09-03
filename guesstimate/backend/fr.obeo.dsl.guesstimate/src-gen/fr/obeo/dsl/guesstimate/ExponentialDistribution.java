/**
 */
package fr.obeo.dsl.guesstimate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Exponential Distribution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The exponential distribution is a continuous probability distribution often used to model the time between events in a Poisson process. It is characterized here by its strictly positive mean and has a peak at zero with a long tail extending to the right.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.ExponentialDistribution#getMean <em>Mean</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getExponentialDistribution()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='meanIsPositive'"
 * @generated
 */
public interface ExponentialDistribution extends DistributionSetting {
	/**
	 * Returns the value of the '<em><b>Mean</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mean</em>' attribute.
	 * @see #setMean(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getExponentialDistribution_Mean()
	 * @model default="1"
	 * @generated
	 */
	double getMean();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.ExponentialDistribution#getMean <em>Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mean</em>' attribute.
	 * @see #getMean()
	 * @generated
	 */
	void setMean(double value);

} // ExponentialDistribution
