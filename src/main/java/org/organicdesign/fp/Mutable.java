// Copyright 2014-02-15 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp;

/**
 Java 7 and earlier require that any variables declared outside a lambda, but used inside a lambda
 must be final.  Occasionally that is really inconvenient and the sub-classes in this class wrap
 various classes to let you work around this limitation.
 */
public class Mutable {

    public static class BooleanRef {
        private boolean b;
        private BooleanRef(boolean boo) { b = boo; }
        public static BooleanRef of(boolean boo) {
            return new BooleanRef(boo);
        }
        public boolean isTrue() { return b; }
        public void set(boolean boo) { b = boo; }
    }

    public static class IntRef {
        private int i;
        private IntRef(int in) { i = in; }
        public static IntRef of(int in) { return new IntRef(in); }
        public int value() { return i; }
        public IntRef increment() { i++; return this; }
        public IntRef set(int x) { i = x; return this; }
    }

    public static class ObjectRef<T> {
        private T t;
        private ObjectRef(T in) { t = in; }
        public static <T> ObjectRef<T> of(T obj) {
            return new ObjectRef<T>(obj);
        }
        public T value() { return t; }
        public ObjectRef<T> set(T x) { t = x; return this; }
    }
}
