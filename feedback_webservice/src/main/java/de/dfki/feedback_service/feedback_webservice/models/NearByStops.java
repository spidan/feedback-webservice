package de.dfki.feedback_service.feedback_webservice.models;

import java.util.ArrayList;
import java.util.List;

public class NearByStops {
    private int totalItems;
    private List<String> stopUrls;
    private List<Stop> stops;
    private String first, last, next, previous;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<String> getStopUrls() {
        return stopUrls;
    }

    public void setStopUrls(List<String> stopUrls) {
        this.stopUrls = stopUrls;
    }

    public List<Stop> getStops() {
        return stops;
    }

    public void setStops(List<Stop> stops) {
        this.stops = stops;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public void addMember(String member) {
        if (stopUrls == null) {
            stopUrls = new ArrayList<>();
        }
        stopUrls.add(member);
    }

    public void addStop(Stop stop) {
        if (stops == null) {
            stops = new ArrayList<>();
        }
        stops.add(stop);
    }
}
