package de.dfki.feedback_service.feedback_webservice.utils;

import com.google.firebase.messaging.Notification;
import com.google.gson.Gson;
import de.dfki.feedback_service.feedback_webservice.models.*;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Utils {
    public static Model rdfToModel(String rdfString, RDFFormat rdfFormat) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(rdfString.getBytes());
        return Rio.parse(inputStream, "", rdfFormat);
    }

    public static void printRDF4JModel(Model model) {
        System.out.println("Namespaces: ");
        for (Namespace n : model.getNamespaces()) {
            System.out.println(n);
        }
        System.out.println("\n\n");
        Iterator<Statement> statementIterator = model.iterator();
        while (statementIterator.hasNext()) {
            System.out.println(statementIterator.next());
        }
    }

    public static Feedback convertXmlToFeedback(String xmlFeedback) {
        JAXBContext jaxbContext;
        Feedback feedback = null;
        try {
            jaxbContext = JAXBContext.newInstance(Feedback.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            feedback = (Feedback) jaxbUnmarshaller.unmarshal(new StringReader(xmlFeedback));

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return feedback;
    }

    public static Feedback convertJsonToFeedback(String jsonFeedback) {
        return new Feedback(new Location(), 0, "", "", "");
    }

    public static String serializeToXml(Object object) throws Exception {
        if (object == null)
            return "";
        Serializer serializer = new Persister();
        StringWriter stringWriter = new StringWriter();
        serializer.write(object, stringWriter);


        return stringWriter.toString();
    }

    public static Object deserializeToObject(String xml, Object object) throws Exception {
        Serializer serializer = new Persister();
        object = serializer.read(object.getClass(), xml);
        return object;
    }

    public static boolean isValidXml(final String xml) {
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes(Charset.defaultCharset()));
            saxParser.parse(inputStream, new DefaultHandler());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            return false;
        }
        return true;

    }

    public static boolean isValidJson(final String json) {
        try {
            new JSONObject(json);
        } catch (JSONException ex) {
            try {
                new JSONArray(json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String getRequest(String url, Map<String, String> params) {
        RestTemplate restTemplate = new RestTemplate();
        if (params == null) {
            return restTemplate.getForObject(url, String.class);
        }
        String response;
        try {
            response = restTemplate.getForObject(url, String.class, params);
        } catch (HttpClientErrorException e) {
            response = e.getLocalizedMessage();
            e.printStackTrace();
        } catch (HttpServerErrorException e) {
            response = e.getLocalizedMessage();
            e.printStackTrace();
        }
        return response;
    }

    public static String postNearbyStops(String to, NearByStops nearByStops, int radius, int page) {
        String divider = "###";
        String url = "https://fcm.googleapis.com/fcm/send";
        String serverKey = "AAAAusB_NmI:APA91bFWhVGcWspYny62D3jYK9Ii_YIeu7HrkAUG7MtQjBENrVM59Tcrs9zg0Hc1ByaoZ72ysOoF3cima3nD7wC3AqX1Wj3GT5IlhYneuh0Gdv-87Qy_kA5Zp0BBiMKf7mn3Sa0jndxs";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "key=" + serverKey);
        FirebaseMessage firebaseMessage = new FirebaseMessage();
        Map<String, List<Stop>> data = new HashMap<>();
        firebaseMessage.setTo(to);

        String notificationTitle = "Nearby Stops";
        String notificationContent = "no" + divider + radius + divider + page;


        if (nearByStops.getTotalItems() > 0) {
            data.put("stops", nearByStops.getStops());
            notificationContent = nearByStops.getTotalItems() + divider + radius + divider + page;
        }
        firebaseMessage.setData(data);
        Notification notification = new Notification(notificationTitle, notificationContent);
        firebaseMessage.setNotification(notification);
        Gson gson = new Gson();
        String jsnMssg = gson.toJson(firebaseMessage);
        System.out.println("Utils.postNearbyStops");
        System.out.println("jsnMssg = " + jsnMssg);
        HttpEntity<String> request = new HttpEntity<>(jsnMssg, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        return response.getBody();
    }

    public static String queryNearByStops(double lat, double lng, int radius, int size) {

        String provider = "flixbus";
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "https://usg-demo-1.sb.dfki.de:30283/service/smartmaas" +
                "/gtfsld/api/v1/providers/{provider}/stops/nearBy?lat={lat}&" +
                "lng={lng}&radius={radius}&size={size}";

        Map<String, String> params = new HashMap<>();
        params.put("provider", provider);
        params.put("lat", String.valueOf(lat));
        params.put("lng", String.valueOf(lng));
        params.put("radius", String.valueOf(radius));
        params.put("size", String.valueOf(size));

        return restTemplate.getForObject(baseUrl, String.class, params);
    }

    public static NearByStops convertTurtleToNearByStops(String turtle, RDFFormat rdfFormat) throws IOException {
        NearByStops nearByStops = new NearByStops();
//        Model model = ModelFactory.createDefaultModel();
        InputStream stream = new ByteArrayInputStream(turtle.getBytes());
        Model model = Rio.parse(stream, "", rdfFormat);

        for (Statement s : model) {
            Resource subject = s.getSubject();
            IRI predicate = s.getPredicate();
            Value object = s.getObject();
            switch (predicate.getLocalName()) {
                case "totalItems":
                    int totalItems = -1;
                    try {
                        int index = object.toString().indexOf("^^");
                        if (object.toString().contains("^^")) {
                            totalItems = Integer.parseInt(object.toString().substring(0, index));
                        } else totalItems = Integer.parseInt(object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    nearByStops.setTotalItems(totalItems);
                    break;
                case "first":
                    nearByStops.setFirst(object.toString());
                    break;
                case "last":
                    nearByStops.setLast(object.toString());
                    break;
                case "next":
                    nearByStops.setNext(object.toString());
                    break;
                case "previous":
                    nearByStops.setPrevious(object.toString());
                    break;
                case "member":
                    nearByStops.addMember(object.toString());
                    break;
            }
        }

//        model.read(stream, null, rdfFormat);
//        StmtIterator iterator = model.listStatements();

//        while (iterator.hasNext()) {
//            Statement statement = iterator.nextStatement();
//            Resource subject = statement.getSubject();
//            Property predicate = statement.getPredicate();
//            RDFNode object = statement.getObject();
//            switch (predicate.getLocalName()) {
//                case "totalItems":
//                    int totalItems = -1;
//                    try {
//                        int index = object.toString().indexOf("^^");
//                        if (object.toString().contains("^^")) {
//                            totalItems = Integer.parseInt(object.toString().substring(0, index));
//                        } else totalItems = Integer.parseInt(object.toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    nearByStops.setTotalItems(totalItems);
//                    break;
//                case "first":
//                    nearByStops.setFirst(object.toString());
//                    break;
//                case "last":
//                    nearByStops.setLast(object.toString());
//                    break;
//                case "next":
//                    nearByStops.setNext(object.toString());
//                    break;
//                case "previous":
//                    nearByStops.setPrevious(object.toString());
//                    break;
//                case "member":
//                    nearByStops.addMember(object.toString());
//                    break;
//            }
//        }
        return nearByStops;
    }

    public static Stop convertTurtleToStop(String turtle, RDFFormat rdfFormat) throws IOException {
        Stop stop = new Stop();
//        Model model = ModelFactory.createDefaultModel();
        InputStream stream = new ByteArrayInputStream(turtle.getBytes());
        Model model = Rio.parse(stream, "", rdfFormat);
//        model.read(stream, null, rdfFormat);
//        StmtIterator iterator = model.listStatements();

        for (Statement statement : model) {
            Resource subject = statement.getSubject();
            IRI predicate = statement.getPredicate();
            Value object = statement.getObject();
            switch (predicate.getLocalName()) {
                case "description":
                    stop.setDescription(object.toString());
                    break;
                case "identifier":
                    stop.setIdentifier(object.toString());
                    break;
                case "timeZone":
                    stop.setTimeZone(object.toString());
                    break;
                case "lat":
                    double lat = 0;
                    try {
                        int index = object.toString().indexOf("^^");
                        if (object.toString().contains("^^")) {
                            lat = Double.parseDouble(object.toString().substring(0, index));
                        } else lat = Integer.parseInt(object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stop.setLat(lat);
                    break;
                case "long":
                    double lng = 0;
                    try {
                        int index = object.toString().indexOf("^^");
                        if (object.toString().contains("^^")) {
                            lng = Double.parseDouble(object.toString().substring(0, index));
                        } else lng = Integer.parseInt(object.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stop.setLng(lng);
                    break;
                case "name":
                    stop.setName(object.toString());
                    break;
            }

        }
//        while (iterator.hasNext()) {
//            Statement statement = iterator.nextStatement();
//            Resource subject = statement.getSubject();
//            Property predicate = statement.getPredicate();
//            RDFNode object = statement.getObject();
//            switch (predicate.getLocalName()) {
//                case "description":
//                    stop.setDescription(object.toString());
//                    break;
//                case "identifier":
//                    stop.setIdentifier(object.toString());
//                    break;
//                case "timeZone":
//                    stop.setTimeZone(object.toString());
//                    break;
//                case "lat":
//                    double lat = 0;
//                    try {
//                        int index = object.toString().indexOf("^^");
//                        if (object.toString().contains("^^")) {
//                            lat = Double.parseDouble(object.toString().substring(0, index));
//                        } else lat = Integer.parseInt(object.toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    stop.setLat(lat);
//                    break;
//                case "long":
//                    double lng = 0;
//                    try {
//                        int index = object.toString().indexOf("^^");
//                        if (object.toString().contains("^^")) {
//                            lng = Double.parseDouble(object.toString().substring(0, index));
//                        } else lng = Integer.parseInt(object.toString());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    stop.setLng(lng);
//                    break;
//                case "name":
//                    stop.setName(object.toString());
//                    break;
//            }
//        }
        return stop;
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2, String unit) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            byte m = 0;

            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            if (unit.equals("K")) {
                dist = dist * 1.609344;
            } else if (unit.equals("N")) {
                dist = dist * 0.8684;
            }
            return (dist);
        }
    }

    public static int string2Integer(String s) throws NumberFormatException {
        if (StringUtils.isNumeric(s)) {
            return Integer.parseInt(s);
        } else throw new NumberFormatException("The input -" + s + ": is not numeric");


    }
}
