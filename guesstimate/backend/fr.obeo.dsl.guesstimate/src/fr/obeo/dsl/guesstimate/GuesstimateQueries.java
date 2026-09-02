package fr.obeo.dsl.guesstimate;

import org.eclipse.emf.ecore.EObject;

public class GuesstimateQueries {

	public static Sheet getParentSheet(EObject any) {
		EObject cur = any;
		while (cur != null && !(cur instanceof Sheet)) {
			cur = cur.eContainer();
		}
		return (Sheet) cur;
	}

}
