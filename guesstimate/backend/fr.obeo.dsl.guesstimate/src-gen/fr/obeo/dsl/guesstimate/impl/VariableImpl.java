/**
 */
package fr.obeo.dsl.guesstimate.impl;

import fr.obeo.dsl.guesstimate.BetaDistribution;
import fr.obeo.dsl.guesstimate.BinomialDistribution;
import fr.obeo.dsl.guesstimate.ExponentialDistribution;
import fr.obeo.dsl.guesstimate.FormulaSetting;
import fr.obeo.dsl.guesstimate.GammaDistribution;
import fr.obeo.dsl.guesstimate.GuesstimateFactory;
import fr.obeo.dsl.guesstimate.LogNormalDistribution;
import fr.obeo.dsl.guesstimate.NormalDistribution;
import fr.obeo.dsl.guesstimate.PoissonDistribution;
import fr.obeo.dsl.guesstimate.TriangularDistribution;
import fr.obeo.dsl.guesstimate.UniformDistribution;
import fr.obeo.dsl.guesstimate.Variable;
import fr.obeo.dsl.guesstimate.VariableSettings;
import fr.obeo.dsl.guesstimate.GuesstimatePackage;
import fr.obeo.dsl.guesstimate.VariableType;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Variable</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.VariableImpl#getName <em>Name</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.VariableImpl#getDocumentation <em>Documentation</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.VariableImpl#getDefinition <em>Definition</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.VariableImpl#getType <em>Type</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.VariableImpl#getSettings <em>Settings</em>}</li>
 * </ul>
 *
 * @generated
 */
public class VariableImpl extends MinimalEObjectImpl.Container implements Variable {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #getDocumentation() <em>Documentation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDocumentation()
	 * @generated
	 * @ordered
	 */
	protected static final String DOCUMENTATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDocumentation() <em>Documentation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDocumentation()
	 * @generated
	 * @ordered
	 */
	protected String documentation = DOCUMENTATION_EDEFAULT;

	/**
	 * The default value of the '{@link #getDefinition() <em>Definition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefinition()
	 * @generated
	 * @ordered
	 */
	protected static final String DEFINITION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDefinition() <em>Definition</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDefinition()
	 * @generated
	 * @ordered
	 */
	protected String definition = DEFINITION_EDEFAULT;

	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final VariableType TYPE_EDEFAULT = VariableType.FORMULA;

	/**
	 * The cached value of the '{@link #getSettings() <em>Settings</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSettings()
	 * @generated
	 * @ordered
	 */
	protected VariableSettings settings;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected VariableImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GuesstimatePackage.Literals.VARIABLE;
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
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.VARIABLE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDocumentation() {
		return documentation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDocumentation(String newDocumentation) {
		String oldDocumentation = documentation;
		documentation = newDocumentation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.VARIABLE__DOCUMENTATION, oldDocumentation, documentation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDefinition() {
		return definition;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDefinition(String newDefinition) {
		String oldDefinition = definition;
		definition = newDefinition;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.VARIABLE__DEFINITION, oldDefinition, definition));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public VariableType getType() {
		return getType(settings);
	}

	private static VariableType getType(VariableSettings value) {
		if (value instanceof FormulaSetting) return VariableType.FORMULA;
		if (value instanceof NormalDistribution) return VariableType.NORMAL;
		if (value instanceof UniformDistribution) return VariableType.UNIFORM;
		if (value instanceof LogNormalDistribution) return VariableType.LOGNORMAL;
		if (value instanceof BetaDistribution) return VariableType.BETA;
		if (value instanceof TriangularDistribution) return VariableType.TRIANGULAR;
		if (value instanceof BinomialDistribution) return VariableType.BINOMIAL;
		if (value instanceof PoissonDistribution) return VariableType.POISSON;
		if (value instanceof ExponentialDistribution) return VariableType.EXPONENTIAL;
		if (value instanceof GammaDistribution) return VariableType.GAMMA;
		return null;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public void setType(VariableType newType) {
		if (newType == getType()) return;
		if (newType == null) {
			setSettings(null);
			return;
		}
		setSettings(switch (newType) {
			case FORMULA -> GuesstimateFactory.eINSTANCE.createFormulaSetting();
			case NORMAL -> GuesstimateFactory.eINSTANCE.createNormalDistribution();
			case UNIFORM -> GuesstimateFactory.eINSTANCE.createUniformDistribution();
			case LOGNORMAL -> GuesstimateFactory.eINSTANCE.createLogNormalDistribution();
			case BETA -> GuesstimateFactory.eINSTANCE.createBetaDistribution();
			case TRIANGULAR -> GuesstimateFactory.eINSTANCE.createTriangularDistribution();
			case BINOMIAL -> GuesstimateFactory.eINSTANCE.createBinomialDistribution();
			case POISSON -> GuesstimateFactory.eINSTANCE.createPoissonDistribution();
			case EXPONENTIAL -> GuesstimateFactory.eINSTANCE.createExponentialDistribution();
			case GAMMA -> GuesstimateFactory.eINSTANCE.createGammaDistribution();
		});
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public VariableSettings getSettings() {
		return settings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public NotificationChain basicSetSettings(VariableSettings newSettings, NotificationChain msgs) {
		VariableSettings oldSettings = settings;
		VariableType oldType = getType(oldSettings);
		settings = newSettings;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GuesstimatePackage.VARIABLE__SETTINGS, oldSettings, newSettings);
			if (msgs == null) msgs = notification; else msgs.add(notification);
			VariableType newType = getType(newSettings);
			if (oldType != newType) msgs.add(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.VARIABLE__TYPE, oldType, newType));
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSettings(VariableSettings newSettings) {
		if (newSettings != settings) {
			NotificationChain msgs = null;
			if (settings != null)
				msgs = ((InternalEObject)settings).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - GuesstimatePackage.VARIABLE__SETTINGS, null, msgs);
			if (newSettings != null)
				msgs = ((InternalEObject)newSettings).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - GuesstimatePackage.VARIABLE__SETTINGS, null, msgs);
			msgs = basicSetSettings(newSettings, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.VARIABLE__SETTINGS, newSettings, newSettings));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GuesstimatePackage.VARIABLE__SETTINGS:
				return basicSetSettings(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GuesstimatePackage.VARIABLE__NAME:
				return getName();
			case GuesstimatePackage.VARIABLE__DOCUMENTATION:
				return getDocumentation();
			case GuesstimatePackage.VARIABLE__DEFINITION:
				return getDefinition();
			case GuesstimatePackage.VARIABLE__TYPE:
				return getType();
			case GuesstimatePackage.VARIABLE__SETTINGS:
				return getSettings();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case GuesstimatePackage.VARIABLE__NAME:
				setName((String)newValue);
				return;
			case GuesstimatePackage.VARIABLE__DOCUMENTATION:
				setDocumentation((String)newValue);
				return;
			case GuesstimatePackage.VARIABLE__DEFINITION:
				setDefinition((String)newValue);
				return;
			case GuesstimatePackage.VARIABLE__TYPE:
				setType((VariableType)newValue);
				return;
			case GuesstimatePackage.VARIABLE__SETTINGS:
				setSettings((VariableSettings)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case GuesstimatePackage.VARIABLE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case GuesstimatePackage.VARIABLE__DOCUMENTATION:
				setDocumentation(DOCUMENTATION_EDEFAULT);
				return;
			case GuesstimatePackage.VARIABLE__DEFINITION:
				setDefinition(DEFINITION_EDEFAULT);
				return;
			case GuesstimatePackage.VARIABLE__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case GuesstimatePackage.VARIABLE__SETTINGS:
				setSettings((VariableSettings)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case GuesstimatePackage.VARIABLE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case GuesstimatePackage.VARIABLE__DOCUMENTATION:
				return DOCUMENTATION_EDEFAULT == null ? documentation != null : !DOCUMENTATION_EDEFAULT.equals(documentation);
			case GuesstimatePackage.VARIABLE__DEFINITION:
				return DEFINITION_EDEFAULT == null ? definition != null : !DEFINITION_EDEFAULT.equals(definition);
			case GuesstimatePackage.VARIABLE__TYPE:
				return getType() != null;
			case GuesstimatePackage.VARIABLE__SETTINGS:
				return settings != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(", documentation: ");
		result.append(documentation);
		result.append(", definition: ");
		result.append(definition);
		result.append(')');
		return result.toString();
	}

} //VariableImpl
