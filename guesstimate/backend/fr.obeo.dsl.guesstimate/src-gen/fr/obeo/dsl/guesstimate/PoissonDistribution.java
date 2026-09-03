/**
 */
package fr.obeo.dsl.guesstimate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Poisson Distribution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The Poisson distribution is a discrete probability distribution that expresses the probability of a given number of events occurring in a fixed interval of time or space. Defined by the rate parameter (λ), it is suitable for modeling count data, such as the number of emails received per hour or the number of accidents at a traffic intersection in a day.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.PoissonDistribution#getMean <em>Mean</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getPoissonDistribution()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='meanIsPositive'"
 * @generated
 */
public interface PoissonDistribution extends DistributionSetting {
	/**
	 * Returns the value of the '<em><b>Mean</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The strictly positive expected number of events in the observed interval, also known as the Poisson rate parameter λ.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Mean</em>' attribute.
	 * @see #setMean(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getPoissonDistribution_Mean()
	 * @model default="1"
	 * @generated
	 */
	double getMean();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.PoissonDistribution#getMean <em>Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mean</em>' attribute.
	 * @see #getMean()
	 * @generated
	 */
	void setMean(double value);

} // PoissonDistribution
