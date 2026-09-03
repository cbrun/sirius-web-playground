/**
 */
package fr.obeo.dsl.guesstimate;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Formula Setting</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * A variable which is defined by a formula using other variables.
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.FormulaSetting#getFormula <em>Formula</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.FormulaSetting#getInputs <em>Inputs</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getFormulaSetting()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='unknownVariable invalidSyntax'"
 * @generated
 */
public interface FormulaSetting extends VariableSettings {
	/**
	 * Returns the value of the '<em><b>Formula</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Formula</em>' attribute.
	 * @see #setFormula(String)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getFormulaSetting_Formula()
	 * @model default="0" required="true"
	 * @generated
	 */
	String getFormula();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.FormulaSetting#getFormula <em>Formula</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Formula</em>' attribute.
	 * @see #getFormula()
	 * @generated
	 */
	void setFormula(String value);

	/**
	 * Returns the value of the '<em><b>Inputs</b></em>' reference list.
	 * The list contents are of type {@link fr.obeo.dsl.guesstimate.Variable}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Inputs</em>' reference list.
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getFormulaSetting_Inputs()
	 * @model keys="name"
	 * @generated
	 */
	EList<Variable> getInputs();

} // FormulaSetting
