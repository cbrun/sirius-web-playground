/**
 */
package fr.obeo.dsl.guesstimate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Triangular Distribution</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * The triangular distribution is a continuous probability distribution with a triangular-shaped probability density function, defined by three parameters: the minimum value (a), the maximum value (b), and the mode (c), which is the peak of the triangle. It is used when the exact distribution of data is unknown but a rough estimate with a known minimum, maximum, and most likely value can be provided. Common applications include project management for estimating the duration of tasks and in decision-making scenarios where limited sample data is available.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.TriangularDistribution#getMin <em>Min</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.TriangularDistribution#getMax <em>Max</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.TriangularDistribution#getMode <em>Mode</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getTriangularDistribution()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='parametersAreValid'"
 * @generated
 */
public interface TriangularDistribution extends VariableSettings {
	/**
	 * Returns the value of the '<em><b>Min</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The minimum value (a) is the lowest possible outcome in the triangular distribution. It represents the lower bound of the data range and ensures that no values in the distribution fall below this point. For example, if you are estimating the time to complete a task and the least amount of time it could take is 2 hours, then the minimum value (a) would be 2.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Min</em>' attribute.
	 * @see #setMin(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getTriangularDistribution_Min()
	 * @model default="0"
	 * @generated
	 */
	double getMin();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.TriangularDistribution#getMin <em>Min</em>}' attribute.
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
	 * The maximum value (b) is the highest possible outcome in the triangular distribution. It sets the upper limit of the data range, ensuring that no values exceed this point. For instance, if the most time it could take to complete the task is 10 hours, then the maximum value (b) would be 10.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Max</em>' attribute.
	 * @see #setMax(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getTriangularDistribution_Max()
	 * @model default="1"
	 * @generated
	 */
	double getMax();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.TriangularDistribution#getMax <em>Max</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Max</em>' attribute.
	 * @see #getMax()
	 * @generated
	 */
	void setMax(double value);

	/**
	 * Returns the value of the '<em><b>Mode</b></em>' attribute.
	 * The default value is <code>"0.5"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The mode (c) is the most likely outcome within the range of the triangular distribution. It represents the peak of the triangle and is the value where the probability density is highest. In the context of task completion time, if the most likely time to complete the task is 5 hours, then the mode (c) would be 5. The mode must lie between the minimum (a) and maximum (b) values.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Mode</em>' attribute.
	 * @see #setMode(double)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getTriangularDistribution_Mode()
	 * @model default="0.5"
	 * @generated
	 */
	double getMode();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.TriangularDistribution#getMode <em>Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mode</em>' attribute.
	 * @see #getMode()
	 * @generated
	 */
	void setMode(double value);

} // TriangularDistribution
