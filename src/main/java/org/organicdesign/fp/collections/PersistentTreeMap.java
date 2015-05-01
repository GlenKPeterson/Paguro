/**
 Copyright (c) Rich Hickey. All rights reserved. The use and distribution terms for this software are covered by the
 Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can be found in the file epl-v10.html
 at the root of this distribution. By using this software in any fashion, you are agreeing to be bound by the terms of
 this license. You must not remove this notice, or any other, from this software.
 */

/* rich May 20, 2006 */
package org.organicdesign.fp.collections;

import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Stack;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.function.Function0;
import org.organicdesign.fp.function.Function2;
import org.organicdesign.fp.permanent.Sequence;

/**
 Persistent Red Black Tree. Note that instances of this class are constant values
 i.e. add/remove etc return new values.

 See Okasaki, Kahrs, Larsen et al

 @author Rich Hickey (Primary author)
 @author Glen Peterson (Java-centric editor)
 */

public class PersistentTreeMap<K,V> implements ImMapSorted<K,V> {

    private final Comparator<K> comp;
    private final Node<K,V> tree;
    private final int size;

    final static public PersistentTreeMap EMPTY = new PersistentTreeMap<>(Function2.defaultComparator(), null, 0);

    @SuppressWarnings("unchecked")
    public static <K, V> PersistentTreeMap<K,V> empty() {
        return (PersistentTreeMap<K,V>) EMPTY;
    }

    private PersistentTreeMap(Comparator<K> c, Node<K,V> t, int n) {
        comp = c; tree = t; size = n;
    }

    public static <K extends Comparable<K>,V> PersistentTreeMap<K,V> of(K key, V val) {
        return new PersistentTreeMap<K,V>(Function2.defaultComparator(), null, 0).assoc(key, val);
    }

    public static <K,V> PersistentTreeMap<K,V> of(K key, V val, Comparator<K> c) {
        return new PersistentTreeMap<K,V>(c, null, 0).assoc(key, val);
    }

    public static <K,V> PersistentTreeMap<K,V> of(Comparator<K> c) {
        return new PersistentTreeMap<>(c, null, 0);
    }

    /**
     Returns a view of the mappings contained in this map.  The set should actually contain UnMap.Entry items, but that
     return signature is illegal in Java, so you'll just have to remember.
     */
    @Override public ImSet<Entry<K,V>> entrySet() {
        // This is the pretty way to do it.
//        return this.foldLeft(ImSet.empty(), (accum, entry) -> accum.put(entry));

        // This may be faster, but I haven't timed it.

        // Preserve comparator!
        ImSet<Entry<K,V>> ret = PersistentTreeSet.ofComp((a, b) -> comp.compare(a.getKey(), b.getKey()));

        UnIterator<UnEntry<K,V>> iter = this.iterator();
        while (iter.hasNext()) { ret = ret.put(iter.next()); }
        return ret;
    }

    /** This is correct, but O(n). */
    @Override public int hashCode() { return (size() == 0) ? 0 : UnIterable.hashCode(keySet()); }

    /** This is correct, but definitely O(n), same as java.util.ArrayList. */
    @Override public boolean equals(Object other) {
        return (other != null) &&
                (other instanceof ImMapSorted) &&
                (this.size() == ((ImMapSorted) other).size()) &&
                UnIterable.equals(this, (ImMapSorted) other);
    }

    /** Returns a view of the keys contained in this map. */
    @Override public ImSet<K> keySet() { return PersistentTreeSet.ofMap(this); }

    /** {@inheritDoc} */
    @Override public ImMapSorted<K,V> subMap(K fromKey, K toKey) {
        int diff = comp.compare(fromKey, toKey);

        if (diff > 0) {
            throw new IllegalArgumentException("fromKey is greater than toKey");
        }
        Node<K,V> last = last();
        K lastKey = last.key();
        int compFromKeyLastKey = comp.compare(fromKey, lastKey);

        // If no intersect, return empty. We aren't checking the toKey vs. the firstKey() because that's a single pass
        // through the iterator loop which is probably as cheap as checking here.
        if ( (diff == 0) || (compFromKeyLastKey > 0) )  {
            return empty();
        }
        // If map is entirely contained, just return it.
        if ( (comp.compare(fromKey, firstKey()) <= 0) &&
                    (comp.compare(toKey, lastKey) > 0) ) {
            return this;
        }
        // Don't iterate through entire map for only the last item.
        if (compFromKeyLastKey == 0) {
            return of(last.getKey(), last.getValue(), comp);
        }

        ImMapSorted<K,V> ret = new PersistentTreeMap<>(comp, null, 0);
        UnIterator<UnEntry<K,V>> iter = this.iterator();
        while (iter.hasNext()) {
            UnEntry<K,V> next = iter.next();
            K key = next.getKey();
            if (comp.compare(toKey, key) <= 0) {
                break;
            }
            if (comp.compare(fromKey, key) > 0) {
                continue;
            }
            ret = ret.assoc(key, next.getValue());
        }
        return ret;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        StringBuilder sB = new StringBuilder("PersistentTreeMap(");
        int i = 0;
        for (UnEntry<K,V> entry : this) {
            if (i > 0) { sB.append(","); }
            if (i > 4) { break; }
            sB.append("UnEntry(").append(entry.getKey()).append(",").append(entry.getValue()).append(")");
            i++;
        }
        if (i < size()) {
            sB.append("...");
        }
        return sB.append(")").toString();
    }

    /** {@inheritDoc} */
    @Override public UnCollection<V> values() {
        class ValueColl<B,Z> implements UnCollection<B> {
            private final Function0<UnIterator<UnEntry<Z,B>>> iterFactory;
            private ValueColl(Function0<UnIterator<UnEntry<Z, B>>> f) { iterFactory = f; }

            @Override public int size() { return size; }

            @Override public UnIterator<B> iterator() {
                final UnIterator<UnMap.UnEntry<Z,B>> iter = iterFactory.apply();
                return new UnIterator<B>() {
                    @Override public boolean hasNext() { return iter.hasNext(); }
                    @Override public B next() { return iter.next().getValue(); }
                };
            }
            @Override public int hashCode() { return UnIterable.hashCode(this); }
            @Override public boolean equals(Object o) {
                if (this == o) { return true; }
                if ( (o == null) || !(o instanceof Iterable) ) { return false; }
                return UnIterable.equals(this, (Iterable) o);
            }
            @Override public String toString() { return UnIterable.toString("ValueWrapper", this); }
        }
        return new ValueColl<>(() -> this.iterator());
    }

    @Override public Option<UnEntry<K,V>> head() {
        Node<K,V> t = tree;
        if (t != null) {
            while (t.left() != null) {
                t = t.left();
            }
        }
        return Option.of(t);
    }

    /** {@inheritDoc} */
    @Override public ImMapSorted<K,V> tailMap(K fromKey) {
        Node<K,V> last = last();
        K lastKey = last.key();
        int compFromKeyLastKey = comp.compare(fromKey, lastKey);

        // If no intersect, return empty. We aren't checking the toKey vs. the firstKey() because that's a single pass
        // through the iterator loop which is probably as cheap as checking here.
        if (compFromKeyLastKey > 0) {
            return empty();
        }
        // If map is entirely contained, just return it.
        if (comp.compare(fromKey, firstKey()) <= 0) {
            return this;
        }
        // Don't iterate through entire map for only the last item.
        if (compFromKeyLastKey == 0) {
            return of(last.getKey(), last.getValue(), comp);
        }

        ImMapSorted<K,V> ret = empty();
        UnIterator<UnEntry<K,V>> iter = this.iterator();
        while (iter.hasNext()) {
            UnEntry<K,V> next = iter.next();
            K key = next.getKey();
            if (comp.compare(fromKey, key) > 0) {
                continue;
            }
            ret = ret.assoc(key, next.getValue());
        }
        return ret;
    }


    @Override public Sequence<UnEntry<K,V>> tail() {
        if (size() > 1) {
            return without(firstKey());
//            // The iterator is designed to do this quickly.  It also prevents an infinite loop here.
//            UnIterator<UnEntry<K,V>> iter = this.iterator();
//            // Drop the head
//            iter.next();
//            return tailMap(iter.next().getKey());
        }
        return Sequence.emptySequence();
    }

    // TODO: Replace with Mutable.Ref, or make methods return Tuple2.
    private class Box<E> {
        public E val;
        public Box(E val) { this.val = val; }
    }

//    @SuppressWarnings("unchecked")
//    static public <S, K extends S, V extends S> PersistentTreeMap<K,V> create(ISeq<S> items) {
//        PersistentTreeMap<K,V> ret = empty();
//        for (; items != null; items = items.next().next()) {
//            if (items.next() == null)
//                throw new IllegalArgumentException(String.format("No value supplied for key: %s", items.head()));
//            ret = ret.assoc((K) items.head(), (V) RT.second(items));
//        }
//        return ret;
//    }

//    @SuppressWarnings("unchecked")
//    static public <S, K extends S, V extends S>
//    PersistentTreeMap<K,V> create(Comparator<K> comp, ISeq<S> items) {
//        PersistentTreeMap<K,V> ret = new PersistentTreeMap<>(comp);
//        for (; items != null; items = items.next().next()) {
//            if (items.next() == null)
//                throw new IllegalArgumentException(String.format("No value supplied for key: %s", items.head()));
//            ret = ret.assoc((K) items.head(), (V) RT.second(items));
//        }
//        return ret;
//    }

    @Override public Comparator<? super K> comparator() { return comp; }

    @SuppressWarnings("unchecked")
    @Override public boolean containsKey(Object key) {
        return entryAt((K) key) != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        if (key == null) { return null; }
        Entry<K,V> entry = entryAt((K) key);
        if (entry == null) { return null; }
        return entry.getValue();
    }

// public PersistentTreeMap<K,V> assocEx(K key, V val) {
// Inherits default implementation of assocEx from IPersistentMap

    @Override
    public PersistentTreeMap<K,V> assoc(K key, V val) {
        Box<Node<K,V>> found = new Box<>(null);
        Node<K,V> t = add(tree, key, val, found);
        //null == already contains key
        if (t == null) {
            Node<K,V> foundNode = found.val;

            //note only get same collection on identity of val, not equals()
            if (foundNode.val() == val) {
                return this;
            }
            return new PersistentTreeMap<>(comp, replace(tree, key, val), size);
        }
        return new PersistentTreeMap<>(comp, t.blacken(), size + 1);
    }

    @Override public PersistentTreeMap<K,V> without(K key) {
        Box<Node<K,V>> found = new Box<>(null);
        Node<K,V> t = remove(tree, key, found);
        if (t == null) {
            //null == doesn't contain key
            if (found.val == null) {
                return this;
            }
            //empty
            return new PersistentTreeMap<>(comp, null, 0);
        }
        return new PersistentTreeMap<>(comp, t.blacken(), size - 1);
    }

//    @Override
//    public ISeq<Map.Entry<K,V>> seq() {
//        if (size > 0)
//            return Seq.create(tree, true, size);
//        return null;
//    }
//
//    @Override
//    public ISeq<Map.Entry<K,V>> rseq() {
//        if (size > 0)
//            return Seq.create(tree, false, size);
//        return null;
//    }

//    @Override
//    public Object entryKey(Map.Entry<K,V> entry) {
//        return entry.getKey();
//    }

//    // I don't know what to do with this.
//// The other methods on Sorted seem to care only about the key, and the implementations of them
//// here work that way.  This one, however, returns a sequence of Map.Entry<K,V> or Node<K,V>
//// If I understood why, maybe I could do better.
//    @SuppressWarnings("unchecked")
//    @Override
//    public ISeq<Map.Entry<K,V>> seq(boolean ascending) {
//        if (size > 0)
//            return Seq.create(tree, ascending, size);
//        return null;
//    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public ISeq<Map.Entry<K,V>> seqFrom(Object key, boolean ascending) {
//        if (size > 0) {
//            ISeq<Node<K,V>> stack = null;
//            Node<K,V> t = tree;
//            while (t != null) {
//                int c = doCompare((K) key, t.key);
//                if (c == 0) {
//                    stack = RT.cons(t, stack);
//                    return new Seq<>(stack, ascending);
//                } else if (ascending) {
//                    if (c < 0) {
//                        stack = RT.cons(t, stack);
//                        t = t.left();
//                    } else
//                        t = t.right();
//                } else {
//                    if (c > 0) {
//                        stack = RT.cons(t, stack);
//                        t = t.right();
//                    } else
//                        t = t.left();
//                }
//            }
//            if (stack != null)
//                return new Seq<>(stack, ascending);
//        }
//        return null;
//    }

    @Override
    public UnIterator<UnMap.UnEntry<K,V>> iterator() { return new NodeIterator<>(tree, true); }

//    public NodeIterator<K,V> reverseIterator() { return new NodeIterator<>(tree, false); }

    @Override public K firstKey() {
        if (size() < 1) { throw new NoSuchElementException("this map is empty"); }
        return head().get().getKey();
    }

    @Override public K lastKey() {
        Node<K,V> max = last();
        if (max == null) {
            throw new NoSuchElementException("this map is empty");
        }
        return max.getKey();
    }

    public Node<K,V> last() {
        Node<K,V> t = tree;
        if (t != null) {
            while (t.right() != null)
                t = t.right();
        }
        return t;
    }

//    public int depth() {
//        return depth(tree);
//    }

//    int depth(Node<K,V> t) {
//        if (t == null)
//            return 0;
//        return 1 + Math.max(depth(t.left()), depth(t.right()));
//    }

// public Object valAt(Object key){
// Default implementation now inherited from ILookup

    @Override public int size() { return size; }

    public Node<K,V> entryAt(K key) {
        Node<K,V> t = tree;
        while (t != null) {
            int c = doCompare(key, t.key);
            if (c == 0)
                return t;
            else if (c < 0)
                t = t.left();
            else
                t = t.right();
        }
        return null; // t; // t is always null
    }

    public int doCompare(K k1, K k2) {
//	if(comp != null)
        return comp.compare(k1, k2);
//	return ((Comparable) k1).compareTo(k2);
    }

    private Node<K,V> add(Node<K,V> t, K key, V val, Box<Node<K,V>> found) {
        if (t == null) {
            if (val == null)
                return new Red<>(key);
            return new RedVal<>(key, val);
        }
        int c = doCompare(key, t.key);
        if (c == 0) {
            found.val = t;
            return null;
        }
        Node<K,V> ins = c < 0 ? add(t.left(), key, val, found) : add(t.right(), key, val, found);
        if (ins == null) //found below
            return null;
        if (c < 0)
            return t.addLeft(ins);
        return t.addRight(ins);
    }

    private Node<K,V> remove(Node<K,V> t, K key, Box<Node<K,V>> found) {
        if (t == null)
            return null; //not found indicator
        int c = doCompare(key, t.key);
        if (c == 0) {
            found.val = t;
            return append(t.left(), t.right());
        }
        Node<K,V> del = c < 0 ? remove(t.left(), key, found) : remove(t.right(), key, found);
        if (del == null && found.val == null) //not found below
            return null;
        if (c < 0) {
            if (t.left() instanceof Black)
                return balanceLeftDel(t.key, t.val(), del, t.right());
            else
                return red(t.key, t.val(), del, t.right());
        }
        if (t.right() instanceof Black)
            return balanceRightDel(t.key, t.val(), t.left(), del);
        return red(t.key, t.val(), t.left(), del);
//		return t.removeLeft(del);
//	return t.removeRight(del);
    }

    //static <K,V, K1 extends K, K2 extends K, V1 extends V, V2 extends V>
//Node<K,V> append(Node<K1,V1> left, Node<K2,V2> right){
    @SuppressWarnings("unchecked")
    private static <K, V> Node<K,V> append(Node<? extends K,? extends V> left,
                                   Node<? extends K,? extends V> right) {
        if (left == null)
            return (Node<K,V>) right;
        else if (right == null)
            return (Node<K,V>) left;
        else if (left instanceof Red) {
            if (right instanceof Red) {
                Node<K,V> app = append(left.right(), right.left());
                if (app instanceof Red)
                    return red(app.key, app.val(),
                               red(left.key, left.val(), left.left(), app.left()),
                               red(right.key, right.val(), app.right(), right.right()));
                else
                    return red(left.key, left.val(), left.left(), red(right.key, right.val(), app, right.right()));
            } else
                return red(left.key, left.val(), left.left(), append(left.right(), right));
        } else if (right instanceof Red)
            return red(right.key, right.val(), append(left, right.left()), right.right());
        else //black/black
        {
            Node<K,V> app = append(left.right(), right.left());
            if (app instanceof Red)
                return red(app.key, app.val(),
                           black(left.key, left.val(), left.left(), app.left()),
                           black(right.key, right.val(), app.right(), right.right()));
            else
                return balanceLeftDel(left.key, left.val(), left.left(), black(right.key, right.val(), app, right.right()));
        }
    }

    private static <K, V, K1 extends K, V1 extends V>
    Node<K,V> balanceLeftDel(K1 key, V1 val,
                             Node<? extends K,? extends V> del,
                             Node<? extends K,? extends V> right) {
        if (del instanceof Red)
            return red(key, val, del.blacken(), right);
        else if (right instanceof Black)
            return rightBalance(key, val, del, right.redden());
        else if (right instanceof Red && right.left() instanceof Black)
            return red(right.left().key, right.left().val(),
                       black(key, val, del, right.left().left()),
                       rightBalance(right.key, right.val(), right.left().right(), right.right().redden()));
        else
            throw new UnsupportedOperationException("Invariant violation");
    }

    private static <K, V, K1 extends K, V1 extends V>
    Node<K,V> balanceRightDel(K1 key, V1 val,
                              Node<? extends K,? extends V> left,
                              Node<? extends K,? extends V> del) {
        if (del instanceof Red)
            return red(key, val, left, del.blacken());
        else if (left instanceof Black)
            return leftBalance(key, val, left.redden(), del);
        else if (left instanceof Red && left.right() instanceof Black)
            return red(left.right().key, left.right().val(),
                       leftBalance(left.key, left.val(), left.left().redden(), left.right().left()),
                       black(key, val, left.right().right(), del));
        else
            throw new UnsupportedOperationException("Invariant violation");
    }

    private static <K, V, K1 extends K, V1 extends V>
    Node<K,V> leftBalance(K1 key, V1 val,
                          Node<? extends K,? extends V> ins,
                          Node<? extends K,? extends V> right) {
        if (ins instanceof Red && ins.left() instanceof Red)
            return red(ins.key, ins.val(), ins.left().blacken(), black(key, val, ins.right(), right));
        else if (ins instanceof Red && ins.right() instanceof Red)
            return red(ins.right().key, ins.right().val(),
                       black(ins.key, ins.val(), ins.left(), ins.right().left()),
                       black(key, val, ins.right().right(), right));
        else
            return black(key, val, ins, right);
    }


    private static <K, V, K1 extends K, V1 extends V>
    Node<K,V> rightBalance(K1 key, V1 val,
                           Node<? extends K,? extends V> left,
                           Node<? extends K,? extends V> ins) {
        if (ins instanceof Red && ins.right() instanceof Red)
            return red(ins.key, ins.val(), black(key, val, left, ins.left()), ins.right().blacken());
        else if (ins instanceof Red && ins.left() instanceof Red)
            return red(ins.left().key, ins.left().val(),
                       black(key, val, left, ins.left().left()),
                       black(ins.key, ins.val(), ins.left().right(), ins.right()));
        else
            return black(key, val, left, ins);
    }

    private Node<K,V> replace(Node<K,V> t, K key, V val) {
        int c = doCompare(key, t.key);
        return t.replace(t.key,
                         c == 0 ? val : t.val(),
                         c < 0 ? replace(t.left(), key, val) : t.left(),
                         c > 0 ? replace(t.right(), key, val) : t.right());
    }

    @SuppressWarnings({"unchecked", "RedundantCast", "Convert2Diamond"})
    private static <K, V, K1 extends K, V1 extends V>
    Red<K,V> red(K1 key, V1 val,
                 Node<? extends K,? extends V> left,
                 Node<? extends K,? extends V> right) {
        if (left == null && right == null) {
            if (val == null)
                return new Red<K,V>(key);
            return new RedVal<K,V>(key, val);
        }
        if (val == null)
            return new RedBranch<K,V>((K) key, (Node<K,V>) left, (Node<K,V>) right);
        return new RedBranchVal<K,V>((K) key, (V) val, (Node<K,V>) left, (Node<K,V>) right);
    }

    @SuppressWarnings({"unchecked", "RedundantCast", "Convert2Diamond"})
    private static <K, V, K1 extends K, V1 extends V>
    Black<K,V> black(K1 key, V1 val,
                     Node<? extends K,? extends V> left,
                     Node<? extends K,? extends V> right) {
        if (left == null && right == null) {
            if (val == null)
                return new Black<>(key);
            return new BlackVal<K,V>(key, val);
        }
        if (val == null)
            return new BlackBranch<K,V>((K) key, (Node<K,V>) left, (Node<K,V>) right);
        return new BlackBranchVal<K,V>((K) key, (V) val, (Node<K,V>) left, (Node<K,V>) right);
    }

//    public static class Reduced<A> {
//        public final A val;
//        private Reduced(A a) { val = a; }
//    }

    private static abstract class Node<K, V> implements UnEntry<K,V> {
        final K key;

        Node(K key) { this.key = key; }

        public K key() { return key; }

        public V val() { return null; }

        @Override
        public K getKey() { return key(); }

        @Override
        public V getValue() { return val(); }

        Node<K,V> left() { return null; }

        Node<K,V> right() { return null; }

        abstract Node<K,V> addLeft(Node<K,V> ins);

        abstract Node<K,V> addRight(Node<K,V> ins);

        @SuppressWarnings("UnusedDeclaration")
        abstract Node<K,V> removeLeft(Node<K,V> del);

        @SuppressWarnings("UnusedDeclaration")
        abstract Node<K,V> removeRight(Node<K,V> del);

        abstract Node<K,V> blacken();

        abstract Node<K,V> redden();

        Node<K,V> balanceLeft(Node<K,V> parent) { return black(parent.key, parent.val(), this, parent.right()); }

        Node<K,V> balanceRight(Node<K,V> parent) { return black(parent.key, parent.val(), parent.left(), this); }

        abstract Node<K,V> replace(K key, V val, Node<K,V> left, Node<K,V> right);

        // Not used in this data structure, but these nodes can be returned!
        @Override public int hashCode() { return key.hashCode(); }

        // Not used in this data structure, but these nodes can be returned!
        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if ( (o == null) || ! (o instanceof Map.Entry) ) { return false; }
            Map.Entry that = (Map.Entry) o;
            return Objects.equals(key, that.getKey());
        }

        @Override public String toString() {
            return "Node(" + key + ")";
        }

//        public <R> R kvreduce(Function3<R,K,V,R> f, R init) {
//            if (left() != null) {
//                init = left().kvreduce(f, init);
//                if (init instanceof Reduced)
//                    return init;
//            }
//            init = f.apply(init, key(), val());
//            if (init instanceof Reduced)
//                return init;
//
//            if (right() != null) {
//                init = right().kvreduce(f, init);
//            }
//            return init;
//        }
    } // end class Node.

    private static class Black<K, V> extends Node<K,V> {
        public Black(K key) { super(key); }

        @Override Node<K,V> addLeft(Node<K,V> ins) { return ins.balanceLeft(this); }

        @Override Node<K,V> addRight(Node<K,V> ins) { return ins.balanceRight(this); }

        @Override Node<K,V> removeLeft(Node<K,V> del) { return balanceLeftDel(key, val(), del, right()); }

        @Override Node<K,V> removeRight(Node<K,V> del) { return balanceRightDel(key, val(), left(), del); }

        @Override Node<K,V> blacken() { return this; }

        @Override Node<K,V> redden() { return new Red<>(key); }

        @Override
        Node<K,V> replace(K key, V val, Node<K,V> left, Node<K,V> right) { return black(key, val, left, right); }
    }

    private static class BlackVal<K, V> extends Black<K,V> {
        final V val;

        public BlackVal(K key, V val) {
            super(key);
            this.val = val;
        }

        @Override public V val() { return val; }

        @Override Node<K,V> redden() { return new RedVal<>(key, val); }

        // Not used in this data structure, but these nodes can be returned!
        @Override public int hashCode() {
            int ret = 0;
            if (key != null) { ret = key.hashCode(); }
            if (val != null) { return ret ^ val.hashCode(); }
            // If it's uninitialized, it's equal to every other uninitialized instance.
            return ret;
        }

        // Not used in this data structure, but these nodes can be returned!
        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if ( (o == null) || ! (o instanceof Map.Entry) ) { return false; }
            Map.Entry that = (Map.Entry) o;
            return Objects.equals(key, that.getKey()) && Objects.equals(val, that.getValue());
        }

        @Override public String toString() {
            return "BlackVal(" + key + "," + val + ")";
        }
    }

    private static class BlackBranch<K, V> extends Black<K,V> {
        final Node<K,V> left;

        final Node<K,V> right;

        public BlackBranch(K key, Node<K,V> left, Node<K,V> right) {
            super(key);
            this.left = left;
            this.right = right;
        }

        @Override
        public Node<K,V> left() { return left; }

        @Override
        public Node<K,V> right() { return right; }

        @Override
        Node<K,V> redden() { return new RedBranch<>(key, left, right); }

    }

    private static class BlackBranchVal<K, V> extends BlackBranch<K,V> {
        final V val;

        public BlackBranchVal(K key, V val, Node<K,V> left, Node<K,V> right) {
            super(key, left, right);
            this.val = val;
        }

        @Override public V val() { return val; }

        @Override Node<K,V> redden() { return new RedBranchVal<>(key, val, left, right); }

        // Not used in this data structure, but these nodes can be returned!
        @Override public int hashCode() {
            int ret = 0;
            if (key != null) { ret = key.hashCode(); }
            if (val != null) { return ret ^ val.hashCode(); }
            // If it's uninitialized, it's equal to every other uninitialized instance.
            return ret;
        }

        // Not used in this data structure, but these nodes can be returned!
        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if ( (o == null) || ! (o instanceof Map.Entry) ) { return false; }
            Map.Entry that = (Map.Entry) o;
            return Objects.equals(key, that.getKey()) && Objects.equals(val, that.getValue());
        }

        @Override public String toString() {
            return "BlackBranchVal(" + key + "," + val + ")";
        }
    }

    private static class Red<K, V> extends Node<K,V> {
        public Red(K key) { super(key); }

        @Override Node<K,V> addLeft(Node<K,V> ins) { return red(key, val(), ins, right()); }

        @Override Node<K,V> addRight(Node<K,V> ins) { return red(key, val(), left(), ins); }

        @Override Node<K,V> removeLeft(Node<K,V> del) { return red(key, val(), del, right()); }

        @Override Node<K,V> removeRight(Node<K,V> del) { return red(key, val(), left(), del); }

        @Override Node<K,V> blacken() { return new Black<>(key); }

        @Override
        Node<K,V> redden() { throw new UnsupportedOperationException("Invariant violation"); }

        @Override
        Node<K,V> replace(K key, V val, Node<K,V> left, Node<K,V> right) { return red(key, val, left, right); }

    }

    private static class RedVal<K, V> extends Red<K,V> {
        final V val;

        public RedVal(K key, V val) {
            super(key);
            this.val = val;
        }

        @Override public V val() { return val; }

        @Override Node<K,V> blacken() { return new BlackVal<>(key, val); }

        // Not used in this data structure, but these nodes can be returned!
        @Override public int hashCode() {
            int ret = 0;
            if (key != null) { ret = key.hashCode(); }
            if (val != null) { return ret ^ val.hashCode(); }
            // If it's uninitialized, it's equal to every other uninitialized instance.
            return ret;
        }

        // Not used in this data structure, but these nodes can be returned!
        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if ( (o == null) || ! (o instanceof Map.Entry) ) { return false; }
            Map.Entry that = (Map.Entry) o;
            return Objects.equals(key, that.getKey()) && Objects.equals(val, that.getValue());
        }

        @Override public String toString() {
            return "RedVal(" + key + "," + val + ")";
        }
    }

    private static class RedBranch<K, V> extends Red<K,V> {
        final Node<K,V> left;

        final Node<K,V> right;

        public RedBranch(K key, Node<K,V> left, Node<K,V> right) {
            super(key);
            this.left = left;
            this.right = right;
        }

        @Override public Node<K,V> left() { return left; }

        @Override public Node<K,V> right() { return right; }

        @Override Node<K,V> balanceLeft(Node<K,V> parent) {
            if (left instanceof Red)
                return red(key, val(), left.blacken(), black(parent.key, parent.val(), right, parent.right()));
            else if (right instanceof Red)
                return red(right.key, right.val(), black(key, val(), left, right.left()),
                           black(parent.key, parent.val(), right.right(), parent.right()));
            else
                return super.balanceLeft(parent);

        }

        @Override Node<K,V> balanceRight(Node<K,V> parent) {
            if (right instanceof Red)
                return red(key, val(), black(parent.key, parent.val(), parent.left(), left), right.blacken());
            else if (left instanceof Red)
                return red(left.key, left.val(), black(parent.key, parent.val(), parent.left(), left.left()),
                           black(key, val(), left.right(), right));
            else
                return super.balanceRight(parent);
        }

        @Override Node<K,V> blacken() { return new BlackBranch<>(key, left, right); }

    }


    private static class RedBranchVal<K, V> extends RedBranch<K,V> {
        final V val;

        public RedBranchVal(K key, V val, Node<K,V> left, Node<K,V> right) {
            super(key, left, right);
            this.val = val;
        }

        @Override public V val() { return val; }

        @Override Node<K,V> blacken() { return new BlackBranchVal<>(key, val, left, right); }

        // Not used in this data structure, but these nodes can be returned!
        @Override public int hashCode() {
            int ret = 0;
            if (key != null) { ret = key.hashCode(); }
            if (val != null) { return ret ^ val.hashCode(); }
            // If it's uninitialized, it's equal to every other uninitialized instance.
            return ret;
        }

        // Not used in this data structure, but these nodes can be returned!
        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if ( (o == null) || ! (o instanceof Map.Entry) ) { return false; }
            Map.Entry that = (Map.Entry) o;
            return Objects.equals(key, that.getKey()) && Objects.equals(val, that.getValue());
        }

        @Override public String toString() {
            return "RedBranchVal(" + key + "," + val + ")";
        }
    }


//    static public class Seq<K, V> extends ASeq<Map.Entry<K,V>> {
//        final ISeq<Node<K,V>> stack;
//        final boolean asc;
//        final int cnt;
//
//        public Seq(ISeq<Node<K,V>> stack, boolean asc) {
//            this.stack = stack;
//            this.asc = asc;
//            this.cnt = -1;
//        }
//
//        public Seq(ISeq<Node<K,V>> stack, boolean asc, int cnt) {
//            this.stack = stack;
//            this.asc = asc;
//            this.cnt = cnt;
//        }
//
//        Seq(ISeq<Node<K,V>> stack, boolean asc, int cnt) {
//            super();
//            this.stack = stack;
//            this.asc = asc;
//            this.cnt = cnt;
//        }
//
//        static <K, V> Seq<K,V> create(Node<K,V> t, boolean asc, int cnt) {
//            return new Seq<>(push(t, null, asc), asc, cnt);
//        }
//
//        static <K, V> ISeq<Node<K,V>> push(Node<K,V> t, ISeq<Node<K,V>> stack, boolean asc) {
//            while (t != null) {
//                stack = RT.cons(t, stack);
//                t = asc ? t.left() : t.right();
//            }
//            return stack;
//        }
//
//        @Override
//        public Node<K,V> head() {
//            return stack.head();
//        }
//
//        @Override
//        public ISeq<Map.Entry<K,V>> next() {
//            Node<K,V> t = stack.head();
//            ISeq<Node<K,V>> nextstack = push(asc ? t.right() : t.left(), stack.next(), asc);
//            if (nextstack != null) {
//                return new Seq<>(nextstack, asc, cnt - 1);
//            }
//            return null;
//        }
//
//        @Override
//        public int count() {
//            if (cnt < 0)
//                return super.count();
//            return cnt;
//        }
//    }

    static public class NodeIterator<K, V> implements UnIterator<UnMap.UnEntry<K,V>> {
        private Stack<Node<K,V>> stack = new Stack<>();
        private final boolean asc;

        NodeIterator(Node<K,V> t, boolean asc) {
            this.asc = asc;
            push(t);
        }

        private void push(Node<K,V> t) {
            while (t != null) {
                stack.push(t);
                t = asc ? t.left() : t.right();
            }
        }

        @Override public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override public UnMap.UnEntry<K,V> next() {
            Node<K,V> t = stack.pop();
            push(asc ? t.right() : t.left());
            return t;
        }
    }

//    static class KeyIterator<K> implements Iterator<K> {
//        NodeIterator<K,?> it;
//
//        KeyIterator(NodeIterator<K,?> it) {
//            this.it = it;
//        }
//
//        @Override
//        public boolean hasNext() {
//            return it.hasNext();
//        }
//
//        @Override
//        public K next() {
//            return it.next().getKey();
//        }
//
//        @Override
//        public void remove() {
//            throw new UnsupportedOperationException();
//        }
//    }
//
//    static class ValIterator<V> implements Iterator<V> {
//        NodeIterator<?,V> it;
//
//        ValIterator(NodeIterator<?,V> it) {
//            this.it = it;
//        }
//
//        @Override
//        public boolean hasNext() {
//            return it.hasNext();
//        }
//
//        @Override
//        public V next() {
//            return it.next().getValue();
//        }
//
//        @Override
//        public void remove() {
//            throw new UnsupportedOperationException();
//        }
//    }
}
