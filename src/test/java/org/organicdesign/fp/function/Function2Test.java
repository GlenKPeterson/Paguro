package org.organicdesign.fp.function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public class Function2Test {
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new Function2<Integer,Integer,Integer>() {
            @Override public Integer apply(Integer a, Integer b) throws Exception {
                if (a < b) {
                    throw new IOException("test exception");
                }
                return a;
            }
        }.apply_(1,2);
    }

    @Test(expected = IllegalStateException.class)
    public void applyIllegalStateException() {
        new Function2<Integer,Integer,Integer>() {
            @Override public Integer apply(Integer a, Integer b) throws Exception {
                if (a < b) {
                    throw new IllegalStateException("test exception");
                }
                return a;
            }
        }.apply_(3,4);
    }
}
