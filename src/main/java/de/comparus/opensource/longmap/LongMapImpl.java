package de.comparus.opensource.longmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * This class is HashMap implementation with long type keys
 * @author Vitaliy Polishchuk
 * @param <V> Value, which may contains this map
 */
public class LongMapImpl<V> implements LongMap<V> {

    /*  fields  */

    private static final int DEFAULT_CAPACITY = 4;

    private static final int MAX_CAPACITY = 1 << 30;

    private static final float DEFAULT_LOAD_FACTOR = 1.0f;

    private final float loadFactor;

    private int capacity;
    private int threshold;
    private int size;

    private Node[] buckets;


    /*  inner classes  */

    /**
     * Inner class, that represents key value pair
     * @param <V> Value, which may contains the pair
     */
    private static class Node<V> {

        private final int hash;
        private final long key;
        private V value;
        private Node<V> next;


        /*  constructors  */

        public Node(int hash, long key, V value, Node<V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        /*  getters  */

        public int getHash() {
            return hash;
        }

        public long getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public Node<V> getNext() {
            return next;
        }


        /*  setters  */

        public void setValue(V value) {
            this.value = value;
        }

        public void setNext(Node<V> next) {
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {

            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return hash == node.hash &&
                    key == node.key &&
                    Objects.equals(value, node.value) &&
                    Objects.equals(next, node.next);
        }

        @Override
        public int hashCode() {

            return Objects.hash(hash, key, value, next);
        }
    }


    /* constructors */

    public LongMapImpl() {

        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public LongMapImpl(int capacity) {

        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public LongMapImpl(int capacity, float loadFactor) {

        if (capacity < 0) {
            throw new IllegalArgumentException("");
        } else if (capacity > MAX_CAPACITY) {
            capacity = MAX_CAPACITY;
        }

        if (loadFactor < 0) {
            throw new IllegalArgumentException("");
        }

        this.capacity = capacity;
        this.loadFactor = loadFactor;
        this.threshold = (int) (capacity * loadFactor);
        buckets = new Node[capacity];
    }


    /* public methods */

    @Override
    public V put(long key, V value) {

        int hash = hash(key);

        Node<V> currentNode = buckets[hash];

        Node<V> previousNode = null;

        Node<V> newNode = new Node<>(hash, key, value, null);

        if (currentNode == null) {

            buckets[hash] = newNode;

            ++size;

            if (size >= threshold) {
                resize(buckets.length * 2);
            }

            return null;
        }

        while (currentNode != null) {

            if (currentNode.getHash() == hash &&
                    currentNode.getKey() == key) {

                V oldValue = currentNode.getValue();

                currentNode.setValue(value);

                return oldValue;
            }

            previousNode = currentNode;

            currentNode = currentNode.getNext();
        }

        previousNode.setNext(newNode);

        ++size;

        if (size >= threshold) {
            resize(buckets.length * 2);
        }

        return null;
    }

    @Override
    public V get(long key) {

        int hash = hash(key);

        if (buckets[hash] != null) {

            Node<V> currentNode = buckets[hash];

            while (currentNode != null) {

                if (currentNode.getKey() == key) {

                    return currentNode.getValue();
                }

                currentNode = currentNode.getNext();
            }
        }

        return null;
    }

    @Override
    public V remove(long key) {

        int hash = hash(key);

        Node<V> nodeToRemove = buckets[hash];

        Node<V> previousNode = null;

        while (nodeToRemove != null) {

            if (nodeToRemove.getKey() == key) {

                // if element is first in the bucket
                if (previousNode == null) {

                    // if element is single in the bucket
                    if (nodeToRemove.getNext() == null) {

                        buckets[hash] = null;

                        --size;

                        return nodeToRemove.getValue();
                    }

                    // if element is the first but not the last in the bucket
                    buckets[hash] = nodeToRemove.getNext();

                    --size;

                    return nodeToRemove.getValue();
                }

                // if element is in the middle of the bucket
                if (nodeToRemove.getNext() != null) {

                    previousNode.setNext(nodeToRemove.getNext());

                    --size;

                    return nodeToRemove.getValue();
                }

                // if element is the last in the bucket
                previousNode.setNext(null);

                --size;

                return nodeToRemove.getValue();
            }

            previousNode = nodeToRemove;

            nodeToRemove = nodeToRemove.getNext();
        }

        return null;
    }

    @Override
    public boolean isEmpty() {

        return size == 0;
    }

    @Override
    public boolean containsKey(long key) {

        int hash = hash(key);

        Node<V> currentNode = buckets[hash];

        while (currentNode != null) {

            if (currentNode.getKey() == key) {

                return true;
            }

            currentNode = currentNode.getNext();
        }

        return false;
    }

    @Override
    public boolean containsValue(V value) {

        if (value == null) {

            return containsNullValue();
        }

        for (int i = 0; i < buckets.length; ++i) {

            Node<V> currentNode = buckets[i];

            while (currentNode != null) {

                V nodeValue = currentNode.getValue();

                if (nodeValue != null && nodeValue.equals(value)) {

                    return true;
                }

                currentNode = currentNode.getNext();
            }
        }

        return false;
    }

    @Override
    public long[] keys() {

        long[] keys = new long[size];

        int keysIndex = 0;

        for (int i = 0; i < buckets.length; ++i) {

            Node<V> currentNode = buckets[i];

            while (currentNode != null) {

                keys[keysIndex] = currentNode.getKey();

                currentNode = currentNode.getNext();

                ++keysIndex;
            }
        }

        return keys;
    }

    /**
     * @return Returns all non null values from map
     */
    @Override
    public Collection<V> values() {

        Collection<V> nonNullValues = new ArrayList<>();

        for (int i = 0; i < buckets.length; ++i) {

            Node<V> currentNode = buckets[i];

            while (currentNode != null) {

                if (currentNode.getValue() != null) {

                    nonNullValues.add(currentNode.getValue());
                }

                currentNode = currentNode.getNext();
            }
        }

        return nonNullValues;
    }

    @Override
    public long size() {

        return this.size;
    }

    @Override
    public void clear() {

        for (int i = 0; i < buckets.length; ++i) {

            buckets[i] = null;
        }

        size = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LongMapImpl<?> longMap = (LongMapImpl<?>) o;
        return Float.compare(longMap.loadFactor, loadFactor) == 0 &&
                capacity == longMap.capacity &&
                threshold == longMap.threshold &&
                size == longMap.size &&
                Arrays.equals(buckets, longMap.buckets);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(loadFactor, capacity, threshold, size);
        result = 31 * result + Arrays.hashCode(buckets);
        return result;
    }


    /* private methods */

    /**
     * Checks, does the map contain null value or not
     * @return Returns true, if map contains null value
     */
    private boolean containsNullValue() {

        for (int i = 0; i < buckets.length; ++i) {

            Node<V> currentNode = buckets[i];

            while (currentNode != null) {

                if (currentNode.getValue() == null) {

                    return true;
                }

                currentNode = currentNode.getNext();
            }
        }

        return false;
    }

    private void resize(int newCapacity) {

        this.capacity = newCapacity;
        
        this.threshold = (int) (this.capacity * this.loadFactor);

        Node[] newBuckets = new Node[this.capacity];

        for (int i = 0; i < buckets.length; ++i) {

            Node<V> currentNode = buckets[i];

            // if bucket contains some node
            if (currentNode != null) {

                // deleting reference from bucket
                buckets[i] = null;
                
                do {

                    // making new hash for node
                    int newHash = hash(currentNode.getKey());

                    // creating new node
                    Node<V> newNode = new Node<V>(newHash,
                                                  currentNode.getKey(),
                                                  currentNode.getValue(),
                                             null);

                    // if the new bucket already contains node, moving to the last node
                    if (newBuckets[newHash] != null) {
                        
                        Node<V> newCurrentNode = newBuckets[newHash];
                        
                        while (newCurrentNode.getNext() != null) {

                            newCurrentNode = newCurrentNode.getNext();
                        }

                        newCurrentNode.setNext(newNode);
                        
                    } else {

                        newBuckets[newHash] = newNode;
                    }

                    currentNode = currentNode.getNext();
                    
                } while (currentNode != null);
            }
        }
        
        this.buckets = newBuckets;
    }

    private int hash(long key) {

        return (int) (key & (capacity - 1));
    }
}
