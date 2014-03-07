// Copyright 2014-03-06 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp
import org.scalatest._
import java.util.Arrays

class TestFunctionUtils extends FlatSpec with Matchers {
  "FunctionUtils" should "compose predicates with and and or" in {
    assert(FunctionUtils.and() eq FunctionUtils.accept())
    assert(FunctionUtils.and(FunctionUtils.accept()) eq FunctionUtils.accept())
    assert(FunctionUtils.and(FunctionUtils.accept(),
                             FunctionUtils.accept(),
                             FunctionUtils.accept()) eq FunctionUtils.accept())
    assert(FunctionUtils.and(FunctionUtils.reject()) eq FunctionUtils.reject())


    assert(FunctionUtils.or() eq FunctionUtils.reject())
    assert(FunctionUtils.or(FunctionUtils.accept()) eq FunctionUtils.accept())
    assert(FunctionUtils.or(FunctionUtils.reject(),
                            FunctionUtils.reject(),
                            FunctionUtils.reject(),
                            FunctionUtils.accept()) eq FunctionUtils.accept())
    assert(FunctionUtils.or(FunctionUtils.accept(),
                            FunctionUtils.reject(),
                            FunctionUtils.reject(),
                            FunctionUtils.reject()) eq FunctionUtils.accept())

    assert(FunctionUtils.or(FunctionUtils.reject()) eq FunctionUtils.reject())
  }
}
