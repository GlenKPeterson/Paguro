package org.organicdesign.fp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.PersistentVector;
import org.organicdesign.fp.collections.UnmodIterable;
import org.organicdesign.fp.experimental.RrbTree1;

// Instructions: http://openjdk.java.net/projects/code-tools/jmh/

public class MyBenchmark {

    public static ImList<Integer> buildList(ImList<Integer> empty, int maxIdx) {
        for (int i = 0; i < maxIdx; i++) {
            empty = empty.append(i);
        }
        return empty;
    }

    public static Integer iterateList(UnmodIterable<Integer> is) {
        Integer last = null;
        for (Integer item : is) {
            last = item;
        }
        return last;
    }

    @Benchmark public void BuildRrb100000() { buildList(RrbTree1.empty(), 100000); }
    @Benchmark public void IterateRrb100000(Rrb100000 rrb) { iterateList(rrb.rrb); }

    @State(Scope.Thread) public static class Rrb100000 {
        public ImList<Integer> rrb = buildList(RrbTree1.empty(), 100000);
    }

    @State(Scope.Thread) public static class Vec100000 {
        public ImList<Integer> vec = buildList(PersistentVector.empty(), 100000);
    }

    @Benchmark public void BuildVec100000() { buildList(PersistentVector.empty(), 100000); }
    @Benchmark public void IterateVec100000(Vec100000 vec) { iterateList(vec.vec); }

}
