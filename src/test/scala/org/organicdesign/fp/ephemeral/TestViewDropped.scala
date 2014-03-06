// Copyright 2014-03-05 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.ephemeral

import org.scalatest._
import java.util

class TestViewDropped extends FlatSpec with Matchers {
  "A View" should "drop items in a single drop" in {
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(0).toJavaArrayList() ===
           util.Arrays.asList(1,2,3,4,5,6,7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(1).toJavaArrayList() ===
           util.Arrays.asList(2,3,4,5,6,7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(2).toJavaArrayList() ===
           util.Arrays.asList(3,4,5,6,7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(3).toJavaArrayList() ===
           util.Arrays.asList(4,5,6,7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(8).toJavaArrayList() ===
           util.Arrays.asList(9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(9).toJavaArrayList() ===
           util.Arrays.asList())
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(10).toJavaArrayList() ===
           util.Arrays.asList())
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(10000).toJavaArrayList() ===
           util.Arrays.asList())
  }

  it should "throw an IllegalArgumentException if a negative number of drops are specified" in {
    intercept[IllegalArgumentException] {
      ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(-1)
    }
    intercept[IllegalArgumentException] {
      ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(-99)
    }
  }

  it should "drop items in multiple drops" in {
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(1).drop(1).toJavaArrayList() ===
           util.Arrays.asList(3,4,5,6,7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).drop(1).drop(1).drop(1).toJavaArrayList() ===
           util.Arrays.asList(4,5,6,7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
               .drop(1).drop(1).drop(1).drop(1).drop(1)
               .drop(1).drop(1).drop(1).toJavaArrayList() ===
           util.Arrays.asList(9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
               .drop(1).drop(1).drop(1).drop(1).drop(1)
               .drop(1).drop(1).drop(1).drop(1).toJavaArrayList() ===
           util.Arrays.asList())
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
               .drop(1).drop(1).drop(1).drop(1).drop(1)
               .drop(1).drop(1).drop(1).drop(1).drop(1).toJavaArrayList() ===
           util.Arrays.asList())
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
               .drop(1).drop(1).drop(1).drop(1).drop(1)
               .drop(1).drop(1).drop(1).drop(1).drop(1)
               .drop(1).drop(1).drop(1).drop(1).drop(1).toJavaArrayList() ===
           util.Arrays.asList())

    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
               .drop(0).drop(1).drop(2).drop(3).toJavaArrayList() ===
           util.Arrays.asList(7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
               .drop(3).drop(2).drop(1).drop(0).toJavaArrayList() ===
           util.Arrays.asList(7,8,9))
  }
}
