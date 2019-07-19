package me.engineersbox.playerrev.methodlib;

import java.util.List;
import java.util.Objects;

/**
 * @param <K> Type of key (absolute)
 * @param <V> Type of value relating to key (dynamic)
 */
public class DynamicEnum<K, V extends List<?>> {

    private Entry<K, V>[] buckets;
    private static final int INITIAL_CAPACITY = 1 << 4; // 16
    private int size = 0;

    public DynamicEnum() {
        this(INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
	public DynamicEnum(int capacity) {
        this.buckets = new Entry[capacity];
    }

    public void put(K key, V value) {
    	
        Entry<K, V> entry = new Entry<>(key, value, null);
        int bucket = getHash(key) % getBucketSize();
        Entry<K, V> existing = buckets[bucket];

        if (existing == null) {
            buckets[bucket] = entry;
            size++;

        } else {

            while (existing.next != null) {
                if (existing.key.equals(key)) {
                    existing.value = value;
                    return;
                }
                existing = existing.next;
            }

            if (existing.key.equals(key)) {
                existing.value = value;
            } else {
                existing.next = entry;
                size++;
            }

        }

    }

    public V get(K key) {
    	
        Entry<K, V> bucket = buckets[getHash(key) % getBucketSize()];

        while (bucket != null) {
            if (key == bucket.key) {
                return bucket.value;
            }
            bucket = bucket.next;
        }
        return null;
    }

    public int size() {
        return size;
    }

    private int getBucketSize() {
        return buckets.length;
    }

    private int getHash(K key) {
        return key == null ? 0 : Math.abs(key.hashCode());
    }
    
    public boolean isValid(K key, V val) {
    	
         int bucket = getHash(key) % getBucketSize();
         Entry<K, V> existing = buckets[bucket];
         
         while (existing.next != null) {
             if (existing.key.toString().equalsIgnoreCase(key.toString())) {
                 existing.value.contains(val);
                 return true;
             }
             existing = existing.next;
         }
         return false;
    }
    
    public String valueOfByAlias(V val) {
    	
    	for (Entry<K, V> entry : buckets) {
    		if (Objects.equals(val, entry.getValue())) {
    			return entry.getKey().toString();
    		}
    	}
		return null;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for (Entry<?, ?> entry : buckets) {
            sb.append("[");
            while (entry != null) {
                sb.append(entry);
                if (entry.next != null) {
                    sb.append(", ");
                }
                entry = entry.next;
            }
            sb.append("]");
        }
        return "{" + sb.toString() + "}";
    }

    static class Entry<K, V> {

        final K key;
        V value;
        Entry<K, V> next;

        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public Entry<K, V> getNext() {
            return next;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj instanceof Entry) {
                Entry<?, ?> entry = (Entry<?, ?>) obj;
                return key.equals(entry.getKey()) &&
                        value.equals(entry.getValue());
            }
            return false;
        }

        @Override
        public int hashCode() {

            int hash = 13;
            hash = 17 * hash + ((key == null) ? 0 : key.hashCode());
            hash = 17 * hash + ((value == null) ? 0 : value.hashCode());
            return hash;

        }

        @Override
        public String toString() {
            return "{" + key + ", " + value + "}";
        }

    }

}
