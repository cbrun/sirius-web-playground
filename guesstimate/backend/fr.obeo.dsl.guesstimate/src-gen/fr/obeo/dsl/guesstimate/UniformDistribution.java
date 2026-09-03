/**
 */
package fr.obeo.dsl.guesstimate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Uniform Distribution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The uniform distribution is a continuous probability distribution where all outcomes are equally likely within a specified range, defined by minimum (a) and maximum (b) values. This distribution forms a flat, rectangular shape and is ideal for modeling scenarios with equally likely outcomes, like generating random numbers or simulating the roll of a fair die.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.UniformDistribution#getMin <em>Min</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.UniformDistribution#getMax <em>Max</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getUniformDistribution()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='minMaxAreConsistent'"
 * @generated
 */
public interface UniformDistribution extends VariableSettings {
	/**
	 * Returns the value of the '<em><b>Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The minimum value is the lowest possible outcome in the uniform distribution. It represents the lower bound of the range over which all outcomes are equally likely. For example, if you are generating random numbers between 1 and 10, the minimum value (a) would be 1.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Min</em>' attribute.
	 * @see #setMin(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getUniformDistribution_Min()
	 * @model
	 * @generated
	 */
	double getMin();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.UniformDistribution#getMin <em>Min</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Min</em>' attribute.
	 * @see #getMin()
	 * @generated
	 */
	void setMin(double value);

	/**
	 * Returns the value of the '<em><b>Max</b></em>' attribute.
	 * The default value is <code>"1"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The maximum value (b) is the highest possible outcome in the uniform distribution. It sets the upper limit of the range where all outcomes are equally likely. In the same example of generating random numbers between 1 and 10, the maximum value (b) would be 10.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Max</em>' attribute.
	 * @see #setMax(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getUniformDistribution_Max()
	 * @model default="1"
	 * @generated
	 */
	double getMax();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.UniformDistribution#getMax <em>Max</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max</em>' attribute.
	 * @see #getMax()
	 * @generated
	 */
	void setMax(double value);

} // UniformDistribution
