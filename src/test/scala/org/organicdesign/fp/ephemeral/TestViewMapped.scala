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
import org.organicdesign.fp.{JavaFunction1, FunctionUtils}

class TestViewMapped extends FlatSpec with Matchers {
  "A View" should "map items in one batch" in {
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).map(FunctionUtils.identity()).toJavaArrayList() ===
           Arrays.asList(1,2,3,4,5,6,7,8,9))

    val plusOne = JavaFunction1((x:Int) => x + 1)
    val minusOne = JavaFunction1((x:Int) => x - 1)

    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).map(plusOne).toJavaArrayList ===
           Arrays.asList(2,3,4,5,6,7,8,9,10))
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).map(minusOne).toJavaArrayList ===
           Arrays.asList(0,1,2,3,4,5,6,7,8))

    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).map(plusOne).map(minusOne).toJavaArrayList ===
           Arrays.asList(1,2,3,4,5,6,7,8,9))

    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).map(minusOne).map(plusOne).toJavaArrayList ===
           Arrays.asList(1,2,3,4,5,6,7,8,9))

    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).map(plusOne).map(plusOne).toJavaArrayList ===
           Arrays.asList(3,4,5,6,7,8,9,10,11))

    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
               .map(plusOne).map(plusOne).map(plusOne).map(plusOne).map(plusOne)
               .map(plusOne).map(plusOne).map(plusOne).map(plusOne).map(plusOne)
               .toJavaArrayList ===
           Arrays.asList(11,12,13,14,15,16,17,18,19))

    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9)
               .map(minusOne).map(minusOne).map(minusOne).map(minusOne).map(minusOne)
               .map(minusOne).map(minusOne).map(minusOne).map(minusOne).map(minusOne)
               .toJavaArrayList ===
           Arrays.asList(-9,-8,-7,-6,-5,-4,-3,-2,-1))

  }
}
