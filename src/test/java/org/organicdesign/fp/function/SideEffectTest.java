package org.organicdesign.fp.function;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

@RunWith(JUnit4.class)
public class SideEffectTest {
    @Test(expected = RuntimeException.class)
    public void applyIOException() {
        new SideEffect() {
            @Override public void apply() throws Exception {
                throw new IOException("test exception");
            }
        }.apply_();
    }

    @Test(expected = IllegalStateException.class)
    public void applyIllegalStateException() {
        new SideEffect() {
            @Override public void apply() throws Exception {
                throw new IllegalStateException("test exception");
            }
        }.apply_();
    }
}
