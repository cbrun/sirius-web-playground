/**
 */
package fr.obeo.dsl.guesstimate.impl;

import fr.obeo.dsl.guesstimate.BetaDistribution;
import fr.obeo.dsl.guesstimate.BinomialDistribution;
import fr.obeo.dsl.guesstimate.ExponentialDistribution;
import fr.obeo.dsl.guesstimate.FormulaSetting;
import fr.obeo.dsl.guesstimate.GammaDistribution;
import fr.obeo.dsl.guesstimate.Variable;
import fr.obeo.dsl.guesstimate.VariableSettings;
import fr.obeo.dsl.guesstimate.GuesstimateFactory;
import fr.obeo.dsl.guesstimate.GuesstimatePackage;
import fr.obeo.dsl.guesstimate.VariableType;
import fr.obeo.dsl.guesstimate.LogNormalDistribution;
import fr.obeo.dsl.guesstimate.NormalDistribution;
import fr.obeo.dsl.guesstimate.PoissonDistribution;
import fr.obeo.dsl.guesstimate.Sheet;
import fr.obeo.dsl.guesstimate.TriangularDistribution;
import fr.obeo.dsl.guesstimate.UniformDistribution;
import fr.obeo.dsl.guesstimate.util.GuesstimateValidator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class GuesstimatePackageImpl extends EPackageImpl implements GuesstimatePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass variableEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass variableSettingsEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass normalDistributionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass logNormalDistributionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass uniformDistributionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass betaDistributionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass triangularDistributionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass binomialDistributionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass formulaSettingEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass sheetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass poissonDistributionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass exponentialDistributionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass gammaDistributionEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum variableTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType probabilityEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see fr.obeo.dsl.guesstimate.GuesstimatePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private GuesstimatePackageImpl() {
		super(eNS_URI, GuesstimateFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 *
	 * <p>This method is used to initialize {@link GuesstimatePackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static GuesstimatePackage init() {
		if (isInited) return (GuesstimatePackage)EPackage.Registry.INSTANCE.getEPackage(GuesstimatePackage.eNS_URI);

		// Obtain or create and register package
		Object registeredGuesstimatePackage = EPackage.Registry.INSTANCE.get(eNS_URI);
		GuesstimatePackageImpl theGuesstimatePackage = registeredGuesstimatePackage instanceof GuesstimatePackageImpl ? (GuesstimatePackageImpl)registeredGuesstimatePackage : new GuesstimatePackageImpl();

		isInited = true;

		// Create package meta-data objects
		theGuesstimatePackage.createPackageContents();

		// Initialize created meta-data
		theGuesstimatePackage.initializePackageContents();

		// Register package validator
		EValidator.Registry.INSTANCE.put
			(theGuesstimatePackage,
			 new EValidator.Descriptor() {
				 @Override
				 public EValidator getEValidator() {
					 return GuesstimateValidator.INSTANCE;
				 }
			 });

		// Mark meta-data to indicate it can't be changed
		theGuesstimatePackage.freeze();

		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(GuesstimatePackage.eNS_URI, theGuesstimatePackage);
		return theGuesstimatePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getVariable() {
		return variableEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getVariable_Name() {
		return (EAttribute)variableEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getVariable_Documentation() {
		return (EAttribute)variableEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getVariable_Definition() {
		return (EAttribute)variableEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getVariable_Type() {
		return (EAttribute)variableEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getVariable_Settings() {
		return (EReference)variableEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getVariableSettings() {
		return variableSettingsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getNormalDistribution() {
		return normalDistributionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNormalDistribution_Mean() {
		return (EAttribute)normalDistributionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getNormalDistribution_StandardDeviation() {
		return (EAttribute)normalDistributionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getLogNormalDistribution() {
		return logNormalDistributionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLogNormalDistribution_LogMean() {
		return (EAttribute)logNormalDistributionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getLogNormalDistribution_LogStandardDeviation() {
		return (EAttribute)logNormalDistributionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getUniformDistribution() {
		return uniformDistributionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUniformDistribution_Min() {
		return (EAttribute)uniformDistributionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getUniformDistribution_Max() {
		return (EAttribute)uniformDistributionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBetaDistribution() {
		return betaDistributionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBetaDistribution_Alpha() {
		return (EAttribute)betaDistributionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBetaDistribution_Beta() {
		return (EAttribute)betaDistributionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getTriangularDistribution() {
		return triangularDistributionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTriangularDistribution_Min() {
		return (EAttribute)triangularDistributionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTriangularDistribution_Max() {
		return (EAttribute)triangularDistributionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getTriangularDistribution_Mode() {
		return (EAttribute)triangularDistributionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getBinomialDistribution() {
		return binomialDistributionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBinomialDistribution_Trials() {
		return (EAttribute)binomialDistributionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getBinomialDistribution_ProbabilityOfSuccess() {
		return (EAttribute)binomialDistributionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getFormulaSetting() {
		return formulaSettingEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getFormulaSetting_Formula() {
		return (EAttribute)formulaSettingEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getSheet() {
		return sheetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EReference getSheet_Variables() {
		return (EReference)sheetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getSheet_SampleSize() {
		return (EAttribute)sheetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EOperation getSheet__Resample() {
		return sheetEClass.getEOperations().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getPoissonDistribution() {
		return poissonDistributionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getPoissonDistribution_Mean() {
		return (EAttribute)poissonDistributionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getExponentialDistribution() {
		return exponentialDistributionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getExponentialDistribution_Mean() {
		return (EAttribute)exponentialDistributionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EClass getGammaDistribution() {
		return gammaDistributionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGammaDistribution_Shape() {
		return (EAttribute)gammaDistributionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EAttribute getGammaDistribution_Scale() {
		return (EAttribute)gammaDistributionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EEnum getVariableType() {
		return variableTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EDataType getProbability() {
		return probabilityEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public GuesstimateFactory getGuesstimateFactory() {
		return (GuesstimateFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		variableEClass = createEClass(VARIABLE);
		createEAttribute(variableEClass, VARIABLE__NAME);
		createEAttribute(variableEClass, VARIABLE__DOCUMENTATION);
		createEAttribute(variableEClass, VARIABLE__DEFINITION);
		createEAttribute(variableEClass, VARIABLE__TYPE);
		createEReference(variableEClass, VARIABLE__SETTINGS);

		variableSettingsEClass = createEClass(VARIABLE_SETTINGS);

		normalDistributionEClass = createEClass(NORMAL_DISTRIBUTION);
		createEAttribute(normalDistributionEClass, NORMAL_DISTRIBUTION__MEAN);
		createEAttribute(normalDistributionEClass, NORMAL_DISTRIBUTION__STANDARD_DEVIATION);

		logNormalDistributionEClass = createEClass(LOG_NORMAL_DISTRIBUTION);
		createEAttribute(logNormalDistributionEClass, LOG_NORMAL_DISTRIBUTION__LOG_MEAN);
		createEAttribute(logNormalDistributionEClass, LOG_NORMAL_DISTRIBUTION__LOG_STANDARD_DEVIATION);

		uniformDistributionEClass = createEClass(UNIFORM_DISTRIBUTION);
		createEAttribute(uniformDistributionEClass, UNIFORM_DISTRIBUTION__MIN);
		createEAttribute(uniformDistributionEClass, UNIFORM_DISTRIBUTION__MAX);

		betaDistributionEClass = createEClass(BETA_DISTRIBUTION);
		createEAttribute(betaDistributionEClass, BETA_DISTRIBUTION__ALPHA);
		createEAttribute(betaDistributionEClass, BETA_DISTRIBUTION__BETA);

		triangularDistributionEClass = createEClass(TRIANGULAR_DISTRIBUTION);
		createEAttribute(triangularDistributionEClass, TRIANGULAR_DISTRIBUTION__MIN);
		createEAttribute(triangularDistributionEClass, TRIANGULAR_DISTRIBUTION__MAX);
		createEAttribute(triangularDistributionEClass, TRIANGULAR_DISTRIBUTION__MODE);

		binomialDistributionEClass = createEClass(BINOMIAL_DISTRIBUTION);
		createEAttribute(binomialDistributionEClass, BINOMIAL_DISTRIBUTION__TRIALS);
		createEAttribute(binomialDistributionEClass, BINOMIAL_DISTRIBUTION__PROBABILITY_OF_SUCCESS);

		formulaSettingEClass = createEClass(FORMULA_SETTING);
		createEAttribute(formulaSettingEClass, FORMULA_SETTING__FORMULA);

		sheetEClass = createEClass(SHEET);
		createEReference(sheetEClass, SHEET__VARIABLES);
		createEAttribute(sheetEClass, SHEET__SAMPLE_SIZE);
		createEOperation(sheetEClass, SHEET___RESAMPLE);

		poissonDistributionEClass = createEClass(POISSON_DISTRIBUTION);
		createEAttribute(poissonDistributionEClass, POISSON_DISTRIBUTION__MEAN);

		exponentialDistributionEClass = createEClass(EXPONENTIAL_DISTRIBUTION);
		createEAttribute(exponentialDistributionEClass, EXPONENTIAL_DISTRIBUTION__MEAN);

		gammaDistributionEClass = createEClass(GAMMA_DISTRIBUTION);
		createEAttribute(gammaDistributionEClass, GAMMA_DISTRIBUTION__SHAPE);
		createEAttribute(gammaDistributionEClass, GAMMA_DISTRIBUTION__SCALE);

		// Create enums
		variableTypeEEnum = createEEnum(VARIABLE_TYPE);

		// Create data types
		probabilityEDataType = createEDataType(PROBABILITY);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		normalDistributionEClass.getESuperTypes().add(this.getVariableSettings());
		logNormalDistributionEClass.getESuperTypes().add(this.getVariableSettings());
		uniformDistributionEClass.getESuperTypes().add(this.getVariableSettings());
		betaDistributionEClass.getESuperTypes().add(this.getVariableSettings());
		triangularDistributionEClass.getESuperTypes().add(this.getVariableSettings());
		binomialDistributionEClass.getESuperTypes().add(this.getVariableSettings());
		formulaSettingEClass.getESuperTypes().add(this.getVariableSettings());
		poissonDistributionEClass.getESuperTypes().add(this.getVariableSettings());
		exponentialDistributionEClass.getESuperTypes().add(this.getVariableSettings());
		gammaDistributionEClass.getESuperTypes().add(this.getVariableSettings());

		// Initialize classes, features, and operations; add parameters
		initEClass(variableEClass, Variable.class, "Variable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getVariable_Name(), ecorePackage.getEString(), "name", null, 1, 1, Variable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getVariable_Documentation(), ecorePackage.getEString(), "documentation", null, 0, 1, Variable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getVariable_Definition(), ecorePackage.getEString(), "definition", null, 0, 1, Variable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getVariable_Type(), this.getVariableType(), "type", null, 1, 1, Variable.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getVariable_Settings(), this.getVariableSettings(), null, "settings", null, 1, 1, Variable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(variableSettingsEClass, VariableSettings.class, "VariableSettings", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(normalDistributionEClass, NormalDistribution.class, "NormalDistribution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getNormalDistribution_Mean(), ecorePackage.getEDouble(), "mean", null, 0, 1, NormalDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNormalDistribution_StandardDeviation(), ecorePackage.getEDouble(), "standardDeviation", "1", 0, 1, NormalDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(logNormalDistributionEClass, LogNormalDistribution.class, "LogNormalDistribution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLogNormalDistribution_LogMean(), ecorePackage.getEDouble(), "logMean", null, 0, 1, LogNormalDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLogNormalDistribution_LogStandardDeviation(), ecorePackage.getEDouble(), "logStandardDeviation", "1", 0, 1, LogNormalDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(uniformDistributionEClass, UniformDistribution.class, "UniformDistribution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getUniformDistribution_Min(), ecorePackage.getEDouble(), "min", null, 0, 1, UniformDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getUniformDistribution_Max(), ecorePackage.getEDouble(), "max", "1", 0, 1, UniformDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(betaDistributionEClass, BetaDistribution.class, "BetaDistribution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBetaDistribution_Alpha(), ecorePackage.getEDouble(), "alpha", "2", 0, 1, BetaDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBetaDistribution_Beta(), ecorePackage.getEDouble(), "beta", "2", 0, 1, BetaDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(triangularDistributionEClass, TriangularDistribution.class, "TriangularDistribution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getTriangularDistribution_Min(), ecorePackage.getEDouble(), "min", "0", 0, 1, TriangularDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTriangularDistribution_Max(), ecorePackage.getEDouble(), "max", "1", 0, 1, TriangularDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getTriangularDistribution_Mode(), ecorePackage.getEDouble(), "mode", "0.5", 0, 1, TriangularDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(binomialDistributionEClass, BinomialDistribution.class, "BinomialDistribution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getBinomialDistribution_Trials(), ecorePackage.getEInt(), "trials", "10", 0, 1, BinomialDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getBinomialDistribution_ProbabilityOfSuccess(), this.getProbability(), "probabilityOfSuccess", "0.5", 1, 1, BinomialDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(formulaSettingEClass, FormulaSetting.class, "FormulaSetting", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getFormulaSetting_Formula(), ecorePackage.getEString(), "formula", "0", 1, 1, FormulaSetting.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(sheetEClass, Sheet.class, "Sheet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSheet_Variables(), this.getVariable(), null, "variables", null, 0, -1, Sheet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSheet_SampleSize(), ecorePackage.getEInt(), "sampleSize", "10000", 0, 1, Sheet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEOperation(getSheet__Resample(), null, "resample", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(poissonDistributionEClass, PoissonDistribution.class, "PoissonDistribution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPoissonDistribution_Mean(), ecorePackage.getEDouble(), "mean", "1", 0, 1, PoissonDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(exponentialDistributionEClass, ExponentialDistribution.class, "ExponentialDistribution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getExponentialDistribution_Mean(), ecorePackage.getEDouble(), "mean", "1", 0, 1, ExponentialDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(gammaDistributionEClass, GammaDistribution.class, "GammaDistribution", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getGammaDistribution_Shape(), ecorePackage.getEDouble(), "shape", "2", 0, 1, GammaDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getGammaDistribution_Scale(), ecorePackage.getEDouble(), "scale", "1", 0, 1, GammaDistribution.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(variableTypeEEnum, VariableType.class, "VariableType");
		addEEnumLiteral(variableTypeEEnum, VariableType.FORMULA);
		addEEnumLiteral(variableTypeEEnum, VariableType.NORMAL);
		addEEnumLiteral(variableTypeEEnum, VariableType.UNIFORM);
		addEEnumLiteral(variableTypeEEnum, VariableType.LOGNORMAL);
		addEEnumLiteral(variableTypeEEnum, VariableType.BETA);
		addEEnumLiteral(variableTypeEEnum, VariableType.TRIANGULAR);
		addEEnumLiteral(variableTypeEEnum, VariableType.BINOMIAL);
		addEEnumLiteral(variableTypeEEnum, VariableType.POISSON);
		addEEnumLiteral(variableTypeEEnum, VariableType.EXPONENTIAL);
		addEEnumLiteral(variableTypeEEnum, VariableType.GAMMA);

		// Initialize data types
		initEDataType(probabilityEDataType, Double.class, "Probability", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://www.eclipse.org/emf/2002/Ecore
		createEcoreAnnotations();
		// http://www.eclipse.org/emf/2002/GenModel
		createGenModelAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://www.eclipse.org/emf/2002/Ecore</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createEcoreAnnotations() {
		String source = "http://www.eclipse.org/emf/2002/Ecore";
		addAnnotation
		  (variableEClass,
		   source,
		   new String[] {
			   "constraints", "nameIsValid"
		   });
		addAnnotation
		  (normalDistributionEClass,
		   source,
		   new String[] {
			   "constraints", "parametersAreValid"
		   });
		addAnnotation
		  (logNormalDistributionEClass,
		   source,
		   new String[] {
			   "constraints", "parametersAreValid"
		   });
		addAnnotation
		  (uniformDistributionEClass,
		   source,
		   new String[] {
			   "constraints", "minMaxAreConsistent"
		   });
		addAnnotation
		  (betaDistributionEClass,
		   source,
		   new String[] {
			   "constraints", "parametersAreValid"
		   });
		addAnnotation
		  (triangularDistributionEClass,
		   source,
		   new String[] {
			   "constraints", "parametersAreValid"
		   });
		addAnnotation
		  (binomialDistributionEClass,
		   source,
		   new String[] {
			   "constraints", "trialsAreValid"
		   });
		addAnnotation
		  (formulaSettingEClass,
		   source,
		   new String[] {
			   "constraints", "unknownVariable invalidSyntax acyclicDependencies"
		   });
		addAnnotation
		  (sheetEClass,
		   source,
		   new String[] {
			   "constraints", "sampleSizeIsPositive"
		   });
		addAnnotation
		  (poissonDistributionEClass,
		   source,
		   new String[] {
			   "constraints", "meanIsPositive"
		   });
		addAnnotation
		  (exponentialDistributionEClass,
		   source,
		   new String[] {
			   "constraints", "meanIsPositive"
		   });
		addAnnotation
		  (gammaDistributionEClass,
		   source,
		   new String[] {
			   "constraints", "parametersAreValid"
		   });
		addAnnotation
		  (probabilityEDataType,
		   source,
		   new String[] {
			   "constraints", "valueIsValid"
		   });
	}

	/**
	 * Initializes the annotations for <b>http://www.eclipse.org/emf/2002/GenModel</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createGenModelAnnotations() {
		String source = "http://www.eclipse.org/emf/2002/GenModel";
		addAnnotation
		  (getVariable_Name(),
		   source,
		   new String[] {
			   "documentation", "The variable name. Must respect the rules of a programming language identifier."
		   });
		addAnnotation
		  (getVariable_Documentation(),
		   source,
		   new String[] {
			   "documentation", "Any human readable description."
		   });
		addAnnotation
		  (getVariable_Definition(),
		   source,
		   new String[] {
			   "documentation", "A shortcut in natural language to set the distribution kind and its parameters."
		   });
		addAnnotation
		  (normalDistributionEClass,
		   source,
		   new String[] {
			   "documentation", "The normal distribution is a continuous probability distribution that is symmetrical around its mean, forming a bell-shaped curve. It\'s defined by the mean (\u00b5) and standard deviation (\u03c3), with most data points falling close to the mean and probabilities tapering off equally in both directions. Use it for modeling natural phenomena like heights, test scores, or any situation where the Central Limit Theorem applies, such as averaging a large number of independent variables."
		   });
		addAnnotation
		  (getNormalDistribution_Mean(),
		   source,
		   new String[] {
			   "documentation", "The mean is the central value of the normal distribution, representing the average or expected value. It is the point around which the data is symmetrically distributed. In a dataset of people\'s heights, for example, the mean height is the average height of all individuals in the dataset."
		   });
		addAnnotation
		  (getNormalDistribution_StandardDeviation(),
		   source,
		   new String[] {
			   "documentation", "The standard deviation (\u03c3) measures the spread or dispersion of the distribution. It indicates how much the individual data points deviate from the mean. A smaller \u03c3 means the data points are closer to the mean, while a larger \u03c3 means they are more spread out. In the context of heights, a smaller standard deviation indicates that most people have heights close to the average, whereas a larger standard deviation indicates more variability in heights."
		   });
		addAnnotation
		  (logNormalDistributionEClass,
		   source,
		   new String[] {
			   "documentation", "The lognormal distribution is a continuous probability distribution where the logarithm of the variable follows a normal distribution. Defined by the mean (\u00b5) and standard deviation (\u03c3) of the variable\'s natural logarithm, it is right-skewed and suitable for modeling data that grows multiplicatively, such as stock prices, investment returns, and the sizes of biological organisms."
		   });
		addAnnotation
		  (getLogNormalDistribution_LogMean(),
		   source,
		   new String[] {
			   "documentation", "The mean of the natural logarithm of the variable. It determines the location of the distribution on the logarithmic scale."
		   });
		addAnnotation
		  (getLogNormalDistribution_LogStandardDeviation(),
		   source,
		   new String[] {
			   "documentation", "The strictly positive standard deviation of the natural logarithm of the variable. It controls the spread of the distribution on the logarithmic scale."
		   });
		addAnnotation
		  (uniformDistributionEClass,
		   source,
		   new String[] {
			   "documentation", "The uniform distribution is a continuous probability distribution where all outcomes are equally likely within a specified range, defined by minimum (a) and maximum (b) values. This distribution forms a flat, rectangular shape and is ideal for modeling scenarios with equally likely outcomes, like generating random numbers or simulating the roll of a fair die."
		   });
		addAnnotation
		  (getUniformDistribution_Min(),
		   source,
		   new String[] {
			   "documentation", "The minimum value is the lowest possible outcome in the uniform distribution. It represents the lower bound of the range over which all outcomes are equally likely. For example, if you are generating random numbers between 1 and 10, the minimum value (a) would be 1."
		   });
		addAnnotation
		  (getUniformDistribution_Max(),
		   source,
		   new String[] {
			   "documentation", "The maximum value (b) is the highest possible outcome in the uniform distribution. It sets the upper limit of the range where all outcomes are equally likely. In the same example of generating random numbers between 1 and 10, the maximum value (b) would be 10."
		   });
		addAnnotation
		  (betaDistributionEClass,
		   source,
		   new String[] {
			   "documentation", "The beta distribution is a continuous probability distribution defined on the interval [0, 1], often used to model probabilities themselves. With two shape parameters, \u03b1 (alpha) and \u03b2 (beta), it can take various forms (uniform, U-shaped, etc.), making it useful for modeling the probability of success in Bernoulli trials when the probability is not fixed, or in Bayesian statistics as a conjugate prior for binomial proportions."
		   });
		addAnnotation
		  (getBetaDistribution_Alpha(),
		   source,
		   new String[] {
			   "documentation", "Alpha (\u03b1) is a shape parameter that influences the distribution\'s shape. It is a positive real number that determines the skewness of the distribution. A higher value of \u03b1, when paired with a lower value of \u03b2, will skew the distribution to the right, indicating that higher probabilities are more likely. For instance, if you are modeling the probability of success in a series of trials, a higher \u03b1 might suggest that success is relatively more frequent."
		   });
		addAnnotation
		  (getBetaDistribution_Beta(),
		   source,
		   new String[] {
			   "documentation", "Beta (\u03b2) is another shape parameter that, like alpha, influences the shape of the distribution. It is also a positive real number. When \u03b2 is higher relative to \u03b1, the distribution skews to the left, indicating that lower probabilities are more likely. Together with \u03b1, \u03b2 defines the shape and scale of the beta distribution. For example, a higher \u03b2 might indicate that failure is more frequent in a series of trials."
		   });
		addAnnotation
		  (triangularDistributionEClass,
		   source,
		   new String[] {
			   "documentation", "The triangular distribution is a continuous probability distribution with a triangular-shaped probability density function, defined by three parameters: the minimum value (a), the maximum value (b), and the mode (c), which is the peak of the triangle. It is used when the exact distribution of data is unknown but a rough estimate with a known minimum, maximum, and most likely value can be provided. Common applications include project management for estimating the duration of tasks and in decision-making scenarios where limited sample data is available."
		   });
		addAnnotation
		  (getTriangularDistribution_Min(),
		   source,
		   new String[] {
			   "documentation", "The minimum value (a) is the lowest possible outcome in the triangular distribution. It represents the lower bound of the data range and ensures that no values in the distribution fall below this point. For example, if you are estimating the time to complete a task and the least amount of time it could take is 2 hours, then the minimum value (a) would be 2."
		   });
		addAnnotation
		  (getTriangularDistribution_Max(),
		   source,
		   new String[] {
			   "documentation", "The maximum value (b) is the highest possible outcome in the triangular distribution. It sets the upper limit of the data range, ensuring that no values exceed this point. For instance, if the most time it could take to complete the task is 10 hours, then the maximum value (b) would be 10."
		   });
		addAnnotation
		  (getTriangularDistribution_Mode(),
		   source,
		   new String[] {
			   "documentation", "The mode (c) is the most likely outcome within the range of the triangular distribution. It represents the peak of the triangle and is the value where the probability density is highest. In the context of task completion time, if the most likely time to complete the task is 5 hours, then the mode (c) would be 5. The mode must lie between the minimum (a) and maximum (b) values."
		   });
		addAnnotation
		  (binomialDistributionEClass,
		   source,
		   new String[] {
			   "documentation", "The binomial distribution is a discrete probability distribution that describes the number of successes in a fixed number of independent Bernoulli trials, each with the same probability of success (p). Defined by the number of trials (n) and probability of success (p), it is ideal for scenarios like modeling the number of heads in a series of coin tosses or the number of defective items in a batch of products."
		   });
		addAnnotation
		  (getBinomialDistribution_Trials(),
		   source,
		   new String[] {
			   "documentation", "The number of trials (n) in a binomial distribution is a non-negative integer representing the total number of independent and identical Bernoulli trials conducted. Each trial is an experiment or process with exactly two possible outcomes: success or failure. For example, in a scenario where you flip a coin 10 times, the number of trials (n) would be 10."
		   });
		addAnnotation
		  (getBinomialDistribution_ProbabilityOfSuccess(),
		   source,
		   new String[] {
			   "documentation", "The probability of success (p) is a value between 0 and 1, representing the likelihood of achieving a success in each individual trial. This probability remains constant across all trials. For instance, if you are rolling a die and interested in the probability of rolling a 4, the probability of success (p) would be 1/6. In the context of a coin flip, if you define heads as a success, the probability of success (p) would be 0.5 (assuming a fair coin)."
		   });
		addAnnotation
		  (formulaSettingEClass,
		   source,
		   new String[] {
			   "documentation", "A variable which is defined by a formula using other variables."
		   });
		addAnnotation
		  (poissonDistributionEClass,
		   source,
		   new String[] {
			   "documentation", "The Poisson distribution is a discrete probability distribution that expresses the probability of a given number of events occurring in a fixed interval of time or space. Defined by the rate parameter (\u03bb), it is suitable for modeling count data, such as the number of emails received per hour or the number of accidents at a traffic intersection in a day."
		   });
		addAnnotation
		  (getPoissonDistribution_Mean(),
		   source,
		   new String[] {
			   "documentation", "The strictly positive expected number of events in the observed interval, also known as the Poisson rate parameter \u03bb."
		   });
		addAnnotation
		  (exponentialDistributionEClass,
		   source,
		   new String[] {
			   "documentation", "The exponential distribution is a continuous probability distribution often used to model the time between events in a Poisson process. It is characterized here by its strictly positive mean and has a peak at zero with a long tail extending to the right."
		   });
		addAnnotation
		  (gammaDistributionEClass,
		   source,
		   new String[] {
			   "documentation", "The gamma distribution is a continuous probability distribution defined by strictly positive shape (k) and scale (\u03b8) parameters. It is useful for modeling waiting times with multiple stages, such as the time until the k-th event in a queuing process."
		   });
		addAnnotation
		  (probabilityEDataType,
		   source,
		   new String[] {
			   "documentation", "A finite probability between 0 and 1, inclusive."
		   });
	}

} //GuesstimatePackageImpl
