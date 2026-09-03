/*******************************************************************************
 * Copyright (c) 2026 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package fr.obeo.dsl.guesstimate.configuration;

import fr.obeo.dsl.guesstimate.FormulaSetting;
import fr.obeo.dsl.guesstimate.GuesstimatePackage;
import fr.obeo.dsl.guesstimate.Sheet;
import fr.obeo.dsl.guesstimate.Variable;
import fr.obeo.dsl.guesstimate.VariableServices;
import fr.obeo.dsl.guesstimate.VariableSettings;
import fr.obeo.dsl.guesstimate.simulation.SamplingSimulationAdapter;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.core.api.IEditingContextProcessor;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.springframework.stereotype.Service;

/**
 * Installs the adapter responsible for updating derived Guesstimate model data.
 *
 * @author cedric
 */
@Service
public class GuesstimateAutomaticModelUpdate implements IEditingContextProcessor {

    @Override
    public void preProcess(IEditingContext editingContext) {

    }

    @Override
    public void postProcess(IEditingContext editingContext) {
        if (editingContext instanceof IEMFEditingContext siriusWebEditingContext) {
            boolean hasUpdateAdapter = siriusWebEditingContext.getDomain().getResourceSet().eAdapters().stream()
                    .anyMatch(GuesstimateUpdateModelAdapter.class::isInstance);
            if (!hasUpdateAdapter) {
                siriusWebEditingContext.getDomain().getResourceSet().eAdapters().add(new GuesstimateUpdateModelAdapter());
            }

        }
    }
}

class GuesstimateUpdateModelAdapter extends EContentAdapter {

    @Override
    public void notifyChanged(Notification notification) {
        super.notifyChanged(notification);
        /*
         * we have an important inefficiency here, while the tools are executed, we are notified, and as such are doing
         * work several times for a given change.
         */
        if (!notification.isTouch()) {

            // if (notification.getNotifier() instanceof EObject) {
            // objectsToValidat.add((EObject) notification.getNotifier());
            // }
            // Diagnostician validator = new Diagnostician();
            //
            // for (EObject eObject : objectsToValidat) {
            // Diagnostic diag = validator.validate(eObject);
            // DiagnosticAttachAdapter.getOrCreate(eObject).setDiagnostic(diag);
            // if (diag.getSeverity() == Diagnostic.ERROR) {
            // System.out.println("Validation ERROR : " + diag.getMessage() + " " + diag.getSource());
            // }
            // }
            if (notification.getNotifier() instanceof VariableSettings) {
                EObject container = ((EObject) notification.getNotifier()).eContainer();
                if (notification.getNotifier() instanceof FormulaSetting && notification.getFeature() == GuesstimatePackage.eINSTANCE.getFormulaSetting_Formula()) {
                    new VariableServices().setFormula((FormulaSetting) notification.getNotifier(), (String) notification.getNewValue());
                } else if (container instanceof Variable) {
                    SamplingSimulationAdapter.getOrCreate(container).resetApacheStateFromSettings();
                }
            }

            if (notification.getNotifier() instanceof Sheet && notification.getFeature() == GuesstimatePackage.eINSTANCE.getSheet_SampleSize()) {
                ((Sheet) notification.getNotifier()).resample();
            }

        }

    }

    @Override
    protected void addAdapter(Notifier notifier) {
        super.addAdapter(notifier);
        if (notifier instanceof Sheet) {
            ((Sheet) notifier).resample();
        }
    }
}
