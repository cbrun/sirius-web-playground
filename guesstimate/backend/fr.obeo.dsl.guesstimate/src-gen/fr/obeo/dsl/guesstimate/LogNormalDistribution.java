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
 *   <li>{@link fr.obeo.dsl.guesstimate.LogNormalDistribution#getLogMean <em>Log Mean</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.LogNormalDistribution#getLogStandardDeviation <em>Log Standard Deviation</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getLogNormalDistribution()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='parametersAreValid'"
 * @generated
 */
public interface LogNormalDistribution extends VariableSettings {
	/**
	 * Returns the value of the '<em><b>Log Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The mean of the natural logarithm of the variable. It determines the location of the distribution on the logarithmic scale.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Log Mean</em>' attribute.
	 * @see #setLogMean(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getLogNormalDistribution_LogMean()
	 * @model
	 * @generated
	 */
	double getLogMean();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.LogNormalDistribution#getLogMean <em>Log Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Log Mean</em>' attribute.
	 * @see #getLogMean()
	 * @generated
	 */
	void setLogMean(double value);

	/**
	 * Returns the value of the '<em><b>Log Standard Deviation</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The strictly positive standard deviation of the natural logarithm of the variable. It controls the spread of the distribution on the logarithmic scale.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Log Standard Deviation</em>' attribute.
	 * @see #setLogStandardDeviation(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getLogNormalDistribution_LogStandardDeviation()
	 * @model default="1"
	 * @generated
	 */
	double getLogStandardDeviation();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.LogNormalDistribution#getLogStandardDeviation <em>Log Standard Deviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Log Standard Deviation</em>' attribute.
	 * @see #getLogStandardDeviation()
	 * @generated
	 */
	void setLogStandardDeviation(double value);

} // LogNormalDistribution
