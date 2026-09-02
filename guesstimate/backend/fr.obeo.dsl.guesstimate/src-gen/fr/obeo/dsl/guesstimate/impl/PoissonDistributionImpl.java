/**
 */
package fr.obeo.dsl.guesstimate.impl;

import fr.obeo.dsl.guesstimate.GuesstimatePackage;
import fr.obeo.dsl.guesstimate.PoissonDistribution;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Poisson Distribution</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.PoissonDistributionImpl#getP <em>P</em>}</li>
 *   <li>{@link fr.obeo.dsl.guesstimate.impl.PoissonDistributionImpl#getEpsilon <em>Epsilon</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PoissonDistributionImpl extends MinimalEObjectImpl.Container implements PoissonDistribution {
	/**
	 * The default value of the '{@link #getP() <em>P</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getP()
	 * @generated
	 * @ordered
	 */
	protected static final double P_EDEFAULT = 1.0;

	/**
	 * The cached value of the '{@link #getP() <em>P</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getP()
	 * @generated
	 * @ordered
	 */
	protected double p = P_EDEFAULT;

	/**
	 * The default value of the '{@link #getEpsilon() <em>Epsilon</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEpsilon()
	 * @generated
	 * @ordered
	 */
	protected static final double EPSILON_EDEFAULT = 0.0;

	/**
	 * The cached value of the '{@link #getEpsilon() <em>Epsilon</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEpsilon()
	 * @generated
	 * @ordered
	 */
	protected double epsilon = EPSILON_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PoissonDistributionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GuesstimatePackage.Literals.POISSON_DISTRIBUTION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getP() {
		return p;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setP(double newP) {
		double oldP = p;
		p = newP;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.POISSON_DISTRIBUTION__P, oldP, p));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public double getEpsilon() {
		return epsilon;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEpsilon(double newEpsilon) {
		double oldEpsilon = epsilon;
		epsilon = newEpsilon;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GuesstimatePackage.POISSON_DISTRIBUTION__EPSILON,
					oldEpsilon, epsilon));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case GuesstimatePackage.POISSON_DISTRIBUTION__P:
			return getP();
		case GuesstimatePackage.POISSON_DISTRIBUTION__EPSILON:
			return getEpsilon();
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
		case GuesstimatePackage.POISSON_DISTRIBUTION__P:
			setP((Double) newValue);
			return;
		case GuesstimatePackage.POISSON_DISTRIBUTION__EPSILON:
			setEpsilon((Double) newValue);
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
		case GuesstimatePackage.POISSON_DISTRIBUTION__P:
			setP(P_EDEFAULT);
			return;
		case GuesstimatePackage.POISSON_DISTRIBUTION__EPSILON:
			setEpsilon(EPSILON_EDEFAULT);
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
		case GuesstimatePackage.POISSON_DISTRIBUTION__P:
			return p != P_EDEFAULT;
		case GuesstimatePackage.POISSON_DISTRIBUTION__EPSILON:
			return epsilon != EPSILON_EDEFAULT;
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
		result.append(" (p: ");
		result.append(p);
		result.append(", epsilon: ");
		result.append(epsilon);
		result.append(')');
		return result.toString();
	}

} //PoissonDistributionImpl
