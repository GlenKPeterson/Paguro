// Copyright (c) 2014-03-07 PlanBase Inc. & Glen Peterson
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.organicdesign.fp.ephemeral.View;

import static junit.framework.TestCase.assertTrue;
import static org.organicdesign.fp.FunctionUtils.*;

@RunWith(JUnit4.class)
public class BooleanCombinerTest {

    @Test
    @SuppressWarnings("unchecked")
    public void combineAnd() {
        assertTrue(BooleanCombiner.AND.combineArray() == ACCEPT);
        assertTrue(BooleanCombiner.AND.combine(null) == ACCEPT);

        assertTrue(BooleanCombiner.AND.combineArray(accept()) == ACCEPT);
        assertTrue(BooleanCombiner.AND.combine(View.ofArray(accept())) == ACCEPT);

        assertTrue(BooleanCombiner.AND.combineArray(reject()) == REJECT);
        assertTrue(BooleanCombiner.AND.combine(View.ofArray(reject())) == REJECT);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void combineOr() {
        assertTrue(BooleanCombiner.OR.combineArray() == REJECT);
        assertTrue(BooleanCombiner.OR.combine(null) == REJECT);

        assertTrue(BooleanCombiner.OR.combineArray(accept()) == ACCEPT);
        assertTrue(BooleanCombiner.OR.combine(View.ofArray(accept())) == ACCEPT);

        assertTrue(BooleanCombiner.OR.combineArray(reject()) == REJECT);
        assertTrue(BooleanCombiner.OR.combine(View.ofArray(reject())) == REJECT);
    }
}
