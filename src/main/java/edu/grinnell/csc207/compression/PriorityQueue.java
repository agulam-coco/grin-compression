/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.grinnell.csc207.compression;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jason
 * @param <T>
 */
public class PriorityQueue<T extends Comparable<T>> {

    private List<T> queue = new ArrayList<>();

    public PriorityQueue() {

    }

    /**
     * Adds data to the priority queue by the priority of frequency
     *
     * @param data to be added to queue
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
     * Returns the size of the queue
     * @return integer size
     */
    public int size(){return queue.size();}
    
    /**
     * Removes the last item from the queue
     * @return 
     */
    public T pop(){
        return queue.remove(this.size()-1);
    }

}
