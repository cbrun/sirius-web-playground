package fr.obeo.playground.restfulemf.sample.configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.sirius.components.core.RepresentationMetadata;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.components.emf.services.EditingContext;
import org.eclipse.sirius.components.emf.utils.EMFResourceUtils;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.eclipse.sirius.emfjson.resource.JsonResourceImpl;
import org.eclipse.sirius.web.persistence.entities.DocumentEntity;
import org.eclipse.sirius.web.persistence.entities.ProjectEntity;
import org.eclipse.sirius.web.persistence.repositories.IDocumentRepository;
import org.eclipse.sirius.web.persistence.repositories.IProjectRepository;
import org.eclipse.sirius.web.services.api.id.IDParser;
import org.eclipse.sirius.web.services.api.projects.IProjectTemplateInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.google.common.base.Stopwatch;

import graphql.com.google.common.collect.Lists;
import graphql.com.google.common.collect.Maps;

@Configuration
public class ManyModelsProjectTemplatesInitializer implements IProjectTemplateInitializer {

	private final Logger logger = LoggerFactory.getLogger(ManyModelsProjectTemplatesInitializer.class);

	@Autowired
	private final IProjectRepository projectRepository = null;

	@Autowired
	private final IDocumentRepository documentRepository = null;

	@Override
	public boolean canHandle(String templateId) {
		return List.of(ManyModelsProjectTemplatesProvider.MANYMODELS_TEMPLATE_ID,
				ManyModelsProjectTemplatesProvider.ONEMILLION_TEMPLATE_ID).contains(templateId);
	}

	@Override
	public Optional<RepresentationMetadata> handle(String templateId, IEditingContext editingContext) {
		Optional<RepresentationMetadata> result = Optional.empty();
		// @formatter:off
        Optional<AdapterFactoryEditingDomain> optionalEditingDomain = Optional.of(editingContext)
                .filter(EditingContext.class::isInstance)
                .map(EditingContext.class::cast)
                .map(EditingContext::getDomain);
        // @formatter:on
		Optional<UUID> editingContextUUID = new IDParser().parse(editingContext.getId());
		List<URI> modelsToCreate = Lists.newArrayList();
		if (templateId.equals(ManyModelsProjectTemplatesProvider.MANYMODELS_TEMPLATE_ID)) {
			modelsToCreate.add(URI.createURI("classpath:/NobelPrize.bpmn"));
			modelsToCreate.add(URI.createURI("classpath:/Big_Guy.flow"));
			modelsToCreate.add(URI.createURI("classpath:/linux-kernel.uml"));
			modelsToCreate.add(URI.createURI("classpath:/library.ecore"));
			modelsToCreate.add(URI.createURI("classpath:/reverse1.ecorebin"));
		} else if (templateId.equals(ManyModelsProjectTemplatesProvider.ONEMILLION_TEMPLATE_ID)) {
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse1.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse2.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse3.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse4.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse5.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse6.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse7.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse8.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse9.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse10.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse11.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse12.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse13.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse14.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse15.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse16.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse17.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse18.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse19.ecorebin"));
			modelsToCreate.add(URI.createURI("classpath:/1Modeling/reverse20.ecorebin"));
		}
		if (optionalEditingDomain.isPresent() && editingContextUUID.isPresent()) {
			Optional<ProjectEntity> prj = this.projectRepository.findById(editingContextUUID.get());
			if (prj.isPresent()) {

				for (URI modelToCreate : modelsToCreate) {

					DocumentEntity documentEntity = new DocumentEntity();
					documentEntity.setProject(prj.get());
					documentEntity.setName(modelToCreate.lastSegment());
					try {
						Resource localXMI = loadFromXMI(modelToCreate);

						URI uri = URI.createURI("sirius:///" + documentEntity.getId());
						Map<String, Object> options = new HashMap<>();
						options.putAll(new EMFResourceUtils().getFastJSONSaveOptions());
						EObjectIDManager idManager = new EObjectIDManager();
						options.put(JsonResource.OPTION_ID_MANAGER, idManager);
						Resource jsonRes = new JsonResourceImpl(uri, options);
						jsonRes.getContents().addAll(localXMI.getContents());

						Stopwatch binSave = Stopwatch.createStarted();
						try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
							jsonRes.save(outputStream, options);
							documentEntity.setContent(new String(outputStream.toByteArray(), "UTF-8"));
							documentEntity = this.documentRepository.save(documentEntity);
						} catch (IOException e) {
							logger.error("Error loading initializing project from template models", e);
						} finally {
							binSave.stop();
						}

					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
			}

		}
		return result;
	}

	private Resource loadFromXMI(URI uri) throws IOException {
		if ("ecorebin".equals(uri.fileExtension())) {
			XMLResource bR = new XMLResourceImpl(uri);
			Map<String, Object> options = Maps.newLinkedHashMap();
			options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
			bR.load(options);
			return bR;
		} else {
			XMIResourceImpl bR = new XMIResourceImpl(uri);
			bR.load(new EMFResourceUtils().getXMILoadOptions());
			return bR;
		}
	}

}