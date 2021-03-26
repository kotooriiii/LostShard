package com.github.kotooriiii.structure;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;

public class FixedLinkedList<T> extends LinkedList<T> {

    private int maxElements;

    public FixedLinkedList(int maxElements) {
        this.maxElements = maxElements;
    }
    @Override
    public boolean add(T o) {

        if (this.size() == maxElements) {
            this.poll();
        }

        return super.add(o);
    }
}
