package org.example.domainmodel;

import java.util.ArrayList;

public abstract class Observable {
    protected ArrayList<Observer> observers;

    public void attach(Observer o) {
        observers.add(o);
    }
    public void detach(Observer o) {
        observers.remove(o);
    }

    public abstract void notifyObservers();
}
