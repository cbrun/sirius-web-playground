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

import fr.obeo.dsl.guesstimate.GuesstimatePackage;
import fr.obeo.dsl.guesstimate.GuesstimateQueries;
import fr.obeo.dsl.guesstimate.Sheet;
import fr.obeo.dsl.guesstimate.Variable;
import fr.obeo.dsl.guesstimate.VariableSettings;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;

/**
 * Collects the Guesstimate sheets whose derived samples must be recomputed.
 *
 * @author cedric
 */
public class GuesstimateUpdateModelAdapter extends EContentAdapter {

    private final Set<Sheet> dirtySheets = new LinkedHashSet<>();

    @Override
    public void notifyChanged(Notification notification) {
        super.notifyChanged(notification);
        if (!notification.isTouch()) {
            this.handleNotification(notification);
        }
    }

    private void handleNotification(Notification notification) {
        Object notifier = notification.getNotifier();
        Object feature = notification.getFeature();
        if (notifier instanceof VariableSettings variableSettings) {
            this.markDirty(variableSettings);
        } else if (notifier instanceof Variable variable
                && (feature == GuesstimatePackage.eINSTANCE.getVariable_Settings()
                        || feature == GuesstimatePackage.eINSTANCE.getVariable_Name())) {
            this.markDirty(variable);
        } else if (notifier instanceof Sheet sheet
                && (feature == GuesstimatePackage.eINSTANCE.getSheet_Variables()
                        || feature == GuesstimatePackage.eINSTANCE.getSheet_SampleSize())) {
            this.dirtySheets.add(sheet);
        }
    }

    private void markDirty(EObject eObject) {
        Sheet sheet = GuesstimateQueries.getParentSheet(eObject);
        if (sheet != null) {
            this.dirtySheets.add(sheet);
        }
    }

    /**
     * Recomputes every dirty sheet once and clears the pending work.
     */
    public void resampleDirtySheets() {
        var sheetsToResample = List.copyOf(this.dirtySheets);
        this.dirtySheets.clear();
        sheetsToResample.forEach(Sheet::resample);
    }

    @Override
    protected void addAdapter(Notifier notifier) {
        super.addAdapter(notifier);
        if (notifier instanceof Sheet sheet) {
            this.dirtySheets.add(sheet);
        }
    }
}
