# Feedback Service web backend

The Feedback Service Web Backend consists of two components: a *RDF4J Server* implementation to store RDF data and the actual *web backend* to provide endpoints for interacting with the RDF4J server.

## Build & Run

In order to build and run the project:

1. From command line:
  * run `mvn install` to build the rdf4jserver and the web backend components
  * Run start_rdf4j_server.bat file
  * Open "localhost:8090/workbench/repositories" in the browser. If required, change RDF4J Server Url to the one used by the web backend
  * start the web backend via
  
   `java -jar /feedback_webservice/target/feedback_webservice-0.0.1-SNAPSHOT.jar <optional: rdf4j server location> `
  * The standard address used by the web backend to connect to the rdf4j server is `http://localhost:8090/rdf4` . In order to change this, e.g. because the server is not running on localhost, provide the address to be used as command line argument. The web backend itself runs on **port 8803** by default. 

2. Via Docker:
  * If necessary, make changes to the docker-compose as needed
  * Start everything via `docker-compose up`

## Set up the repositories

In order to work properly with the web backend, the repositories need to be configured and populated with data.

1. Set up a repository for feedback example data:
  * Choose "New Repository" in the menu on the left side
     * Type: Native Store
	 * ID: feedback_example
	 * Title: FeedbackExampleData
  * Click next:
     * Type: Native Java Store, ID and Title as above.
  * click Create
2. Add Data to the repository
  * In the *Repositories* list, select the freshly created example_feedback repository
  * On the left hand side, under *Modify*, select *Add*
  * Either upload the exampleDataset.ttl via the file upload button, or paste the rdf data to be added into the text box and click *Upload*
3. Set up a repository for travel mode example data
  * Choose "New Repository" in the menu on the left side. This time, use *travelmodedata* as ID
  * Click create
  * You can generate example data automatically via a POST request to this endpoint:
  
  `http://<web backend location>/feedback_service/travelmode/generateTestData?number=<number of datasets to be generated>`
  
## Using the feedback service

### User feedbacks
In order to retrieve user feedbacks in JSON-LD format, the following requests are supported:

* `GET <backend_location>/feedback_service/allFeedbacks`, accept: application/ld+json : Get the entire data set of feedbacks stored in the triple store at the current time
* `GET <backend_location>/feedback_service/feedback` , accept: application/ld+json, Param feedbackId: Get the feedback with the given ID
* `GET <backend_location>/feedback_service/feedbackWithPermanentReason`, accept: application/ld+json: Get the feedbacks with a reason that is considered to be "permanent"
* `GET <backend_location>/feedback_service/allPermanentReasons`, accept: application/ld+json: Get a list of reasons that are currently causing delays, along with the feedbacks by which they were reported

Additionally, the following endpoint can be used to convert RDF data in turtle format to JSON-LD:
* `POST <backend_location>/ttlToJson`, accept: application/ld+json, content-type: text/turtle, Body: the turtle data to be converted. 

### Travelmode data
* `GET <backend_location>/feedback_service/travelmode`, accept: application/ld+json, Parameter: userName : get the most recent travel mode of the given user
* `GET <backend_location>/feedback_service/byVehicleType`, accept: application/ld+json, Parameter: vehicleType : get all users last to be seen with the given vehicleType. VehicleType can be one of [sfm:Bus, sfm:Bicycle, sfm:Train, sfm:Car, sfm:OnFoot]

Additionally, more random travel mode test data can be generated with the following request:

`http://<web backend location>/feedback_service/travelmode/generateTestData?number=<number of datasets to be generated>`

## Acknowledgements
This work has been supported by the German Federal Ministry for Economic Affairs and Energy (BMWi) (FKZ 01MD18014 C) as part of the project Smart MaaS, and by the German Federal Ministry of Education and Research through the MOSAIK project (grant no. 01IS18070-C). The project Smart MaaS is part of the technology program “Smart Service Welt II”, which is funded by the German Federal Ministry for Economic Affairs and Energy (BMWi).
