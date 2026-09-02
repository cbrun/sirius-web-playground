/**
 */
package fr.obeo.dsl.guesstimate;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sheet</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.Sheet#getVariables <em>Variables</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.Sheet#getSampleSize <em>Sample Size</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getSheet()
 * @model
 * @generated
 */
public interface Sheet extends EObject {
	/**
	 * Returns the value of the '<em><b>Variables</b></em>' containment reference list.
	 * The list contents are of type {@link fr.obeo.dsl.guesstimate.Variable}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Variables</em>' containment reference list.
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getSheet_Variables()
	 * @model containment="true"
	 * @generated
	 */
	EList<Variable> getVariables();

	/**
	 * Returns the value of the '<em><b>Sample Size</b></em>' attribute.
	 * The default value is <code>"10000"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sample Size</em>' attribute.
	 * @see #setSampleSize(int)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getSheet_SampleSize()
	 * @model default="10000"
	 * @generated
	 */
	int getSampleSize();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.Sheet#getSampleSize <em>Sample Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sample Size</em>' attribute.
	 * @see #getSampleSize()
	 * @generated
	 */
	void setSampleSize(int value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	void resample();

} // Sheet
