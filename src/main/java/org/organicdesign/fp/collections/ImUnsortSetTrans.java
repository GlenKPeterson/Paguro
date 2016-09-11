// Copyright 2016 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.collections;

/**
 Declare your set as ImUnsortSetTrans, call asTransient(), build it, then call
 mySet = mySet.persistent() without having to declare a new variable.
 */
public interface ImUnsortSetTrans<E> extends ImUnsortSet<E> {

    /** Returns a transient/mutable version of this (persistent/immutable) set. */
    @Override default ImUnsortSetTrans<E> asTransient() { return this; }

    /** {@inheritDoc} */
    @Override
    ImUnsortSetTrans<E> put(E val);

    /** {@inheritDoc} */
    @Override
    ImUnsortSetTrans<E> without(E key);
}
