/**
 */
package fr.obeo.dsl.guesstimate.impl;

import fr.obeo.dsl.guesstimate.GuesstimatePackage;
import fr.obeo.dsl.guesstimate.LogNormalDistribution;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Log Normal Distribution</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.LogNormalDistributionImpl#getLogMean <em>Log Mean</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.LogNormalDistributionImpl#getLogStandardDeviation <em>Log Standard Deviation</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LogNormalDistributionImpl extends MinimalEObjectImpl.Container implements LogNormalDistribution {
	/**
	 * The default value of the '{@link #getLogMean() <em>Log Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLogMean()
	 * @generated
	 * @ordered
	 */
	protected static final double LOG_MEAN_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getLogMean() <em>Log Mean</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLogMean()
	 * @generated
	 * @ordered
	 */
	protected double logMean = LOG_MEAN_EDEFAULT;

	/**
	 * The default value of the '{@link #getLogStandardDeviation() <em>Log Standard Deviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLogStandardDeviation()
	 * @generated
	 * @ordered
	 */
	protected static final double LOG_STANDARD_DEVIATION_EDEFAULT = 1.0;

	/**
	 * The cached value of the '{@link #getLogStandardDeviation() <em>Log Standard Deviation</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLogStandardDeviation()
	 * @generated
	 * @ordered
	 */
	protected double logStandardDeviation = LOG_STANDARD_DEVIATION_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LogNormalDistributionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GuesstimatePackage.Literals.LOG_NORMAL_DISTRIBUTION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getLogMean() {
		return logMean;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLogMean(double newLogMean) {
		double oldLogMean = logMean;
		logMean = newLogMean;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.LOG_NORMAL_DISTRIBUTION__LOG_MEAN, oldLogMean, logMean));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getLogStandardDeviation() {
		return logStandardDeviation;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLogStandardDeviation(double newLogStandardDeviation) {
		double oldLogStandardDeviation = logStandardDeviation;
		logStandardDeviation = newLogStandardDeviation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.LOG_NORMAL_DISTRIBUTION__LOG_STANDARD_DEVIATION, oldLogStandardDeviation, logStandardDeviation));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GuesstimatePackage.LOG_NORMAL_DISTRIBUTION__LOG_MEAN:
				return getLogMean();
			case GuesstimatePackage.LOG_NORMAL_DISTRIBUTION__LOG_STANDARD_DEVIATION:
				return getLogStandardDeviation();
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
			case GuesstimatePackage.LOG_NORMAL_DISTRIBUTION__LOG_MEAN:
				setLogMean((Double)newValue);
				return;
			case GuesstimatePackage.LOG_NORMAL_DISTRIBUTION__LOG_STANDARD_DEVIATION:
				setLogStandardDeviation((Double)newValue);
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
			case GuesstimatePackage.LOG_NORMAL_DISTRIBUTION__LOG_MEAN:
				setLogMean(LOG_MEAN_EDEFAULT);
				return;
			case GuesstimatePackage.LOG_NORMAL_DISTRIBUTION__LOG_STANDARD_DEVIATION:
				setLogStandardDeviation(LOG_STANDARD_DEVIATION_EDEFAULT);
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
			case GuesstimatePackage.LOG_NORMAL_DISTRIBUTION__LOG_MEAN:
				return logMean != LOG_MEAN_EDEFAULT;
			case GuesstimatePackage.LOG_NORMAL_DISTRIBUTION__LOG_STANDARD_DEVIATION:
				return logStandardDeviation != LOG_STANDARD_DEVIATION_EDEFAULT;
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
		result.append(" (logMean: ");
		result.append(logMean);
		result.append(", logStandardDeviation: ");
		result.append(logStandardDeviation);
		result.append(')');
		return result.toString();
	}

} //LogNormalDistributionImpl
