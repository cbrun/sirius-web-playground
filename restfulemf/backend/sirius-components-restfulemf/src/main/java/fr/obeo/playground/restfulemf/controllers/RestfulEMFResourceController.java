package fr.obeo.playground.restfulemf.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sirius.components.collaborative.api.IEditingContextEventProcessorRegistry;
import org.eclipse.sirius.components.core.api.IEditingContextSearchService;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.components.emf.services.JSONResourceFactory;
import org.eclipse.sirius.components.emf.services.api.IEMFEditingContext;
import org.eclipse.sirius.components.emf.utils.EMFResourceUtils;
import org.eclipse.sirius.web.domain.boundedcontexts.project.Project;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.Document;
import org.eclipse.sirius.web.domain.boundedcontexts.semanticdata.services.api.ISemanticDataSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import fr.obeo.playground.restfulemf.ReplaceResourceContentInput;
import fr.obeo.playground.restfulemf.SheetDataTable;
import graphql.com.google.common.collect.Sets;
import reactor.core.publisher.Mono;

/**
 * @author Cedric Brun <cedric.brun@obeo.fr>
 */
@RestController
public class RestfulEMFResourceController {

	@Autowired
	private final IEditingContextSearchService editingContextSearchService = null;

	@Autowired
	private ISemanticDataSearchService semanticDataSearchService;

	@Autowired
	private IEditingContextEventProcessorRegistry editingContextEventProcessorRegistry;

	@Autowired
	private List<EPackage> registeredPackages = Lists.newArrayList();

	private final Logger logger = LoggerFactory.getLogger(RestfulEMFResourceController.class);

	@GetMapping("/projects/{projectId:.*}/epackages/bin")
	@ResponseBody
	byte[] getEPackages(@PathVariable String projectId) {
		URI uri = URI.createURI("sirius:///" + projectId + "/epackages");

		Resource res = new XMLResourceImpl(uri);
		Map<String, Object> options = new HashMap<>();
		options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
		/*
		 * we are using the same copier to make sure the references are retained accross
		 * EPackages.
		 */
		Copier copier = new EcoreUtil.Copier();
		for (EPackage ePackage : registeredPackages) {
			res.getContents().add(copier.copy(ePackage));
		}
		copier.copyReferences();

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			res.save(outputStream, options);
			return outputStream.toByteArray();
		} catch (IOException e) {
			this.logger.error("Error  " + projectId + "/epackages to binary stream.", e);
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}

	@GetMapping("/projects/{projectId:.*}/{documentName:.*}/bin")
	@ResponseBody
	byte[] getBinaryResource(@PathVariable String projectId, @PathVariable String documentName) {
		this.logger.info("GET"); //$NON-NLS-1$
		Optional<Resource> res = getResource(projectId, documentName);
		if (res.isPresent()) {
			Resource resource = res.get();
			EObjectIDManager idManager = new EObjectIDManager();

			Stopwatch binSave = Stopwatch.createStarted();
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				XMLResource binR = new XMLResourceImpl(resource.getURI());

				Map<String, Object> outOps = new HashMap<>();
				outOps.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
				binR.getContents().addAll(resource.getContents());
				Iterator<EObject> it = binR.getAllContents();
				while (it.hasNext()) {
					EObject cur = it.next();
					Optional<String> id = idManager.findId(cur);
					if (id.isPresent()) {
						binR.setID(cur, id.get());
					}
				}
				binR.save(outputStream, outOps);
				byte[] result = outputStream.toByteArray();
				this.logger.info("GET, content: " + result.length + " bytes."); // $NON-NL
				return result;
			} catch (IOException e) {
				this.logger.error("Error saving document " + projectId + "/" + documentName + " to  binary stream.", e);
			} finally {
				binSave.stop();
			}
			this.logger.info("binary save : " + binSave.elapsed(TimeUnit.MILLISECONDS) + " ms."); //$NON-NLS-1$ //$NON-NLS-2$
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}

	@GetMapping("/projects/{projectId:.*}/documents")
	@ResponseBody
	Map<String, String> getDocuments(@PathVariable String projectId) {

		AggregateReference<Project, UUID> projectKey = AggregateReference.to(UUID.fromString(projectId));

		var optionalSemanticData = this.semanticDataSearchService.findByProject(projectKey);

		if (optionalSemanticData.isPresent()) {
			Map<String, String> results = Maps.newLinkedHashMap();
			Set<Document> projectDocs = optionalSemanticData.get().getDocuments();
			for (Document document : projectDocs) {
				results.put(document.getId().toString(), document.getName());
			}
			return results;
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}

	private Optional<Document> findDocumentBasedOnIDorNames(String projectId, String documentName) {
		Optional<Document> found = Optional.empty();
		AggregateReference<Project, UUID> projectKey = AggregateReference.to(UUID.fromString(projectId));

		var optionalSemanticData = this.semanticDataSearchService.findByProject(projectKey);

		if (optionalSemanticData.isPresent()) {

			Set<Document> projectDocs = optionalSemanticData.get().getDocuments();
			for (Document document : projectDocs) {
				if (document.getName().equals(documentName)) {
					found = Optional.of(document);
				}
			}
			// FIXME this logic needs additional treatment to provide more flexibility ,
			// here we are just falling back to the first document.
			if (found.isEmpty() && projectDocs.size() > 0) {
				found = Optional.of(projectDocs.iterator().next());
			}
		} else {
			this.logger.error("Error finding project " + projectId + ". ");
		}
		return found;
	}

	private URI createURIForDocument(Document doc) {
		URI uri = URI.createURI("sirius:///" + doc.getId());
		return uri;
	}

	@GetMapping("/projects/{projectId:.*}/{documentName:.*}/xmi")
	@ResponseBody
	byte[] getXMIResource(@PathVariable String projectId, @PathVariable String documentName) {
		this.logger.info("GET XMI"); //$NON-NLS-1$
		byte[] result = this.getXMI(projectId, documentName, false);
		return result;
	}

	@GetMapping("/projects/{projectId:.*}/{documentName:.*}/csv")
	@ResponseBody
	String getCSVResource(@PathVariable String projectId, @PathVariable String documentName,
			@RequestParam(defaultValue = "\t", name = "sep") String separator) {
		this.logger.info("GET CSV"); //$NON-NLS-1$
		Optional<Resource> res = getResource(projectId, documentName);
		if (res.isPresent()) {
			Resource resource = res.get();
			EObjectIDManager idManager = new EObjectIDManager();
			SheetDataTable table = new SheetDataTable();

			int nbObj = 0;
			Iterator<EObject> it = resource.getAllContents();
			Set<EObject> toIgnore = Sets.newLinkedHashSet();
			while (it.hasNext()) {
				Notifier notifier = it.next();
				if (notifier instanceof EObject && !toIgnore.contains(notifier)) {
					EObject eObj = (EObject) notifier;
					Optional<String> id = idManager.findId(eObj);
					if (id.isPresent()) {
						String lineUUID = id.get().toString();
						table.updateValue(lineUUID, "eClass", eObj.eClass().getName()); //$NON-NLS-1$
						nbObj = putAttributesInTable(table, nbObj, eObj, lineUUID);
						// for each containment with upper bound 1, we add contained element attributes
						// on the same line making it easier to process.
						for (EReference childRef : eObj.eClass().getEAllContainments()) {
							if (childRef.getUpperBound() == 1) {
								EObject child = (EObject) eObj.eGet(childRef);
								if (child != null) {
									toIgnore.add(child);
									table.updateValue(lineUUID, childRef.getName(), child.eClass().getName()); // $NON-NLS-1$
									nbObj = putAttributesInTable(table, nbObj, child, lineUUID);
								}
							}

						}

					}
				}
			}
			logger.info("Generating CSV, number of model elements: " + nbObj);

			table.fillEmptyCells();

			StringBuffer buf = new StringBuffer();
			for (List<Object> line : table.getValues()) {
				buf.append('\n');
				buf.append(Joiner.on(separator).join(line));
			}
			buf.append('\n');
			return buf.toString();
		} else {
			// 404
		}
		return "";
	}

	/**
	 * @param table
	 * @param nbObj
	 * @param eObj
	 * @param eObjUUID
	 * @return
	 */
	private int putAttributesInTable(SheetDataTable table, int nbObj, EObject eObj, String eObjUUID) {
		nbObj++;
		for (EAttribute eatt : eObj.eClass().getEAllAttributes()) {
			if (!eatt.isMany()) {
				Object val = eObj.eGet(eatt);
				if (val != null) {
					String strValue = ""; //$NON-NLS-1$
					if (val instanceof String) {
						strValue = "\"" + val + "\""; //$NON-NLS-1$//$NON-NLS-2$
					} else {
						strValue = eatt.getEType().getEPackage().getEFactoryInstance()
								.convertToString((EDataType) eatt.getEType(), val);
					}
					table.updateValue(eObjUUID, eatt.getName(), strValue);
				}
			} else {
				// TODO handle here the case of multi-valued attributes.
			}
		}
		return nbObj;
	}

	@PutMapping("/projects/{projectId:.*}/{documentName:.*}/csv")
	@ResponseBody
	void putCSVResource(@RequestBody String content, @PathVariable String projectId,
			@PathVariable String documentName) {
		this.logger.info("PUT CSV, content: " + content.length() + " size."); //$NON-NLS-1$
	}

	@GetMapping("/projects/{projectId:.*}/{documentName:.*}/xmi.zip")
	@ResponseBody
	byte[] getZippedXMIResource(@PathVariable String projectId, @PathVariable String documentName) {
		byte[] result = this.getXMI(projectId, documentName, true);
		return result;
	}

	private byte[] getXMI(String projectId, String documentName, boolean zipped) {

		Optional<Resource> res = getResource(projectId, documentName);
		if (res.isPresent()) {
			Resource resource = res.get();
			EObjectIDManager idManager = new EObjectIDManager();

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				XMIResource xmiR = new XMIResourceImpl(resource.getURI());
				xmiR.getContents().addAll(resource.getContents());
				Iterator<EObject> it = xmiR.getAllContents();
				while (it.hasNext()) {
					EObject cur = it.next();
					Optional<String> id = idManager.findId(cur);
					if (id.isPresent()) {
						xmiR.setID(cur, id.get());
					}
				}
				Map<String, Object> optionsXMI = new HashMap<>();
				optionsXMI.putAll(new EMFResourceUtils().getXMILoadOptions());
				if (zipped) {
					optionsXMI.put(Resource.OPTION_ZIP, Boolean.TRUE);
				}
				xmiR.save(outputStream, optionsXMI);
				byte[] result = outputStream.toByteArray();
				this.logger.info("GET, content: " + result.length + " bytes."); // $NON-NL
				return result;
			} catch (IOException e) {
				this.logger.error("Error  " + projectId + "/" + documentName + " to  binary stream.", e);
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	@PutMapping("/projects/{projectId:.*}/{documentName:.*}/xmi")
	@ResponseBody
	void putXMIResource(@RequestBody byte[] content, @PathVariable String projectId,
			@PathVariable String documentName) {
		this.logger.info("PUT, content: " + content.length + " bytes."); //$NON-NLS-1$
		this.putXMI(content, projectId, documentName, false);
	}

	@PutMapping("/projects/{projectIdOrName:.*}/{documentName:.*}/xmi.zip")
	@ResponseBody
	void putXMIResourceZipped(@RequestBody byte[] content, @PathVariable String projectIdOrName,
			@PathVariable String documentName) {
		this.logger.info("PUT, content: " + content.length + " bytes."); //$NON-NLS-1$
		this.putXMI(content, projectIdOrName, documentName, true);
	}

	private void putXMI(byte[] content, String projectId, String documentName, boolean zipped) {
		Stopwatch binLoad = Stopwatch.createStarted();
		Optional<Document> found = findDocumentBasedOnIDorNames(projectId, documentName);
		if (found.isPresent()) {
			Document doc = found.get();
			URI uri = createURIForDocument(doc);

			XMIResource xmiRes = new XMIResourceImpl(uri);
			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
				Map<String, Object> options = new HashMap<>();
				if (zipped == true) {
					options.put(Resource.OPTION_ZIP, Boolean.TRUE);
				}
				xmiRes.load(inputStream, options);

				ReplaceResourceContentInput input = new ReplaceResourceContentInput(UUID.randomUUID(), xmiRes);

				Mono<IPayload> result = this.editingContextEventProcessorRegistry.dispatchEvent(projectId, input);
				this.logger.info("pushed document " + result);

			} catch (IOException e) {
				this.logger.error("Error saving document " + projectId + "/" + documentName + " to db.", e);
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			} finally {
				binLoad.stop();
			}
		}
	}

	@PutMapping("/projects/{projectIdOrName:.*}/{documentName:.*}/bin")
	@ResponseBody
	void putBinaryResource(@RequestBody byte[] content, @PathVariable String projectIdOrName,
			@PathVariable String documentName) {
		this.logger.info("PUT, content: " + content.length + " bytes."); //$NON-NLS-1$

		Stopwatch binLoad = Stopwatch.createStarted();
		Optional<Document> found = findDocumentBasedOnIDorNames(projectIdOrName, documentName);
		if (found.isPresent()) {
			Document doc = found.get();
			URI uri = createURIForDocument(doc);

			XMLResource binR = new XMLResourceImpl(uri);
			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
				Map<String, Object> options = new HashMap<>();
				options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
				binR.load(inputStream, options);
				ReplaceResourceContentInput input = new ReplaceResourceContentInput(UUID.randomUUID(), binR);
				Mono<IPayload> result = this.editingContextEventProcessorRegistry.dispatchEvent(projectIdOrName, input);
				this.logger.info("pushed document " + result);

			} catch (IOException e) {
				this.logger.error("Error saving document " + projectIdOrName + "/" + documentName + " to db.", e);
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			} finally {
				binLoad.stop();
			}
		}
	}

	private Optional<org.eclipse.emf.ecore.resource.Resource> getResource(String editingContextId, String documentId) {
		Optional<Document> found = findDocumentBasedOnIDorNames(editingContextId, documentId);
		if (found.isPresent()) {
			return this.editingContextSearchService.findById(editingContextId)
					.filter(IEMFEditingContext.class::isInstance).map(IEMFEditingContext.class::cast)
					.flatMap(editingContext -> {
						var uri = new JSONResourceFactory().createResourceURI(found.get().getId().toString());
						return editingContext.getDomain().getResourceSet().getResources().stream()
								.filter(resource -> resource.getURI().equals(uri)).findFirst();
					});
		} else {
			return Optional.empty();
		}

	}

}
