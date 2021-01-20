package de.dfki.feedback_service.feedback_webservice.models;

public interface ThreadCompleteListener {
    void notifyOfThreadComplete(final NotifyingThread notifyingThread);
}
