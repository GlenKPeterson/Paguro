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
 Josh Bloch says that an enum is the best way to ensure a singleton.
 */
public enum Sentinel {
    /**
     Represents the end of a collection, filter, or view.  Having a sentinel value means that
     such a collection can contain nulls or any value really besides the sentinel.  Because
     this is an enum, you can use the blazingly fast compare-by-reference.
     */
    USED_UP;
}
