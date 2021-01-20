package de.dfki.feedback_service.feedback_webservice.controllers;

import de.dfki.feedback_service.feedback_webservice.swagger.SwaggerConfig;
import de.dfki.feedback_service.feedback_webservice.utils.RDF4JRepositoryHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.query.GraphQuery;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Api(tags = {SwaggerConfig.FEEDBACK_RECEIVER_TAG})
public class TravelmodeController {
    private static final String page_header_key = "Page";
    private static final String size_header_key = "Size";

    @ApiOperation(value = "Receiving the most recent travel mode of a certain user")
    @GetMapping(value = "/travelmode", produces = "application/ld+json")
    @ApiImplicitParam(name = "userName", value = "user name")
    public ResponseEntity<?> getTravelModeOfSpecificUser(@RequestParam final String userName) {
		Repository feedbackRepository = RDF4JRepositoryHandler.getRepository("travelmodedata");
		String queryString = "PREFIX foaf: <http://xmlns.com/foaf/spec/> \n"
				+ "PREFIX  smf: <http://www.dfki.de/SmartMaaS/feedback#> \n"
				+ "PREFIX  time: <http://www.w3.org/2006/time#> \n"
				+ "DESCRIBE ?mode \n"
				+ "WHERE { \n"
				+ "?mode foaf:accountName \"" + userName + "\" . \n"
				+ "?mode time:xsdDateTime ?timestamp . \n"
				+ "} \n"
				+ "ORDER BY ?timestamp \n"
				+ "LIMIT 1";
		System.out.println("Sending query: \n" + queryString);
		try (RepositoryConnection conn = feedbackRepository.getConnection()) {
			ByteArrayOutputStream modelAsJsonLd = getQueryResultAsJsonLD(conn, queryString);
			return new ResponseEntity<>(modelAsJsonLd.toString(StandardCharsets.UTF_8), HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>("Could not get travelmode: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	@ApiOperation(value = "Get all users who are currently travelling by a certain means of transport")
    @GetMapping(value = "/travelmode/byVehicleType", produces = "application/ld+json")
    public ResponseEntity<?> getUserOnBus(@RequestParam final String vehicleType) {
		Repository feedbackRepository = RDF4JRepositoryHandler.getRepository("travelmodedata");
		String queryString = "PREFIX foaf: <http://xmlns.com/foaf/spec/> \n"
				+ "PREFIX  smf: <http://www.dfki.de/SmartMaaS/feedback#> \n"
				+ "PREFIX  time: <http://www.w3.org/2006/time#> \n"
				+ "DESCRIBE ?travelmode \n"
				+ "WHERE { \n"
				+ " ?travelmode smf:travelsBy " + vehicleType + " . \n"
				+ " ?travelmode foaf:accountName ?user . \n"
				+ "	?travelmode time:xsdDateTime ?t .\n"
				+ "	FILTER NOT EXISTS {\n"
				+ "	?travelmode foaf:accountName ?user . \n"
				+ " ?travelmode time:xsdDateTime ?t2 . \n"
				+ "	filter (?t2 > ?t) \n"
				+ "	} \n"
				+ "}";
		System.out.println("Sending query: \n" + queryString);
		try (RepositoryConnection conn = feedbackRepository.getConnection()) {
			ByteArrayOutputStream modelAsJsonLd = getQueryResultAsJsonLD(conn, queryString);
			return new ResponseEntity<>(modelAsJsonLd.toString(StandardCharsets.UTF_8), HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<>("Could not get travelmode: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	private ByteArrayOutputStream getQueryResultAsJsonLD(final RepositoryConnection conn, String queryString) throws RepositoryException, QueryEvaluationException, MalformedQueryException, RDFHandlerException {
		GraphQuery query = conn.prepareGraphQuery(queryString);
		GraphQueryResult travelMode = query.evaluate();
		Model resultModel = QueryResults.asModel(travelMode);
		ByteArrayOutputStream modelAsJsonLd = new ByteArrayOutputStream();
		Rio.write(resultModel, modelAsJsonLd, RDFFormat.JSONLD);
		return modelAsJsonLd;
	}

	@ApiOperation(value = "Generate test data")
    @PostMapping(value = "/travelmode/generateTestData", produces = "application/ld+json")
    public ResponseEntity<?> generateTestData(@RequestParam final int number) {
		Repository travelModeRepository = RDF4JRepositoryHandler.getRepository("travelmodedata");
		ModelBuilder builder = new ModelBuilder();
		builder.setNamespace("foaf", "http://xmlns.com/foaf/spec/");
		builder.setNamespace("smf", "http://www.dfki.de/SmartMaaS/feedback#");
		builder.setNamespace("time", "http://www.w3.org/2006/time#");
		builder.setNamespace("exf", "http://www.example.fe/edback#");
		String[] meansOfTransport = new String[5];
		meansOfTransport[0] = "smf:Bicycle";
		meansOfTransport[1] = "smf:Bus";
		meansOfTransport[2] = "smf:Train";
		meansOfTransport[3] = "smf:Car";
		meansOfTransport[4] = "smf:OnFoot";
		for (int i=0; i<number; i++) {
			//generate random user
			int userNumber = (int) Math.floor(Math.random() * 20);
			String userName = "user" + userNumber;
			//generate random means of transport
			int transportIndex = (int) Math.floor(Math.random() * 5);
			//generate random time stamp
			int yearOffset = (int) (Math.random() * 2);
			int year = 2021 - yearOffset;
			int month = (int) (Math.random() * 12 + 1);
			int day = (int) (Math.random() * 27);
			int hour = (int) (Math.random() * 24);
			int minute = (int) (Math.random() * 60);
			int second = (int) (Math.random() * 60);
			//generate random time span
			int timesteps = (int) (Math.random() * 12);
			//generate triples
			for (int t=0; t<timesteps; t++) {
				second = second + t*5;
				if (second > 60) {
					second -= 60;
					minute += 1;
				}
				String secondString = zeroPrefix(second);
				if (minute>60) {
					minute -= 60;
					hour += 1;
				}
				String minuteString = zeroPrefix(minute);
				if (hour > 23) {
					day +=1 ;
				}
				String hourString = zeroPrefix(hour);
				String dayString = zeroPrefix(day);
				String monthString = zeroPrefix(month);
				builder.defaultGraph()
						.subject("exf:travelmodedata"+UUID.randomUUID())
						.add("foaf:accountName", userName)
						.add("smf:travelsBy", meansOfTransport[transportIndex])
						.add("time:xsdDateTime", year +"-"+monthString+"-"+dayString+"T"
								+ hourString +":" +minuteString+":"+secondString);
			}
			try (RepositoryConnection con = travelModeRepository.getConnection()) {
				con.add(builder.build());
			} catch (Exception ex) {
				return new ResponseEntity<>("Something went wrong: " + ex.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
    }

	private String zeroPrefix(int inputString) {
		String resultString = "";
		resultString += inputString;
		if (resultString.length() != 2)
			resultString="0" + resultString;
		return resultString;
	}
}
