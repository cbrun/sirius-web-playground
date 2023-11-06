/*****************************************************************************
 * Copyright (c) 2023 CEA LIST, Obeo.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Obeo - Initial API and implementation
 *****************************************************************************/
package fr.obeo.playground.restfulemf;

import com.google.common.collect.Iterables;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.junit.jupiter.api.Test;

/**
 * @author cedric
 */
public class RESTDemoTests {

	@Test
	public void emfFrameworkSetup() throws Exception {
		Map<String, Object> options = new HashMap<>();
		options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
		Resource model = new XMLResourceImpl(URI.createURI("http://localhost:8080/projects/Travel Agency/MyModel.uml"));
		model.load(options);
	}

	/**
	 * Demo code showing how to access an UML model hosted on a Papyrus Web
	 * instance, loading it using only reflective APIs (and not the UML EMF Java
	 * API), changing the model content using EMF reflective APIs (eGet/eSet) and
	 * saving the model back, while retaining the IDs.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testReflectiveWithUMLModel() throws Exception {
		/*
		 * XML Resource with binary option is the best choice as it also serializes
		 * extrinsic IDs and use better defaults than BinaryResourceImpl.
		 */
		Map<String, Object> options = new HashMap<>();
		options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);

		/*
		 * configure a ResourceSet to load the models from the web instance by
		 * retrieving and registering the EPackages which are used.
		 */
		ResourceSet set = new ResourceSetImpl();
		String projectNameOrID = "ea808cf6-4aec-4e4f-a208-ff5931677c53";
//		String projectNameOrID = "TravelAgency";

		String baseURL = "http://localhost:8080/projects/";
		Resource usedEPackages = new XMLResourceImpl(URI.createURI(baseURL + projectNameOrID + "/epackages/bin"));
		set.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		usedEPackages.load(options);

		for (EPackage pak : Iterables.filter(usedEPackages.getContents(), EPackage.class)) {
			if (set.getPackageRegistry().getEPackage(pak.getNsURI()) == null) {
				set.getPackageRegistry().put(pak.getNsURI(), pak);
			}
			System.out.println("Registered : " + pak.getNsURI());
		}
		String url = baseURL + projectNameOrID + "/TravalAgency.uml/bin";
		Resource model = new XMLResourceImpl(URI.createURI(url));
		set.getResources().add(model);
		model.load(options);
		/*
		 * Let's work on the model reflectively.
		 */
		Iterator<EObject> it = model.getAllContents();
		while (it.hasNext()) {
			EObject e = it.next();
			if (e.eClass().getName().equals("Property") && e.eClass().getEPackage().getName().equals("uml")) {
				System.out.println(e);
				EStructuralFeature nameFeature = e.eClass().getEStructuralFeature("name");
				String nameValue = (String) e.eGet(nameFeature);
//				e.eSet(nameFeature, nameValue.replace("Renamed", "")); // remove "Renamed"
				e.eSet(nameFeature, nameValue + "Renamed"); // append "Renamed"

			}
		}
		model.save(options);
	}

	// @Test
	// public void testReflectiveWithManualRegistration() throws Exception {
	// ResourceSet set = new ResourceSetImpl();
	// Map<String, Object> options = new HashMap<>();
	// options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
	// Resource usedEPackages = new
	// XMLResourceImpl(URI.createURI("http://localhost:8080/projects/ea808cf6-4aec-4e4f-a208-ff5931677c53/epackages/bin"));;
	// set.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
	// usedEPackages.load(options);
	// for (EPackage pak : Iterables.filter(usedEPackages.getContents(),
	// EPackage.class)) {
	// if (set.getPackageRegistry().getEPackage(pak.getNsURI()) == null) {
	// set.getPackageRegistry().put(pak.getNsURI(), pak);
	// }
	// System.out.println("Registered : " + pak.getNsURI());
	// }
	// set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
	// set.getPackageRegistry().put(TypesPackage.eNS_URI, TypesPackage.eINSTANCE);
	// // set.getPackageRegistry().put(PapyrusJavaPackage.eNS_URI,
	// PapyrusJavaPackage.eINSTANCE);
	// String url =
	// "http://localhost:8080/projects/ea808cf6-4aec-4e4f-a208-ff5931677c53/TravalAgency.uml/bin";
	// Resource model = new XMLResourceImpl(URI.createURI(url));
	// set.getResources().add(model);
	// model.load(options);
	// // not working as we are reflective here !!!
	// Iterator<Property> it = Iterators.filter(model.getAllContents(),
	// Property.class);
	// while (it.hasNext()) {
	// Property e = it.next();
	// e.setName(e.getName() + "JavaIsTyped");
	// }
	// model.save(options);
	// }

	//
	// @Test
	// public void testUML() throws Exception {
	// Registry globalFactoryRegistryInstance = Resource.Factory.Registry.INSTANCE;
	//
	// // initialize the registry from the global
	// Registry factoryRegistry = new ResourceFactoryRegistryImpl();
	// Map<String, Object> protocolToFactoryMap =
	// factoryRegistry.getProtocolToFactoryMap();
	// globalFactoryRegistryInstance.getProtocolToFactoryMap().forEach((key, value)
	// -> protocolToFactoryMap.put(key,
	// value));
	// Map<String, Object> extensionToFactoryMap =
	// factoryRegistry.getExtensionToFactoryMap();
	// globalFactoryRegistryInstance.getExtensionToFactoryMap().forEach((key, value)
	// -> extensionToFactoryMap.put(key,
	// value));
	// Map<String, Object> contentTypeToFactoryMap =
	// factoryRegistry.getContentTypeToFactoryMap();
	// globalFactoryRegistryInstance.getContentTypeToFactoryMap().forEach((key,
	// value) ->
	// contentTypeToFactoryMap.put(key, value));
	//
	// // Add factory associated to uml extension
	// factoryRegistry.getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION,
	// new UMLResourceFactoryImpl());
	//
	// ResourceSet set = new ResourceSetImpl();
	// set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
	// set.getPackageRegistry().put(PapyrusJavaPackage.eNS_URI,
	// PapyrusJavaPackage.eINSTANCE);
	// set.getPackageRegistry().put(Types, set)
	// set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("uml", new
	// UMLResourceFactoryImpl());
	// set.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new
	// XMI2UMLResourceFactoryImpl());
	// set.getResourceFactoryRegistry().getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
	// new
	// XMIResourceFactoryImpl());
	// String url =
	// "http://localhost:8080/projects/ee476797-9898-4165-b565-1207c288e2a2/TravalAgency.uml/bin";
	//
	// Resource model = new BinaryResourceImpl(URI.createURI(url));
	// Map<String, Object> options = new HashMap<>();
	// options.put(BinaryResourceImpl.OPTION_VERSION, Version.VERSION_1_1);
	// options.put(BinaryResourceImpl.OPTION_STYLE_BINARY_ENUMERATOR, Boolean.TRUE);
	// options.put(BinaryResourceImpl.OPTION_STYLE_BINARY_DATE, Boolean.TRUE);
	// options.put(BinaryResourceImpl.OPTION_STYLE_BINARY_FLOATING_POINT,
	// Boolean.TRUE);
	//
	// model.load(options);
	//
	// Iterator<Property> it = Iterators.filter(model.getAllContents(),
	// Property.class);
	// while (it.hasNext()) {
	// Property p = it.next();
	// System.out.println(p);
	// p.setName(p.getName() + "Renamed");
	// }
	//
	// model.save(Collections.EMPTY_MAP);
	// }

}
