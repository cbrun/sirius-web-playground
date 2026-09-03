/*******************************************************************************
 * Copyright (c) 2024, 2026 Obeo.
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.sirius.components.emf.ResourceMetadataAdapter;
import org.eclipse.sirius.components.emf.services.IDAdapter;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.view.ColorPalette;
import org.eclipse.sirius.components.view.FixedColor;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.builder.generated.diagram.DiagramBuilders;
import org.eclipse.sirius.components.view.builder.generated.form.FormBuilders;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilder;
import org.eclipse.sirius.components.view.builder.generated.view.ViewBuilders;
import org.eclipse.sirius.components.view.diagram.ArrangeLayoutDirection;
import org.eclipse.sirius.components.view.diagram.ArrowStyle;
import org.eclipse.sirius.components.view.diagram.DiagramDescription;
import org.eclipse.sirius.components.view.diagram.DiagramLayoutOption;
import org.eclipse.sirius.components.view.diagram.HeaderSeparatorDisplayMode;
import org.eclipse.sirius.components.view.diagram.InsideLabelPosition;
import org.eclipse.sirius.components.view.diagram.LabelEditTool;
import org.eclipse.sirius.components.view.diagram.LineStyle;
import org.eclipse.sirius.components.view.diagram.NodeDescription;
import org.eclipse.sirius.components.view.form.BarChartDescription;
import org.eclipse.sirius.components.view.form.FlexboxContainerDescription;
import org.eclipse.sirius.components.view.form.FormDescription;
import org.eclipse.sirius.components.view.form.FormElementDescription;
import org.eclipse.sirius.components.view.form.FormElementIf;
import org.eclipse.sirius.components.view.form.GroupDescription;

import fr.obeo.dsl.guesstimate.GuesstimatePackage;
import fr.obeo.dsl.guesstimate.ServiceMethod;
import fr.obeo.dsl.guesstimate.Services;
import fr.obeo.dsl.guesstimate.VariableServices;

/**
 * Builds the Sirius view used by Guesstimate.
 *
 * @author cedric
 */
public class GuesstimateViews {

	private static final String VIEWS_RESOURCE_PATH = "guesstimate guesstimateModelView";

	private final FixedColor main;

	private final FixedColor variableColor;

	private final FixedColor errorColor;

	private final FixedColor grey;

	private final FixedColor background;

	private final FixedColor black;

	private final FixedColor lightGrey;

	public GuesstimateViews() {
		// @formatter:off
        ViewBuilders b = new ViewBuilders();
         this.main =b
                .newFixedColor()
                .name("Main")
                .value("rgb(51, 76, 111)")
                .build();

         this.lightGrey = b
                .newFixedColor()
                .name("Light Grey")
                .value("rgb(117, 117, 117)")
                .build();

         this.errorColor = b
                 .newFixedColor()
                 .name("Light Grey")
                 .value("rgb(255, 0, 0)")
                 .build();

        this.grey = b
                .newFixedColor()
                .name("Grey")
                .value("rgb(66, 66, 66)")
                .build();

         this.background = b
                .newFixedColor()
                .name("Background")
                .value("rgb(250, 250, 250)")
                .build();

         this.black = b
                .newFixedColor()
                .name("Black")
                .value("rgb(0, 0, 0)")
                .build();
         this.variableColor = this.main;
    }

    /**
     * Creates the complete Guesstimate view.
     *
     * @return the Guesstimate view
     */
    public View create() {
        var view = new ViewBuilder().build();
        view.getDescriptions().add(this.buildCustomFormForDetails());
        view.getDescriptions().add(this.theGeneralDiagram());
        view.getColorPalettes().add(this.guesstimatePalette());

        String resourcePath = UUID.nameUUIDFromBytes(VIEWS_RESOURCE_PATH.getBytes(StandardCharsets.UTF_8)).toString();
        Resource resource = new JSONResourceFactory().createResourceFromPath(resourcePath);
        resource.eAdapters().add(new ResourceMetadataAdapter(VIEWS_RESOURCE_PATH));

        view.eAllContents().forEachRemaining(eObject -> {
            var id = UUID.nameUUIDFromBytes(EcoreUtil.getURI(eObject).toString().getBytes(StandardCharsets.UTF_8));
            eObject.eAdapters().add(new IDAdapter(id));
        });
        resource.getContents().add(view);
        return view;
    }

    private ColorPalette guesstimatePalette() {
        return new ViewBuilders()
                .newColorPalette()
                .colors(this.main, this.lightGrey, this.grey, this.background, this.black)
                .build();
        // @formatter:on

	}

	private FormDescription buildCustomFormForDetails() {
		GuesstimatePackage domain = GuesstimatePackage.eINSTANCE;
		ViewBuilders v = new ViewBuilders();
		FormBuilders f = new FormBuilders();
		// @formatter:off


        GroupDescription docGroup = f.newGroupDescription()
                .name("Distribution Doc")
                .labelExpression("Guide")
                .children(f.newLabelDescription()
                        .name("riche text for doc")
                        .valueExpression(ServiceMethod.of0(VariableServices::getGuideDocumentation).aqlSelf())
                        .style(f.newLabelDescriptionStyle()
                                .build())
                        .build())
                .build();

        GroupDescription propertiesGroup = f.newGroupDescription()
                .name("Properties")
                .labelExpression("Properties")
                .children(
                        f.newTextfieldDescription()
                        .name("edit name")
                        .labelExpression("Name")
                        .valueExpression("aql:self.name")
                        .helpExpression(EcoreUtil.getDocumentation(domain.getVariable_Name()))
                        .body(v.newSetValue()
                                .featureName("name")
                                .valueExpression("aql:newValue")
                                .build())
                        .style(f.newTextfieldDescriptionStyle()
                                .build())
                        .build(),
                        f.newTextfieldDescription()
                        .name("edit description")
                        .labelExpression("Description")
                        .helpExpression(EcoreUtil.getDocumentation(domain.getVariable_Documentation()))
                        .valueExpression("aql:self.documentation")
                        .body(v.newSetValue()
                                .featureName("documentation")
                                .valueExpression("aql:newValue")
                                .build())
                        .style(f.newTextfieldDescriptionStyle()
                                .build())
                        .build(),
                        f.newRadioDescription()
                                .labelExpression("Type of variable")
                                .candidateLabelExpression("aql:candidate.name")
                                .candidatesExpression(ServiceMethod.of0(VariableServices::getTypeEnumCandidates).aqlSelf())
                                .valueExpression(ServiceMethod.of0(VariableServices::getTypeEnumValue).aqlSelf())
                                .body(v.newSetValue()
                                        .featureName("type")
                                        .valueExpression(ServiceMethod.of1(VariableServices::setTypeEnumValue).aqlSelf("newValue"))
                                        .build())
                                .build())

                .build();

        BarChartDescription barChart = f.newBarChartDescription()
                .name("Density Bar Chart")
                .labelExpression("aql:'Density'")
                .valuesExpression(ServiceMethod.of0(Services::getDensityValues).aqlSelf())
                .keysExpression(ServiceMethod.of0(Services::getDensityKeys).aqlSelf())
                .yAxisLabelExpression("aql:'number of samples'")
                .style(f.newBarChartDescriptionStyle()
                        .fontSize(8)
                        .barsColor("")
                        .build())
                .build();
        FlexboxContainerDescription summary = f.newFlexboxContainerDescription()
                .name("summary box")
                .labelExpression("Values")
                .children( f.newTextfieldDescription()
                        .labelExpression("aql:'mean value'")
                        .valueExpression(ServiceMethod.of0(Services::mean).aql("self." + ServiceMethod.of0(Services::summary).name() + "()"))
                        .style(f.newTextfieldDescriptionStyle()
                                .build())
                        .build(),
                        f.newTextfieldDescription()
                        .labelExpression("aql:'minimum value'")
                        .valueExpression(ServiceMethod.of0(Services::min).aql("self." + ServiceMethod.of0(Services::summary).name() + "()"))
                        .style(f.newTextfieldDescriptionStyle()
                                .build())
                        .build(),
                        f.newTextfieldDescription()
                        .labelExpression("aql:'maximum value'")
                        .valueExpression(ServiceMethod.of0(Services::max).aql("self." + ServiceMethod.of0(Services::summary).name() + "()"))
                        .style(f.newTextfieldDescriptionStyle()
                                .build())
                        .build(),
                        f.newTextfieldDescription()
                        .labelExpression("aql:'Number of samples'")
                        .valueExpression(ServiceMethod.of0(Services::n).aql("self." + ServiceMethod.of0(Services::summary).name() + "()"))
                        .style(f.newTextfieldDescriptionStyle()
                                .build())
                        .build(),
                        f.newTextfieldDescription()
                        .labelExpression("aql:'Standard deviation'")
                        .valueExpression(ServiceMethod.of0(Services::sd).aql("self." + ServiceMethod.of0(Services::summary).name() + "()"))
                        .style(f.newTextfieldDescriptionStyle()
                                .build())
                        .build(),
                        f.newTextfieldDescription()
                        .labelExpression("aql:'Variance'")
                        .valueExpression(ServiceMethod.of0(Services::var).aql("self." + ServiceMethod.of0(Services::summary).name() + "()"))
                        .style(f.newTextfieldDescriptionStyle()
                                .build())
                        .build())
                .build();



        var childrenFeaturesEditors = new ArrayList<FormElementDescription>();
        childrenFeaturesEditors.add(buildFeatureEditorsForChildBasedOnType(v, f, GuesstimatePackage.eINSTANCE.getBetaDistribution(), "settings"));
        childrenFeaturesEditors.add(buildFeatureEditorsForChildBasedOnType(v, f, GuesstimatePackage.eINSTANCE.getBinomialDistribution(), "settings"));
        childrenFeaturesEditors.add(buildFeatureEditorsForChildBasedOnType(v, f, GuesstimatePackage.eINSTANCE.getExponentialDistribution(), "settings"));
        childrenFeaturesEditors.add(buildFeatureEditorsForChildBasedOnType(v, f, GuesstimatePackage.eINSTANCE.getGammaDistribution(), "settings"));
        childrenFeaturesEditors.add(buildFeatureEditorsForChildBasedOnType(v, f, GuesstimatePackage.eINSTANCE.getLogNormalDistribution(), "settings"));
        childrenFeaturesEditors.add(buildFeatureEditorsForChildBasedOnType(v, f, GuesstimatePackage.eINSTANCE.getNormalDistribution(), "settings"));
        childrenFeaturesEditors.add(buildFeatureEditorsForChildBasedOnType(v, f, GuesstimatePackage.eINSTANCE.getPoissonDistribution(), "settings"));
        childrenFeaturesEditors.add(buildFeatureEditorsForChildBasedOnType(v, f, GuesstimatePackage.eINSTANCE.getTriangularDistribution(), "settings"));
        childrenFeaturesEditors.add(buildFeatureEditorsForChildBasedOnType(v, f, GuesstimatePackage.eINSTANCE.getUniformDistribution(), "settings"));
        childrenFeaturesEditors.add(buildFeatureEditorsForChildBasedOnType(v, f, GuesstimatePackage.eINSTANCE.getFormulaSetting(), "settings"));


        GroupDescription settings = f.newGroupDescription()
                .name("settings")
                .labelExpression("Parameters")
                .children(
                        childrenFeaturesEditors.toArray(new FormElementDescription[0])
                        )
                .build();


        FormDescription editor = f.newFormDescription()
                .name("Details")
                .domainType(typeName(domain.getVariable()))
                .titleExpression("aql: self.name + ' summary'")
                .pages(f.newPageDescription()
                        .domainType(typeName(domain.getVariable()))
                        .labelExpression("Page")
                        .groups(propertiesGroup,
                                docGroup,
                                settings,
                                f.newGroupDescription()
                                    .children(
                                            barChart,
                                            summary)
                                    .build()
                                )
                        .build())
                .build();
        // @formatter:on
		return editor;
	}

	/**
	 * @param v
	 * @param f
	 * @param childType
	 * @param childReferenceName
	 */
	private FormElementDescription buildFeatureEditorsForChildBasedOnType(ViewBuilders v, FormBuilders f,
			EClass childType, String childReferenceName) {
		FormElementIf enclosingIf = f.newFormElementIf()
				.predicateExpression("aql:self.settings.oclIsKindOf(" + typeName(childType) + ")").build();
		for (EAttribute childParameter : childType.getEAllAttributes()) {

			if (childParameter == GuesstimatePackage.eINSTANCE.getFormulaSetting_Formula()) {
				enclosingIf.getChildren()
						.add(f.newTextfieldDescription().labelExpression(childParameter.getName())
								.valueExpression("aql:self." + childReferenceName + "." + childParameter.getName())
								.helpExpression(EcoreUtil.getDocumentation(childParameter))
								.body(v.newChangeContext().expression("aql:self." + childReferenceName)
										.children(v.newSetValue().featureName(childParameter.getName())
												.valueExpression(ServiceMethod.of1(VariableServices::setFormula)
														.aqlSelf("newValue"))
												.build())
										.build())
								.style(f.newTextfieldDescriptionStyle().build()).build());
			} else if (childParameter.getEType() == GuesstimatePackage.eINSTANCE.getProbability()) {
				enclosingIf.getChildren().add(f.newTextfieldDescription().labelExpression(childParameter.getName())
						.valueExpression(
								"aql:self." + childReferenceName + "." + childParameter.getName() + " * 100.0 +'%'")
						.helpExpression(EcoreUtil.getDocumentation(childParameter))
						.body(v.newChangeContext().expression("aql:self." + childReferenceName)
								.children(v.newSetValue().featureName(childParameter.getName())
										.valueExpression(ServiceMethod.of1(Services::parseProbability).aqlSelf("newValue"))
										.build())
								.build())
						.style(f.newTextfieldDescriptionStyle().build()).build());
			} else {
				enclosingIf.getChildren()
						.add(f.newTextfieldDescription().labelExpression(childParameter.getName())
								.valueExpression("aql:self." + childReferenceName + "." + childParameter.getName())
								.helpExpression(EcoreUtil.getDocumentation(childParameter))
								.body(v.newChangeContext().expression("aql:self." + childReferenceName)
										.children(v.newSetValue().featureName(childParameter.getName())
												.valueExpression("aql:newValue").build())
										.build())
								.style(f.newTextfieldDescriptionStyle().build()).build());
			}
		}
		return enclosingIf;
	}

	private DiagramDescription theGeneralDiagram() {
		GuesstimatePackage domain = GuesstimatePackage.eINSTANCE;
		DiagramBuilders b = new DiagramBuilders();
		ViewBuilders v = new ViewBuilders();

		// @formatter:off

        LabelEditTool editLabel = b.newLabelEditTool()
                .name("edit any element label from the diagrams")
                .body(v.newChangeContext()
						.expression(ServiceMethod.of1(Services::smartEdit).aqlSelf("newLabel"))
                        .build()
                        )
                .build();

//        NodeDescription testImage = b.newNodeDescription()
//                .name("Mapping With Image")
//                .domainType(typeName(domain.getVariable()))
//                .style(b.newImageNodeStyleDescription()
//                        .shape("images/normal-dis.svg")
//                        .borderColor(this.black)
//                        .build())
//                .build();
//

        NodeDescription distributionMapping = b.newNodeDescription()
                .name("Variables")
                .domainType(typeName(domain.getVariable()))
                .defaultWidthExpression("80")
                .defaultHeightExpression("30")
                .insideLabel(b.newInsideLabelDescription()
                        .labelExpression("aql:self.name + ' : ' + self.documentation")
                        .position(InsideLabelPosition.TOP_LEFT)
                        .style(b.newInsideLabelStyle()
                                .labelColor(this.variableColor)
                                .showIconExpression("true")
                                .fontSize(8)
                                .headerSeparatorDisplayMode(HeaderSeparatorDisplayMode.IF_CHILDREN)
                                .withHeader(true)
                                .borderSize(0)
                                .borderColor(this.variableColor)
                                .build())
                        .conditionalStyles(b.newConditionalInsideLabelStyle()
                                .condition(ServiceMethod.of0(Services::hasValidationError).aqlSelf())
                                .style(b.newInsideLabelStyle()
                                        .labelColor(this.errorColor)
                                        .showIconExpression("true")
                                        .fontSize(8)
                                        .headerSeparatorDisplayMode(HeaderSeparatorDisplayMode.ALWAYS)
                                        .borderSize(0)
                                        .borderColor(this.errorColor)
                                        .build())
                                .build())
                        .build())
                .style(b.newRectangularNodeStyleDescription()
                        .borderRadius(8)
                        .borderSize(1)
                        .borderColor(this.variableColor)
                        .background(this.background)
                        .borderLineStyle(LineStyle.SOLID)
                        .build())
                .palette(b.newNodePalette()
                        .labelEditTool(editLabel)
                        .build())
                .build();


        DiagramDescription diag = b.newDiagramDescription()
                .name("Variables and Computations")
                .arrangeLayoutDirection(ArrangeLayoutDirection.UP)
                .domainType(typeName(domain.getSheet()))
                .layoutOption(DiagramLayoutOption.AUTO_UNTIL_MANUAL)
                .style(b.newDiagramStyleDescription().build())
                .nodeDescriptions(distributionMapping)
                .edgeDescriptions(b.newEdgeDescription()
                        .name("link from variables having formulas to other variables")
                        .sourceDescriptions(distributionMapping)
                        .targetDescriptions(distributionMapping)
                        .targetExpression("aql:self.eAllContents("+typeName(domain.getFormulaSetting())+").inputs" )
                        .centerLabelExpression("") // otherwise self.name is used :-/
                        .style(b.newEdgeStyle()
                                .borderColor(this.variableColor)
                                .color(this.variableColor)
                                .borderLineStyle(LineStyle.SOLID)
                                .edgeWidth(1)
                                .borderSize(0)
                                .sourceArrowStyle(ArrowStyle.INPUT_ARROW)
                                .targetArrowStyle(ArrowStyle.NONE)
                                .build())
                        .build())
                .palette(b.newDiagramPalette()
                        .nodeTools(b.newNodeTool()
                                .name("Variable")
                                .iconURLsExpression(this.iconFromType(domain.getVariable()))
                                .body(v.newChangeContext()
                                		.expression("aql:self")
                                		.children(v.newCreateInstance()
                                				.referenceName("variables")
                                				.variableName("created")
                                				.typeName(this.typeName(domain.getVariable()))
                                				.children(v.newChangeContext()
                                                        .expression("aql:created")
                                                        .children(v.newSetValue()
                                                                .featureName("name")
                                                                .valueExpression(ServiceMethod.of0(Services::getDefaultName)
                                                                        .aql("created.eContainer(" + this.typeName(domain.getSheet()) + ")"))
                                                                .build(),
                                                                v.newSetValue()
                                                                        .featureName("type")
                                                                        .valueExpression("aql:guesstimate::VariableType::normal")
                                                                        .build())
                                                        .build())
                                				.build()


                                				)
                                		.build())
                                .build()
                                )
                        .build())
                .build();
     // @formatter:on
		Diagnostician validator = new Diagnostician() {
			@Override
			protected boolean isValidateContentsRecursively() {
				return true;
			}
		};
		Diagnostic result = validator.validate(diag);

		if (result.getSeverity() == Diagnostic.ERROR) {
			throw new IllegalStateException("Invalid Guesstimate view:" + this.diagnosticsMessages(result, ""));
		}
		return diag;

	}

	/**
	 * @param distribution
	 * @return
	 */
	private String iconFromType(EClass distribution) {
		return "/icons/full/obj16/" + distribution.getName() + ".svg";
	}

	/**
	 * @param result
	 * @param indent
	 */
	private String diagnosticsMessages(Diagnostic result, String indent) {
		var builder = new StringBuilder();
		builder.append(indent).append(this.getSeverity(result)).append(result.getMessage()).append("\n");
		String childIndent = indent + "--";
		for (Diagnostic child : result.getChildren()) {
			builder.append(this.diagnosticsMessages(child, childIndent));
		}
		return builder.toString();
	}

	/**
	 * @param result
	 * @return
	 */
	private String getSeverity(Diagnostic result) {
		switch (result.getSeverity()) {
		case Diagnostic.ERROR:
			return "ERROR";
		case Diagnostic.CANCEL:
			return "CANCEL";
		case Diagnostic.OK:
			return "OK";
		case Diagnostic.WARNING:
			return "WARNING";
		default:
			return "UNKNOWN:" + result.getSeverity();
		}
	}

	/**
	 * @param sheet
	 * @return
	 */
	private String typeName(EClass clazz) {
		return clazz.getEPackage().getName() + "::" + clazz.getName();
	}

}
