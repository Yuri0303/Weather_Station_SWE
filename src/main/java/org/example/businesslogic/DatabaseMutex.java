package org.example.businesslogic;

import java.util.concurrent.Semaphore;

public class DatabaseMutex {
    public static Semaphore mutex = new Semaphore(1, true);
}
