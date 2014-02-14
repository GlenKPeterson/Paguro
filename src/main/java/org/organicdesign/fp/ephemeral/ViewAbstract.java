// Copyright 2014-01-08 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.ephemeral;

import org.organicdesign.fp.RealizableAbstract;
import org.organicdesign.fp.Sentinal;
import org.organicdesign.fp.function.BiFunction;
import org.organicdesign.fp.function.Consumer;
import org.organicdesign.fp.function.Function;
import org.organicdesign.fp.function.Predicate;

public abstract class ViewAbstract<T> extends RealizableAbstract<T> implements View<T> {

    @Override
    public abstract T next();

    @Override
    public <U> View<U> map(Function<T,U> func) {
        return ViewMapped.of(this, func);
    }

    @Override
    public View<T> filter(Predicate<T> pred) {
        return ViewFiltered.of(this, pred);
    }

    @Override
    public void forEach(Consumer<T> se) {
        T item = next();
        while (item != Sentinal.USED_UP) {
            se.accept_(item);
            item = next();
        }
    }

    @Override
    public T firstMatching(Predicate<T> pred) {
        T item = next();
        while (item != Sentinal.USED_UP) {
            if (pred.test_(item)) { return item; }
            item = next();
        }
        return null;
    }

    @Override
    public <U> U reduce(BiFunction<T,U,U> fun, U u) {
        T item = next();
        while (item != Sentinal.USED_UP) {
            u = fun.apply_(item, u);
            item = next();
        }
        return u;
    }
}
