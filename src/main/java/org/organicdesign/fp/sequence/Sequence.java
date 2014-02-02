// Copyright 2014-01-20 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.sequence;

// hasNext() is not thread-safe.  Instead, use a USED_UP sentinel value that the client has to
// check and provide only a next() method.  This does not have to ensure any ordering.  It may
// for some collection types and not for others.
public interface Sequence<T> {
    public static final Object USED_UP = new Object();
    public static final Sequence<Object> EMPTY_SEQUENCE = new Sequence<Object>() {
        @Override
        public Object first() {
            return USED_UP;
        }
        @Override
        public Sequence<Object> rest() {
            return this;
        }
    };
    public T first();
    public Sequence<T> rest();
}