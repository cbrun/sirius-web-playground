/**
 */
package fr.obeo.dsl.guesstimate;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see fr.obeo.dsl.guesstimate.GuesstimateFactory
 * @model kind="package"
 * @generated
 */
public interface GuesstimatePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "guesstimate";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.obeo.fr/guesstimate";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "guesstimate";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	GuesstimatePackage eINSTANCE = fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl.init();

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.VariableImpl <em>Variable</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.VariableImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getVariable()
	 * @generated
	 */
	int VARIABLE = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Documentation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__DOCUMENTATION = 1;

	/**
	 * The feature id for the '<em><b>Definition</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__DEFINITION = 2;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__TYPE = 3;

	/**
	 * The feature id for the '<em><b>Distribution</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE__DISTRIBUTION = 4;

	/**
	 * The number of structural features of the '<em>Variable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE_FEATURE_COUNT = 5;

	/**
	 * The number of operations of the '<em>Variable</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VARIABLE_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.DistributionSetting <em>Distribution Setting</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.DistributionSetting
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getDistributionSetting()
	 * @generated
	 */
	int DISTRIBUTION_SETTING = 1;

	/**
	 * The number of structural features of the '<em>Distribution Setting</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_SETTING_FEATURE_COUNT = 0;

	/**
	 * The number of operations of the '<em>Distribution Setting</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DISTRIBUTION_SETTING_OPERATION_COUNT = 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.NormalDistributionImpl <em>Normal Distribution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.NormalDistributionImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getNormalDistribution()
	 * @generated
	 */
	int NORMAL_DISTRIBUTION = 2;

	/**
	 * The feature id for the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION__MEAN = DISTRIBUTION_SETTING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Standard Deviation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION__STANDARD_DEVIATION = DISTRIBUTION_SETTING_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Normal Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION_FEATURE_COUNT = DISTRIBUTION_SETTING_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Normal Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NORMAL_DISTRIBUTION_OPERATION_COUNT = DISTRIBUTION_SETTING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.LogNormalDistributionImpl <em>Log Normal Distribution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.LogNormalDistributionImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getLogNormalDistribution()
	 * @generated
	 */
	int LOG_NORMAL_DISTRIBUTION = 3;

	/**
	 * The feature id for the '<em><b>Log Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION__LOG_MEAN = DISTRIBUTION_SETTING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Log Standard Deviation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION__LOG_STANDARD_DEVIATION = DISTRIBUTION_SETTING_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Log Normal Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION_FEATURE_COUNT = DISTRIBUTION_SETTING_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Log Normal Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOG_NORMAL_DISTRIBUTION_OPERATION_COUNT = DISTRIBUTION_SETTING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.UniformDistributionImpl <em>Uniform Distribution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.UniformDistributionImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getUniformDistribution()
	 * @generated
	 */
	int UNIFORM_DISTRIBUTION = 4;

	/**
	 * The feature id for the '<em><b>Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION__MIN = DISTRIBUTION_SETTING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION__MAX = DISTRIBUTION_SETTING_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Uniform Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION_FEATURE_COUNT = DISTRIBUTION_SETTING_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Uniform Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNIFORM_DISTRIBUTION_OPERATION_COUNT = DISTRIBUTION_SETTING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.BetaDistributionImpl <em>Beta Distribution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.BetaDistributionImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getBetaDistribution()
	 * @generated
	 */
	int BETA_DISTRIBUTION = 5;

	/**
	 * The feature id for the '<em><b>Alpha</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION__ALPHA = DISTRIBUTION_SETTING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Beta</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION__BETA = DISTRIBUTION_SETTING_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Beta Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION_FEATURE_COUNT = DISTRIBUTION_SETTING_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Beta Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BETA_DISTRIBUTION_OPERATION_COUNT = DISTRIBUTION_SETTING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.TriangularDistributionImpl <em>Triangular Distribution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.TriangularDistributionImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getTriangularDistribution()
	 * @generated
	 */
	int TRIANGULAR_DISTRIBUTION = 6;

	/**
	 * The feature id for the '<em><b>Min</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION__MIN = DISTRIBUTION_SETTING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Max</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION__MAX = DISTRIBUTION_SETTING_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION__MODE = DISTRIBUTION_SETTING_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Triangular Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_FEATURE_COUNT = DISTRIBUTION_SETTING_FEATURE_COUNT + 3;

	/**
	 * The number of operations of the '<em>Triangular Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TRIANGULAR_DISTRIBUTION_OPERATION_COUNT = DISTRIBUTION_SETTING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.BinomialDistributionImpl <em>Binomial Distribution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.BinomialDistributionImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getBinomialDistribution()
	 * @generated
	 */
	int BINOMIAL_DISTRIBUTION = 7;

	/**
	 * The feature id for the '<em><b>Trials</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION__TRIALS = DISTRIBUTION_SETTING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Probability Of Success</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION__PROBABILITY_OF_SUCCESS = DISTRIBUTION_SETTING_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Binomial Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION_FEATURE_COUNT = DISTRIBUTION_SETTING_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Binomial Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINOMIAL_DISTRIBUTION_OPERATION_COUNT = DISTRIBUTION_SETTING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.FormulaSettingImpl <em>Formula Setting</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.FormulaSettingImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getFormulaSetting()
	 * @generated
	 */
	int FORMULA_SETTING = 8;

	/**
	 * The feature id for the '<em><b>Formula</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMULA_SETTING__FORMULA = DISTRIBUTION_SETTING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Inputs</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMULA_SETTING__INPUTS = DISTRIBUTION_SETTING_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Formula Setting</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMULA_SETTING_FEATURE_COUNT = DISTRIBUTION_SETTING_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Formula Setting</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FORMULA_SETTING_OPERATION_COUNT = DISTRIBUTION_SETTING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.SheetImpl <em>Sheet</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.SheetImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getSheet()
	 * @generated
	 */
	int SHEET = 9;

	/**
	 * The feature id for the '<em><b>Variables</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SHEET__VARIABLES = 0;

	/**
	 * The feature id for the '<em><b>Sample Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SHEET__SAMPLE_SIZE = 1;

	/**
	 * The number of structural features of the '<em>Sheet</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SHEET_FEATURE_COUNT = 2;

	/**
	 * The operation id for the '<em>Resample</em>' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SHEET___RESAMPLE = 0;

	/**
	 * The number of operations of the '<em>Sheet</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SHEET_OPERATION_COUNT = 1;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.PoissonDistributionImpl <em>Poisson Distribution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.PoissonDistributionImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getPoissonDistribution()
	 * @generated
	 */
	int POISSON_DISTRIBUTION = 10;

	/**
	 * The feature id for the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POISSON_DISTRIBUTION__MEAN = DISTRIBUTION_SETTING_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Poisson Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POISSON_DISTRIBUTION_FEATURE_COUNT = DISTRIBUTION_SETTING_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Poisson Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int POISSON_DISTRIBUTION_OPERATION_COUNT = DISTRIBUTION_SETTING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.ExponentialDistributionImpl <em>Exponential Distribution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.ExponentialDistributionImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getExponentialDistribution()
	 * @generated
	 */
	int EXPONENTIAL_DISTRIBUTION = 11;

	/**
	 * The feature id for the '<em><b>Mean</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPONENTIAL_DISTRIBUTION__MEAN = DISTRIBUTION_SETTING_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Exponential Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPONENTIAL_DISTRIBUTION_FEATURE_COUNT = DISTRIBUTION_SETTING_FEATURE_COUNT + 1;

	/**
	 * The number of operations of the '<em>Exponential Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXPONENTIAL_DISTRIBUTION_OPERATION_COUNT = DISTRIBUTION_SETTING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.impl.GammaDistributionImpl <em>Gamma Distribution</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.impl.GammaDistributionImpl
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getGammaDistribution()
	 * @generated
	 */
	int GAMMA_DISTRIBUTION = 12;

	/**
	 * The feature id for the '<em><b>Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION__SHAPE = DISTRIBUTION_SETTING_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Scale</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION__SCALE = DISTRIBUTION_SETTING_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Gamma Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION_FEATURE_COUNT = DISTRIBUTION_SETTING_FEATURE_COUNT + 2;

	/**
	 * The number of operations of the '<em>Gamma Distribution</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int GAMMA_DISTRIBUTION_OPERATION_COUNT = DISTRIBUTION_SETTING_OPERATION_COUNT + 0;

	/**
	 * The meta object id for the '{@link fr.obeo.dsl.guesstimate.VariableType <em>Variable Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see fr.obeo.dsl.guesstimate.VariableType
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getVariableType()
	 * @generated
	 */
	int VARIABLE_TYPE = 13;

	/**
	 * The meta object id for the '<em>Probability</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.lang.Double
	 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getProbability()
	 * @generated
	 */
	int PROBABILITY = 14;

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.Variable <em>Variable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Variable</em>'.
	 * @see fr.obeo.dsl.guesstimate.Variable
	 * @generated
	 */
	EClass getVariable();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.Variable#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see fr.obeo.dsl.guesstimate.Variable#getName()
	 * @see #getVariable()
	 * @generated
	 */
	EAttribute getVariable_Name();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.Variable#getDocumentation <em>Documentation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Documentation</em>'.
	 * @see fr.obeo.dsl.guesstimate.Variable#getDocumentation()
	 * @see #getVariable()
	 * @generated
	 */
	EAttribute getVariable_Documentation();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.Variable#getDefinition <em>Definition</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Definition</em>'.
	 * @see fr.obeo.dsl.guesstimate.Variable#getDefinition()
	 * @see #getVariable()
	 * @generated
	 */
	EAttribute getVariable_Definition();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.Variable#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see fr.obeo.dsl.guesstimate.Variable#getType()
	 * @see #getVariable()
	 * @generated
	 */
	EAttribute getVariable_Type();

	/**
	 * Returns the meta object for the containment reference '{@link fr.obeo.dsl.guesstimate.Variable#getDistribution <em>Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Distribution</em>'.
	 * @see fr.obeo.dsl.guesstimate.Variable#getDistribution()
	 * @see #getVariable()
	 * @generated
	 */
	EReference getVariable_Distribution();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.DistributionSetting <em>Distribution Setting</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Distribution Setting</em>'.
	 * @see fr.obeo.dsl.guesstimate.DistributionSetting
	 * @generated
	 */
	EClass getDistributionSetting();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.NormalDistribution <em>Normal Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Normal Distribution</em>'.
	 * @see fr.obeo.dsl.guesstimate.NormalDistribution
	 * @generated
	 */
	EClass getNormalDistribution();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.NormalDistribution#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see fr.obeo.dsl.guesstimate.NormalDistribution#getMean()
	 * @see #getNormalDistribution()
	 * @generated
	 */
	EAttribute getNormalDistribution_Mean();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.NormalDistribution#getStandardDeviation <em>Standard Deviation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Standard Deviation</em>'.
	 * @see fr.obeo.dsl.guesstimate.NormalDistribution#getStandardDeviation()
	 * @see #getNormalDistribution()
	 * @generated
	 */
	EAttribute getNormalDistribution_StandardDeviation();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.LogNormalDistribution <em>Log Normal Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Log Normal Distribution</em>'.
	 * @see fr.obeo.dsl.guesstimate.LogNormalDistribution
	 * @generated
	 */
	EClass getLogNormalDistribution();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.LogNormalDistribution#getLogMean <em>Log Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Log Mean</em>'.
	 * @see fr.obeo.dsl.guesstimate.LogNormalDistribution#getLogMean()
	 * @see #getLogNormalDistribution()
	 * @generated
	 */
	EAttribute getLogNormalDistribution_LogMean();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.LogNormalDistribution#getLogStandardDeviation <em>Log Standard Deviation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Log Standard Deviation</em>'.
	 * @see fr.obeo.dsl.guesstimate.LogNormalDistribution#getLogStandardDeviation()
	 * @see #getLogNormalDistribution()
	 * @generated
	 */
	EAttribute getLogNormalDistribution_LogStandardDeviation();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.UniformDistribution <em>Uniform Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Uniform Distribution</em>'.
	 * @see fr.obeo.dsl.guesstimate.UniformDistribution
	 * @generated
	 */
	EClass getUniformDistribution();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.UniformDistribution#getMin <em>Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min</em>'.
	 * @see fr.obeo.dsl.guesstimate.UniformDistribution#getMin()
	 * @see #getUniformDistribution()
	 * @generated
	 */
	EAttribute getUniformDistribution_Min();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.UniformDistribution#getMax <em>Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max</em>'.
	 * @see fr.obeo.dsl.guesstimate.UniformDistribution#getMax()
	 * @see #getUniformDistribution()
	 * @generated
	 */
	EAttribute getUniformDistribution_Max();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.BetaDistribution <em>Beta Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Beta Distribution</em>'.
	 * @see fr.obeo.dsl.guesstimate.BetaDistribution
	 * @generated
	 */
	EClass getBetaDistribution();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.BetaDistribution#getAlpha <em>Alpha</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Alpha</em>'.
	 * @see fr.obeo.dsl.guesstimate.BetaDistribution#getAlpha()
	 * @see #getBetaDistribution()
	 * @generated
	 */
	EAttribute getBetaDistribution_Alpha();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.BetaDistribution#getBeta <em>Beta</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Beta</em>'.
	 * @see fr.obeo.dsl.guesstimate.BetaDistribution#getBeta()
	 * @see #getBetaDistribution()
	 * @generated
	 */
	EAttribute getBetaDistribution_Beta();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.TriangularDistribution <em>Triangular Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Triangular Distribution</em>'.
	 * @see fr.obeo.dsl.guesstimate.TriangularDistribution
	 * @generated
	 */
	EClass getTriangularDistribution();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.TriangularDistribution#getMin <em>Min</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Min</em>'.
	 * @see fr.obeo.dsl.guesstimate.TriangularDistribution#getMin()
	 * @see #getTriangularDistribution()
	 * @generated
	 */
	EAttribute getTriangularDistribution_Min();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.TriangularDistribution#getMax <em>Max</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Max</em>'.
	 * @see fr.obeo.dsl.guesstimate.TriangularDistribution#getMax()
	 * @see #getTriangularDistribution()
	 * @generated
	 */
	EAttribute getTriangularDistribution_Max();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.TriangularDistribution#getMode <em>Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mode</em>'.
	 * @see fr.obeo.dsl.guesstimate.TriangularDistribution#getMode()
	 * @see #getTriangularDistribution()
	 * @generated
	 */
	EAttribute getTriangularDistribution_Mode();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.BinomialDistribution <em>Binomial Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Binomial Distribution</em>'.
	 * @see fr.obeo.dsl.guesstimate.BinomialDistribution
	 * @generated
	 */
	EClass getBinomialDistribution();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.BinomialDistribution#getTrials <em>Trials</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Trials</em>'.
	 * @see fr.obeo.dsl.guesstimate.BinomialDistribution#getTrials()
	 * @see #getBinomialDistribution()
	 * @generated
	 */
	EAttribute getBinomialDistribution_Trials();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.BinomialDistribution#getProbabilityOfSuccess <em>Probability Of Success</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Probability Of Success</em>'.
	 * @see fr.obeo.dsl.guesstimate.BinomialDistribution#getProbabilityOfSuccess()
	 * @see #getBinomialDistribution()
	 * @generated
	 */
	EAttribute getBinomialDistribution_ProbabilityOfSuccess();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.FormulaSetting <em>Formula Setting</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Formula Setting</em>'.
	 * @see fr.obeo.dsl.guesstimate.FormulaSetting
	 * @generated
	 */
	EClass getFormulaSetting();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.FormulaSetting#getFormula <em>Formula</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Formula</em>'.
	 * @see fr.obeo.dsl.guesstimate.FormulaSetting#getFormula()
	 * @see #getFormulaSetting()
	 * @generated
	 */
	EAttribute getFormulaSetting_Formula();

	/**
	 * Returns the meta object for the reference list '{@link fr.obeo.dsl.guesstimate.FormulaSetting#getInputs <em>Inputs</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Inputs</em>'.
	 * @see fr.obeo.dsl.guesstimate.FormulaSetting#getInputs()
	 * @see #getFormulaSetting()
	 * @generated
	 */
	EReference getFormulaSetting_Inputs();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.Sheet <em>Sheet</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sheet</em>'.
	 * @see fr.obeo.dsl.guesstimate.Sheet
	 * @generated
	 */
	EClass getSheet();

	/**
	 * Returns the meta object for the containment reference list '{@link fr.obeo.dsl.guesstimate.Sheet#getVariables <em>Variables</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Variables</em>'.
	 * @see fr.obeo.dsl.guesstimate.Sheet#getVariables()
	 * @see #getSheet()
	 * @generated
	 */
	EReference getSheet_Variables();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.Sheet#getSampleSize <em>Sample Size</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Sample Size</em>'.
	 * @see fr.obeo.dsl.guesstimate.Sheet#getSampleSize()
	 * @see #getSheet()
	 * @generated
	 */
	EAttribute getSheet_SampleSize();

	/**
	 * Returns the meta object for the '{@link fr.obeo.dsl.guesstimate.Sheet#resample() <em>Resample</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the '<em>Resample</em>' operation.
	 * @see fr.obeo.dsl.guesstimate.Sheet#resample()
	 * @generated
	 */
	EOperation getSheet__Resample();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.PoissonDistribution <em>Poisson Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Poisson Distribution</em>'.
	 * @see fr.obeo.dsl.guesstimate.PoissonDistribution
	 * @generated
	 */
	EClass getPoissonDistribution();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.PoissonDistribution#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see fr.obeo.dsl.guesstimate.PoissonDistribution#getMean()
	 * @see #getPoissonDistribution()
	 * @generated
	 */
	EAttribute getPoissonDistribution_Mean();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.ExponentialDistribution <em>Exponential Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Exponential Distribution</em>'.
	 * @see fr.obeo.dsl.guesstimate.ExponentialDistribution
	 * @generated
	 */
	EClass getExponentialDistribution();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.ExponentialDistribution#getMean <em>Mean</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mean</em>'.
	 * @see fr.obeo.dsl.guesstimate.ExponentialDistribution#getMean()
	 * @see #getExponentialDistribution()
	 * @generated
	 */
	EAttribute getExponentialDistribution_Mean();

	/**
	 * Returns the meta object for class '{@link fr.obeo.dsl.guesstimate.GammaDistribution <em>Gamma Distribution</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Gamma Distribution</em>'.
	 * @see fr.obeo.dsl.guesstimate.GammaDistribution
	 * @generated
	 */
	EClass getGammaDistribution();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.GammaDistribution#getShape <em>Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Shape</em>'.
	 * @see fr.obeo.dsl.guesstimate.GammaDistribution#getShape()
	 * @see #getGammaDistribution()
	 * @generated
	 */
	EAttribute getGammaDistribution_Shape();

	/**
	 * Returns the meta object for the attribute '{@link fr.obeo.dsl.guesstimate.GammaDistribution#getScale <em>Scale</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Scale</em>'.
	 * @see fr.obeo.dsl.guesstimate.GammaDistribution#getScale()
	 * @see #getGammaDistribution()
	 * @generated
	 */
	EAttribute getGammaDistribution_Scale();

	/**
	 * Returns the meta object for enum '{@link fr.obeo.dsl.guesstimate.VariableType <em>Variable Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Variable Type</em>'.
	 * @see fr.obeo.dsl.guesstimate.VariableType
	 * @generated
	 */
	EEnum getVariableType();

	/**
	 * Returns the meta object for data type '{@link java.lang.Double <em>Probability</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * A finite probability between 0 and 1, inclusive.
     * <!-- end-model-doc -->
	 * @return the meta object for data type '<em>Probability</em>'.
	 * @see java.lang.Double
	 * @model instanceClass="java.lang.Double"
	 *        annotation="http://www.eclipse.org/emf/2002/Ecore constraints='valueIsValid'"
	 * @generated
	 */
	EDataType getProbability();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	GuesstimateFactory getGuesstimateFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each operation of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.VariableImpl <em>Variable</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.VariableImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getVariable()
		 * @generated
		 */
		EClass VARIABLE = eINSTANCE.getVariable();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VARIABLE__NAME = eINSTANCE.getVariable_Name();

		/**
		 * The meta object literal for the '<em><b>Documentation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VARIABLE__DOCUMENTATION = eINSTANCE.getVariable_Documentation();

		/**
		 * The meta object literal for the '<em><b>Definition</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VARIABLE__DEFINITION = eINSTANCE.getVariable_Definition();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VARIABLE__TYPE = eINSTANCE.getVariable_Type();

		/**
		 * The meta object literal for the '<em><b>Distribution</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VARIABLE__DISTRIBUTION = eINSTANCE.getVariable_Distribution();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.DistributionSetting <em>Distribution Setting</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.DistributionSetting
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getDistributionSetting()
		 * @generated
		 */
		EClass DISTRIBUTION_SETTING = eINSTANCE.getDistributionSetting();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.NormalDistributionImpl <em>Normal Distribution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.NormalDistributionImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getNormalDistribution()
		 * @generated
		 */
		EClass NORMAL_DISTRIBUTION = eINSTANCE.getNormalDistribution();

		/**
		 * The meta object literal for the '<em><b>Mean</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NORMAL_DISTRIBUTION__MEAN = eINSTANCE.getNormalDistribution_Mean();

		/**
		 * The meta object literal for the '<em><b>Standard Deviation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NORMAL_DISTRIBUTION__STANDARD_DEVIATION = eINSTANCE.getNormalDistribution_StandardDeviation();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.LogNormalDistributionImpl <em>Log Normal Distribution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.LogNormalDistributionImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getLogNormalDistribution()
		 * @generated
		 */
		EClass LOG_NORMAL_DISTRIBUTION = eINSTANCE.getLogNormalDistribution();

		/**
		 * The meta object literal for the '<em><b>Log Mean</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOG_NORMAL_DISTRIBUTION__LOG_MEAN = eINSTANCE.getLogNormalDistribution_LogMean();

		/**
		 * The meta object literal for the '<em><b>Log Standard Deviation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOG_NORMAL_DISTRIBUTION__LOG_STANDARD_DEVIATION = eINSTANCE.getLogNormalDistribution_LogStandardDeviation();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.UniformDistributionImpl <em>Uniform Distribution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.UniformDistributionImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getUniformDistribution()
		 * @generated
		 */
		EClass UNIFORM_DISTRIBUTION = eINSTANCE.getUniformDistribution();

		/**
		 * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNIFORM_DISTRIBUTION__MIN = eINSTANCE.getUniformDistribution_Min();

		/**
		 * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute UNIFORM_DISTRIBUTION__MAX = eINSTANCE.getUniformDistribution_Max();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.BetaDistributionImpl <em>Beta Distribution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.BetaDistributionImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getBetaDistribution()
		 * @generated
		 */
		EClass BETA_DISTRIBUTION = eINSTANCE.getBetaDistribution();

		/**
		 * The meta object literal for the '<em><b>Alpha</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BETA_DISTRIBUTION__ALPHA = eINSTANCE.getBetaDistribution_Alpha();

		/**
		 * The meta object literal for the '<em><b>Beta</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BETA_DISTRIBUTION__BETA = eINSTANCE.getBetaDistribution_Beta();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.TriangularDistributionImpl <em>Triangular Distribution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.TriangularDistributionImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getTriangularDistribution()
		 * @generated
		 */
		EClass TRIANGULAR_DISTRIBUTION = eINSTANCE.getTriangularDistribution();

		/**
		 * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRIANGULAR_DISTRIBUTION__MIN = eINSTANCE.getTriangularDistribution_Min();

		/**
		 * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRIANGULAR_DISTRIBUTION__MAX = eINSTANCE.getTriangularDistribution_Max();

		/**
		 * The meta object literal for the '<em><b>Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute TRIANGULAR_DISTRIBUTION__MODE = eINSTANCE.getTriangularDistribution_Mode();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.BinomialDistributionImpl <em>Binomial Distribution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.BinomialDistributionImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getBinomialDistribution()
		 * @generated
		 */
		EClass BINOMIAL_DISTRIBUTION = eINSTANCE.getBinomialDistribution();

		/**
		 * The meta object literal for the '<em><b>Trials</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BINOMIAL_DISTRIBUTION__TRIALS = eINSTANCE.getBinomialDistribution_Trials();

		/**
		 * The meta object literal for the '<em><b>Probability Of Success</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute BINOMIAL_DISTRIBUTION__PROBABILITY_OF_SUCCESS = eINSTANCE.getBinomialDistribution_ProbabilityOfSuccess();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.FormulaSettingImpl <em>Formula Setting</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.FormulaSettingImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getFormulaSetting()
		 * @generated
		 */
		EClass FORMULA_SETTING = eINSTANCE.getFormulaSetting();

		/**
		 * The meta object literal for the '<em><b>Formula</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FORMULA_SETTING__FORMULA = eINSTANCE.getFormulaSetting_Formula();

		/**
		 * The meta object literal for the '<em><b>Inputs</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FORMULA_SETTING__INPUTS = eINSTANCE.getFormulaSetting_Inputs();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.SheetImpl <em>Sheet</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.SheetImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getSheet()
		 * @generated
		 */
		EClass SHEET = eINSTANCE.getSheet();

		/**
		 * The meta object literal for the '<em><b>Variables</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SHEET__VARIABLES = eINSTANCE.getSheet_Variables();

		/**
		 * The meta object literal for the '<em><b>Sample Size</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SHEET__SAMPLE_SIZE = eINSTANCE.getSheet_SampleSize();

		/**
		 * The meta object literal for the '<em><b>Resample</b></em>' operation.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EOperation SHEET___RESAMPLE = eINSTANCE.getSheet__Resample();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.PoissonDistributionImpl <em>Poisson Distribution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.PoissonDistributionImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getPoissonDistribution()
		 * @generated
		 */
		EClass POISSON_DISTRIBUTION = eINSTANCE.getPoissonDistribution();

		/**
		 * The meta object literal for the '<em><b>Mean</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute POISSON_DISTRIBUTION__MEAN = eINSTANCE.getPoissonDistribution_Mean();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.ExponentialDistributionImpl <em>Exponential Distribution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.ExponentialDistributionImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getExponentialDistribution()
		 * @generated
		 */
		EClass EXPONENTIAL_DISTRIBUTION = eINSTANCE.getExponentialDistribution();

		/**
		 * The meta object literal for the '<em><b>Mean</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXPONENTIAL_DISTRIBUTION__MEAN = eINSTANCE.getExponentialDistribution_Mean();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.impl.GammaDistributionImpl <em>Gamma Distribution</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.impl.GammaDistributionImpl
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getGammaDistribution()
		 * @generated
		 */
		EClass GAMMA_DISTRIBUTION = eINSTANCE.getGammaDistribution();

		/**
		 * The meta object literal for the '<em><b>Shape</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GAMMA_DISTRIBUTION__SHAPE = eINSTANCE.getGammaDistribution_Shape();

		/**
		 * The meta object literal for the '<em><b>Scale</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute GAMMA_DISTRIBUTION__SCALE = eINSTANCE.getGammaDistribution_Scale();

		/**
		 * The meta object literal for the '{@link fr.obeo.dsl.guesstimate.VariableType <em>Variable Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see fr.obeo.dsl.guesstimate.VariableType
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getVariableType()
		 * @generated
		 */
		EEnum VARIABLE_TYPE = eINSTANCE.getVariableType();

		/**
		 * The meta object literal for the '<em>Probability</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.lang.Double
		 * @see fr.obeo.dsl.guesstimate.impl.GuesstimatePackageImpl#getProbability()
		 * @generated
		 */
		EDataType PROBABILITY = eINSTANCE.getProbability();

	}

} //GuesstimatePackage
