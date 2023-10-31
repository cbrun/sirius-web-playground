package fr.obeo.playground.restfulemf.controllers;

import org.eclipse.sirius.components.collaborative.diagrams.export.svg.DiagramExportService;
import org.eclipse.sirius.web.services.api.document.IDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Cedric Brun <cedric.brun@obeo.fr>
 */
@RestController
public class SVGDiagramController {

	@Autowired
	private IDocumentService documentService;

	@Autowired
	private DiagramExportService export;

	private final Logger logger = LoggerFactory.getLogger(SVGDiagramController.class);

	@GetMapping("/projects/{projectId:.*}/{diagramName:.*}/svg")
	@ResponseBody
	String getSVG(@PathVariable String projectId, @PathVariable String diagramName) {
		this.logger.info("GET"); //$NON-NLS-1$
		return "<svg></svg>";
	}

}
