package fr.obeo.playground.restfulemf.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.sirius.components.collaborative.api.IEditingContextEventProcessorRegistry;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.components.emf.utils.EMFResourceUtils;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.eclipse.sirius.emfjson.resource.JsonResourceImpl;
import org.eclipse.sirius.web.services.api.document.Document;
import org.eclipse.sirius.web.services.api.document.IDocumentService;
import org.eclipse.sirius.web.services.api.projects.IProjectService;
import org.eclipse.sirius.web.services.api.projects.Project;
import org.eclipse.sirius.web.services.editingcontext.api.IEditingDomainFactoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import fr.obeo.playground.restfulemf.ReplaceResourceContentInput;
import fr.obeo.playground.restfulemf.SheetDataTable;
import reactor.core.publisher.Mono;

/**
 * @author Cedric Brun <cedric.brun@obeo.fr>
 */
@RestController
public class RestfulEMFResourceController {

	@Autowired
	private IDocumentService documentService;

	@Autowired
	private IProjectService projectService;

	@Autowired
	private IEditingDomainFactoryService editingDomainFactory;

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
		Optional<Document> found = findDocumentBasedOnIDorNames(projectId, documentName);
		if (found.isPresent()) {
			Document doc = found.get();
			ResourceSet loadingResourceSet = this.prepareResourceSet(doc);

			URI uri = createURIForDocument(doc);
			Map<String, Object> options = new HashMap<>();
			EObjectIDManager idManager = new EObjectIDManager();
			options.put(JsonResource.OPTION_ID_MANAGER, idManager);
			Resource resource = new JsonResourceImpl(uri, options);
			loadingResourceSet.getResources().add(resource);

			Optional<byte[]> optionalBytes = this.documentService.getBytes(doc, IDocumentService.RESOURCE_KIND_JSON);
			if (optionalBytes.isPresent()) {
				try (var inputStream = new ByteArrayInputStream(optionalBytes.get())) {
					resource.load(inputStream, options);
					loadingResourceSet.getResources().add(resource);
				} catch (IOException exception) {
					this.logger.warn(exception.getMessage(), exception);
				}

				Stopwatch binSave = Stopwatch.createStarted();
				try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
					XMLResource binR = new XMLResourceImpl(uri);

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
					this.logger.error("Error saving document " + projectId + "/" + documentName + " to  binary stream.",
							e);
				} finally {
					binSave.stop();
				}
				this.logger.info("binary save : " + binSave.elapsed(TimeUnit.MILLISECONDS) + " ms."); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				// 404
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	}

	private Optional<Document> findDocumentBasedOnIDorNames(String projectId, String documentName) {
		Optional<Document> found = Optional.empty();
		Optional<Project> prj = Optional.empty();
		try {
			prj = this.projectService.getProject(UUID.fromString(projectId));
		} catch (IllegalArgumentException e) {
			// the parameter is not an UUID, it most likely is a project name.
			Iterator<Project> it = this.projectService.getProjects().iterator();
			while (it.hasNext() && prj.isEmpty()) {
				Project p = it.next();
				if (projectId.equals(p.getName())) {
					prj = Optional.of(p);
				}
			}
		}
		if (prj.isPresent()) {
			List<Document> projectDocs = this.documentService.getDocuments(prj.get().getId().toString());
			for (Document document : projectDocs) {
				if (document.getName().equals(documentName)) {
					found = Optional.of(document);
				}
			}
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
		Optional<Document> found = findDocumentBasedOnIDorNames(projectId, documentName);
		if (found.isPresent()) {
			Document doc = found.get();
			ResourceSet loadingResourceSet = this.prepareResourceSet(doc);
			URI uri = createURIForDocument(doc);
			Map<String, Object> options = new HashMap<>();
			EObjectIDManager idManager = new EObjectIDManager();
			options.put(JsonResource.OPTION_ID_MANAGER, idManager);
			Resource resource = new JsonResourceImpl(uri, options);
			loadingResourceSet.getResources().add(resource);
			SheetDataTable table = new SheetDataTable();

			Optional<byte[]> optionalBytes = this.documentService.getBytes(doc, IDocumentService.RESOURCE_KIND_JSON);
			if (optionalBytes.isPresent()) {
				try (var inputStream = new ByteArrayInputStream(optionalBytes.get())) {
					resource.load(inputStream, options);
					loadingResourceSet.getResources().add(resource);
				} catch (IOException exception) {
					this.logger.warn(exception.getMessage(), exception);
				}

				int nbObj = 0;
				Iterator<EObject> it = resource.getAllContents();
				while (it.hasNext()) {
					Notifier notifier = it.next();
					if (notifier instanceof EObject) {
						EObject eObj = (EObject) notifier;
						Optional<String> id = idManager.findId(eObj);
						if (id.isPresent()) {
							String eObjUUID = id.get().toString();
							table.updateValue(eObjUUID, "eClass", eObj.eClass().getName()); //$NON-NLS-1$
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
			}
		} else {
			// 404
		}
		return "";
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
		Optional<Document> found = findDocumentBasedOnIDorNames(projectId, documentName);
		if (found.isPresent()) {
			Document doc = found.get();
			ResourceSet loadingResourceSet = this.prepareResourceSet(doc);
			URI uri = createURIForDocument(doc);
			Map<String, Object> options = new HashMap<>();
			EObjectIDManager idManager = new EObjectIDManager();
			options.put(JsonResource.OPTION_ID_MANAGER, idManager);
			Resource resource = new JsonResourceImpl(uri, options);
			loadingResourceSet.getResources().add(resource);

			Optional<byte[]> optionalBytes = this.documentService.getBytes(doc, IDocumentService.RESOURCE_KIND_JSON);
			if (optionalBytes.isPresent()) {
				try (var inputStream = new ByteArrayInputStream(optionalBytes.get())) {
					resource.load(inputStream, options);
					loadingResourceSet.getResources().add(resource);
				} catch (IOException exception) {
					this.logger.warn(exception.getMessage(), exception);
				}

				try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
					XMIResource xmiR = new XMIResourceImpl(uri);
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
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
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

				ReplaceResourceContentInput input = new ReplaceResourceContentInput(UUID.randomUUID(),
						doc.getProject().getId().toString(), xmiRes);

				Mono<IPayload> result = this.editingContextEventProcessorRegistry
						.dispatchEvent(doc.getProject().getId().toString(), input);
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
				ReplaceResourceContentInput input = new ReplaceResourceContentInput(UUID.randomUUID(),
						doc.getProject().getId().toString(), binR);
				Mono<IPayload> result = this.editingContextEventProcessorRegistry
						.dispatchEvent(doc.getProject().getId().toString(), input);
				this.logger.info("pushed document " + result);

			} catch (IOException e) {
				this.logger.error("Error saving document " + projectIdOrName + "/" + documentName + " to db.", e);
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			} finally {
				binLoad.stop();
			}
		}
	}

	private ResourceSet prepareResourceSet(Document doc) {
		return editingDomainFactory.createEditingDomain(doc.getProject().getId().toString()).getResourceSet();
	}

}
