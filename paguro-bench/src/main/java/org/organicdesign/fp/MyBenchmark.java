package org.organicdesign.fp;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.organicdesign.fp.collections.BaseList;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.PersistentVector;
import org.organicdesign.fp.collections.RrbTree;
import org.organicdesign.fp.collections.RrbTree.ImRrbt;
import org.organicdesign.fp.collections.RrbTree.MutableRrbt;
import scala.collection.immutable.Vector$;
import scala.collection.immutable.VectorIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.organicdesign.fp.collections.RrbTree.empty;
import static org.organicdesign.fp.collections.RrbTree.emptyMutable;

// Instructions: http://openjdk.java.net/projects/code-tools/jmh/

@SuppressWarnings("WeakerAccess")
public class MyBenchmark {

    static List<Integer> buildList2(int maxIdx) {
        List<Integer> empty = new ArrayList<>();
        for (int i = 0; i < maxIdx; i++) {
            empty.add(i);
        }
        return empty;
    }

    static scala.collection.immutable.Vector<Integer> buildScala(int maxIdx) {
        scala.collection.immutable.Vector<Integer> empty = Vector$.MODULE$.empty();
        for (int i = 0; i < maxIdx; i++) {
            empty = empty.appendBack(i);
        }
        return empty;
    }

    @SuppressWarnings("unchecked")
    static <T extends BaseList<Integer>> T buildList(T empty, int maxIdx) {
        for (int i = 0; i < maxIdx; i++) {
            empty = (T) empty.append(i);
        }
        return empty;
    }

    static ImRrbt<Integer> insertAtZeroRrb(int maxIdx) {
        ImRrbt<Integer> empty = empty();
        for (int i = maxIdx; i >= 0; i--) {
            empty = empty.insert(0, i);
        }
        return empty;
    }

    public static MutableRrbt<Integer> insertAtZeroRrbMut(int maxIdx) {
        MutableRrbt<Integer> empty = RrbTree.emptyMutable();
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

    public static scala.collection.immutable.Vector<Integer> insertAtZeroScala(int maxIdx) {
        scala.collection.immutable.Vector<Integer> empty = Vector$.MODULE$.empty();
        for (int i = maxIdx; i >= 0; i--) {
            empty = empty.appendFront(i);
        }
        return empty;
    }

    public static RrbTree<Integer> randomInsertRrb(RrbTree empty, int maxIdx) {
        Random rnd = new Random();
        for (int i = 0; i < maxIdx; i++) {
            empty = empty.insert(i > 1 ? rnd.nextInt(i) : 0, i);
        }
        return empty;
    }

    public static List<Integer> randomInsertList(int maxIdx) {
        Random rnd = new Random();
        List<Integer> empty = new ArrayList<>();
        for (int i = 0; i < maxIdx; i++) {
            empty.add(i > 1 ? rnd.nextInt(i) : 0, i);
        }
        return empty;
    }

    // Is this supported?
//    public static scala.collection.immutable.Vector<Integer> randomInsertScala(int maxIdx) {
//        Random rnd = new Random();
//        scala.collection.immutable.Vector<Integer> empty = Vector$.MODULE$.empty();
//        for (int i = 0; i < maxIdx; i++) {
    // This is a replace, not an insert!
//            empty.updateAt(i > 1 ? rnd.nextInt(i) : 0, i);
//        }
//        return empty;
//    }

    static Integer iterateList(Iterable<Integer> is) {
        Integer last = null;
        for (Integer item : is) {
            last = item;
        }
        return last;
    }

    static Integer iterateScala(scala.collection.immutable.Vector<Integer> is) {
        Integer last = null;
        VectorIterator<Integer> iter = is.iterator();
        while (iter.hasNext()) {
            last = iter.next();
        }
        return last;
    }

    static Integer getEach(List<Integer> is) {
        Integer last = null;
        int size = is.size();
        for (int i = size - 1; i >= 0; i--) {
            last = is.get(i);
        }
        return last;
    }

    static Integer getEachScala(scala.collection.immutable.Vector<Integer> is) {
        Integer last = null;
        int size = is.size();
        for (int i = size - 1; i >= 0; i--) {
            last = is.apply(i);
        }
        return last;
    }

    @State(Scope.Thread) public static class Rrb1 {
        ImList<Integer> rrb = buildList(empty(), 1);
    }
    @State(Scope.Thread) public static class Rrb10 {
        ImList<Integer> rrb = buildList(empty(), 10);
    }
    @State(Scope.Thread) public static class Rrb100 {
        ImList<Integer> rrb = buildList(empty(), 100);
    }
    @State(Scope.Thread) public static class Rrb1000 {
        ImList<Integer> rrb = buildList(empty(), 1000);
    }
    @State(Scope.Thread) public static class Rrb10000 {
        ImList<Integer> rrb = buildList(empty(), 10000);
    }
    @State(Scope.Thread) public static class Rrb100000 {
        ImList<Integer> rrb = buildList(empty(), 100000);
    }
    @State(Scope.Thread) public static class Rrb1000000 {
        ImList<Integer> rrb = buildList(empty(), 1000000);
    }
    @State(Scope.Thread) public static class Rrb10000000 {
        ImList<Integer> rrb = buildList(empty(), 10000000);
    }
    @State(Scope.Thread) public static class Rrb100000000 {
        ImList<Integer> rrb = buildList(empty(), 100000000);
    }

    @State(Scope.Thread) public static class Scala1 {
        scala.collection.immutable.Vector<Integer> scala = buildScala(1);
    }
    @State(Scope.Thread) public static class Scala10 {
        scala.collection.immutable.Vector<Integer> scala = buildScala(10);
    }
    @State(Scope.Thread) public static class Scala100 {
        scala.collection.immutable.Vector<Integer> scala = buildScala(100);
    }
    @State(Scope.Thread) public static class Scala1000 {
        scala.collection.immutable.Vector<Integer> scala = buildScala(1000);
    }
    @State(Scope.Thread) public static class Scala10000 {
        scala.collection.immutable.Vector<Integer> scala = buildScala(10000);
    }
    @State(Scope.Thread) public static class Scala100000 {
        scala.collection.immutable.Vector<Integer> scala = buildScala(100000);
    }
    @State(Scope.Thread) public static class Scala1000000 {
        scala.collection.immutable.Vector<Integer> scala = buildScala(1000000);
    }
    @State(Scope.Thread) public static class Scala10000000 {
        scala.collection.immutable.Vector<Integer> scala = buildScala(10000000);
    }
    @State(Scope.Thread) public static class Scala100000000 {
        scala.collection.immutable.Vector<Integer> scala = buildScala(100000000);
    }

    @State(Scope.Thread) public static class RrbRel1 {
        RrbTree<Integer> rrb = randomInsertRrb(empty(), 1);
    }
    @State(Scope.Thread) public static class RrbRel10 {
        RrbTree<Integer> rrb = randomInsertRrb(empty(), 10);
    }
    @State(Scope.Thread) public static class RrbRel100 {
        RrbTree<Integer> rrb = randomInsertRrb(empty(), 100);
    }
    @State(Scope.Thread) public static class RrbRel1000 {
        RrbTree<Integer> rrb = randomInsertRrb(empty(), 1000);
    }
    @State(Scope.Thread) public static class RrbRel10000 {
        RrbTree<Integer> rrb = randomInsertRrb(empty(), 10000);
    }
    @State(Scope.Thread) public static class RrbRel100000 {
        RrbTree<Integer> rrb = randomInsertRrb(empty(), 100000);
    }
    @State(Scope.Thread) public static class RrbRel1000000 {
        RrbTree<Integer> rrb = randomInsertRrb(empty(), 1000000);
    }
    @State(Scope.Thread) public static class RrbRel10000000 {
        RrbTree<Integer> rrb = randomInsertRrb(empty(), 10000000);
    }

    @State(Scope.Thread) public static class Vec1 {
        ImList<Integer> vec = buildList(PersistentVector.empty(), 1);
    }
    @State(Scope.Thread) public static class Vec10 {
        ImList<Integer> vec = buildList(PersistentVector.empty(), 10);
    }
    @State(Scope.Thread) public static class Vec100 {
        ImList<Integer> vec = buildList(PersistentVector.empty(), 100);
    }
    @State(Scope.Thread) public static class Vec1000 {
        ImList<Integer> vec = buildList(PersistentVector.empty(), 1000);
    }
    @State(Scope.Thread) public static class Vec10000 {
        ImList<Integer> vec = buildList(PersistentVector.empty(), 10000);
    }
    @State(Scope.Thread) public static class Vec100000 {
        ImList<Integer> vec = buildList(PersistentVector.empty(), 100000);
    }
    @State(Scope.Thread) public static class Vec1000000 {
        ImList<Integer> vec = buildList(PersistentVector.empty(), 1000000);
    }
    @State(Scope.Thread) public static class Vec10000000 {
        ImList<Integer> vec = buildList(PersistentVector.empty(), 10000000);
    }
    @State(Scope.Thread) public static class Vec100000000 {
        ImList<Integer> vec = buildList(PersistentVector.empty(), 100000000);
    }

    @State(Scope.Thread) public static class List1 { public List<Integer> list = buildList2(1); }
    @State(Scope.Thread) public static class List10 { public List<Integer> list = buildList2(10); }
    @State(Scope.Thread) public static class List100 { public List<Integer> list = buildList2(100); }
    @State(Scope.Thread) public static class List1000 { public List<Integer> list = buildList2(1000); }
    @State(Scope.Thread) public static class List10000 { public List<Integer> list = buildList2(10000); }
    @State(Scope.Thread) public static class List100000 { public List<Integer> list = buildList2(100000); }
    @State(Scope.Thread) public static class List1000000 { public List<Integer> list = buildList2(1000000); }
    @State(Scope.Thread) public static class List10000000 { public List<Integer> list = buildList2(10000000); }
    @State(Scope.Thread) public static class List100000000 { public List<Integer> list = buildList2(100000000); }

    // ===================================================== Tests =====================================================

    @Benchmark public void BuildList1() { buildList2(1); }
    @Benchmark public void BuildList10() { buildList2(10); }
    @Benchmark public void BuildList100() { buildList2(100); }
    @Benchmark public void BuildList1000() { buildList2(1000); }
    @Benchmark public void BuildList10000() { buildList2(10000); }
    @Benchmark public void BuildList100000() { buildList2(100000); }
    @Benchmark public void BuildList1000000() { buildList2(1000000); }
    @Benchmark public void BuildList10000000() { buildList2(10000000); }
    @Benchmark public void BuildList100000000() { buildList2(100000000); }

    @Benchmark public void BuildRrb1() { buildList(empty(), 1); }
    @Benchmark public void BuildRrb10() { buildList(empty(), 10); }
    @Benchmark public void BuildRrb100() { buildList(empty(), 100); }
    @Benchmark public void BuildRrb1000() { buildList(empty(), 1000); }
    @Benchmark public void BuildRrb10000() { buildList(empty(), 10000); }
    @Benchmark public void BuildRrb100000() { buildList(empty(), 100000); }
    @Benchmark public void BuildRrb1000000() { buildList(empty(), 1000000); }
    @Benchmark public void BuildRrb10000000() { buildList(empty(), 10000000); }

    @Benchmark public void BuildRrbMut1() { buildList(RrbTree.emptyMutable(), 1); }
    @Benchmark public void BuildRrbMut10() { buildList(RrbTree.emptyMutable(), 10); }
    @Benchmark public void BuildRrbMut100() { buildList(RrbTree.emptyMutable(), 100); }
    @Benchmark public void BuildRrbMut1000() { buildList(RrbTree.emptyMutable(), 1000); }
    @Benchmark public void BuildRrbMut10000() { buildList(RrbTree.emptyMutable(), 10000); }
    @Benchmark public void BuildRrbMut100000() { buildList(RrbTree.emptyMutable(), 100000); }
    @Benchmark public void BuildRrbMut1000000() { buildList(RrbTree.emptyMutable(), 1000000); }
    @Benchmark public void BuildRrbMut10000000() { buildList(RrbTree.emptyMutable(), 10000000); }
    @Benchmark public void BuildRrbMut100000000() { buildList(RrbTree.emptyMutable(), 100000000); }

    @Benchmark public void BuildVec1() { buildList(PersistentVector.empty(), 1); }
    @Benchmark public void BuildVec10() { buildList(PersistentVector.empty(), 10); }
    @Benchmark public void BuildVec100() { buildList(PersistentVector.empty(), 100); }
    @Benchmark public void BuildVec1000() { buildList(PersistentVector.empty(), 1000); }
    @Benchmark public void BuildVec10000() { buildList(PersistentVector.empty(), 10000); }
    @Benchmark public void BuildVec100000() { buildList(PersistentVector.empty(), 100000); }
    @Benchmark public void BuildVec1000000() { buildList(PersistentVector.empty(), 1000000); }
    @Benchmark public void BuildVec10000000() { buildList(PersistentVector.empty(), 10000000); }

    @Benchmark public void BuildVecMut1() { buildList(PersistentVector.emptyMutable(), 1); }
    @Benchmark public void BuildVecMut10() { buildList(PersistentVector.emptyMutable(), 10); }
    @Benchmark public void BuildVecMut100() { buildList(PersistentVector.emptyMutable(), 100); }
    @Benchmark public void BuildVecMut1000() { buildList(PersistentVector.emptyMutable(), 1000); }
    @Benchmark public void BuildVecMut10000() { buildList(PersistentVector.emptyMutable(), 10000); }
    @Benchmark public void BuildVecMut100000() { buildList(PersistentVector.emptyMutable(), 100000); }
    @Benchmark public void BuildVecMut1000000() { buildList(PersistentVector.emptyMutable(), 1000000); }
    @Benchmark public void BuildVecMut10000000() { buildList(PersistentVector.emptyMutable(), 10000000); }
    @Benchmark public void BuildVecMut100000000() { buildList(PersistentVector.emptyMutable(), 100000000); }

    @Benchmark public void BuildScala1() { buildScala(1); }
    @Benchmark public void BuildScala10() { buildScala(10); }
    @Benchmark public void BuildScala100() { buildScala(100); }
    @Benchmark public void BuildScala1000() { buildScala(1000); }
    @Benchmark public void BuildScala10000() { buildScala(10000); }
    @Benchmark public void BuildScala100000() { buildScala(100000); }
    @Benchmark public void BuildScala1000000() { buildScala(1000000); }
    @Benchmark public void BuildScala10000000() { buildScala(10000000); }

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
    @Benchmark public void InsertZeroRrb10000000() { insertAtZeroRrb(10000000); }

    @Benchmark public void InsertZeroRrbMut1() { insertAtZeroRrbMut(1); }
    @Benchmark public void InsertZeroRrbMut10() { insertAtZeroRrbMut(10); }
    @Benchmark public void InsertZeroRrbMut100() { insertAtZeroRrbMut(100); }
    @Benchmark public void InsertZeroRrbMut1000() { insertAtZeroRrbMut(1000); }
    @Benchmark public void InsertZeroRrbMut10000() { insertAtZeroRrbMut(10000); }
    @Benchmark public void InsertZeroRrbMut100000() { insertAtZeroRrbMut(100000); }
    @Benchmark public void InsertZeroRrbMut1000000() { insertAtZeroRrbMut(1000000); }
    @Benchmark public void InsertZeroRrbMut10000000() { insertAtZeroRrbMut(10000000); }

    @Benchmark public void InsertZeroScala1() { insertAtZeroScala(1); }
    @Benchmark public void InsertZeroScala10() { insertAtZeroScala(10); }
    @Benchmark public void InsertZeroScala100() { insertAtZeroScala(100); }
    @Benchmark public void InsertZeroScala1000() { insertAtZeroScala(1000); }
    @Benchmark public void InsertZeroScala10000() { insertAtZeroScala(10000); }
    @Benchmark public void InsertZeroScala100000() { insertAtZeroScala(100000); }
    @Benchmark public void InsertZeroScala1000000() { insertAtZeroScala(1000000); }
    @Benchmark public void InsertZeroScala10000000() { insertAtZeroScala(10000000); }


    @Benchmark public void IterateList1(List1 list) { iterateList(list.list); }
    @Benchmark public void IterateList10(List10 list) { iterateList(list.list); }
    @Benchmark public void IterateList100(List100 list) { iterateList(list.list); }
    @Benchmark public void IterateList1000(List1000 list) { iterateList(list.list); }
    @Benchmark public void IterateList10000(List10000 list) { iterateList(list.list); }
    @Benchmark public void IterateList100000(List100000 list) { iterateList(list.list); }
    @Benchmark public void IterateList1000000(List1000000 list) { iterateList(list.list); }
    @Benchmark public void IterateList10000000(List10000000 list) { iterateList(list.list); }

    @Benchmark public void IterateRrb1(Rrb1 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb10(Rrb10 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb100(Rrb100 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb1000(Rrb1000 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb10000(Rrb10000 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb100000(Rrb100000 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb1000000(Rrb1000000 rrb) { iterateList(rrb.rrb); }
    @Benchmark public void IterateRrb10000000(Rrb10000000 rrb) { iterateList(rrb.rrb); }

    @Benchmark public void IterateScala1(Scala1 scala) { iterateScala(scala.scala); }
    @Benchmark public void IterateScala10(Scala10 scala) { iterateScala(scala.scala); }
    @Benchmark public void IterateScala100(Scala100 scala) { iterateScala(scala.scala); }
    @Benchmark public void IterateScala1000(Scala1000 scala) { iterateScala(scala.scala); }
    @Benchmark public void IterateScala10000(Scala10000 scala) { iterateScala(scala.scala); }
    @Benchmark public void IterateScala100000(Scala100000 scala) { iterateScala(scala.scala); }
    @Benchmark public void IterateScala1000000(Scala1000000 scala) { iterateScala(scala.scala); }
    @Benchmark public void IterateScala10000000(Scala10000000 scala) { iterateScala(scala.scala); }

    @Benchmark public void IterateVec1(Vec1 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec10(Vec10 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec100(Vec100 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec1000(Vec1000 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec10000(Vec10000 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec100000(Vec100000 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec1000000(Vec1000000 vec) { iterateList(vec.vec); }
    @Benchmark public void IterateVec10000000(Vec10000000 vec) { iterateList(vec.vec); }

    @Benchmark public void GetEachList1(List1 list) { getEach(list.list); }
    @Benchmark public void GetEachList10(List10 list) { getEach(list.list); }
    @Benchmark public void GetEachList100(List100 list) { getEach(list.list); }
    @Benchmark public void GetEachList1000(List1000 list) { getEach(list.list); }
    @Benchmark public void GetEachList10000(List10000 list) { getEach(list.list); }
    @Benchmark public void GetEachList100000(List100000 list) { getEach(list.list); }
    @Benchmark public void GetEachList1000000(List1000000 list) { getEach(list.list); }
    @Benchmark public void GetEachList10000000(List10000000 list) { getEach(list.list); }
    @Benchmark public void GetEachList100000000(List100000000 list) { getEach(list.list); }

    @Benchmark public void GetEachRrb1(Rrb1 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrb10(Rrb10 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrb100(Rrb100 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrb1000(Rrb1000 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrb10000(Rrb10000 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrb100000(Rrb100000 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrb1000000(Rrb1000000 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrb10000000(Rrb10000000 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrb100000000(Rrb100000000 rrb) { getEach(rrb.rrb); }

    @Benchmark public void GetEachRrbRel1(RrbRel1 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrbRel10(RrbRel10 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrbRel100(RrbRel100 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrbRel1000(RrbRel1000 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrbRel10000(RrbRel10000 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrbRel100000(RrbRel100000 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrbRel1000000(RrbRel1000000 rrb) { getEach(rrb.rrb); }
    @Benchmark public void GetEachRrbRel10000000(RrbRel10000000 rrb) { getEach(rrb.rrb); }

    @Benchmark public void GetEachScala1(Scala1 scala) { getEachScala(scala.scala); }
    @Benchmark public void GetEachScala10(Scala10 scala) { getEachScala(scala.scala); }
    @Benchmark public void GetEachScala100(Scala100 scala) { getEachScala(scala.scala); }
    @Benchmark public void GetEachScala1000(Scala1000 scala) { getEachScala(scala.scala); }
    @Benchmark public void GetEachScala10000(Scala10000 scala) { getEachScala(scala.scala); }
    @Benchmark public void GetEachScala100000(Scala100000 scala) { getEachScala(scala.scala); }
    @Benchmark public void GetEachScala1000000(Scala1000000 scala) { getEachScala(scala.scala); }
    @Benchmark public void GetEachScala10000000(Scala10000000 scala) { getEachScala(scala.scala); }
    @Benchmark public void GetEachScala100000000(Scala100000000 scala) { getEachScala(scala.scala); }

    @Benchmark public void GetEachVec1(Vec1 vec) { getEach(vec.vec); }
    @Benchmark public void GetEachVec10(Vec10 vec) { getEach(vec.vec); }
    @Benchmark public void GetEachVec100(Vec100 vec) { getEach(vec.vec); }
    @Benchmark public void GetEachVec1000(Vec1000 vec) { getEach(vec.vec); }
    @Benchmark public void GetEachVec10000(Vec10000 vec) { getEach(vec.vec); }
    @Benchmark public void GetEachVec100000(Vec100000 vec) { getEach(vec.vec); }
    @Benchmark public void GetEachVec1000000(Vec1000000 vec) { getEach(vec.vec); }
    @Benchmark public void GetEachVec10000000(Vec10000000 vec) { getEach(vec.vec); }
    @Benchmark public void GetEachVec100000000(Vec100000000 vec) { getEach(vec.vec); }

    @Benchmark public void RandInsertList1() { randomInsertList(1); }
    @Benchmark public void RandInsertList10() { randomInsertList(10); }
    @Benchmark public void RandInsertList100() { randomInsertList(100); }
    @Benchmark public void RandInsertList1000() { randomInsertList(1000); }
    @Benchmark public void RandInsertList10000() { randomInsertList(10000); }
    @Benchmark public void RandInsertList100000() { randomInsertList(100000); }

    @Benchmark public void RandInsertRrb1() { randomInsertRrb(empty(), 1); }
    @Benchmark public void RandInsertRrb10() { randomInsertRrb(empty(), 10); }
    @Benchmark public void RandInsertRrb100() { randomInsertRrb(empty(), 100); }
    @Benchmark public void RandInsertRrb1000() { randomInsertRrb(empty(), 1000); }
    @Benchmark public void RandInsertRrb10000() { randomInsertRrb(empty(), 10000); }
    @Benchmark public void RandInsertRrb100000() { randomInsertRrb(empty(), 100000); }
    @Benchmark public void RandInsertRrb1000000() { randomInsertRrb(empty(), 1000000); }

    @Benchmark public void RandInsertRrbMut1() { randomInsertRrb(emptyMutable(), 1); }
    @Benchmark public void RandInsertRrbMut10() { randomInsertRrb(emptyMutable(), 10); }
    @Benchmark public void RandInsertRrbMut100() { randomInsertRrb(emptyMutable(), 100); }
    @Benchmark public void RandInsertRrbMut1000() { randomInsertRrb(emptyMutable(), 1000); }
    @Benchmark public void RandInsertRrbMut10000() { randomInsertRrb(emptyMutable(), 10000); }
    @Benchmark public void RandInsertRrbMut100000() { randomInsertRrb(emptyMutable(), 100000); }
    @Benchmark public void RandInsertRrbMut1000000() { randomInsertRrb(emptyMutable(), 1000000); }

    // Don't think it supports random inserts!
//    @Benchmark public void RandInsertScala1() { randomInsertScala(1); }
//    @Benchmark public void RandInsertScala10() { randomInsertScala(10); }
//    @Benchmark public void RandInsertScala100() { randomInsertScala(100); }
//    @Benchmark public void RandInsertScala1000() { randomInsertScala(1000); }
//    @Benchmark public void RandInsertScala10000() { randomInsertScala(10000); }
//    @Benchmark public void RandInsertScala100000() { randomInsertScala(100000); }
//    @Benchmark public void RandInsertScala1000000() { randomInsertScala(1000000); }
//    @Benchmark public void RandInsertScala10000000() { randomInsertScala(10000000); }
}
