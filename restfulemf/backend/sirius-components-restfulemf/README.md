
Component to provide REST endpoints for model documents exposed as XMI, Binary, Zipped XMI or CSV resource.

Thanks to this you can:
 - access the model elements as CSV suitable for Jupyter notebook.
 - open and edit an EMF model in Sirius Desktop when it is hosted on Sirius Web
 - write a Java program which gets the model, do something, and save the changes back to the server while only using EMF APIs, even without having the dedicated Ecore Java API (the EPackages can be retrieved from a REST endpoint)


### REST Endpoints

#### Binary Resource


`/projects/{Name or ID of the project}/{Name or ID of the document}/bin`


Should be the preferred resource format when the client is using the EMF runtime as the serialization is compact, fast, and supports the element IDs.
Here is a sample usage from a Java client using the EMF runtime.

```java
Map<String, Object> options = new HashMap<>();
options.put(XMLResource.OPTION_BINARY, Boolean.TRUE);
Resource model = new XMLResourceImpl(URI.createURI("http://localhost:8080/projects/Travel Agency/MyModel.uml"));
model.load(options);
//...
// do some changes on the model
//
model.save(options); // changes gets propagated back on the server.

```


#### XMI Resource

`/projects/{Name or ID of the project}/{Name or ID of the document}/xmi`


#### Zipped XMI Resource

`/projects/{Name or ID of the project}/{Name or ID of the document}/xmi.zip`


#### CSV Resource

`/projects/{Name or ID of the project}/{Name or ID of the document}/csv`

**The CSV endpoint** currently only supports `GET` requests and no `PUT`.


#### Reflective access to EPackages
If you don't have prior knowledge of the specific EPackages you can retrieve the list of EPackages declared for a given project using this endpoint:

`/projects/{Name or ID of the project}/epackages/bin`

You'll get a binary resource with all the EPackages, enabling you to load and process the models reflectively in such a way:

```java
Resource usedEPackages = new XMLResourceImpl(URI.createURI(baseURL + projectNameOrID + "/epackages/bin"));
	set.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
	usedEPackages.load(options);

	for (EPackage pak : Iterables.filter(usedEPackages.getContents(), EPackage.class)) {
		if (set.getPackageRegistry().getEPackage(pak.getNsURI()) == null) {
			set.getPackageRegistry().put(pak.getNsURI(), pak);
		}
		System.out.println("Registered : " + pak.getNsURI());
	}
```


### Maturity & Status
This is prototype, suitable for POC but not for production, this is a starting point.
**/!\ All the model/projects data gets accessible for non-logged users**


ID's are provided in XMI and Binary serialization, that means when you save the resource back the objects identity will be kept.

`GET` and `PUT` are supported for XMI, XMI.zip and Binary endpoints.

Only `GET` is supported for CSV for now.

#### Notably missing

Access-rights/authorization management.

Errors management and HTTP error codes.

only mono-valued attributes are exposed through CSV.


#### What could be explored next ?

Managing PUT-like requests for CSV would be great if a client is able to leverage that.
