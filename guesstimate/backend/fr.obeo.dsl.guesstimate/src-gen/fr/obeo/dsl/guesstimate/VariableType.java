/**
 */
package fr.obeo.dsl.guesstimate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Interval Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#getVariableType()
 * @model
 * @generated
 */
public enum VariableType implements Enumerator {
	/**
	 * The '<em><b>Formula</b></em>' literal object.
	 * <!-- begin-user-doc -->
	* <!-- end-user-doc -->
	 * @see #FORMULA_VALUE
	 * @generated
	 * @ordered
	 */
	FORMULA(1, "formula", "formula"),
	/**
	 * The '<em><b>Normal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	* <!-- end-user-doc -->
	 * @see #NORMAL_VALUE
	 * @generated
	 * @ordered
	 */
	NORMAL(10, "normal", "normal"),
	/**
	 * The '<em><b>Uniform</b></em>' literal object.
	 * <!-- begin-user-doc -->
	* <!-- end-user-doc -->
	 * @see #UNIFORM_VALUE
	 * @generated
	 * @ordered
	 */
	UNIFORM(11, "uniform", "uniform"),
	/**
	 * The '<em><b>Lognormal</b></em>' literal object.
	 * <!-- begin-user-doc -->
	* <!-- end-user-doc -->
	 * @see #LOGNORMAL_VALUE
	 * @generated
	 * @ordered
	 */
	LOGNORMAL(12, "lognormal", "lognormal"),
	/**
	 * The '<em><b>Beta</b></em>' literal object.
	 * <!-- begin-user-doc -->
	* <!-- end-user-doc -->
	 * @see #BETA_VALUE
	 * @generated
	 * @ordered
	 */
	BETA(13, "beta", "beta"),
	/**
	 * The '<em><b>Triangular</b></em>' literal object.
	 * <!-- begin-user-doc -->
	* <!-- end-user-doc -->
	 * @see #TRIANGULAR_VALUE
	 * @generated
	 * @ordered
	 */
	TRIANGULAR(14, "triangular", "triangular"),
	/**
	 * The '<em><b>Binomial</b></em>' literal object.
	 * <!-- begin-user-doc -->
	* <!-- end-user-doc -->
	 * @see #BINOMIAL_VALUE
	 * @generated
	 * @ordered
	 */
	BINOMIAL(15, "binomial", "binomial"),
	/**
	 * The '<em><b>Poisson</b></em>' literal object.
	 * <!-- begin-user-doc -->
	* <!-- end-user-doc -->
	 * @see #POISSON_VALUE
	 * @generated
	 * @ordered
	 */
	POISSON(16, "poisson", "poisson"),
	/**
	 * The '<em><b>Exponential</b></em>' literal object.
	 * <!-- begin-user-doc -->
	* <!-- end-user-doc -->
	 * @see #EXPONENTIAL_VALUE
	 * @generated
	 * @ordered
	 */
	EXPONENTIAL(17, "exponential", "exponential"),
	/**
	 * The '<em><b>Gamma</b></em>' literal object.
	 * <!-- begin-user-doc -->
	* <!-- end-user-doc -->
	 * @see #GAMMA_VALUE
	 * @generated
	 * @ordered
	 */
	GAMMA(18, "gamma", "gamma");

	/**
	 * The '<em><b>Formula</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #FORMULA
	 * @model name="formula"
	 * @generated
	 * @ordered
	 */
	public static final int FORMULA_VALUE = 1;

	/**
	 * The '<em><b>Normal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #NORMAL
	 * @model name="normal"
	 * @generated
	 * @ordered
	 */
	public static final int NORMAL_VALUE = 10;

	/**
	 * The '<em><b>Uniform</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #UNIFORM
	 * @model name="uniform"
	 * @generated
	 * @ordered
	 */
	public static final int UNIFORM_VALUE = 11;

	/**
	 * The '<em><b>Lognormal</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #LOGNORMAL
	 * @model name="lognormal"
	 * @generated
	 * @ordered
	 */
	public static final int LOGNORMAL_VALUE = 12;

	/**
	 * The '<em><b>Beta</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BETA
	 * @model name="beta"
	 * @generated
	 * @ordered
	 */
	public static final int BETA_VALUE = 13;

	/**
	 * The '<em><b>Triangular</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #TRIANGULAR
	 * @model name="triangular"
	 * @generated
	 * @ordered
	 */
	public static final int TRIANGULAR_VALUE = 14;

	/**
	 * The '<em><b>Binomial</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #BINOMIAL
	 * @model name="binomial"
	 * @generated
	 * @ordered
	 */
	public static final int BINOMIAL_VALUE = 15;

	/**
	 * The '<em><b>Poisson</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #POISSON
	 * @model name="poisson"
	 * @generated
	 * @ordered
	 */
	public static final int POISSON_VALUE = 16;

	/**
	 * The '<em><b>Exponential</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #EXPONENTIAL
	 * @model name="exponential"
	 * @generated
	 * @ordered
	 */
	public static final int EXPONENTIAL_VALUE = 17;

	/**
	 * The '<em><b>Gamma</b></em>' literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #GAMMA
	 * @model name="gamma"
	 * @generated
	 * @ordered
	 */
	public static final int GAMMA_VALUE = 18;

	/**
	 * An array of all the '<em><b>Variable Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static final VariableType[] VALUES_ARRAY = new VariableType[] {
			FORMULA,
			NORMAL,
			UNIFORM,
			LOGNORMAL,
			BETA,
			TRIANGULAR,
			BINOMIAL,
			POISSON,
			EXPONENTIAL,
			GAMMA,
		};

	/**
	 * A public read-only list of all the '<em><b>Variable Type</b></em>' enumerators.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final List<VariableType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

	/**
	 * Returns the '<em><b>Variable Type</b></em>' literal with the specified literal value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param literal the literal.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static VariableType get(String literal) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			VariableType result = VALUES_ARRAY[i];
			if (result.toString().equals(literal)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Variable Type</b></em>' literal with the specified name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param name the name.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static VariableType getByName(String name) {
		for (int i = 0; i < VALUES_ARRAY.length; ++i) {
			VariableType result = VALUES_ARRAY[i];
			if (result.getName().equals(name)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Returns the '<em><b>Variable Type</b></em>' literal with the specified integer value.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the integer value.
	 * @return the matching enumerator or <code>null</code>.
	 * @generated
	 */
	public static VariableType get(int value) {
		switch (value) {
			case FORMULA_VALUE: return FORMULA;
			case NORMAL_VALUE: return NORMAL;
			case UNIFORM_VALUE: return UNIFORM;
			case LOGNORMAL_VALUE: return LOGNORMAL;
			case BETA_VALUE: return BETA;
			case TRIANGULAR_VALUE: return TRIANGULAR;
			case BINOMIAL_VALUE: return BINOMIAL;
			case POISSON_VALUE: return POISSON;
			case EXPONENTIAL_VALUE: return EXPONENTIAL;
			case GAMMA_VALUE: return GAMMA;
		}
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final int value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String name;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private final String literal;

	/**
	 * Only this class can construct instances.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private VariableType(int value, String name, String literal) {
		this.value = value;
		this.name = name;
		this.literal = literal;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getValue() {
	  return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getName() {
	  return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getLiteral() {
	  return literal;
	}

	/**
	 * Returns the literal value of the enumerator, which is its string representation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		return literal;
	}

} //VariableType
