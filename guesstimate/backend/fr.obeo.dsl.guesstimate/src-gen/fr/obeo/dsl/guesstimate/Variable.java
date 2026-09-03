/**
 */
package fr.obeo.dsl.guesstimate;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Variable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.Variable#getName <em>Name</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.Variable#getDocumentation <em>Documentation</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.Variable#getDefinition <em>Definition</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.Variable#getType <em>Type</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.Variable#getSettings <em>Settings</em>}</li>
 * </ul>
 *
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getVariable()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='nameIsValid'"
 * @generated
 */
public interface Variable extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * The variable name. Must respect the rules of a programming language identifier.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getVariable_Name()
	 * @model required="true"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.Variable#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Documentation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Any human readable description.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Documentation</em>' attribute.
	 * @see #setDocumentation(String)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getVariable_Documentation()
	 * @model
	 * @generated
	 */
	String getDocumentation();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.Variable#getDocumentation <em>Documentation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Documentation</em>' attribute.
	 * @see #getDocumentation()
	 * @generated
	 */
	void setDocumentation(String value);

	/**
	 * Returns the value of the '<em><b>Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * A shortcut in natural language to set the distribution kind and its parameters.
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Definition</em>' attribute.
	 * @see #setDefinition(String)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getVariable_Definition()
	 * @model
	 * @generated
	 */
	String getDefinition();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.Variable#getDefinition <em>Definition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Definition</em>' attribute.
	 * @see #getDefinition()
	 * @generated
	 */
	void setDefinition(String value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * The literals are from the enumeration {@link fr.obeo.dsl.guesstimate.VariableType}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see fr.obeo.dsl.guesstimate.VariableType
	 * @see #setType(VariableType)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getVariable_Type()
	 * @model required="true" transient="true" volatile="true" derived="true"
	 * @generated
	 */
	VariableType getType();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.Variable#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see fr.obeo.dsl.guesstimate.VariableType
	 * @see #getType()
	 * @generated
	 */
	void setType(VariableType value);

	/**
	 * Returns the value of the '<em><b>Settings</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Settings</em>' containment reference.
	 * @see #setSettings(VariableSettings)
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getVariable_Settings()
	 * @model containment="true" required="true"
	 * @generated
	 */
	VariableSettings getSettings();

	/**
	 * Sets the value of the '{@link fr.obeo.dsl.guesstimate.Variable#getSettings <em>Settings</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Settings</em>' containment reference.
	 * @see #getSettings()
	 * @generated
	 */
	void setSettings(VariableSettings value);

} // Variable
