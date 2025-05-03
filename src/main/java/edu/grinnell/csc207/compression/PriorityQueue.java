package edu.grinnell.csc207.compression;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom priority queue class that orders elements by their natural ordering.
 *
 * @param <T> the type of elements in the queue, must be Comparable
 *
 */
public class PriorityQueue<T extends Comparable<T>> {

    private List<T> queue = new ArrayList<>();

    /**
     * Default constructor for PriorityQueue
     */
    public PriorityQueue() {
    }

    /**
     * Adds data to the priority queue by the priority of frequency.
     *
     * @param data the data to be added to the queue
     */
    public void add(T data) {
        int i = 0;
        while (i < queue.size()) {
            if (queue.get(i).compareTo(data) >= 0) {
                break;
            }
            i++;
        }
        queue.add(i, data);
    }

    /**
     * Returns the size of the queue.
     *
     * @return the number of elements in the queue
     */
    public int size() {
        return queue.size();
    }

    /**
     * Removes the last item from the queue.
     *
     * @return the element removed from the queue
     */
    public T pop() {
        return queue.remove(this.size() - 1);
    }
}
