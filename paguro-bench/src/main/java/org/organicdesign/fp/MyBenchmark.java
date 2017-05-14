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

    @State(Scope.Thread) public static class Rrb10 {
        public ImList<Integer> rrb = buildList(RrbTree1.empty(), 10);
    }
    @State(Scope.Thread) public static class Rrb100 {
        public ImList<Integer> rrb = buildList(RrbTree1.empty(), 100);
    }
    @State(Scope.Thread) public static class Rrb1000 {
        public ImList<Integer> rrb = buildList(RrbTree1.empty(), 1000);
    }
    @State(Scope.Thread) public static class Rrb10000 {
        public ImList<Integer> rrb = buildList(RrbTree1.empty(), 10000);
    }
    @State(Scope.Thread) public static class Rrb100000 {
        public ImList<Integer> rrb = buildList(RrbTree1.empty(), 100000);
    }
    @State(Scope.Thread) public static class Rrb1000000 {
        public ImList<Integer> rrb = buildList(RrbTree1.empty(), 1000000);
    }

    @State(Scope.Thread) public static class Vec10 {
        public ImList<Integer> vec = buildList(PersistentVector.empty(), 10);
    }
    @State(Scope.Thread) public static class Vec100 {
        public ImList<Integer> vec = buildList(PersistentVector.empty(), 100);
    }
    @State(Scope.Thread) public static class Vec1000 {
        public ImList<Integer> vec = buildList(PersistentVector.empty(), 1000);
    }
    @State(Scope.Thread) public static class Vec10000 {
        public ImList<Integer> vec = buildList(PersistentVector.empty(), 10000);
    }
    @State(Scope.Thread) public static class Vec100000 {
        public ImList<Integer> vec = buildList(PersistentVector.empty(), 100000);
    }
    @State(Scope.Thread) public static class Vec1000000 {
        public ImList<Integer> vec = buildList(PersistentVector.empty(), 1000000);
    }

    @Benchmark public void BuildRrb10() { buildList(RrbTree1.empty(), 10); }
    @Benchmark public void BuildRrb100() { buildList(RrbTree1.empty(), 100); }
    @Benchmark public void BuildRrb1000() { buildList(RrbTree1.empty(), 1000); }
    @Benchmark public void BuildRrb10000() { buildList(RrbTree1.empty(), 10000); }
    @Benchmark public void BuildRrb100000() { buildList(RrbTree1.empty(), 100000); }
    @Benchmark public void BuildRrb1000000() { buildList(RrbTree1.empty(), 1000000); }

    @Benchmark public void BuildVec10() { buildList(PersistentVector.empty(), 10); }
    @Benchmark public void BuildVec100() { buildList(PersistentVector.empty(), 100); }
    @Benchmark public void BuildVec1000() { buildList(PersistentVector.empty(), 1000); }
    @Benchmark public void BuildVec10000() { buildList(PersistentVector.empty(), 10000); }
    @Benchmark public void BuildVec100000() { buildList(PersistentVector.empty(), 100000); }
    @Benchmark public void BuildVec1000000() { buildList(PersistentVector.empty(), 1000000); }

    @Benchmark public void IterateRrb10(Rrb10 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb100(Rrb100 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb1000(Rrb1000 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb10000(Rrb10000 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb100000(Rrb100000 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb1000000(Rrb1000000 rrb) { iterateList(rrb.rrb); }

    @Benchmark public void IterateVec10(Vec10 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec100(Vec100 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec1000(Vec1000 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec10000(Vec10000 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec100000(Vec100000 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec1000000(Vec1000000 vec) { iterateList(vec.vec); }

}
