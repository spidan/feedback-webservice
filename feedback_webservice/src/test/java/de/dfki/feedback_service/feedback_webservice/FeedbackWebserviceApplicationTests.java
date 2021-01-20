package de.dfki.feedback_service.feedback_webservice;

import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackWebserviceApplicationTests {
    private final static Logger LOGGER = Logger.getLogger(FeedbackWebserviceApplication.class.getName());
    private final static String rdf4jServer = "http://localhost:8090/rdf4j";

    @Test
    public void contextLoads() {
    }
}
