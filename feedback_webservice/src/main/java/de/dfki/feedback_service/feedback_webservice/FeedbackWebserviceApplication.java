package de.dfki.feedback_service.feedback_webservice;

import de.dfki.feedback_service.feedback_webservice.swagger.SwaggerConfig;
import de.dfki.feedback_service.feedback_webservice.utils.RDF4JRepositoryHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.logging.Logger;

@SpringBootApplication
public class FeedbackWebserviceApplication {
    private static final Logger LOGGER = Logger.getLogger(FeedbackWebserviceApplication.class.getName());
    private static final String baseLocation = "http://localhost:8803/feedback_service";

    public static void main(String[] args) {
		if (args.length>0) {
			RDF4JRepositoryHandler.setServer(args[0]);
		}
        SpringApplication.run(FeedbackWebserviceApplication.class, args);
        LOGGER.info("View Swagger UI at " + baseLocation + SwaggerConfig.SWAGGER_UI_LOCATION);
    }
}
