// Copyright (c) 2014-03-08 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.experiments;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.ephemeral.View;

public class ViewFromIntRange implements View<Long> {

    private final IntRange range;
    private long idx = 0;

    ViewFromIntRange(IntRange r) { range = r; }

    static View<Long> of(IntRange r) {
        if ((r == null) || (r.size() < 1)) { return View.emptyView(); }
        return new ViewFromIntRange(r);
    }

    @Override
    public Option<Long> next() {
        if (idx < range.size()) {
            Long ret = range.get(idx);
            idx++;
            return Option.of(ret);
        }
        return Option.none();
    }
}
