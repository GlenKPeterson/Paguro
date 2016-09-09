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
 The super-interface of PersistentHashSet (excludes TreeSet).
 */
public interface ImUnsortSet<E> extends ImSet<E> {

    /**
     Returns a transient (mutable builder) version of this set that is not thread safe.
     */
    ImUnsortSetTrans<E> asTransient();

    /**
     Returns a persistent/immutable version of this (maybe transient) set.
     Once you call persist() on it, you can't mutate the transient one anymore (or you'll get an
     exception).  This is because they share most of the underlying implementation.
     Theoretically, this should be on ImUnsortSetTrans, but sometimes you want to know that a set
     is persistent, sometimes you want to know it's transient.  It's hard to pick only one, but
     that's how inheritance works.
     */
    ImUnsortSet<E> persistent();

    /** {@inheritDoc} */
    @Override
    ImUnsortSet<E> put(E val);

    /** {@inheritDoc} */
    @Override
    ImUnsortSet<E> without(E key);
}
