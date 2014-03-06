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
import java.util.Arrays

class TestViewTaken extends FlatSpec with Matchers {
  "A View" should "take items in one batch" in {
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(9999).toJavaArrayList ===
           Arrays.asList(1,2,3,4,5,6,7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(10).toJavaArrayList ===
           Arrays.asList(1,2,3,4,5,6,7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(9).toJavaArrayList ===
           Arrays.asList(1,2,3,4,5,6,7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(8).toJavaArrayList ===
           Arrays.asList(1,2,3,4,5,6,7,8))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(7).toJavaArrayList ===
           Arrays.asList(1,2,3,4,5,6,7))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(3).toJavaArrayList ===
           Arrays.asList(1,2,3))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(2).toJavaArrayList ===
           Arrays.asList(1,2))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(1).toJavaArrayList ===
           Arrays.asList(1))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(0).toJavaArrayList ===
           Arrays.asList())
  }

  it should "throw an IllegalArgumentException if a negative number of takes are specified" in {
    intercept[IllegalArgumentException] {
      ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(-1)
    }
    intercept[IllegalArgumentException] {
      ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(-99)
    }
  }

  it should "take with multiple applications of take" in {
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(10).take(9999).take(10).toJavaArrayList ===
           Arrays.asList(1,2,3,4,5,6,7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(9).take(9).take(9).toJavaArrayList ===
           Arrays.asList(1,2,3,4,5,6,7,8,9))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(8).take(7).take(6).toJavaArrayList ===
           Arrays.asList(1,2,3,4,5,6))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(6).take(7).take(8).toJavaArrayList ===
           Arrays.asList(1,2,3,4,5,6))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(999).take(1).take(9999999).toJavaArrayList ===
           Arrays.asList(1))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(9999).take(0).take(3).toJavaArrayList ===
           Arrays.asList())
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(0).take(99999999).take(9999999).toJavaArrayList ===
           Arrays.asList())
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).take(99).take(9999).take(0).toJavaArrayList ===
           Arrays.asList())
  }
}
