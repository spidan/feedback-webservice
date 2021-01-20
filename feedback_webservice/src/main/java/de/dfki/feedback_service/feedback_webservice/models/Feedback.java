package de.dfki.feedback_service.feedback_webservice.models;


import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root
//@ApiModel(description = "Model for Feedback Input")
public class Feedback {
    private String feedbackURI = "http://www.example.com/feedback";
    public static final String example = "@prefix ex:    <http://example.org/> .\n" +
            "\n" +
            "ex:feedback  ex:location_latitude  \"7.021859999999999\" ;\n" +
            "        ex:location_longitude  \"49.24471\" ;\n" +
            "        ex:location_name       \"Waldhausweg 17, 66123 Saarbrücken, Germany\" ;\n" +
            "        ex:radius              \"km\" , \"83\" ;\n" +
            "        ex:stuck_duration      \"5\" , \"minute\" ;\n" +
            "        ex:travelsOn           \"5\" , \"bus\" .";
    @Element
//    @ApiModelProperty(notes = "Address of the feedback")
    private Location location;
    @Element
//    @ApiModelProperty(notes = "Amount of time for user to be stuck on the road", example = "15")
    private int stuckTime;
    @Element
//    @ApiModelProperty(notes = "Unit of the time", example = "minute")
    private String measurementUnit;
    @Element
//    @ApiModelProperty(notes = "Vehicle of the user", example = "Bus")
    private String vehicle;
    @Element
//    @ApiModelProperty(notes = "Number of the vehicle", example = "102")
    private String vehicleNo;
    @Element
    private int radius;
    @ElementList
    private List<Reason> reasons;

    public Feedback() {

    }

    public Feedback(Location location, int stuckTime, String measurementUnit, String vehicle, String vehicleNo) {
        this.location = location;
        this.stuckTime = stuckTime;
        this.measurementUnit = measurementUnit;
        this.vehicle = vehicle;
        this.vehicleNo = vehicleNo;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getStuckTime() {
        return stuckTime;
    }

    public void setStuckTime(int stuckTime) {
        this.stuckTime = stuckTime;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<Reason> getReasons() {
        return reasons;
    }

    public void setReasons(List<Reason> reasons) {
        this.reasons = reasons;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getFeedbackURI() {
        return feedbackURI;
    }

    public void setFeedbackURI(String feedbackURI) {
        this.feedbackURI = feedbackURI;
    }

//    public String getRDFOutput(String rdfOutputFormat) {
//        Model model = ModelFactory.createDefaultModel();
//        Property geo = model.createProperty("https://www.w3.org/2003/01/geo/wgs84_pos#", "location");
//        Property ns3 = model.createProperty("http://purl.org/NET/c4dm/timeline.owl#", "stuck_duration");
//        Property vhc = model.createProperty("http://purl.org/dc/elements/1.1/", "travelsOn");
//        Property cse = model.createProperty("http://dublincore.org/documents/dces/", "because_of");
//        Property rsn = model.createProperty("http://www.w3.org/2002/07/owl#", "reasons");
//
//        Resource reasons = model.createResource(RDF.List);
//        for (Reason reason : getReasons()) {
//            reasons.addProperty(cse, reason.getName());
//        }
//        Resource feedback = model.createResource(getFeedbackURI())
//                .addProperty(geo, getLocation().getName())
//                .addProperty(ns3, getMeasurementUnit())
//                .addProperty(ns3, String.valueOf(getStuckTime()))
//                .addProperty(vhc, getVehicleNo())
//                .addProperty(vhc, getVehicle())
//                .addProperty(rsn, reasons);
//
//        model.setNsPrefix(RDF.List.getLocalName(), RDF.List.getNameSpace());
//        model.setNsPrefix("owl", rsn.getNameSpace());
//        model.setNsPrefix("cse", cse.getNameSpace());
//        model.setNsPrefix("vhc", vhc.getNameSpace());
//        model.setNsPrefix("geo", geo.getNameSpace());
//        model.setNsPrefix("ns3", ns3.getNameSpace());
//        StringWriter out = new StringWriter();
//        model.write(out, rdfOutputFormat);
//        return String.valueOf(out);
//    }
}
