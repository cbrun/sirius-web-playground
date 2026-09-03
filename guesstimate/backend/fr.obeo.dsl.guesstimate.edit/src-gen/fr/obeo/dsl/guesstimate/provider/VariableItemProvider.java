/**
 */
package fr.obeo.dsl.guesstimate.provider;

import fr.obeo.dsl.guesstimate.GuesstimateFactory;
import fr.obeo.dsl.guesstimate.Variable;
import fr.obeo.dsl.guesstimate.VariableType;
import fr.obeo.dsl.guesstimate.GuesstimatePackage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.ResourceLocator;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedImage;
import org.eclipse.emf.edit.provider.IChildCreationExtender;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;

/**
 * This is the item provider adapter for a {@link fr.obeo.dsl.guesstimate.Variable} object.
 * <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * @generated
 */
public class VariableItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider,
		IStructuredItemContentProvider, ITreeItemContentProvider, IItemLabelProvider, IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public VariableItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addNamePropertyDescriptor(object);
			addDocumentationPropertyDescriptor(object);
			addDefinitionPropertyDescriptor(object);
			addTypePropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Name feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addNamePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Variable_name_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Variable_name_feature", "_UI_Variable_type"),
				 GuesstimatePackage.Literals.VARIABLE__NAME,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Documentation feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDocumentationPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Variable_documentation_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Variable_documentation_feature", "_UI_Variable_type"),
				 GuesstimatePackage.Literals.VARIABLE__DOCUMENTATION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Definition feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDefinitionPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Variable_definition_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Variable_definition_feature", "_UI_Variable_type"),
				 GuesstimatePackage.Literals.VARIABLE__DEFINITION,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * This adds a property descriptor for the Type feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addTypePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add
			(createItemPropertyDescriptor
				(((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
				 getResourceLocator(),
				 getString("_UI_Variable_type_feature"),
				 getString("_UI_PropertyDescriptor_description", "_UI_Variable_type_feature", "_UI_Variable_type"),
				 GuesstimatePackage.Literals.VARIABLE__TYPE,
				 true,
				 false,
				 false,
				 ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
				 null,
				 null));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean hasChildren(Object object) {
		return hasChildren(object, true);
	}

	/**
	 * This returns Variable.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated NOT
	 */
	@Override
	public Object getImage(Object object) {
		List<Object> images = new ArrayList<Object>(2);

		if (object instanceof Variable) {
			VariableType type = ((Variable) object).getType();
			if (type == null) {
				images.add(getResourceLocator().getImage("full/obj16/Variable.svg"));
			} else switch (type) {
			case EXPONENTIAL:
				images.add(getResourceLocator().getImage("full/obj16/Variable_overlay.svg"));
				images.add(getResourceLocator().getImage("full/obj16/ExponentialDistribution.svg"));
				break;
			case GAMMA:
				images.add(getResourceLocator().getImage("full/obj16/Variable_overlay.svg"));
				images.add(getResourceLocator().getImage("full/obj16/GammaDistribution.svg"));
				break;
			case NORMAL:
				images.add(getResourceLocator().getImage("full/obj16/Variable_overlay.svg"));
				images.add(getResourceLocator().getImage("full/obj16/NormalDistribution.svg"));
				break;
			case LOGNORMAL:
				images.add(getResourceLocator().getImage("full/obj16/Variable_overlay.svg"));
				images.add(getResourceLocator().getImage("full/obj16/LogNormalDistribution.svg"));
				break;
			case FORMULA:
				images.add(getResourceLocator().getImage("full/obj16/Variable_overlay.svg"));
				images.add(getResourceLocator().getImage("full/obj16/FormulaDistribution.svg"));
				break;
			case BINOMIAL:
				images.add(getResourceLocator().getImage("full/obj16/Variable_overlay.svg"));
				images.add(getResourceLocator().getImage("full/obj16/BinomialDistribution.svg"));
				break;
			case BETA:
				images.add(getResourceLocator().getImage("full/obj16/Variable_overlay.svg"));
				images.add(getResourceLocator().getImage("full/obj16/BetaDistribution.svg"));
				break;
			case POISSON:
				images.add(getResourceLocator().getImage("full/obj16/Variable_overlay.svg"));
				images.add(getResourceLocator().getImage("full/obj16/PoissonDistribution.svg"));
				break;
			case UNIFORM:
				images.add(getResourceLocator().getImage("full/obj16/Variable_overlay.svg"));
				images.add(getResourceLocator().getImage("full/obj16/UniformDistribution.svg"));
				break;
			case TRIANGULAR:
				images.add(getResourceLocator().getImage("full/obj16/Variable_overlay.svg"));
				images.add(getResourceLocator().getImage("full/obj16/TriangularDistribution.svg"));
				break;
			}

		}

		ComposedImage composedImage = new ComposedImage(images);
		return overlayImage(object, composedImage);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected boolean shouldComposeCreationImage() {
		return true;
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((Variable)object).getName();
		return label == null || label.length() == 0 ?
			getString("_UI_Variable_type") :
			getString("_UI_Variable_type") + " " + label;
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(Variable.class)) {
			case GuesstimatePackage.VARIABLE__NAME:
			case GuesstimatePackage.VARIABLE__DOCUMENTATION:
			case GuesstimatePackage.VARIABLE__DEFINITION:
			case GuesstimatePackage.VARIABLE__TYPE:
			case GuesstimatePackage.VARIABLE__SETTINGS:
				fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
				return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);
	}

	/**
	 * Return the resource locator for this item provider's resources. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return GuesstimateEditPlugin.INSTANCE;
	}

}
