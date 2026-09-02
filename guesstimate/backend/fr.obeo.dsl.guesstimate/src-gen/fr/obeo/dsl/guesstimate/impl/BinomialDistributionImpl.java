/**
 */
package fr.obeo.dsl.guesstimate.impl;

import fr.obeo.dsl.guesstimate.BinomialDistribution;
import fr.obeo.dsl.guesstimate.GuesstimatePackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Binomial Distribution</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.BinomialDistributionImpl#getTrials <em>Trials</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.BinomialDistributionImpl#getP <em>P</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BinomialDistributionImpl extends MinimalEObjectImpl.Container implements BinomialDistribution {
	/**
	 * The default value of the '{@link #getTrials() <em>Trials</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTrials()
	 * @generated
	 * @ordered
	 */
	protected static final int TRIALS_EDEFAULT = 10;

	/**
	 * The cached value of the '{@link #getTrials() <em>Trials</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTrials()
	 * @generated
	 * @ordered
	 */
	protected int trials = TRIALS_EDEFAULT;

	/**
	 * The default value of the '{@link #getP() <em>P</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getP()
	 * @generated
	 * @ordered
	 */
	protected static final Double P_EDEFAULT = new Double(0.5);

	/**
	 * The cached value of the '{@link #getP() <em>P</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getP()
	 * @generated
	 * @ordered
	 */
	protected Double p = P_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BinomialDistributionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GuesstimatePackage.Literals.BINOMIAL_DISTRIBUTION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getTrials() {
		return trials;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTrials(int newTrials) {
		int oldTrials = trials;
		trials = newTrials;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.BINOMIAL_DISTRIBUTION__TRIALS,
					oldTrials, trials));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Double getP() {
		return p;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setP(Double newP) {
		Double oldP = p;
		p = newP;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.BINOMIAL_DISTRIBUTION__P, oldP,
					p));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case GuesstimatePackage.BINOMIAL_DISTRIBUTION__TRIALS:
			return getTrials();
		case GuesstimatePackage.BINOMIAL_DISTRIBUTION__P:
			return getP();
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
		case GuesstimatePackage.BINOMIAL_DISTRIBUTION__TRIALS:
			setTrials((Integer) newValue);
			return;
		case GuesstimatePackage.BINOMIAL_DISTRIBUTION__P:
			setP((Double) newValue);
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
		case GuesstimatePackage.BINOMIAL_DISTRIBUTION__TRIALS:
			setTrials(TRIALS_EDEFAULT);
			return;
		case GuesstimatePackage.BINOMIAL_DISTRIBUTION__P:
			setP(P_EDEFAULT);
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
		case GuesstimatePackage.BINOMIAL_DISTRIBUTION__TRIALS:
			return trials != TRIALS_EDEFAULT;
		case GuesstimatePackage.BINOMIAL_DISTRIBUTION__P:
			return P_EDEFAULT == null ? p != null : !P_EDEFAULT.equals(p);
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
		if (eIsProxy())
			return super.toString();

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (trials: ");
		result.append(trials);
		result.append(", p: ");
		result.append(p);
		result.append(')');
		return result.toString();
	}

} //BinomialDistributionImpl
