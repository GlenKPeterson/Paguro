// Copyright 2014-02-02 PlanBase Inc. and Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp.permanent;

import org.organicdesign.fp.Sentinal;
import org.organicdesign.fp.TransformableAbstract;
import org.organicdesign.fp.function.BiFunction;
import org.organicdesign.fp.function.Consumer;
import org.organicdesign.fp.function.Function;
import org.organicdesign.fp.function.Predicate;

public abstract class SequenceAbstract<T> extends TransformableAbstract<T> implements Sequence<T> {

    @Override
    public abstract T first();

    @Override
    public abstract Sequence<T> rest();

    @Override
    public <U> Sequence<U> map(Function<T,U> func) {
        return SequenceMapped.of(this, func);
    }

    @Override
    public Sequence<T> filter(Predicate<T> func) {
        return SequenceFiltered.of(this, func);
    }

    @Override
    public void forEach(Consumer<T> se) {
        Sequence<T> seq = this;
        T item = seq.first();
        while (item != Sentinal.USED_UP) {
            se.accept_(item);
            // repeat with next element
            seq = seq.rest();
            item = seq.first();
        }
    }

    @Override
    public T firstMatching(Predicate<T> pred) {
        Sequence<T> seq = this;
        T item = seq.first();
        while (item != Sentinal.USED_UP) {
            if (pred.test_(item)) { return item; }
            // repeat with next element
            seq = seq.rest();
            item = seq.first();
        }
        return null;
    }

    @Override
    public <U> U foldLeft(U u, BiFunction<U, T, U> fun) {
        Sequence<T> seq = this;
        T item = seq.first();
        while (item != Sentinal.USED_UP) {
            u = fun.apply_(u, item);
            // repeat with next element
            seq = seq.rest();
            item = seq.first();
        }
        return u;
    }

//    @Override
//    public T reduceLeft(BiFunction<T, T, T> fun) {
//        T item = next();
//        T accum = item;
//        while (item != Sentinal.USED_UP) {
//            item = next();
//            accum = fun.apply_(accum, item);
//        }
//        return accum;
//    }

//    @Override
//    public <U> Sequence<U> flatMap(Function<T,Sequence<U>> func) {
//        return SequenceFlatMapped.of(this, func);
//    }

}
