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
import org.organicdesign.fp.FunctionUtils
import java.util.function

class TestViewMapped extends FlatSpec with Matchers {
  "A View" should "map items in one batch" in {
    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).map(FunctionUtils.identity()).toJavaArrayList() ===
           util.Arrays.asList(1,2,3,4,5,6,7,8,9))

    val plusOne = new java.util.function.Function[Int,Int]{
      override def apply(t:Int):Int = t + 1

      override def andThen[V](after:function.Function[_ >: Int, _ <: V]):
        function.Function[Int, V] = ???

      override def compose[V](before:function.Function[_ >: V, _ <: Int]):
        function.Function[V, Int] = ???
    }

    assert(ViewFromArray.of(1,2,3,4,5,6,7,8,9).map(plusOne).toJavaArrayList() ===
           util.Arrays.asList(2,3,4,5,6,7,8,9,10))

  }
}
