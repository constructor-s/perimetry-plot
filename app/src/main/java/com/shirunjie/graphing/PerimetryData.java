package com.shirunjie.graphing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by shirunjie on 2016-07-11.
 */

public class PerimetryData {
    private Collection<Entry> entries;

    public PerimetryData(Collection<Entry> entries) {
        if (entries == null) {
            entries = new ArrayList<>();
        }
        this.entries = entries;
    }

    public Collection<Entry> getEntries() {
        return entries;
    }

    public static final int MINX = 0;
    public static final int MINY = 1;
    public static final int MAXX = 2;
    public static final int MAXY = 2;

    /**
     *
     * @return {minx, miny, maxx, maxy};
     */
    public double[] getMaxMin() {
        if (entries != null && !entries.isEmpty()) {
            Iterator<Entry> iterator = entries.iterator();
            if (iterator.hasNext()) {
                final Entry firstEntry = iterator.next();
                double minx = firstEntry.getX();
                double maxx = firstEntry.getX();
                double miny = firstEntry.getY();
                double maxy = firstEntry.getY();

                for (; iterator.hasNext(); ) {
                    Entry        entry = iterator.next();
                    final double x     = entry.getX();
                    final double y     = entry.getY();

                    if (x < minx) {
                        minx = x;
                    } else if (x > maxx) {
                        maxx = x;
                    }

                    if (y < miny) {
                        miny = y;
                    } else if (y > maxy) {
                        maxy = y;
                    }
                }

                return new double[] {minx, miny, maxx, maxy};
            }
        }
        return null;
    }

    public boolean add(Entry object) {
        return entries.add(object);
    }

    public void clear() {
        entries.clear();
    }

    public int size() {
        return entries.size();
    }

    public boolean remove(Object object) {
        return entries.remove(object);
    }

    public <T> T[] toArray(T[] array) {
        return entries.toArray(array);
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public Object[] toArray() {
        return entries.toArray();
    }

    public boolean containsAll(Collection<?> collection) {
        return entries.containsAll(collection);
    }

    public boolean addAll(Collection<? extends Entry> collection) {
        return entries.addAll(collection);
    }

    public boolean contains(Object object) {
        return entries.contains(object);
    }

    public boolean retainAll(Collection<?> collection) {
        return entries.retainAll(collection);
    }

    public Iterator<Entry> iterator() {
        return entries.iterator();
    }

    public boolean removeAll(Collection<?> collection) {
        return entries.removeAll(collection);
    }
}
