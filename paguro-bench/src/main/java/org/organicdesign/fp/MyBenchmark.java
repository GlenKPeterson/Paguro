package org.organicdesign.fp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.PersistentVector;
import org.organicdesign.fp.collections.RrbTree;

import java.util.ArrayList;
import java.util.List;

// Instructions: http://openjdk.java.net/projects/code-tools/jmh/

public class MyBenchmark {

    public static List<Integer> buildList2(int maxIdx) {
        List<Integer> empty = new ArrayList<>();
        for (int i = 0; i < maxIdx; i++) {
            empty.add(i);
        }
        return empty;
    }

    public static ImList<Integer> buildList(ImList<Integer> empty, int maxIdx) {
        for (int i = 0; i < maxIdx; i++) {
            empty = empty.append(i);
        }
        return empty;
    }

    public static ImList<Integer> insertAtZeroRrb(int maxIdx) {
        RrbTree<Integer> empty = RrbTree.empty();
        for (int i = maxIdx; i >= 0; i--) {
            empty = empty.insert(0, i);
        }
        return empty;
    }

    public static List<Integer> insertAtZeroList(int maxIdx) {
        ArrayList<Integer> empty = new ArrayList<>();
        for (int i = maxIdx; i >= 0; i--) {
            empty.add(0, i);
        }
        return empty;
    }

    public static Integer iterateList(Iterable<Integer> is) {
        Integer last = null;
        for (Integer item : is) {
            last = item;
        }
        return last;
    }

    @State(Scope.Thread) public static class Rrb1 {
        public ImList<Integer> rrb = buildList(RrbTree.empty(), 1);
    }
    @State(Scope.Thread) public static class Rrb10 {
        public ImList<Integer> rrb = buildList(RrbTree.empty(), 10);
    }
    @State(Scope.Thread) public static class Rrb100 {
        public ImList<Integer> rrb = buildList(RrbTree.empty(), 100);
    }
    @State(Scope.Thread) public static class Rrb1000 {
        public ImList<Integer> rrb = buildList(RrbTree.empty(), 1000);
    }
    @State(Scope.Thread) public static class Rrb10000 {
        public ImList<Integer> rrb = buildList(RrbTree.empty(), 10000);
    }
    @State(Scope.Thread) public static class Rrb100000 {
        public ImList<Integer> rrb = buildList(RrbTree.empty(), 100000);
    }
    @State(Scope.Thread) public static class Rrb1000000 {
        public ImList<Integer> rrb = buildList(RrbTree.empty(), 1000000);
    }
    @State(Scope.Thread) public static class Rrb10000000 {
        public ImList<Integer> rrb = buildList(RrbTree.empty(), 10000000);
    }

    @State(Scope.Thread) public static class Vec1 {
        public ImList<Integer> vec = buildList(PersistentVector.empty(), 1);
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
    @State(Scope.Thread) public static class Vec10000000 {
        public ImList<Integer> vec = buildList(PersistentVector.empty(), 10000000);
    }

    @State(Scope.Thread) public static class List1 { public List<Integer> list = buildList2(1); }
    @State(Scope.Thread) public static class List10 { public List<Integer> list = buildList2(10); }
    @State(Scope.Thread) public static class List100 { public List<Integer> list = buildList2(100); }
    @State(Scope.Thread) public static class List1000 { public List<Integer> list = buildList2(1000); }
    @State(Scope.Thread) public static class List10000 { public List<Integer> list = buildList2(10000); }
    @State(Scope.Thread) public static class List100000 { public List<Integer> list = buildList2(100000); }
    @State(Scope.Thread) public static class List1000000 { public List<Integer> list = buildList2(1000000); }
    @State(Scope.Thread) public static class List10000000 { public List<Integer> list = buildList2(10000000); }


    @Benchmark public void BuildRrb1() { buildList(RrbTree.empty(), 1); }
    @Benchmark public void BuildRrb10() { buildList(RrbTree.empty(), 10); }
    @Benchmark public void BuildRrb100() { buildList(RrbTree.empty(), 100); }
    @Benchmark public void BuildRrb1000() { buildList(RrbTree.empty(), 1000); }
    @Benchmark public void BuildRrb10000() { buildList(RrbTree.empty(), 10000); }
    @Benchmark public void BuildRrb100000() { buildList(RrbTree.empty(), 100000); }
    @Benchmark public void BuildRrb1000000() { buildList(RrbTree.empty(), 1000000); }
    @Benchmark public void BuildRrb10000000() { buildList(RrbTree.empty(), 10000000); }

    @Benchmark public void BuildVec1() { buildList(PersistentVector.empty(), 1); }
    @Benchmark public void BuildVec10() { buildList(PersistentVector.empty(), 10); }
    @Benchmark public void BuildVec100() { buildList(PersistentVector.empty(), 100); }
    @Benchmark public void BuildVec1000() { buildList(PersistentVector.empty(), 1000); }
    @Benchmark public void BuildVec10000() { buildList(PersistentVector.empty(), 10000); }
    @Benchmark public void BuildVec100000() { buildList(PersistentVector.empty(), 100000); }
    @Benchmark public void BuildVec1000000() { buildList(PersistentVector.empty(), 1000000); }
    @Benchmark public void BuildVec10000000() { buildList(PersistentVector.empty(), 10000000); }

    @Benchmark public void BuildList1() { buildList2(1); }
    @Benchmark public void BuildList10() { buildList2(10); }
    @Benchmark public void BuildList100() { buildList2(100); }
    @Benchmark public void BuildList1000() { buildList2(1000); }
    @Benchmark public void BuildList10000() { buildList2(10000); }
    @Benchmark public void BuildList100000() { buildList2(100000); }
    @Benchmark public void BuildList1000000() { buildList2(1000000); }
    @Benchmark public void BuildList10000000() { buildList2(10000000); }

    @Benchmark public void InsertZeroList1() { insertAtZeroList(1); }
    @Benchmark public void InsertZeroList10() { insertAtZeroList(10); }
    @Benchmark public void InsertZeroList100() { insertAtZeroList(100); }
    @Benchmark public void InsertZeroList1000() { insertAtZeroList(1000); }
    @Benchmark public void InsertZeroList10000() { insertAtZeroList(10000); }
    @Benchmark public void InsertZeroList100000() { insertAtZeroList(100000); }
    // These take more than a second.
//    @Benchmark public void InsertZeroList1000000() { insertAtZeroList(1000000); }
//    @Benchmark public void InsertZeroList10000000() { insertAtZeroList(10000000); }

    @Benchmark public void InsertZeroRrb1() { insertAtZeroRrb(1); }
    @Benchmark public void InsertZeroRrb10() { insertAtZeroRrb(10); }
    @Benchmark public void InsertZeroRrb100() { insertAtZeroRrb(100); }
    @Benchmark public void InsertZeroRrb1000() { insertAtZeroRrb(1000); }
    @Benchmark public void InsertZeroRrb10000() { insertAtZeroRrb(10000); }
    @Benchmark public void InsertZeroRrb100000() { insertAtZeroRrb(100000); }
    @Benchmark public void InsertZeroRrb1000000() { insertAtZeroRrb(1000000); }
    // Takes more than a second.
//    @Benchmark public void InsertZeroRrb10000000() { insertAtZeroRrb(10000000); }

    @Benchmark public void IterateRrb1(Rrb1 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb10(Rrb10 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb100(Rrb100 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb1000(Rrb1000 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb10000(Rrb10000 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb100000(Rrb100000 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb1000000(Rrb1000000 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb10000000(Rrb10000000 rrb) { iterateList(rrb.rrb); }

    @Benchmark public void IterateVec1(Vec1 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec10(Vec10 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec100(Vec100 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec1000(Vec1000 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec10000(Vec10000 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec100000(Vec100000 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec1000000(Vec1000000 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec10000000(Vec10000000 vec) { iterateList(vec.vec); }

    @Benchmark public void IterateList1(List1 list) { iterateList(list.list); }
    @Benchmark public void IterateList10(List10 list) { iterateList(list.list); }
    @Benchmark public void IterateList100(List100 list) { iterateList(list.list); }
    @Benchmark public void IterateList1000(List1000 list) { iterateList(list.list); }
    @Benchmark public void IterateList10000(List10000 list) { iterateList(list.list); }
    @Benchmark public void IterateList100000(List100000 list) { iterateList(list.list); }
    @Benchmark public void IterateList1000000(List1000000 list) { iterateList(list.list); }
    @Benchmark public void IterateList10000000(List10000000 list) { iterateList(list.list); }

}
