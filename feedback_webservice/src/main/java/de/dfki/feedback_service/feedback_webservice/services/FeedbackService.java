package de.dfki.feedback_service.feedback_webservice.services;

import de.dfki.feedback_service.feedback_webservice.models.*;
import de.dfki.feedback_service.feedback_webservice.utils.RDF4JRepositoryHandler;
import de.dfki.feedback_service.feedback_webservice.utils.Utils;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class FeedbackService implements ThreadCompleteListener {
    private static final Logger LOGGER = Logger.getLogger(FeedbackService.class.getName());
    private String turtleFeedback;
    private NearByStops nearByStops;
    private Feedback feedback;
    private int page, size;
    private String token;
    private static final String feedbackIDKeyword = "Feedback_xxx";

    public Feedback convert_JSON_or_XML_to_FEEDBACK(String feedbackMssg) throws Exception {
        if (Utils.isValidXml(feedbackMssg)) {
            return (Feedback) Utils.deserializeToObject(feedbackMssg, new Feedback());
        } else {
            return Utils.convertJsonToFeedback(feedbackMssg);
        }
    }

    public Feedback xmlToFeedback(String input) {
        return Utils.convertXmlToFeedback(input);
    }

    public Feedback jsonToFeedback(String input) {
        return Utils.convertJsonToFeedback(input);
    }

    public Feedback rdfToFeedback(String input, RDFFormat rdfFormat) throws IOException {
        //TODO convert model to feedback. But might not be necessary due to direct storing in repo
        List<Reason> reasons = new ArrayList<>();
        Location location = new Location();
        feedback = new Feedback();

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Model model = Rio.parse(inputStream, "", rdfFormat);

        for (Statement s : model) {
//            Resource subject = s.getSubject();
            IRI predicate = s.getPredicate();
            Value object = s.getObject();
//            Statement statement = iterator.nextStatement();
//            Resource subject = statement.getSubject();
//            Property predicate = statement.getPredicate();
//            RDFNode object = statement.getObject();
//
            switch (predicate.getLocalName()) {
                case "because_of":
                    reasons.add(new Reason(object.toString()));
                    break;
                case "name":
                    location.setName(object.toString());
                    break;
                case "long":
                    location.setLng(Double.parseDouble(object.toString()));
                    break;
                case "lat":
                    location.setLat(Double.parseDouble(object.toString()));
                    break;
                case "travelsOn":
                    String vhcl = object.toString();
                    if (vhcl.equals("bus") || vhcl.equals("train") || vhcl.equals("car") ||
                            vhcl.equals("bicycle") || vhcl.equals("foot")) {
                        feedback.setVehicle(vhcl);
                    } else {
                        feedback.setVehicleNo(vhcl);
                    }
                    break;
                case "stuck_duration":
                    try {
                        int duration = Integer.parseInt(object.toString());
                        feedback.setStuckTime(duration);
                    } catch (Exception e) {
                        feedback.setMeasurementUnit(object.toString());
                    }
                    break;
                case "radius":
                    feedback.setRadius(Integer.parseInt(object.toString()));
                    break;
            }
        }
        feedback.setLocation(location);
        feedback.setReasons(reasons);


//        Model model = ModelFactory.createDefaultModel();
//        InputStream stream = new ByteArrayInputStream(input.getBytes());
//
//        model.read(stream, null, rdfFormat);
//        StmtIterator iterator = model.listStatements();
//        while (iterator.hasNext()) {
//            Statement statement = iterator.nextStatement();
//            Resource subject = statement.getSubject();
//            Property predicate = statement.getPredicate();
//            RDFNode object = statement.getObject();
//
//            switch (predicate.getLocalName()) {
//                case "because_of":
//                    reasons.add(new Reason(object.toString()));
//                    break;
//                case "name":
//                    location.setName(object.toString());
//                    break;
//                case "long":
//                    location.setLng(Double.parseDouble(object.toString()));
//                    break;
//                case "lat":
//                    location.setLat(Double.parseDouble(object.toString()));
//                    break;
//                case "travelsOn":
//                    String vhcl = object.toString();
//                    if (vhcl.equals("bus") || vhcl.equals("train") || vhcl.equals("car") ||
//                            vhcl.equals("bicycle") || vhcl.equals("foot")) {
//                        feedback.setVehicle(vhcl);
//                    } else {
//                        feedback.setVehicleNo(vhcl);
//                    }
//                    break;
//                case "stuck_duration":
//                    try {
//                        int duration = Integer.parseInt(object.toString());
//                        feedback.setStuckTime(duration);
//                    } catch (Exception e) {
//                        feedback.setMeasurementUnit(object.toString());
//                    }
//                    break;
//                case "radius":
//                    feedback.setRadius(Integer.parseInt(object.toString()));
//                    break;
//            }
//        }
//        feedback.setLocation(location);
//        feedback.setReasons(reasons);

        return feedback;
    }

    public String getTurtleFeedback() {
        return turtleFeedback;
    }

    public void setTurtleFeedback(String turtleFeedback) {
        this.turtleFeedback = turtleFeedback;
    }

    public NearByStops getNearByStops() {
        return nearByStops;
    }

    public void setNearByStops(NearByStops nearByStops) {
        this.nearByStops = nearByStops;
    }

    @Override
    public void notifyOfThreadComplete(NotifyingThread notifyingThread) {
        System.out.println("FeedbackService.notifyOfThreadComplete");
        nearByStops.addStop(notifyingThread.getStop());
        if (nearByStops.getStops().size() == size - getPage() * size + nearByStops.getTotalItems()
                || nearByStops.getStops().size() == size
                || nearByStops.getStops().size() == nearByStops.getTotalItems()) {
            sendStopsToApp(nearByStops);
        }
    }

    public void startThread(String url, Map<String, String> params) {
        NotifyingThread notifyingThread = new NotifyingThread() {
            @Override
            public void doRun() {
                String result = Utils.getRequest(url, params);
                Stop stop = null;
                try {
                    stop = Utils.convertTurtleToStop(result, RDFFormat.TURTLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stop.setDistanceToUser(Utils.calculateDistance(stop.getLat(), stop.getLng(),
                        feedback.getLocation().getLat(), feedback.getLocation().getLng(), "K"));
                setStop(stop);
            }
        };
        notifyingThread.addListener(this);
        notifyingThread.start();

    }

    public void sendStopsToApp(NearByStops nearByStops) {
        System.out.println("FeedbackService.sendStopsToApp");
        Utils.postNearbyStops(getToken(), nearByStops, feedback.getRadius(), getPage());
    }

    public void findNearbyStops(Feedback feedback, int page, int size) {
//        try {
//            this.page = Integer.parseInt(page);
//            this.size = Integer.parseInt(size);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        String url = "https://usg-demo-4.sb.dfki.de:30101/service/gtfsld/" +
                "api/v1/providers/{provider}/stops/nearBy?" +
                "lat={lat}&lng={lng}&radius={radius}&size={size}&page={page}";
        Map<String, String> params = new HashMap<>();
        params.put("provider", "flixbus");
        params.put("lat", String.valueOf(feedback.getLocation().getLat()));
        params.put("lng", String.valueOf(feedback.getLocation().getLng()));
        params.put("radius", String.valueOf((feedback.getRadius() * 100000)));
        params.put("size", String.valueOf(size));
        params.put("page", String.valueOf(page));

        String feedbackTurtle = Utils.getRequest(url, params);
        setTurtleFeedback(feedbackTurtle);
        try {
            nearByStops = Utils.convertTurtleToNearByStops(getTurtleFeedback(), RDFFormat.TURTLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("FeedbackService.findNearbyStops");
        if (nearByStops.getTotalItems() > 0) {
            for (String stopUrl : nearByStops.getStopUrls()) {
                startThread(stopUrl, null);
            }
        } else {
            sendStopsToApp(nearByStops);
        }
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String setFeedbackID(String feedbackMessage) {
        int ID = 0;
        String feedbackWithID_Regex = ".+#(Feedback|feedback)\\d+";
        String subjectKeyword = "s";
        String queryString = "PREFIX base:  <http://www.dfki.de/SmartMaaS/feedback#>\n" +
                "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "SELECT ?" + subjectKeyword + "\n" +
                "WHERE {\n" +
                "?" + subjectKeyword + " rdf:type base:Feedback.\n" +
                "}";
        try (RepositoryConnection repositoryConnection =
                     RDF4JRepositoryHandler.getFeedbackRepository().getConnection()) {
            TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(queryString);
            try (TupleQueryResult queryResult = tupleQuery.evaluate()) {
                while (queryResult.hasNext()) {
                    BindingSet bindings = queryResult.next();
                    Value valueOfS = bindings.getValue(subjectKeyword);

                    if (valueOfS.stringValue().matches(feedbackWithID_Regex)) {
                        String feedbackIdStr = valueOfS.stringValue().replaceAll(".+#(Feedback|feedback)", "");
                        try {
                            int feedbackId = Utils.string2Integer(feedbackIdStr);
                            if (feedbackId > ID) {
                                ID = feedbackId;
                            }
                        } catch (NumberFormatException e) {
                            LOGGER.info("The subject --" + valueOfS.stringValue() + "-- produces the " +
                                    "error --" + e.getLocalizedMessage() + "--");
                        }
                    }
                }
            }
        }
        /* !!!!!!
         Feedback app is unaware of the amount of feedback messages in feedback repository.
         Therefore, instead of a correct Feedback ID, it writes "Feedback_xxx" which is replaced with
         the correct Feedback ID in feedback service.
        */
        String feedbackID = "Feedback" + (ID + 1);
        return feedbackMessage.replaceAll(feedbackIDKeyword, feedbackID);
    }


}
