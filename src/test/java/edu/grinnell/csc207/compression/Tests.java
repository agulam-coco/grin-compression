package edu.grinnell.csc207.compression;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;

public class Tests {

    PriorityQueue testQueue;

    @BeforeEach
    public void testSetup() {
        testQueue = new PriorityQueue();
    }

    @Test
    public void testPriorityQueueOneItem() {
        testQueue.add(new Node("a", 1));
        assertEquals(1, testQueue.size(), "The queue's size is not 1");
    }

    @Test
    public void testPriorityQueueTwoItems() {
        testQueue.add(new Node('b', 1));
        testQueue.add(new Node('a', 2));

        assertEquals(2, testQueue.size(), "The queue's size is not 2");

        Node popped = (Node) testQueue.pop();
        assertTrue((char) popped.key == 'a');

        popped = (Node) testQueue.pop();
        assertTrue((char) popped.key == 'b');
        assertEquals(0, testQueue.size(), "The queue is not empty after 2 pops");

    }

    @Test
    public void testOrderWithMultipleItems() {
        testQueue.add(new Node('x', 10));
        testQueue.add(new Node('y', 5));
        testQueue.add(new Node('z', 15));

        Node popped = (Node) testQueue.pop();
        assertEquals('z', popped.key, "First popped should be item with highest priority");

        popped = (Node) testQueue.pop();
        assertEquals('x', popped.key, "Second popped should be next highest");

        popped = (Node) testQueue.pop();
        assertEquals('y', popped.key, "Third popped should be item with lowest priority");
    }

    @Test
    public void testQueueSizeAfterMixedOperations() {
        testQueue.add(new Node('a', 1));
        testQueue.add(new Node('b', 2));
        testQueue.pop();
        testQueue.add(new Node('c', 3));

        assertEquals(2, testQueue.size(), "Queue size should reflect number of active items");
    }
}
