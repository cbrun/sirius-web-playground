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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl.BinaryIO.Version;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.sirius.components.collaborative.api.IEditingContextEventProcessorRegistry;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.components.emf.services.EObjectIDManager;
import org.eclipse.sirius.emfjson.resource.JsonResource;
import org.eclipse.sirius.emfjson.resource.JsonResourceFactoryImpl;
import org.eclipse.sirius.emfjson.resource.JsonResourceImpl;
import org.eclipse.sirius.web.services.api.document.Document;
import org.eclipse.sirius.web.services.api.document.IDocumentService;
import org.eclipse.sirius.web.services.editingcontext.api.IEditingDomainFactoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;

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
	private IEditingDomainFactoryService editingDomainFactory;
	
	@Autowired
	private IEditingContextEventProcessorRegistry editingContextEventProcessorRegistry;

	private final Logger logger = LoggerFactory.getLogger(RestfulEMFResourceController.class);

	@GetMapping("/projects/{projectId:.*}/{documentIndex:.*}/bin")
	@ResponseBody
	byte[] getBinaryResource(@PathVariable String projectId, @PathVariable int documentIndex) {
		this.logger.info("GET"); //$NON-NLS-1$

		byte[] result = new byte[0];
		List<Document> documents = this.documentService.getDocuments(projectId);
		if (documentIndex < documents.size()) {
			Document doc = documents.get(documentIndex);
			ResourceSet loadingResourceSet = this.prepareResourceSet(projectId);

			URI uri = createURIForDocument(doc);
			Resource resource = new JsonResourceFactoryImpl().createResource(uri);
			loadingResourceSet.getResources().add(resource);

			Optional<byte[]> optionalBytes = this.documentService.getBytes(doc, IDocumentService.RESOURCE_KIND_JSON);
			if (optionalBytes.isPresent()) {
				EObjectIDManager idManager = new EObjectIDManager();
				Map<String, Object> options = new HashMap<>();
				options.put(JsonResource.OPTION_ID_MANAGER, idManager);
				try (var inputStream = new ByteArrayInputStream(optionalBytes.get())) {
					resource.load(inputStream, options);
					loadingResourceSet.getResources().add(resource);
				} catch (IOException exception) {
					this.logger.warn(exception.getMessage(), exception);
				}
			}
			Stopwatch binSave = Stopwatch.createStarted();

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				Resource binR = new BinaryResourceImpl(uri);
				binR.getContents().addAll(resource.getContents());
				Map<String, Object> options = new HashMap<>();
				options.put(BinaryResourceImpl.OPTION_VERSION, Version.VERSION_1_1);
				options.put(BinaryResourceImpl.OPTION_STYLE_BINARY_ENUMERATOR, Boolean.TRUE);
				options.put(BinaryResourceImpl.OPTION_STYLE_BINARY_DATE, Boolean.TRUE);
				options.put(BinaryResourceImpl.OPTION_STYLE_BINARY_FLOATING_POINT, Boolean.TRUE);
				binR.save(outputStream, options);
				result = outputStream.toByteArray();
			} catch (IOException e) {
				this.logger.error("Error saving document " + projectId + "/" + documentIndex + " to  binary stream.",
						e);
			} finally {
				binSave.stop();
			}
			this.logger.info("binary save : " + binSave.elapsed(TimeUnit.MILLISECONDS) + " ms."); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			// 404
		}
		this.logger.info("GET, content: " + result.length + " bytes."); //$NON-NLS-1$
		return result;
	}

	private URI createURIForDocument(Document doc) {
		URI uri = URI.createURI("sirius:///" + doc.getId() + "/");
		return uri;
	}

	@GetMapping("/projects/{projectId:.*}/{documentIndex:.*}/xmi")
	@ResponseBody
	byte[] getXMIResource(@PathVariable String projectId, @PathVariable int documentIndex) {
		this.logger.info("GET XMI"); //$NON-NLS-1$
		byte[] result = this.getXMI(projectId, documentIndex, false);
		return result;
	}

	@GetMapping("/projects/{projectId:.*}/{documentIndex:.*}/csv")
	@ResponseBody
	String getCSVResource(@PathVariable String projectId, @PathVariable int documentIndex,
			@RequestParam(defaultValue = "\t", name = "sep") String separator) {
		this.logger.info("GET CSV"); //$NON-NLS-1$
		List<Document> documents = this.documentService.getDocuments(projectId);
		if (documentIndex < documents.size()) {
			Document doc = documents.get(documentIndex);
			ResourceSet loadingResourceSet = this.prepareResourceSet(projectId);
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

	@PutMapping("/projects/{projectId:.*}/{documentIndex:.*}/csv")
	@ResponseBody
	void putCSVResource(@RequestBody String content, @PathVariable String projectId, @PathVariable int documentIndex) {
		this.logger.info("PUT CSV, content: " + content.length() + " size."); //$NON-NLS-1$
	}

	@GetMapping("/projects/{projectId:.*}/{documentIndex:.*}/xmi.zip")
	@ResponseBody
	byte[] getZippedXMIResource(@PathVariable String projectId, @PathVariable int documentIndex) {
		this.logger.info("GET"); //$NON-NLS-1$
		byte[] result = this.getXMI(projectId, documentIndex, true);
		return result;
	}

	private byte[] getXMI(String projectId, int documentIndex, boolean zipped) {
		byte[] result = new byte[0];
		List<Document> documents = this.documentService.getDocuments(projectId);
		if (documentIndex < documents.size()) {
			Document doc = documents.get(documentIndex);
			ResourceSet loadingResourceSet = this.prepareResourceSet(projectId);

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

					if (zipped) {
						optionsXMI.put(Resource.OPTION_ZIP, Boolean.TRUE);
					}
					xmiR.save(outputStream, optionsXMI);
					result = outputStream.toByteArray();
				} catch (IOException e) {
					this.logger.error("Error  " + projectId + "/" + documentIndex + " to  binary stream.", e);
				}
			}
		} else {
			// 404
		}
		this.logger.info("GET, content: " + result.length + " bytes."); //$NON-NLS-1$
		return result;
	}

	@PutMapping("/projects/{projectId:.*}/{documentIndex:.*}/xmi")
	@ResponseBody
	void putXMIResource(@RequestBody byte[] content, @PathVariable String projectId, @PathVariable int documentIndex) {
		this.logger.info("PUT, content: " + content.length + " bytes."); //$NON-NLS-1$
		this.putXMI(content, projectId, documentIndex, false);
	}

	@PutMapping("/projects/{projectId:.*}/{documentIndex:.*}/xmi.zip")
	@ResponseBody
	void putXMIResourceZipped(@RequestBody byte[] content, @PathVariable String projectId,
			@PathVariable int documentIndex) {
		this.logger.info("PUT, content: " + content.length + " bytes."); //$NON-NLS-1$
		this.putXMI(content, projectId, documentIndex, true);
	}

	private void putXMI(byte[] content, String projectId, int documentIndex, boolean zipped) {
		Stopwatch binLoad = Stopwatch.createStarted();
		List<Document> documents = this.documentService.getDocuments(projectId);
		if (documentIndex < documents.size()) {
			Document doc = documents.get(documentIndex);
			URI uri = createURIForDocument(doc);

			XMIResource xmiRes = new XMIResourceImpl(uri);
			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
				Map<String, Object> options = new HashMap<>();
				if (zipped == true) {
					options.put(Resource.OPTION_ZIP, Boolean.TRUE);
				}
				xmiRes.load(inputStream, options);

				ReplaceResourceContentInput input = new ReplaceResourceContentInput(UUID.randomUUID(), projectId,
						xmiRes);

				Mono<IPayload> result = this.editingContextEventProcessorRegistry.dispatchEvent(projectId, input);
				this.logger.info("pushed document " + result);

			} catch (IOException e) {
				this.logger.error("Error saving document " + projectId + "/" + documentIndex + " to db.", e);
			} finally {
				binLoad.stop();
			}
		}
	}

	@PutMapping("/projects/{projectId:.*}/{documentIndex:.*}/bin")
	@ResponseBody
	void putBinaryResource(@RequestBody byte[] content, @PathVariable String projectId,
			@PathVariable int documentIndex) {
		this.logger.info("PUT, content: " + content.length + " bytes."); //$NON-NLS-1$

		Stopwatch binLoad = Stopwatch.createStarted();
		List<Document> documents = this.documentService.getDocuments(projectId);
		if (documentIndex < documents.size()) {
			Document doc = documents.get(documentIndex);
			URI uri = createURIForDocument(doc);

			BinaryResourceImpl binR = new BinaryResourceImpl(uri);
			try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content)) {
				Map<String, Object> options = new HashMap<>();
				options.put(BinaryResourceImpl.OPTION_VERSION, Version.VERSION_1_1);
				options.put(BinaryResourceImpl.OPTION_STYLE_BINARY_ENUMERATOR, Boolean.TRUE);
				options.put(BinaryResourceImpl.OPTION_STYLE_BINARY_DATE, Boolean.TRUE);
				options.put(BinaryResourceImpl.OPTION_STYLE_BINARY_FLOATING_POINT, Boolean.TRUE);
				binR.load(inputStream, options);

				ReplaceResourceContentInput input = new ReplaceResourceContentInput(UUID.randomUUID(), projectId, binR);

				Mono<IPayload> result = this.editingContextEventProcessorRegistry.dispatchEvent(projectId, input);
				this.logger.info("pushed document " + result);

			} catch (IOException e) {
				this.logger.error("Error saving document " + projectId + "/" + documentIndex + " to db.", e);
			} finally {
				binLoad.stop();
			}
		}
	}

	private ResourceSet prepareResourceSet(String projectId) {
		return editingDomainFactory.createEditingDomain(projectId).getResourceSet();
	}

}
