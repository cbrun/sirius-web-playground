/**
 */
package fr.obeo.dsl.guesstimate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Normal Distribution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The normal distribution is a continuous probability distribution that is symmetrical around its mean, forming a bell-shaped curve. It's defined by the mean (µ) and standard deviation (σ), with most data points falling close to the mean and probabilities tapering off equally in both directions. Use it for modeling natural phenomena like heights, test scores, or any situation where the Central Limit Theorem applies, such as averaging a large number of independent variables.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.NormalDistribution#getMean <em>Mean</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.NormalDistribution#getStandardDeviation <em>Standard Deviation</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getNormalDistribution()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='parametersAreValid'"
 * @generated
 */
public interface NormalDistribution extends DistributionSetting {
	/**
	 * Returns the value of the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The mean is the central value of the normal distribution, representing the average or expected value. It is the point around which the data is symmetrically distributed. In a dataset of people's heights, for example, the mean height is the average height of all individuals in the dataset.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Mean</em>' attribute.
	 * @see #setMean(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getNormalDistribution_Mean()
	 * @model
	 * @generated
	 */
	double getMean();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.NormalDistribution#getMean <em>Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mean</em>' attribute.
	 * @see #getMean()
	 * @generated
	 */
	void setMean(double value);

	/**
	 * Returns the value of the '<em><b>Standard Deviation</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The standard deviation (σ) measures the spread or dispersion of the distribution. It indicates how much the individual data points deviate from the mean. A smaller σ means the data points are closer to the mean, while a larger σ means they are more spread out. In the context of heights, a smaller standard deviation indicates that most people have heights close to the average, whereas a larger standard deviation indicates more variability in heights.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Standard Deviation</em>' attribute.
	 * @see #setStandardDeviation(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getNormalDistribution_StandardDeviation()
	 * @model default="1"
	 * @generated
	 */
	double getStandardDeviation();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.NormalDistribution#getStandardDeviation <em>Standard Deviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Standard Deviation</em>' attribute.
	 * @see #getStandardDeviation()
	 * @generated
	 */
	void setStandardDeviation(double value);

} // NormalDistribution
