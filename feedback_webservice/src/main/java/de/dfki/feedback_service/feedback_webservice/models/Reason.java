package de.dfki.feedback_service.feedback_webservice.models;


public class Reason {
    private String name;
    private String additionalInfo;

    public Reason() {
    }

    public Reason(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
