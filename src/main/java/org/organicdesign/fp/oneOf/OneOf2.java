// Copyright 2015 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.oneOf;

import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.function.Fn1;
import org.organicdesign.fp.type.RuntimeTypes;

import java.util.Objects;

import static org.organicdesign.fp.FunctionUtils.stringify;

/**
 Like {@link org.organicdesign.fp.tuple.Tuple2}, OneOf2 is designed to be sub-classed so you can add descriptive names.
 Also because that's just the cleanest way for it to work in Java.

 When Java users say "Pattern Matching" they mean Regular Expressions so the method that behaves
 like what Scala and ML would call Pattern Matching is called "match" instead.  The safest way
 to use Union classes is to always call match() because it forces you to think about how to
 handle each type you could possibly receive.

 Usage:
 <pre><code>
thingy.match(fst -&gt; fst.doOneThing(),
                 sec -&gt; sec.doSomethingElse());
 </code></pre>

 Sometimes it's a programming error to pass one type or another and you may want to throw an
 exception.
 <pre><code>
oneOf.match(fst -&gt; fst.doOneThing(),
                sec -&gt; { throw new IllegalStateException("Asked for a 2nd; only had a 1st."); });
 </code></pre>

 As a shortcut, you can call (using method references) oneOf::throw1 or oneOf::throw2
 <pre><code>
oneOf.match(fst -&gt; fst.doOneThing(),
                oneOf::throw2);
 </code></pre>

 For the shortest syntax and best names, define your own subclass.  This is similar to sub-classing Tuples.
 <pre><code>
static class String_Integer extends OneOf2&lt;String,Integer&gt; {
    // Ensure we use the one and only instance of this runtime types array to prevent duplicate array creation.
    private static final ImList&lt;Class&gt; CLASS_STRING_INTEGER =
            RuntimeTypes.registerClasses(vec(String.class, Integer.class));

    // Constructor
    private String_Integer(String s, Integer i, int n) { super(CLASS_STRING_INTEGER, s, i, n); }

    // Static factory methods
    public static String_Integer ofStr(String s) { return new String_Integer(s, null, 1); }
    public static String_Integer ofInt(Integer i) { return new String_Integer(null, i, 2); }

    // (Optional) "getter" methods that throw a detailed exception if the type isn't what you're expecting.
    public String str() {
        return super.match(s -&gt; s,
                               super::throw2);
    }
    public Integer integer() {
        return super.match(super::throw1,
                               i -&gt; i);
    }
}</code></pre>

 OK, so that's kind of wordy, but it's Java, and you only have to do this once to presumably use it many times.
 equals, hashcode, and toString are all taken care of for you.

 Now you use descriptive and extremely brief syntax:
 <pre><code>
// Type-safe switching - always works at runtime.
x.match(s -> (s == null) ? null : s.lowerCase(),
n -> "This is the number " + n);

// If not a String at runtime throws "Expected a(n) String but found a(n) Integer"
x.str().contains("goody!");

 // If not an Integer at runtime throws "Expected a(n) Integer but found a(n) String"
3 + x.integer();

 </code></pre>
 */
// TODO: Remove RuntimeTypes and do it like OneOf2OrNone instead.  It's too messy otherwise.
// TODO: Should this implement javax.lang.model.type.UnionType somehow?
public class OneOf2<A,B> {

    protected final Object item;
    protected final int sel;
    private final ImList<Class> types;

    protected OneOf2(ImList<Class> runtimeTypes, A a, B b, int s) {
        if (runtimeTypes.size() != 2) {
            throw new IllegalArgumentException("OneOf2 requires exactly 2 types");
        }
        types = RuntimeTypes.registerClasses(runtimeTypes);

        sel = s;
        if (sel == 1) {
            item = a;
        } else if (sel == 2) {
            item = b;
        } else {
            throw new IllegalArgumentException("You must specify whether this holds a(n) " +
                                               RuntimeTypes.name(types.get(0)) + " or a(n) " +
                                               RuntimeTypes.name(types.get(1)));
        }
    }

//    static <A,B> OneOf2<A,B> _1(A a) { return new OneOf2<>(a, null, 1); }
//
//    static <A,B> OneOf2<A,B> _2(B b) { return new OneOf2<>(null, b, 2); }

    // We only store one item and it's type is erased, so we have to cast it at runtime.
    // If sel is managed correctly, it ensures that the cast is accurate.
    @SuppressWarnings("unchecked")
    public <R> R match(Fn1<A, R> fa,
                       Fn1<B, R> fb) {
        if (sel == 1) {
            return fa.apply((A) item);
        }
        return fb.apply((B) item);
    }

    // The A parameter ensures that this is used in the proper branch of the guard
    @SuppressWarnings("UnusedParameters")
    protected <R> R throw1(A a) {
        throw new IllegalStateException("Expected a(n) " +
                                        RuntimeTypes.name(types.get(0)) + " but found a(n) " +
                                        RuntimeTypes.name(types.get(1)));
    }

    // The B parameter ensures that this is used in the proper branch of the guard
    @SuppressWarnings("UnusedParameters")
    protected <R> R throw2(B b){
        throw new IllegalStateException("Expected a(n) " +
                                        RuntimeTypes.name(types.get(1)) + " but found a(n) " +
                                        RuntimeTypes.name(types.get(0)));
    }

    public int hashCode() {
        // Simplest way to make the two items different.
        return Objects.hashCode(item) + sel;
    }

    @SuppressWarnings("unchecked")
    @Override public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof OneOf2)) { return false; }

        OneOf2 that = (OneOf2) other;
        return (sel == that.sel) &&
               Objects.equals(item, that.item);
    }

    @Override public String toString() {
        return RuntimeTypes.name(types.get(sel - 1)) + "(" + stringify(item) + ")";
    }
}