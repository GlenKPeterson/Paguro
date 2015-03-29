package org.organicdesign.fp.function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public class SideEffectTest {
    @SuppressWarnings("deprecation")
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new SideEffect() {
            @Override public void applyEx() throws Exception {
                throw new IOException("test exception");
            }
        }.apply();
    }

    @SuppressWarnings("deprecation")
    @Test(expected = IllegalStateException.class)
    public void applyIllegalStateException() {
        new SideEffect() {
            @Override public void applyEx() throws Exception {
                throw new IllegalStateException("test exception");
            }
        }.apply();
    }
}
