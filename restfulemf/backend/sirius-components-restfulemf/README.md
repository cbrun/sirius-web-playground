
Component to provide REST endpoints for model documents exposed as XMI, Binary, Zipped XMI or CSV resource.

Thanks to this you can:
 - access the model elements as CSV suitable for Jupyter notebook.
 - open and edit an EMF model in Sirius Desktop when it is hosted on Sirius Web
 - write a Java program which gets the model, do something, and save the changes back to the server while only using EMF APIs.

ID's are provided in XMI serialization.

`GET` and `PUT` are supported for XMI, XMI.zip and Binary endpoints.
Only `GET` is supported for CSV.


### Maturity

This is prototype, suitable for POC but not for production.
**/!\ All the model/projects data gets accessible for non-logged users**

#### Notably missing

Access-rights/authorization management.

Errors management and HTTP error codes.

only mono-valued attributes are exposed through CSV.


#### What could be explored next ?

Managing PUT-like requests for CSV would be great if a client is able to leverage that.
