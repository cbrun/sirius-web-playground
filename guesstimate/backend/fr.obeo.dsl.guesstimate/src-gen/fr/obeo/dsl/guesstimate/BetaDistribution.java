/**
 */
package fr.obeo.dsl.guesstimate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Beta Distribution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The beta distribution is a continuous probability distribution defined on the interval [0, 1], often used to model probabilities themselves. With two shape parameters, α (alpha) and β (beta), it can take various forms (uniform, U-shaped, etc.), making it useful for modeling the probability of success in Bernoulli trials when the probability is not fixed, or in Bayesian statistics as a conjugate prior for binomial proportions.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.BetaDistribution#getAlpha <em>Alpha</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.BetaDistribution#getBeta <em>Beta</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getBetaDistribution()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='parametersAreValid'"
 * @generated
 */
public interface BetaDistribution extends DistributionSetting {
	/**
	 * Returns the value of the '<em><b>Alpha</b></em>' attribute.
	 * The default value is <code>"2"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Alpha (α) is a shape parameter that influences the distribution's shape. It is a positive real number that determines the skewness of the distribution. A higher value of α, when paired with a lower value of β, will skew the distribution to the right, indicating that higher probabilities are more likely. For instance, if you are modeling the probability of success in a series of trials, a higher α might suggest that success is relatively more frequent.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Alpha</em>' attribute.
	 * @see #setAlpha(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getBetaDistribution_Alpha()
	 * @model default="2"
	 * @generated
	 */
	double getAlpha();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.BetaDistribution#getAlpha <em>Alpha</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Alpha</em>' attribute.
	 * @see #getAlpha()
	 * @generated
	 */
	void setAlpha(double value);

	/**
	 * Returns the value of the '<em><b>Beta</b></em>' attribute.
	 * The default value is <code>"2"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Beta (β) is another shape parameter that, like alpha, influences the shape of the distribution. It is also a positive real number. When β is higher relative to α, the distribution skews to the left, indicating that lower probabilities are more likely. Together with α, β defines the shape and scale of the beta distribution. For example, a higher β might indicate that failure is more frequent in a series of trials.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Beta</em>' attribute.
	 * @see #setBeta(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getBetaDistribution_Beta()
	 * @model default="2"
	 * @generated
	 */
	double getBeta();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.BetaDistribution#getBeta <em>Beta</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Beta</em>' attribute.
	 * @see #getBeta()
	 * @generated
	 */
	void setBeta(double value);

} // BetaDistribution
