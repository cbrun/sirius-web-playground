package fr.obeo.dsl.guesstimate;

import java.util.Iterator;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterators;

public class DiagnosticAttachAdapter extends AdapterImpl {

	private Diagnostic diagnostic;

	@Override
	public void notifyChanged(Notification msg) {
		super.notifyChanged(msg);
	}

	public static DiagnosticAttachAdapter getOrCreate(EObject eObj) {
		DiagnosticAttachAdapter found = get(eObj);
		if (found == null) {
			found = new DiagnosticAttachAdapter();
			eObj.eAdapters().add(found);
		}
		return found;
	}

	public static DiagnosticAttachAdapter get(EObject eObj) {
		Iterator<DiagnosticAttachAdapter> it = Iterators.filter(eObj.eAdapters().iterator(),
				DiagnosticAttachAdapter.class);
		if (it.hasNext()) {
			return it.next();
		}
		return null;
	}

	public void setDiagnostic(Diagnostic diag) {
		this.diagnostic = diag;
	}

	public Diagnostic getDiagnostic() {
		return diagnostic;
	}

}
