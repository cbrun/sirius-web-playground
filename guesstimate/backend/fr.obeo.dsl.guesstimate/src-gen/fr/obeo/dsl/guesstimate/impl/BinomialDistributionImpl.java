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
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.BinomialDistributionImpl#getProbabilityOfSuccess <em>Probability Of Success</em>}</li>
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
	 * The default value of the '{@link #getProbabilityOfSuccess() <em>Probability Of Success</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProbabilityOfSuccess()
	 * @generated
	 * @ordered
	 */
	protected static final Double PROBABILITY_OF_SUCCESS_EDEFAULT = Double.valueOf(0.5);

	/**
	 * The cached value of the '{@link #getProbabilityOfSuccess() <em>Probability Of Success</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProbabilityOfSuccess()
	 * @generated
	 * @ordered
	 */
	protected Double probabilityOfSuccess = PROBABILITY_OF_SUCCESS_EDEFAULT;

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
	@Override
	public int getTrials() {
		return trials;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setTrials(int newTrials) {
		int oldTrials = trials;
		trials = newTrials;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.BINOMIAL_DISTRIBUTION__TRIALS, oldTrials, trials));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Double getProbabilityOfSuccess() {
		return probabilityOfSuccess;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProbabilityOfSuccess(Double newProbabilityOfSuccess) {
		Double oldProbabilityOfSuccess = probabilityOfSuccess;
		probabilityOfSuccess = newProbabilityOfSuccess;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.BINOMIAL_DISTRIBUTION__PROBABILITY_OF_SUCCESS, oldProbabilityOfSuccess, probabilityOfSuccess));
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
			case GuesstimatePackage.BINOMIAL_DISTRIBUTION__PROBABILITY_OF_SUCCESS:
				return getProbabilityOfSuccess();
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
				setTrials((Integer)newValue);
				return;
			case GuesstimatePackage.BINOMIAL_DISTRIBUTION__PROBABILITY_OF_SUCCESS:
				setProbabilityOfSuccess((Double)newValue);
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
			case GuesstimatePackage.BINOMIAL_DISTRIBUTION__PROBABILITY_OF_SUCCESS:
				setProbabilityOfSuccess(PROBABILITY_OF_SUCCESS_EDEFAULT);
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
			case GuesstimatePackage.BINOMIAL_DISTRIBUTION__PROBABILITY_OF_SUCCESS:
				return PROBABILITY_OF_SUCCESS_EDEFAULT == null ? probabilityOfSuccess != null : !PROBABILITY_OF_SUCCESS_EDEFAULT.equals(probabilityOfSuccess);
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
		result.append(" (trials: ");
		result.append(trials);
		result.append(", probabilityOfSuccess: ");
		result.append(probabilityOfSuccess);
		result.append(')');
		return result.toString();
	}

} //BinomialDistributionImpl
