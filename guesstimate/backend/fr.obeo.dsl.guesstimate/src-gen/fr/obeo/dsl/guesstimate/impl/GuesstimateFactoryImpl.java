/**
 */
package fr.obeo.dsl.guesstimate.impl;

import fr.obeo.dsl.guesstimate.*;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 * @generated
 */
public class GuesstimateFactoryImpl extends EFactoryImpl implements GuesstimateFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	public static GuesstimateFactory init() {
		try {
			GuesstimateFactory theGuesstimateFactory = (GuesstimateFactory) EPackage.Registry.INSTANCE
					.getEFactory(GuesstimatePackage.eNS_URI);
			if (theGuesstimateFactory != null) {
				return theGuesstimateFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new GuesstimateFactoryImpl();
	}

	/**
	 * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc
	 * -->
	 * 
	 * @generated
	 */
	public GuesstimateFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case GuesstimatePackage.VARIABLE:
			return createVariable();
		case GuesstimatePackage.NORMAL_DISTRIBUTION:
			return createNormalDistribution();
		case GuesstimatePackage.LOG_NORMAL_DISTRIBUTION:
			return createLogNormalDistribution();
		case GuesstimatePackage.UNIFORM_DISTRIBUTION:
			return createUniformDistribution();
		case GuesstimatePackage.BETA_DISTRIBUTION:
			return createBetaDistribution();
		case GuesstimatePackage.TRIANGULAR_DISTRIBUTION:
			return createTriangularDistribution();
		case GuesstimatePackage.BINOMIAL_DISTRIBUTION:
			return createBinomialDistribution();
		case GuesstimatePackage.FORMULA_SETTING:
			return createFormulaSetting();
		case GuesstimatePackage.SHEET:
			return createSheet();
		case GuesstimatePackage.POISSON_DISTRIBUTION:
			return createPoissonDistribution();
		case GuesstimatePackage.EXPONENTIAL_DISTRIBUTION:
			return createExponentialDistribution();
		case GuesstimatePackage.GAMMA_DISTRIBUTION:
			return createGammaDistribution();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case GuesstimatePackage.VARIABLE_TYPE:
			return createVariableTypeFromString(eDataType, initialValue);
		case GuesstimatePackage.PERCENTAGE:
			return createPercentageFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case GuesstimatePackage.VARIABLE_TYPE:
			return convertVariableTypeToString(eDataType, instanceValue);
		case GuesstimatePackage.PERCENTAGE:
			return convertPercentageToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Variable createVariable() {
		VariableImpl variable = new VariableImpl();
		return variable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NormalDistribution createNormalDistribution() {
		NormalDistributionImpl normalDistribution = new NormalDistributionImpl();
		return normalDistribution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LogNormalDistribution createLogNormalDistribution() {
		LogNormalDistributionImpl logNormalDistribution = new LogNormalDistributionImpl();
		return logNormalDistribution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public UniformDistribution createUniformDistribution() {
		UniformDistributionImpl uniformDistribution = new UniformDistributionImpl();
		return uniformDistribution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BetaDistribution createBetaDistribution() {
		BetaDistributionImpl betaDistribution = new BetaDistributionImpl();
		return betaDistribution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public TriangularDistribution createTriangularDistribution() {
		TriangularDistributionImpl triangularDistribution = new TriangularDistributionImpl();
		return triangularDistribution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public BinomialDistribution createBinomialDistribution() {
		BinomialDistributionImpl binomialDistribution = new BinomialDistributionImpl();
		return binomialDistribution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FormulaSetting createFormulaSetting() {
		FormulaSettingImpl formulaSetting = new FormulaSettingImpl();
		return formulaSetting;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public Sheet createSheet() {
		SheetImpl sheet = new SheetImpl();
		return sheet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PoissonDistribution createPoissonDistribution() {
		PoissonDistributionImpl poissonDistribution = new PoissonDistributionImpl();
		return poissonDistribution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ExponentialDistribution createExponentialDistribution() {
		ExponentialDistributionImpl exponentialDistribution = new ExponentialDistributionImpl();
		return exponentialDistribution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GammaDistribution createGammaDistribution() {
		GammaDistributionImpl gammaDistribution = new GammaDistributionImpl();
		return gammaDistribution;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public VariableType createVariableTypeFromString(EDataType eDataType, String initialValue) {
		VariableType result = VariableType.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException(
					"The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertVariableTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @not-generated
	 */
	public String convertThreePointsToString(EDataType eDataType, Object instanceValue) {
		return instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Double createPercentageFromString(EDataType eDataType, String initialValue) {
		return (Double) super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertPercentageToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	public GuesstimatePackage getGuesstimatePackage() {
		return (GuesstimatePackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static GuesstimatePackage getPackage() {
		return GuesstimatePackage.eINSTANCE;
	}

} // GuesstimateFactoryImpl
