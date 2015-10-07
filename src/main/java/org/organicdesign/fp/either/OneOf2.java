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

package org.organicdesign.fp.either;

import org.organicdesign.fp.function.Function1;

import java.util.Objects;

/**
 This approximates union of two types for Java.  It works, but it's a little experimental in the
 sense of is this the best way to do it, and how good of an idea is this in general.

 Java has specifically avoided union types in favor of defining a super-type and making those other
 two types extend the new parent.  But sometimes you don't have control of the two types to make
 them inherit from a common ancestor.  You could write a wrapper class, but that can be really
 complicated.  Even if you can add new ancestor to types you control, doing so could involve a
 complex code rewrite of multiple classes, when all you need is to pass one little parameter to one
 function...

 People typically solve this dilemma with an Either&lt;L,R&gt; class with Left&lt;L&gt; and
 Right&lt;R&gt; sub-classes.  In a language with type aliases that's ideal, but the closest Java
 comes to aliases is sub-classing and for that you need a single class to inherit from.  On the
 bright side, this gives you the chance to add descriptive method names at the same time.

 When Java users say "Pattern Matching" they mean Regular Expressions so the method that behaves
 like what Scala and ML would call Pattern Matching is called "typeMatch" instead.  The safest way
 to use OneOf classes is to always call typeMatch() because it forces you to think about how to
 handle each type you could possibly receive.

 Usage:
 <pre><code>
union.typeMatch(fst -&gt; fst.doOneThing(),
                sec -&gt; sec.doSomethingElse());
 </code></pre>

 Sometimes it's a programming error to pass one type or another and you may want to throw an
 exception.
 <pre><code>
union.typeMatch(fst -&gt; fst.doOneThing(),
                sec -&gt; { throw new IllegalStateException("Asked for a 2nd; only had a 1st."); });
 </code></pre>

 As a shortcut, you can call (using method references) union::throw1 or union::throw2
 <pre><code>
union.typeMatch(fst -&gt; fst.doOneThing(),
                union::throw2);
 </code></pre>

 For the shortest syntax and best names, define your own subclass.  This is no where near as
 elegant as sub-classing Tuples.
 <pre><code>
public class A_Or_B extends OneOf2&lt;ClassA,ClassB&gt; {
    // Constructor
    A_Or_B(ClassA cre, ClassB rev, int s) { super(cre, rev, s); }

    // Static factory method for first type
    public static A_Or_B fst(ClassA cre) {
        return new A_Or_B(cre, null, 1);
    }

    // Static factory method for second type
    public static A_Or_B sec(ClassB rev) {
        return new A_Or_B(null, rev, 2);
    }

    // Return first or throw exception
    public ClassA fst() {
        return typeMatch(cre -&gt; cre,
                         super::throw2);
    }

    // Return second or throw exception
    public ClassB sec() {
        return typeMatch(super::throw1,
                         rev -&gt; rev);
    }
}
 </code></pre>

 Now you use descriptive and extremely brief syntax:
 <pre><code>
union.fst().doOneThing();
 </code></pre>

 */
// TODO: Should this implement javax.lang.model.type.UnionType somehow?
public class OneOf2<A,B> {

    private final Object item;
    private final int sel;

    protected OneOf2(A a, B b, int s) {
        sel = s;
        if (sel == 1) {
            item = a;
        } else if (sel == 2) {
            item = b;
        } else {
            throw new IllegalArgumentException("You must specify whether this holds an item of" +
                                               " type 1 or type 2");
        }
    }

    static <A,B> OneOf2<A,B> _1(A a) { return new OneOf2<>(a, null, 1); }

    static <A,B> OneOf2<A,B> _2(B b) { return new OneOf2<>(null, b, 2); }

    // We only store one item and it's type is erased, so we have to cast it at runtime.
    // If sel is managed correctly, it ensures that the cast is accurate.
    @SuppressWarnings("unchecked")
    public <R> R typeMatch(Function1<A, R> fa,
                           Function1<B, R> fb) {
        if (sel == 1) {
            return fa.apply((A) item);
        }
        return fb.apply((B) item);
    }

    // The A parameter ensures that this is used in the proper branch of the guard
    @SuppressWarnings("UnusedParameters")
    public <R> R throw1(A a) {
        throw new IllegalStateException("Asked for a 1st when only had a 2nd.");
    }

    // The B parameter ensures that this is used in the proper branch of the guard
    @SuppressWarnings("UnusedParameters")
    public <R> R throw2(B b) {
        throw new IllegalStateException("Asked for a 2nd when only had a 1st.");
    }

    public int hashCode() {
        if (sel == 1) {
            return (item == null) ? 0 : item.hashCode();
        }
        // Return the 2's compliment so it's different if it's the second item.
        return ~ ((item == null) ? 0 : item.hashCode());
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (this == other) { return true; }
        if (!(other instanceof OneOf2)) { return false; }

        OneOf2 that = (OneOf2) other;

        if (sel == 1) {
            return (boolean) that.typeMatch(thatItem -> Objects.equals(item, thatItem),
                                            x -> false);
        }
        return (boolean) that.typeMatch(x -> false,
                                        thatItem -> Objects.equals(item, thatItem));
    }
}