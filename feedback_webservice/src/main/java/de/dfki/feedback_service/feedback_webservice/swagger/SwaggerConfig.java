package de.dfki.feedback_service.feedback_webservice.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    // View Swagger UI
    public static final String SWAGGER_UI_LOCATION = "/swagger-ui/index.html#/";
    public static final String FEEDBACK_RECEIVER_TAG = "Feedback Receiver";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("de.dfki.feedback_service.feedback_webservice"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo())
                .tags(new Tag(FEEDBACK_RECEIVER_TAG, "Receiving and Processing Feedbacks"));
//                .useDefaultResponseMessages(false)
//                .globalResponseMessage(RequestMethod.POST, getCustomizedResponseMessages());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo("Feedback Service API",
                "This is a Feedback Service API created using Spring Boot",
                "1.0",
                "API Terms of Service URL",
                new Contact("DFKI", "https://www.dfki.de/web/", ""),
                "API License",
                "API License URL",
                Collections.emptyList());
    }

    private List<ResponseMessage> getCustomizedResponseMessages() {
        List<ResponseMessage> responseMessages = new ArrayList<>();
        responseMessages.add(new ResponseMessageBuilder()
                .code(500)
                .message("Server has crashed!")
                .responseModel(new ModelRef("Error"))
                .build());

        responseMessages.add(new ResponseMessageBuilder()
                .code(403)
                .message("You shall not pass!")
                .build());
        return responseMessages;
    }
}
