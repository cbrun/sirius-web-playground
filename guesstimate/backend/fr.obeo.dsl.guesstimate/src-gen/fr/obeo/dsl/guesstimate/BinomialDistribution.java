/**
 */
package fr.obeo.dsl.guesstimate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binomial Distribution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The binomial distribution is a discrete probability distribution that describes the number of successes in a fixed number of independent Bernoulli trials, each with the same probability of success (p). Defined by the number of trials (n) and probability of success (p), it is ideal for scenarios like modeling the number of heads in a series of coin tosses or the number of defective items in a batch of products.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.BinomialDistribution#getTrials <em>Trials</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.BinomialDistribution#getProbabilityOfSuccess <em>Probability Of Success</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getBinomialDistribution()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='trialsAreValid'"
 * @generated
 */
public interface BinomialDistribution extends VariableSettings {
	/**
	 * Returns the value of the '<em><b>Trials</b></em>' attribute.
	 * The default value is <code>"10"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The number of trials (n) in a binomial distribution is a non-negative integer representing the total number of independent and identical Bernoulli trials conducted. Each trial is an experiment or process with exactly two possible outcomes: success or failure. For example, in a scenario where you flip a coin 10 times, the number of trials (n) would be 10.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Trials</em>' attribute.
	 * @see #setTrials(int)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getBinomialDistribution_Trials()
	 * @model default="10"
	 * @generated
	 */
	int getTrials();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.BinomialDistribution#getTrials <em>Trials</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Trials</em>' attribute.
	 * @see #getTrials()
	 * @generated
	 */
	void setTrials(int value);

	/**
	 * Returns the value of the '<em><b>Probability Of Success</b></em>' attribute.
	 * The default value is <code>"0.5"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The probability of success (p) is a value between 0 and 1, representing the likelihood of achieving a success in each individual trial. This probability remains constant across all trials. For instance, if you are rolling a die and interested in the probability of rolling a 4, the probability of success (p) would be 1/6. In the context of a coin flip, if you define heads as a success, the probability of success (p) would be 0.5 (assuming a fair coin).
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Probability Of Success</em>' attribute.
	 * @see #setProbabilityOfSuccess(Double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getBinomialDistribution_ProbabilityOfSuccess()
	 * @model default="0.5" dataType="fr.obeo.dsl.guesstimate.Probability" required="true"
	 * @generated
	 */
	Double getProbabilityOfSuccess();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.BinomialDistribution#getProbabilityOfSuccess <em>Probability Of Success</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Probability Of Success</em>' attribute.
	 * @see #getProbabilityOfSuccess()
	 * @generated
	 */
	void setProbabilityOfSuccess(Double value);

} // BinomialDistribution
