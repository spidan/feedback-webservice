package de.dfki.feedback_service.feedback_webservice.models;

import java.text.DecimalFormat;

public class Stop {
    private transient String url;
    private String description;
    private String identifier;
    private transient String timeZone;
    private String name;
    private double lat, lng;
    private double distanceToUser;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
//        this.lat = Double.parseDouble(new DecimalFormat("##.#").format(lat));

        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
//        this.lng = Double.parseDouble(new DecimalFormat("##.#").format(lng));
        this.lng = lng;
    }

    public double getDistanceToUser() {
        return distanceToUser;
    }

    public void setDistanceToUser(double distanceToUser) {
        this.distanceToUser = Double.parseDouble(new DecimalFormat("##.#").format(distanceToUser));
    }
}
