/**
 */
package fr.obeo.dsl.guesstimate.impl;

import fr.obeo.dsl.guesstimate.Variable;
import fr.obeo.dsl.guesstimate.GuesstimatePackage;
import fr.obeo.dsl.guesstimate.GuesstimateUtils;
import fr.obeo.dsl.guesstimate.Sheet;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sheet</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.SheetImpl#getVariables <em>Variables</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.SheetImpl#getSampleSize <em>Sample Size</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SheetImpl extends MinimalEObjectImpl.Container implements Sheet {
	/**
	 * The cached value of the '{@link #getVariables() <em>Variables</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariables()
	 * @generated
	 * @ordered
	 */
	protected EList<Variable> variables;

	/**
	 * The default value of the '{@link #getSampleSize() <em>Sample Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSampleSize()
	 * @generated
	 * @ordered
	 */
	protected static final int SAMPLE_SIZE_EDEFAULT = 10000;

	/**
	 * The cached value of the '{@link #getSampleSize() <em>Sample Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSampleSize()
	 * @generated
	 * @ordered
	 */
	protected int sampleSize = SAMPLE_SIZE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SheetImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GuesstimatePackage.Literals.SHEET;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Variable> getVariables() {
		if (variables == null) {
			variables = new EObjectContainmentEList<Variable>(Variable.class, this,
					GuesstimatePackage.SHEET__VARIABLES);
		}
		return variables;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSampleSize(int newSampleSize) {
		int oldSampleSize = sampleSize;
		sampleSize = newSampleSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.SHEET__SAMPLE_SIZE, oldSampleSize,
					sampleSize));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @not-generated
	 */
	public void resample() {
		new GuesstimateUtils().completeModel(this);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case GuesstimatePackage.SHEET__VARIABLES:
			return ((InternalEList<?>) getVariables()).basicRemove(otherEnd, msgs);
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
		case GuesstimatePackage.SHEET__VARIABLES:
			return getVariables();
		case GuesstimatePackage.SHEET__SAMPLE_SIZE:
			return getSampleSize();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case GuesstimatePackage.SHEET__VARIABLES:
			getVariables().clear();
			getVariables().addAll((Collection<? extends Variable>) newValue);
			return;
		case GuesstimatePackage.SHEET__SAMPLE_SIZE:
			setSampleSize((Integer) newValue);
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
		case GuesstimatePackage.SHEET__VARIABLES:
			getVariables().clear();
			return;
		case GuesstimatePackage.SHEET__SAMPLE_SIZE:
			setSampleSize(SAMPLE_SIZE_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case GuesstimatePackage.SHEET__VARIABLES:
			return variables != null && !variables.isEmpty();
		case GuesstimatePackage.SHEET__SAMPLE_SIZE:
			return sampleSize != SAMPLE_SIZE_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
		switch (operationID) {
		case GuesstimatePackage.SHEET___RESAMPLE:
			resample();
			return null;
		}
		return super.eInvoke(operationID, arguments);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (sampleSize: ");
		result.append(sampleSize);
		result.append(')');
		return result.toString();
	}

} //SheetImpl
