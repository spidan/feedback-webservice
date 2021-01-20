package de.dfki.feedback_service.feedback_webservice.models;

import com.google.firebase.messaging.Notification;

import java.util.List;
import java.util.Map;

public class FirebaseMessage {
    private Map<String, List<Stop>> data;
    private String to;
    private Notification notification;
    private int totalItems;

    public Map<String, List<Stop>> getData() {
        return data;
    }

    public void setData(Map<String, List<Stop>> data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
}
