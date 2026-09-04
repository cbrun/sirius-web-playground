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
package fr.obeo.dsl.guesstimate.views;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.view.FixedColor;
import org.eclipse.sirius.components.view.diagram.ArrowStyle;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.EdgeDescription;
import org.eclipse.sirius.components.view.diagram.LineStyle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests the Guesstimate view definition.
 *
 * @author cedric
 */
public class GuesstimateViewsTests {

	@Test
	@DisplayName("Given the variables diagram, when formula edges are built, then every operator has its own oriented style")
	public void givenVariablesDiagramWhenFormulaEdgesAreBuiltThenEveryOperatorHasItsOwnOrientedStyle() {
		var view = new GuesstimateViews().create();
		var diagram = view.getDescriptions().stream()
				.filter(DiagramDescription.class::isInstance)
				.map(DiagramDescription.class::cast)
				.findFirst()
				.orElseThrow();

		assertThat(diagram.getEdgeDescriptions()).hasSize(5);
		this.assertOperatorEdge(diagram.getEdgeDescriptions().get(0), "+", "AdditionColor", "FormulaAdditionDependenciesEdge", LineStyle.SOLID, 1);
		this.assertOperatorEdge(diagram.getEdgeDescriptions().get(1), "-", "SubtractionColor", "FormulaSubtractionDependenciesEdge", LineStyle.DASH, 1);
		this.assertOperatorEdge(diagram.getEdgeDescriptions().get(2), "*", "MultiplicationColor", "FormulaMultiplicationDependenciesEdge", LineStyle.DOT, 1);
		this.assertOperatorEdge(diagram.getEdgeDescriptions().get(3), "/", "DivisionColor", "FormulaDivisionDependenciesEdge", LineStyle.DASH_DOT, 1);
		this.assertOperatorEdge(diagram.getEdgeDescriptions().get(4), "^", "PowerColor", "FormulaPowerDependenciesEdge", LineStyle.SOLID, 2);
	}

	@Test
	@DisplayName("Given the Guesstimate view, when descriptions are created, then every named element uses UpperCamelCase")
	public void givenGuesstimateViewWhenDescriptionsAreCreatedThenEveryNamedElementUsesUpperCamelCase() {
		var view = new GuesstimateViews().create();
		var upperCamelCase = Pattern.compile("[A-Z][A-Za-z0-9]*");

		view.eAllContents().forEachRemaining(eObject -> this.assertNameIfPresent(eObject, upperCamelCase));
	}

	private void assertNameIfPresent(EObject eObject, Pattern upperCamelCase) {
		var nameFeature = eObject.eClass().getEStructuralFeature("name");
		if (nameFeature != null && nameFeature.getEType().getInstanceClass() == String.class) {
			var name = (String) eObject.eGet(nameFeature);
			if (name != null && !name.isBlank()) {
				assertThat(name).matches(upperCamelCase);
			}
		}
	}

	private void assertOperatorEdge(EdgeDescription edge, String operator, String colorName, String edgeName, LineStyle lineStyle,
			int edgeWidth) {
		assertThat(edge.getName()).isEqualTo(edgeName);
		assertThat(edge.isIsDomainBasedEdge()).isTrue();
		assertThat(edge.getSourceDescriptions()).hasSize(1);
		assertThat(edge.getTargetDescriptions()).containsExactlyElementsOf(edge.getSourceDescriptions());
		assertThat(edge.getSourceExpression())
				.isEqualTo("aql:self.getReferencedVariablesByOperator('" + operator + "')");
		assertThat(edge.getTargetExpression()).isEqualTo("aql:self");
		assertThat(edge.getCenterLabelExpression())
				.isEqualTo("aql:self.getOperatorLabel(semanticEdgeSource,'" + operator + "')");
		assertThat(edge.getStyle().getColor()).isInstanceOfSatisfying(FixedColor.class,
				color -> assertThat(color.getName()).isEqualTo(colorName));
		assertThat(edge.getStyle().getLineStyle()).isEqualTo(lineStyle);
		assertThat(edge.getStyle().getEdgeWidth()).isEqualTo(edgeWidth);
		assertThat(edge.getStyle().getSourceArrowStyle()).isEqualTo(ArrowStyle.NONE);
		assertThat(edge.getStyle().getTargetArrowStyle()).isEqualTo(ArrowStyle.INPUT_ARROW);
	}
}
