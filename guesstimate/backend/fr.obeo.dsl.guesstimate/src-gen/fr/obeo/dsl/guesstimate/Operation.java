/**
 */
package fr.obeo.dsl.guesstimate;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.Operation#getInputs <em>Inputs</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.Operation#getOutput <em>Output</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.Operation#getFormula <em>Formula</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getOperation()
 * @model
 * @generated
 */
public interface Operation extends EObject {
	/**
	 * Returns the value of the '<em><b>Inputs</b></em>' reference list.
	 * The list contents are of type {@link fr.obeo.dsl.guesstimate.Variable}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Inputs</em>' reference list.
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getOperation_Inputs()
	 * @model keys="name"
	 * @generated
	 */
	EList<Variable> getInputs();

	/**
	 * Returns the value of the '<em><b>Output</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Output</em>' containment reference.
	 * @see #setOutput(Variable)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getOperation_Output()
	 * @model containment="true"
	 * @generated
	 */
	Variable getOutput();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.Operation#getOutput <em>Output</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Output</em>' containment reference.
	 * @see #getOutput()
	 * @generated
	 */
	void setOutput(Variable value);

	/**
	 * Returns the value of the '<em><b>Formula</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Formula</em>' attribute.
	 * @see #setFormula(String)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getOperation_Formula()
	 * @model required="true"
	 * @generated
	 */
	String getFormula();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.Operation#getFormula <em>Formula</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Formula</em>' attribute.
	 * @see #getFormula()
	 * @generated
	 */
	void setFormula(String value);

} // Operation
