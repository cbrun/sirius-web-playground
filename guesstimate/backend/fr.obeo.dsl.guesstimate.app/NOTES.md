### Useful commands while developping

docker run -p 5433:5432 --name guesstimate-postgres \
                            -e POSTGRES_USER=dbuser \
                            -e POSTGRES_PASSWORD=dbpwd \
                            -e POSTGRES_DB=guesstimate-db \
                            -d postgres


docker start /guesstimate-postgres



cd frontend/ && npm ci && npm run build && cd ..
                            
mkdir -p backend/application/syson-application/src/main/resources/static
cp -R frontend/syson/dist/* backend/application/syson-application/src/main/resources/static

### Things that could be interesting with this example

#### The whole project setups and Maven based build.

####  Basic features : instance creation, label edits
Q: what is that variable name again for the new string value ?

####  How to make the images SVG's and what to change ?
 - cf FAQ + getting images from the Eclipse repositories

####  Messages in the UI:
 - tweaking the .properties for the name of features.
 
####  How to integrate another Java framework to support the modeling
 - apache commons, the adapters to bridge the model with it.
 
####  How to regenerate the EMF code without pain and fuss



####  How to write typesafe calls to Java service from AQL queries and avoid most java Strings when building the model.
 

The validation rules and how it translate into the UI.
ARI :  https://github.com/eclipse-sirius/sirius-web/commit/0129cae68d866e8576b443fcf5713d2386c601ba
validation is disabled by default, need to change the application.properties ??: 

`sirius.web.enabled= validation`

> actually this is only needed to re-enable the default EMF validation rules, but custom one should appear anyway.

in addition: make sure to register the validator as a postconstruct of the EMF configuration
`@PostConstruct
public void registerDomainValidator() {
this.eValidatorRegistry.put(GuesstimatePackage.eINSTANCE, new GuesstimateValidator());
}`

And make sure to create a diagnostic with a EStructuralFeature as data as this is how the form editors filter out the validation errors.
Also in the process, had to figure out how to enable the messages translation (or that would fail with an exception).
To do so I tweaked the genmodel to add a qualified class name for the Plugin (in the model section). Then the resource locator is this plugin.

added the dependency :
<dependency>
			<groupId>org.eclipse.sirius</groupId>
			<artifactId>sirius-components-collaborative-validation</artifactId>
			<version>${sirius.web.version}</version>
		</dependency>
or 
	<dependency>
			<groupId>org.eclipse.sirius</groupId>
			<artifactId>sirius-components-validation-graphql</artifactId>
			<version>${sirius.web.version}</version>
		</dependency> 
??

Details View:
 - Form editor with charts
 - EEnum are kinda painful to use in queries and I'm not sure I'm doing it right. => when you want to create option lists or option groups.
 - edit properties of children objects.
 - How to customize the property views
 - how to prevent some sections for some of my model elements

How to add buttons in property views depending on the model state.

How to leverage the metamodel documentation into the tool.
 - within the property views.

How to add Java services and use them into the model description.


How to design and implement a "smart" edit:
 - we have variables, which have names, a description, and a definition.
 I want to be able to edit each one independently
 "A" => no space, so it must be the variable name
 " the something thing" => spaces, so it must be the description
 " : from 10 to 100" => have ':' so it must be the definition.

How to define the graphical modeler decription through Java API.

How to integrate a mini textual language into the model.
 - the use of petitparser for that
 - the use of Adapters

How to display the variables distributions.
 - the charts in the form editor.
 
How to use conditional styles to help the user
 - grey for unused variables (and use of einverse & co)
 - change style based on validation status (!?)
 
How to work on ease label editing
 - smarter service with syntax choices
 
 
How to improve the style:
 - consistent colors
 - alignment
 - contrast
 - repetition (aka size)
 
 
 
How did I implement the behavior/simulation coordination.
 - how to prevent/manage cycles
 - how to update series which are depending on an updated one.
 
An Excel export for the samples (?)


## Short video

details view defined through a declarative model definition, yet using a Java API
documentation integrated in the details view and retrieved from the domain model
charts

create elements, and smart edit 
"from 10 to 30"

 probability to have the machine off = 1 over 5

tP  time for the machine to prepare = from 30 to 35
 --> look details view
 --> look tooltips
 --> look distribution
 

 time for the machine to warmup = from 60 to 40
T: time to get my coffee



