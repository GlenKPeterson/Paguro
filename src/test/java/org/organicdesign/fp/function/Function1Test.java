package org.organicdesign.fp.function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public class Function1Test {
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new Function1<Integer,Integer>() {
            @Override public Integer apply(Integer o) throws Exception {
                if (o < 10) {
                    throw new IOException("test exception");
                }
                return o;
            }
        }.apply_(3);
    }

    @Test(expected = IllegalStateException.class)
    public void applyIllegalStateException() {
        new Function1<Integer,Integer>() {
            @Override public Integer apply(Integer o) throws Exception {
                if (o < 10) {
                    throw new IllegalStateException("test exception");
                }
                return o;
            }
        }.apply_(3);
    }
}
