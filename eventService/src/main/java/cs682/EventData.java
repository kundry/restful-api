package cs682;

import java.util.*;

/**
 * Class that contains the data structure that holds the list
 * of all the events registered in the EventService
 */
public class EventData {
    /** Thread Safe Data Structure thar contains the list of events registered */
    private static final SortedMap<Integer,Event> eventsMap = Collections.synchronizedSortedMap(new TreeMap<Integer,Event>());

    /**
     * Method returns the last event id
     * @return last event id
     */
    public int getLastEventId() {
        if (!eventsMap.isEmpty()) {
            return eventsMap.lastKey();
        } else {
            return 0;
        }
    }

    /**
     * Method that allows to add a new event to the list of
     * events of the EventService
     * @param newEvent new event to be created
     */

     public void addEvent(Event newEvent) {
        eventsMap.put(newEvent.getId(), newEvent);
     }

    /**
     * Method that produces a list of all the
     * events registered in the EventService
     * @return  List of event objects
     */
    public List<Event> getEventsList() {
        synchronized(eventsMap) {
            List<Event> eventsList = new ArrayList<>();
            Set<Integer> keySet = eventsMap.keySet();
            for (Integer key: keySet) {
                eventsList.add(eventsMap.get(key));
            }
            return eventsList;
        }
    }

    /**
     * Method that returns the details of a given
     * event id
     * @param id - event id
     * @return Event object with the details of the event
     */
    public Event getEventDetails(int id) {
        synchronized (eventsMap) {
            return eventsMap.get(id);
        }
    }

    public void printEventList() {
        synchronized(eventsMap) {
            System.out.println("Event List: ");
            Set<Integer> keySet = eventsMap.keySet();
            for (Integer key: keySet) {
                System.out.println(eventsMap.get(key).toString());
            }
        }
    }
}
