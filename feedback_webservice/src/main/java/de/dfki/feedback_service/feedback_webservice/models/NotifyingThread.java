package de.dfki.feedback_service.feedback_webservice.models;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class NotifyingThread extends Thread {
    Stop stop;
    private final Set<ThreadCompleteListener> listeners
            = new CopyOnWriteArraySet<ThreadCompleteListener>();

    public final void addListener(final ThreadCompleteListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(final ThreadCompleteListener listener) {
        listeners.remove(listener);
    }

    private final void notifyListeners() {
        for (ThreadCompleteListener listener : listeners) {
//            setStop(Utils.convertTurtleToStop(result, "TURTLE"));

            listener.notifyOfThreadComplete(this);
        }
    }

    @Override
    public final void run() {
        try {
            doRun();
        } finally {
            notifyListeners();
        }
    }

    public abstract void doRun();

    public Stop getStop() {
        return stop;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }
}
