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
 *   <li>{@link fr.obeo.dsl.guesstimate.PoissonDistribution#getP <em>P</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.PoissonDistribution#getEpsilon <em>Epsilon</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getPoissonDistribution()
 * @model
 * @generated
 */
public interface PoissonDistribution extends DistributionSetting {
	/**
	 * Returns the value of the '<em><b>P</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>P</em>' attribute.
	 * @see #setP(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getPoissonDistribution_P()
	 * @model default="1"
	 * @generated
	 */
	double getP();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.PoissonDistribution#getP <em>P</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>P</em>' attribute.
	 * @see #getP()
	 * @generated
	 */
	void setP(double value);

	/**
	 * Returns the value of the '<em><b>Epsilon</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Epsilon</em>' attribute.
	 * @see #setEpsilon(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getPoissonDistribution_Epsilon()
	 * @model
	 * @generated
	 */
	double getEpsilon();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.PoissonDistribution#getEpsilon <em>Epsilon</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Epsilon</em>' attribute.
	 * @see #getEpsilon()
	 * @generated
	 */
	void setEpsilon(double value);

} // PoissonDistribution
