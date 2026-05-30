package org.example.businesslogic;

import com.sun.nio.sctp.SendFailedNotification;
import org.example.domainmodel.Sensor;
import org.example.domainmodel.SensorState;
import org.example.orm.SensorDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
//FIXME: Da eliminare in favore di DatabaseMutex (?)
//fixme non so se va qui nella business logic, ma deve usare i DAO -> nel domain model non deve stare e nell'orm neache perché non deve fare lei le query
public class SharedListActiveSensors {
    private ArrayList<Sensor> activeSensors;
    private Semaphore mutex;
    public SharedListActiveSensors(){
        activeSensors = new ArrayList<>();
        mutex = new Semaphore(1);
    }

    public void acquireMutex() throws InterruptedException {
        mutex.acquire();
    }

    public void releaseMutex() throws InterruptedException{
        mutex.release();
    }
    public ArrayList<Sensor> getActualActiveSensors() throws SQLException {
        activeSensors.clear();
        try (SensorDAO sensorDAO = new SensorDAO()){
            activeSensors = sensorDAO.getSensorsByState(SensorState.ACTIVE);
        }catch (SQLException e){
            System.err.println("Errore nel recupero dei sensori attivi. Errore nella modalità condivisa: "+e.getMessage());
            e.printStackTrace();
        }

        return activeSensors;
    }
}
