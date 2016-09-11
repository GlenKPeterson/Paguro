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
public interface ImUnsortedSet<E> extends ImSet<E> {

    /**
     Returns a immutable version of this (maybe mutable) set.
     Once you call immutable() on it, you can't mutate the mutable one anymore (or you'll get an
     exception).  This is because they share most of the underlying implementation.
     Theoretically, this should be on MutableUnsortedSet, but sometimes you want to know that a set
     is immutable, sometimes you want to know it's mutable.  It's hard to pick only one, but
     that's how inheritance works.
     */
    ImUnsortedSet<E> immutable();

    /**
     Returns a mutable (builder) version of this set that is not thread safe.
     */
    MutableUnsortedSet<E> mutable();

    /** {@inheritDoc} */
    @Override
    ImUnsortedSet<E> put(E val);

    /** {@inheritDoc} */
    @Override
    ImUnsortedSet<E> without(E key);
}
