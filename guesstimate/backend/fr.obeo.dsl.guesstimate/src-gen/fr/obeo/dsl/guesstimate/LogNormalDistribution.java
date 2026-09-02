/**
 */
package fr.obeo.dsl.guesstimate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Log Normal Distribution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The lognormal distribution is a continuous probability distribution where the logarithm of the variable follows a normal distribution. Defined by the mean (µ) and standard deviation (σ) of the variable's natural logarithm, it is right-skewed and suitable for modeling data that grows multiplicatively, such as stock prices, investment returns, and the sizes of biological organisms.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.LogNormalDistribution#getScale <em>Scale</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.LogNormalDistribution#getShape <em>Shape</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getLogNormalDistribution()
 * @model
 * @generated
 */
public interface LogNormalDistribution extends DistributionSetting {
	/**
	 * Returns the value of the '<em><b>Scale</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The scale parameter (σ) is the standard deviation of the natural logarithm of the variable. It measures the spread or dispersion of the distribution in the log-transformed scale. A higher σ indicates more variability in the data. Using the stock prices example, calculating the standard deviation of the logarithms of the prices gives you the scale..
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Scale</em>' attribute.
	 * @see #setScale(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getLogNormalDistribution_Scale()
	 * @model default="1"
	 * @generated
	 */
	double getScale();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.LogNormalDistribution#getScale <em>Scale</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Scale</em>' attribute.
	 * @see #getScale()
	 * @generated
	 */
	void setScale(double value);

	/**
	 * Returns the value of the '<em><b>Shape</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The location parameter is the mean of the natural logarithm of the variable. It represents the central tendency of the distribution in the log-transformed scale. For instance, if you have data on the prices of stocks, taking the natural logarithm of these prices and finding the average will give you the shape.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Shape</em>' attribute.
	 * @see #setShape(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getLogNormalDistribution_Shape()
	 * @model default="0"
	 * @generated
	 */
	double getShape();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.LogNormalDistribution#getShape <em>Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Shape</em>' attribute.
	 * @see #getShape()
	 * @generated
	 */
	void setShape(double value);

} // LogNormalDistribution
