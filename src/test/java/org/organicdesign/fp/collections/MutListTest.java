package org.organicdesign.fp.collections;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.organicdesign.fp.FunctionUtils.ordinal;

/**
 Created by gpeterso on 9/14/16.
 */
public class MutListTest {
    static class TestList<E> implements MutList<E> {
        private List<E> inner = new ArrayList<>();

        TestList(List<E> ls) { inner = ls; }

        @Override public MutList<E> append(E val) {
            inner.add(val);
            return this;
        }

        @Override public ImList<E> immutable() {
            return PersistentVector.ofIter(inner);
        }

        @Override public MutList<E> replace(int idx, E e) {
            inner.set(idx, e);
            return this;
        }

        @Override public int size() { return inner.size(); }

        @Override public E get(int i) { return inner.get(i); }
    }

    @Test public void testStuff() {
        List<String> control = new ArrayList<>();
        MutList<String> test = new TestList<>(new ArrayList<>());

        for (int i = 0; i < 32; i++) {
            String ord = ordinal(i);
            control.add(ord);
            test.append(ord);
            assertEquals(control.size(), test.size());
            assertEquals(control, test);
        }

        List<String> moreStuff = Arrays.asList("this", "is", "more", "stuff");
        control.addAll(moreStuff);
        test.concat(moreStuff);

        assertEquals(control.size(), test.size());
        assertEquals(control, test);
    }
}