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
		this.assertOperatorEdge(diagram.getEdgeDescriptions().get(0), "+", "Addition", LineStyle.SOLID, 1);
		this.assertOperatorEdge(diagram.getEdgeDescriptions().get(1), "-", "Subtraction", LineStyle.DASH, 1);
		this.assertOperatorEdge(diagram.getEdgeDescriptions().get(2), "*", "Multiplication", LineStyle.DOT, 1);
		this.assertOperatorEdge(diagram.getEdgeDescriptions().get(3), "/", "Division", LineStyle.DASH_DOT, 1);
		this.assertOperatorEdge(diagram.getEdgeDescriptions().get(4), "^", "Power", LineStyle.SOLID, 2);
	}

	private void assertOperatorEdge(EdgeDescription edge, String operator, String colorName, LineStyle lineStyle,
			int edgeWidth) {
		assertThat(edge.getName()).isEqualTo("Formula " + operator + " dependencies");
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
