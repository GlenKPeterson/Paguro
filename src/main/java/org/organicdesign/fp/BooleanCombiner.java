// Copyright (c) 2014-03-07 PlanBase Inc. & Glen Peterson
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

import java.util.function.Predicate;

import org.organicdesign.fp.ephemeral.View;

public enum BooleanCombiner {
    AND {
        @Override
        public <T> Predicate<T> combine(View<Predicate<T>> in) {
            return FunctionUtils.and(in);
        }
        @Override
        @SafeVarargs
        public final <T> Predicate<T> combineArray(Predicate<T>... in) {
            return FunctionUtils.andArray(in);
        }
    },
    OR {
        @Override
        public <T> Predicate<T> combine(View<Predicate<T>> in) {
            return FunctionUtils.or(in);
        }
        @Override
        @SafeVarargs
        public final <T> Predicate<T> combineArray(Predicate<T>... in) {
            return FunctionUtils.orArray(in);
        }
    };
    public abstract <T> Predicate<T> combine(View<Predicate<T>> in);

    @SuppressWarnings("unchecked")
    public abstract <T> Predicate<T> combineArray(Predicate<T>... in);
}
