# Change Log

Bigger headings are better releases.  If you're upgrading, you might want to hit the biggest
releases on the way from an old version to a new one.  Fix any deprecation warnings at each major
release before upgrading to the next one.  The documentation next to each Deprecated annotation
tells you what to use instead.  Once we delete the deprecated methods, that documentation goes too.

# Release 3.1.0: Kotlin compatibility
 - Renamed all mutable collections from Mutable___ to Mut___.

Here is a script to ease your upgrade from 3.0 to 3.1 (INCOMPLETE work in progress)
```bash
# USE CAUTION AND HAVE A BACKUP OF YOUR SOURCE CODE (VERSION CONTROL) - NO GUARANTEES

oldString='MutableUnsortedMap'
newString=MutMap
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

oldString='MutableUnsortedSet'
newString=MutSet
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

sed -i -e 's/\<Function\([0-3]\)\>/Fn\1/g' $(egrep --exclude-dir='.svn' --exclude-dir='.git' -wrIl 'Function[0-3]' *)
```

# Release 3.0.16: RRB Tree
 - Added Option.Some.toString() and unit test for same.

See [3.0 Upgrade](#30-upgrade) notes below if upgrading from 2.x

#### Release 3.0.15: RRB Tree
 - Added Transformable.head():Option.
 If you want .any(Fn1<T,Boolean>):Boolean like Kotlin, call .filter(Fn1<T,Boolean>).head().isSome()
 Couldn't call this first() because SortedSet already has .first() which returns null if the set is empty.
 That's ambiguous if the set contains nulls, so use head() instead.
 - Improved test coverage a little.

#### Release 3.0.14: RRB Tree
 - This is just a version number bump for the official release.

#### Release 3.0.12: RRB Tree
 - Changed all OneOf classes to take a single Object argument, plus each of the classes they can hold.
 This great simplification is thanks to Atul Agrawal!

#### Release 3.0.11: RRB Tree
 - All OneOf classes now use a ZERO-BASED INDEX (used to use one-based).

#### Release 3.0: RRB Tree
Major changes:
 - Added Base___ for Im___ and Mutable___ interfaces to extend.  Mutable___ no longer extends Im___.
 - ImUnsortedSet renamed to ImSet and ImUnsortedMap renamed to ImMap to follow the java.util conventions more closely and for brevity.
   Mutable versions of both interfaces still contain the word "Unsorted" (verbose mutable names encourage immutability).
 - Renamed interfaces Function# to Fn#
 - Added an RRB Tree (still improving performance, but for insert at 0 in large lists it kills ArrayList)
 - Added foldUntil() to Transformable to replace the earlier version of foldLeft with a repeatUntil function parameter.
   Thanks to cal101 for inspiring this!
 - Removed a bunch of deprecated and unused items
 - Added JavaDoc to Git site
 - Changed toString to show entire collection
 - Added rrb() and mutableRrb() to StaticImports for constructing RRB Trees.
 - Moved AbstractUnmodMap, AbstractUnmodSet, and AbstractUnmodIterable from inner classes to top-level classes.
 - Option.of() is now Option.some() and it no longer returns None if you pass it a None.
 - Added StaticImports.xformChars(CharSequence).
 - Added "Union type" classes in oneOf package: OneOf2OrNone, OneOf3 and OneOf4.
 - Added Xform.dropWhile()

# 3.0 Upgrade

Here is a script to ease your upgrade from 2.1.1 to 3.0.14:
```bash
# USE CAUTION AND HAVE A BACKUP OF YOUR SOURCE CODE (VERSION CONTROL) - NO GUARANTEES
oldString='org.organicdesign.fp.LazyRef'
newString='org.organicdesign.fp.function.LazyRef'
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

oldString='org.organicdesign.fp.either.OneOf2'
newString='org.organicdesign.fp.oneOf.OneOf2'
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

oldString='org.organicdesign.fp.Option'
newString='org.organicdesign.fp.oneOf.Option'
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

oldString='org.organicdesign.fp.Or'
newString='org.organicdesign.fp.oneOf.Or'
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

oldString='.foldLeft('
newString='.fold('
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

oldString='.typeMatch('
newString='.match('
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

oldString='.patMat('
newString='.match('
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

oldString='MutableUnsortedMap'
newString=MutMap
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

oldString='MutableUnsortedSet'
newString=MutSet
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

sed -i -e 's/\<Function\([0-3]\)\>/Fn\1/g' $(egrep --exclude-dir='.svn' --exclude-dir='.git' -wrIl 'Function[0-3]' *)
```
If you subclassed OneOf, you can clean up your code.  From:
```java
public class String_Integer extends OneOf2<String,Integer> {
    String_Integer(String cre, Integer rev, int s) { super(cre, rev, s); }

    public static String_Integer str(String cre) { return new String_Integer(cre, null, 1); }
    public static String_Integer in(Integer rev) { return new String_Integer(null, rev, 2); }
```
to (MAKE SURE TO USE ZERO-BASED INDICES!):
```java
public class String_Integer extends OneOf2<String,Integer> {
    String_Integer(Object o, int s) { super(o, String.class, Integer.class, s); }

    public static String_Integer str(String cre) { return new String_Integer(cre, 0); }
    public static String_Integer in(Integer rev) { return new String_Integer(rev, 1); }
```

If you used the super::throw1 and super::throw2 methods, those have disappeared in favor of just using match everywhere.
If you're relied on super::throwN, you may have to manually throw an exception:
```java
match(a -> a,
      b -> { throw new IllegalStateException("Can't call a() on a B"); });
```

Anywhere you have FunctionUtils unmmodSet, unmodList, unmodWhatever, you probably want to replace that with StaticImports.xform().

For instance:
```java
// Old 'n busted
UnmodList<Number> foo(List<Integer> is, List<Double> ds) {
    List<Number> ms = new ArrayList<>(is.size() + ds.size());
    ms.addAll(is);
    ms.addAll(ds);
    return FunctionUtils.unmodList(ms);
}

// New 'n awesome
ImList<Number> foo3(List<Integer> is, List<Double> ds) {
    // Have to specify StaticImports.<Number> to tell Java's type system that we're making a List of Numbers.
    // I think Kotlin can figure this out without hints.
    return StaticImports.<Number>mutableVec()
                   .concat(is)
                   .concat(ds)
                   .immutable();
}
```

Manual upgrade tasks:
 - Do a clean build of your project
   - IntelliJ users right-click on the /src/java/ folder in your project and choose `Rebuild '<default>'`
   - Maven users: `mvn clean package`
 - There will be type errors where you pass a Mutable___ as an Im___.
   If the variable is myVar, simply change it to myVar.immutable() wherever errors show up.
     Unless you really wanted a Mutable___ in which case, you need to change the type signature.

# 2.0 Releases

##### Oops!
I'm sorry I ever made Mutable___ extend Im___.  It's an embarrassing rookie mistake.
I wasn't sure exposing mutable collections was worthwhile, I was "just trying them out" and suddenly found myself using them everywhere.
Well, they're fixed now.  ["Go forth and spread beauty and light!"](https://www.youtube.com/watch?v=9oRctV5Fygc&t=12m28s)

## 2017-05-04 Release 2.1.1
Fixed array class cast exception bug reported by @BrenoTrancoso https://github.com/GlenKPeterson/Paguro/issues/17
Thank you for finding and reporting this tricky issue!

### 2017-01-25 Release 2.1.0
Breaking Changes:
 - All "OneOf" classes are moved to org.organicdesign.fp.oneOf.  This includes
     - OneOf2 org.organicdesign.fp.either
     - Option and Or from org.organicdesign.fp

   OneOf2 now implements equals(), hashCode(), toString(), and has meaningful error messages
   which show the types of the union at runtime in `toString()` and in exception messages.
   Option was moved because it is essentially OneOfOneOrNone.
   Or was moved because it is an implementation of OneOf2.
   Presumably, there will be a OneOf2OrNone, OneOf3, OneOf3OrNone, etc.
 - `Option.patMatch()` and `OneOf2.typeMatch()` methods have been renamed simply `match()`.

Non-breaking Changes:
 - Added org.organicdesign.fp.type.RuntimeTypes.
   This takes types used for generic parameters at compile time and makes them available at runtime.
   Lets people develop programming languages without type erasure on the JVM and
   it comes in very handy at various times, like for OneOf_ (Union types, Or, etc.).
 - Paguro has gone from Functional Transforms in Java 7 to include unmodifiable (copy-on-write) collections like Guava to making Clojure collections and FP concepts convenient in pure-Java.
   Long-term, Paguro is intended to be the Java-compatibility (and maybe Kotlin compatibility?) layer for https://github.com/GlenKPeterson/Cymling
   The Cymling programming language basically splits the difference between Clojure (collections and xforms, assumption of immutability),
   ML (types without objects, records/tuples instead of Clojure's maps), and Kotlin (dot syntax, function syntax, null safety),
   Anyone who likes Paguro might want to keep an eye on Cymling development.


Upgrade Instructions:
You can use sed to fix imports for moved classes.
```bash
# USE CAUTION AND HAVE A BACKUP OF YOUR SOURCE CODE (E.G. VERSION CONTROL) - NO GUARANTEES
oldString='import org.organicdesign.fp.Option'
newString='import org.organicdesign.fp.oneOf.Option'
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

oldString='import org.organicdesign.fp.Or'
newString='import org.organicdesign.fp.oneOf.Or'
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

oldString='import org.organicdesign.fp.either.OneOf2'
newString='import org.organicdesign.fp.oneOf.OneOf2;\nimport org.organicdesign.fp.type.RuntimeTypes'
sed -i -e "s/$oldString/$newString/g" $(fgrep --exclude-dir='.svn' --exclude-dir='.git' -rIl "$oldString" *)

unset oldString
unset newString
```
Then, for any place you exteded OneOf2 you'll manually need to add:
```java
public class Foo_Bar extends OneOf2<Foo,Bar> {
    private static final ImList<Class> TYPES = RuntimeTypes.registerClasses(vec(Foo.class, Bar.class));
    MyClass(Foo f, Bar b, int s) { super(TYPES, f, b, s); }
```

Manually change `.typeMatch(` and `.patMat(` to just `.match(` (or you could use sed as above)

If you used the static method:
```java
Or.patMatch(x,
            g -> g.apply(),
            b -> b.apply())
```
You'll need to check that x cannot be null, then change it to:
```java
x.match(g -> g.apply(),
        b -> b.apply())
```

### 2017-01-16 Release 2.0.20
 - Added Equator.neq() which just returns !eq() (convenience method)

### 2017-01-12 Release 2.0.19
 - Added min() and max() to ComparisonContext.  Each takes a list which could contain nulls
   and tries its best to return a non-null result.  Should it throw an exception if the result
   is null?  I dunno.

### 2017-01-12 Release 2.0.18
 - Fixed ComparisonContext.eq(), lt(), lte(), gt(), gte() to check for null arguments before calling compare()
   because compare() typically throws exceptions for nulls, null always equals null,
   and null never equals anything else.  If a method returns true for equals or false for neq, do
   that, otherwise throw an exception if a null argument is given.
 - Removed Deprecated items from Equator.
 - Added test.

#### 2017-01-08 Release 2.0.16
 - Changed return type of StaticImports: xform() and xformArray() from Transformable to UnmodIterable.
   Thanks to @BrenoTrancoso for finding this and suggesting the fix!

#### 2016-11-13 Release 2.0.15
 - Added the following convenience methods to StaticImports: mutableVec(items...), mutableSet(items...), mutableMap(items...), xformArray(items...).

# 2016-11-13 Release 2.0.14
 - Renamed Maven artifact from UncleJim to Paguro.
 - No other changes made!

## 2016-09-21 Release 2.0.13
 - FunctionUtils
    - The unmodifiable collection wrappers are now serializable, but the empty collections are no longer singletons:
    - Deprecated `EMPTY_UNMOD____` static fields and replaced them with `public static emptyUnmod___()` methods
    - All `unmod___()` methods now return Serializable subclassses of `Unmod____` interfaces with reasonable
    equals(), hashCode(), and now toString() implementations.
    - Removed arrayToString() and mapToString() - Java 8 doesn't need this for map and
    for arrays you should use Arrays.toString() instead.
    - Removed unmodIterable() and EMPTY_UNMOD_ITERABLE.  It's too vague to be useful.
    If you use unmodList() or unmodSortedSet() it safely provides an UnmodSortedIterable.
    unmodSet() and unmodMap() provide UnmodIterables.
 - Deprecated UnmodIterator.Wrapper class and replaced with FunctionUtils.unmodIterator() method.
 - PersistentTreeSet.toString() now returns string values with quotes.
 - The above changes brought the size of the jar file from under 240K to under 230K.
 I bet removing all the deprecated stuff can get us under 220K.
 - Made MutableVector public (but final) and added equals(), hashCode() and toString().
 Also added PersistentVector.emptyMutable() static method because Java type inference has
 real problems otherwise.  Added tests for same.
 - Similarly added emptyMutable() to PersistentHashMap and PersistentHashSet.
 - New interface: Sized, has the single method `int size()` on it.  UnmodCollection and UnmodMap now implement this.
 - New: UnmodIterable.AbstractUnmodIterable defines hashCode() and toString() for all collections
   (except List which needs a different implementation to be compatible with java.util.List).
 - New: UnmodMap.AbstractUnmodMap defines equals() for maps.  Made PersistentHashMap and MutableHashMap extend this. 
 - New: UnmodSet.AbstractUnmodSet defines equals() for sets.  Made PersistentHashSet and MutableHashSet extend this.
 - New: UnmodList.AbstractUnmodList defines equals() and hashcode() for lists.  Made PersistentVector and MutableVector extend this.

### 2016-09-17 Release 2.0.12
 - Changed order of serialization for PersistentTreeMap.  Because it uses a serialization proxy, it
 should still deserialize TreeMaps serialized before this change.  The serialization format has not
 changed, only the order of elements is changed such that it should serialize at approximately the
 same speed and deserialize without any internal rotations or re-balancing (therefore faster).
 Full details in comments in org.organicdesign.fp.collections.PersistentTreeMap.SerializationProxy

### 2016-09-17 Release 2.0.11
 - Renamed static method UnmodIterable.hashCode() to UnmodIterable.hash() to avoid confusion.
 Deprecated old method.
 - Made Option serializable and added tests for same.
 - Added Option.then for chaining Some options.
 - Deprecated and deleted most of the ill-concieved and short-lived KeyVal class.
 - Increased PersistentHashMap test coverage by covering its internal MutableUnsortedMap better.
 - Cleaned up some of the oldest documentation (mostly deleted duplicates).

### 2016-09-14 Release 2.0.10:
 - Renamed UnmodIterator.Implementation to UnmodIterator.Wrapper to mirror UnmodSortedIterator.Wrapper and because it's just a better name.
 - Added tests.

### 2016-09-13 Release 2.0.9:
 - Fixed return type of ImUnsortedMap and MutableUnsortedMap.assoc(Map.Entry).  Thanks @pniederw for reporting!
 - Made ImUnsortedSet use MutableUnsortedSet's implementation of union() instead of the other way around.
 - Added tests.

### 2016-09-11 Release 2.0.8:
 - Fixed illegal cast in MutableList.concat and made test for same.  Thanks to @pniederw for this!

### 2016-09-11 Release 2.0.7:
 - Massive renaming of newly exposed interfaces and methods.
    - ImUnsortMap is now ImUnsortedMap
    - ImUnsortSet is now ImUnsortedSet
    - ImListTrans is now MutableList
    - ImUnsortMapTrans is now MutableUnsortedMap
    - ImUnsortSetTrans is now MutableUnsortedSet
    - The `persistent()` method on all of those interfaces is now `immutable()`
    - The `asTransient()` method on all of those interfaces is now `mutable()`
 - Added union() method to ImUnsortedSet and MutableUnsortedSet and a little test for same.
 - ImListTrans overrides concat(), refining the return type to ImListTrans.  Thanks to @pniederw for this!

This is only a day after releasing these interfaces. After doing serialization, I couldn't freakin'
write anything anymore without saying "transient (meaning mutable)" or "transient (as in not
serializable)".  I know Rich picked Persistent and Transient, but JPA took Persistent and
serialization took Transient so even though I like Rich's terms, I'm changing them to save some
small shred of my sanity.  There are only three terms: Mutable, Unmofidiable, and Immutable.
Mutable means mutate-in-place.  Unmodifiable means you can't change it, and it probably can't
grow without a complete deep copy.  Immutable means it never changes, yet grows efficiently
by producing very shallow copies of most of its internals.

I am leaving Rich's 5 file names as a tribute to how awesome he is and so that people talking
about the Clojure collections won't be surprised, and to show what still carries his license
(Eclipse 1.0).

#### 2016-09-10 Release 2.0.6: USE 2.0.7+ INSTEAD!
 - Added asTransient() to ImList.

#### 2016-09-10 Release 2.0.5: USE 2.0.7+ INSTEAD!
 - Moved persistent() from ImListTrans to ImList and made PersistentVector implement ImList instead of ImListTrans.
   This just makes a lot more sense.  It shouldn't break any sensible client code.
 - Added ImList.reverse().  @pniederw had asked for this.  Sorry for the wait.

#### 2016-09-10 Release 2.0.4: USE 2.0.7+ INSTEAD!
 - Made PersistentTreeMap return serializable Tuple2's.  Actually these are subclasses of internal nodes that still contain
   big chunks of the treemap, but those chunks are transient (not serializable) and private.
   When deserialized, they become plain old Tuple2's.
 - Speed is unchanged after this: 10-item maps/sets are 0-3% faster, but generally near the margin of error for equal speed.
   Large maps/sets (100K elements) are 0-1% slower; within the margin of error.
 - Added ImUnsortSet and ImUnsortSetTrans interfaces to expose the TransientSet implementation.
 - Added ImListTrans interface to expose the Transient Vector implementation.
 - Deprecated ImMapTrans - replaced it with ImUnsortMap and UmUnsortedMapTrans.
 - Radically improved test coverage of PersistentTreeMap and slightly improved PersistentHashMap
 - Added epl-v10.html referenced in Rich's comments.  Should have read the legal terms more carefully earlier.
 - Added CodeStylePaguro.xml for Idea to import.
 - Found some opportunities to use mutable implementations more efficiently.

###### Still *NOT* Serializable
 - FunctionUtils.unmodifiable___() methods are not serializable (yet?).
 - Transformable is not serializable (should it be?)

Issues?  Questions?  Provide feedback on the [Serialization enhancement request](https://github.com/GlenKPeterson/Paguro/issues/10)

### 2016-09-10 Release 2.0.3: Serializable (Part 3)
 - Reverted the most serious breaking changes from previous 2.0.x releases.
   Going from 1.x to 2.0.3+ in the two projects I use Paguro in, no changes were necessary.
 - Tuples all implement Serializable.
   Serializable wants a zero-arg constructor and mutable fields on the parent class in order to deserialize the child class.
   That's the opposite of what this project is about.
   Better to have all tuples serializable than to make anyone write a serialization proxy on a subclass because tuples *aren't* serializable.
   This also means that Tuple2 can still implement Map.Entry, which gets rid of the breaking changes in 2.0.0, .1, and .2.
 - Changed serialized form of RangeOfInt to just serialize the start and end,
   then calculate the size from that.
 - Removed KeyVal class and made Tuples serializable instead.  Back to using tup() instead of kv()
 - Static method UnmodSortedIterable.equals() has been deprecated (renamed to UnmodSortedIterable.equal() to avoid confusion with Object.equals()).

#### 2016-09-01 Release 2.0.2: USE 2.0.3+ INSTEAD!
 - Gave 5 main collections custom serialized forms (after reading Josh Bloch) so that we can change
   the implementations later without breaking any clients who are using them for long-term storage.
 - Decided *NOT* to make any itera**tors** serializable.
 - Improved tests a bit, especially for serialization.

#### 2016-09-01 Release 2.0.1: USE 2.0.3+ INSTEAD!
 - Made UnmodSortedIterable.castFrom... methods generic and serializable (and wrote tests for same).
 - Fixed some Javadoc link errors.

#### 2016-08-27 Release 2.0.0: USE 2.0.3+ INSTEAD - ALL SIGNIFICANT BREAKING CHANGES WERE REVERTED!
This is a major release due to making a new serializable format.
 - Anything that used to be implemented as an anonymous class, object, or lambda is now implemented as an enum or serializable sub-class.
 - Hash codes of all tuples are now calculated by adding together the hash codes of all member items.
   They used to bitwise-or the first two items for compatibility with Map.Entry.
 - Tuple2 no longer implements Map.Entry or UnmodMap.UnEntry.  Instead, a new class KeyVal extends Tuple2, Map.Entry, UnmodMap.UnEntry, and Serializable.
   The new KeyValuePair hashcode method is compatible with the *old* Tuple2 hashcode, but NOT with the *new* Tuple2 hashcode (or equals).
 - Maps construction now requires KeyVals instead of Tuple2's.  StaticImports has a new helper method `kv(k,v)` for this.
 - PersistentTreeMap now returns KeyVal's instead of UnEntry's.
 - Removed PersistentTreeMap.EQUATOR - because I didn't see it used and it hadn't been tested.
 - Moved ComparisonContext interface from inside the Equator interface, to it's own file: org.organicdesign.fp.ComparisonContext.
 - Replaced Equator.ComparisonContext.DEFAULT_CONTEXT with ComparisonContext.CompCtx.DEFAULT
 - KeyVal.toString() is now kv(k,v) like all the other Java toString methods.
 - PersistentHashMap.ArrayNode, .BitMapIndexNode, .HashCollisionNode, and .NodeIter are now all private (were public or package).
   There should never have been any reason to use or access these.

This release also contains the following non-breaking changes:
 - Made serializable: Persistent- HashMap, HashSet, TreeMap, TreeSet, Vector.  RangeOfInt,
 default Equator, default Comparator, default ComparisonContext.
 Thanks @sblommers for spotting this issue and writing the key unit test!
 - Deprecated Equator.DEFAULT_EQUATOR use Equator.Equat.DEFAULT instead.
 - Deprecated Equator.DEFAULT_COMPARATOR use Equator.Comp.DEFAULT instead.

Note: Xform is NOT serializable.  I don't know yet whether that's good or bad.

##### Moved items in 2.0:
```
org.organicdesign.fp.collections.Equator:
DEFAULT_COMPARATOR   is now   Comp.DEFAULT
DEFAULT_EQUATOR      is now   Equat.DEFAULT
ComparisonContext   moved to  org.organicdesign.fp.collections.ComparisonContext
DEFAULT_CONTEXT      is now   org.organicdesign.fp.collections.ComparisonContext.CompCtx.DEFAULT

org.organicdesign.fp.collections.RangeOfInt:
LIST_EQUATOR  is now Equat.LIST

org.organicdesign.fp.function.Fn0:
NULL   is now   Const.NULL
New serializable sub-class for functions that always return the same value:
Constant (Function0.Constant)

org.organicdesign.fp.function.Fn1
IDENTITY  is now  Const.IDENTITY
ACCEPT    is now  ConstBool.ACCEPT
REJECT    is now  ConstBool.REJECT

org.organicdesign.fp.collections.UnmodMap.UnEntry.entryToUnEntry(Map.Entry<K,V> entry)
is now
org.organicdesign.fp.tuple.Tuple2.of(Map.Entry<K,V> entry)
```

##### Not Serializable (and will probably never be)
 - The function interfaces (Function0, Function1, etc.) will *NOT* implement Serializable.
    These interfaces are general and Serializable is too much for implementers to think about (and often irrelevant).
    However, the *constants* and *singletons* in those interfaces have been changed to implement
    Serializable.
 - Iterators are *NOT* serializable.  They aren't in java.util.Collections either.
   If you need an iterator to be serializable for some reason, open an issue and we'll discuss it.
 - Transient-HashSet, -HashMap, and -Vector are not Serializable.

## 2016-03-23 Release 1.0.3
 - Fixed error message for Xform.drop() to "Can't drop less than zero items #6." Thanks @pniederw
 - Fixed "Wrong bounds check in UnmodList.listIterator #7." Thanks @pniederw
 - Fixed "PersistentVector can't handle reverse iteration starting from last element #8."  Thanks @pniederw
 - Fixed RangeOfInt.listIterator and UnmodList and improved tests for same issues as above.
 - Improved test coverage of PersistentVector a little bit.

## 2016-03-22 Release 1.0.2
 - Improved speed of Transformable.toImList() by about 5x by using the Mutable version of the persistent vector internally.  This is thanks to a discussion with Peter Niederwieser @pniederw.  Thank you Peter!

## 2016-03-13 Release 1.0.1
 - Improved some documentation of the toMap methods, used K and V for the key and value types.
 - Otherwise, this has performed well without changes for 4 months - it's stable.
 - Added test dependency on TestUtils project instead of duplicating that code here.

## 2015-11-22 Release 0.12.1
 - Renamed overloaded toString() methods on FunctionUtils to mapToString() and arrayToString().

## 2015-11-16 Release 0.12.0
 - Removed Mutable class because it wasn't thread-safe.  Use java.util.concurrent.atomic.AtomicInteger and AtomicReference instead.
 - Added a comparator parameter to Transformable.toMutableSortedMap().

## 2015-11-15 Release 0.11.0
There are many changes in this point release, but unless you are writing your own collections by subclassing the Unmod interfaces, you probably won't notice.
The main push at this point is near 100% test coverage before a 1.0 release.

 - Deprecated UnmodList.contains().  See note there.  Maybe should have deprecated UnmodCollection.contains() instead?  This still functions the way java.util.List.contains() does, it's just an error to use it.
 - Deprecated UnmodMap.values().  See note there.  Removed all tests for equality on resulting collection.  This still functions the way java.util.Map.values() does, it's just an error to use it.
 Note: UnmodSortedMap.values() is *not* deprecated and now returns an UnmodList, which is appropriate and can be compared for equality.
 - Made null hash to 0 instead of Integer.MIN_VALUE in Equator and ComparisonContext.
 - Made StaticImports final - if someone gave me a good reason to subclass it, I would make it non-final.
 - Made PersistentVector.EMPTY public
 - Moved all Unmodxxxxx.EMPTY and Unmodxxxxx.empty() to FunctionUtils.EMPTY_UNMOD_XXXXX and .emptyUnmodXXXXXX().
 - Changed ComparisonContext helper method names from le to lte (for less-than-or-equal-to) and ge to gte.
 - Allowed ImSortedMap.entrySet() to return and UnmodSortedSet instead of an ImSortedSet.  I may go back on this.
 - Allowed ImSortedMap to inherit .values() from UnmodSortedSet.
 - Added abstract classes to UnmodCollection and UnmodMap to implement .equals() and .hashCode() for easier, correct implementations of these interfaces -
especially for implementing Map.keySet() and Map.entrySet().
 - Removed default implementation of UnmodCollection.contains().
 That's a critical component of a Collection and deserves to have subclasses implement it.
 Especially because it is used in containsAll().
 - Removed unSortIterEntToUnSortIterUnEnt() static method from UnmodMap.  That should be done with a cast, not with code.  Also, it was never used.
 - Added implementations of entrySet(), keySet(), and values() in UnmodMap.  This was painful, but it makes subclassing a snap.
 The implementations rely on AbstractUnmodSet and AbstractUnmodCollection for .equals() and .hashCode() implementations (see above).
 - Regenerated tuples to bring test coverage to 100% for most of them.
 - Removed .toArray() from Transformable.  I'm not promoting array use with Paguro, only providing it for backwards compatibility.
 I could bring it back for the right reason.
 - Added tests, tests, tests.  CodeCov now reports 86%, IntelliJ 92% coverage.

## 2015-10-28 Release 0.10.18
Removed default implementation of UnmodSortedSet.tailSet() because correct implementation there was not possible.
Fixed implementation of UnmodSortedSet.headSet() to be correct.
Removed UnmodList.insert because it was O(n).
Replacing it with a default implementation that's O(log2 n) will take time (a rough implementation is commented out).
Fixed some return types in UnmodSortedMap to be Unmod*Sorted*Sets/Collections instead of UnmodSets/Collections.
Changed which methods have default implementations in UnmodSortedMap to make it easier to subclass.
Improved test coverage by maybe 5%.

## 2015-10-28 Release 0.10.17
Made UnmodList implement listIterator(int i)
Made Collection.containsAll() convert to a Set to bring performance from O(n^2) to O(n).
Made UnmodSet implement containsAll() because that's the highest level where we can do it really fast.
Made default implementation of previousIndex() in UnmodListIterator because there is probably only one useful way to implement this method.
Improved test coverage.

**2015-10-24 0.10.16**: Fixed bug with RangeOfInt.sublist() when the second parameter was size
and the first parameter was between 1 and size - 1.  Added RangeOfInt.of(singleArg) to match the
same method in Clojure and Scala (resulting range is 0 inclusive to singleArg exclusive).
Also clarified that range is always inclusive of the low extent and exclusive of the high one
because this is how ranges in other languages behave.

**2015-10-07 0.10.15**: Added OneOf2 class to approximate a union type.
This is a little bit experimental - see the JavaDocs.

**2015-10-04 0.10.14**: *Release 1.0 Candidate* Removed unused code from PersistentHashMap.
 Making a release in case I made a mistake, so that people can A/B compare.

**2015-10-04 0.10.12**: *Release 1.0 Candidate* Added Tuple4 through Tuple12 plus tests for same.
These classes and tests are generated, not hand coded, so that we can produce more as needed.
I did not add corresponding StaticImports tup() methods for the new tuples.
There is a point where you should be defining classes instead of flinging tuples around.
I don't know where that point is, I suspect it's around Tuple5 or 6.
I can never take things away from StaticImports, so I want to be sure before adding them.
Feedback on this is appreciated.

**2015-10-03 0.10.11**: Summary: just some changes brought about by code review.

 * Removed SideEffect (zero argument function that returns null).  Use `Function0<?>` instead.
 * Removed the Realizable interface and simply moved those methods to Transformable.
 * Made UnmodList extend UnmodSortedCollection instead of UnmodCollection and UnmodSortedIterable.

**2015-09-15 0.10.10**: Changed groupId to org.organicdesign (removed the .fp).  First build deployed to Sonatype repository.

**2015-09-14 0.10.9**: Made tuple fields protected so that sub-classes can wrap them more efficiently in well-named accessor methods.
Also updated docs and fixed one unit test to be within 'profile compact1'.
*Note:* Running tests inside IntelliJ ignores this compiler option, so tests must be run from the command line to ensure compliance.

**2015-09-12 0.10.8**: Renamed ImList.appendOne() to just ImList.append().
Plus, lots of documentation changes.

2015-09-09 version 0.10.7: *Beta!*  Compiled with -profile compact1.

2015-09-08 version 0.10.6 Fixed bug: Xform would blow up later if you passed a null to its
constructor.

2015-09-08 version 0.10.5 moved all unmod____ method wrappers from StaticImports to FunctionUtils
because they just aren't nearly as useful as they used to be.  xform() has taken over nearly 100%
of the use cases for the unmod____ methods.  Removed Varargs methods from everything except the
three data definition functions in StaticImports.

2015-09-08 version 0.10.3 added xform() method to StaticImports as a convenient and more efficient
way to start a transformation than by using the unmod____ wrappers.

2015-08-30 version 0.10.2 Xform tests at 100%.  Applied Xform to UnmodIterable which makes it
apply to the entire project.  Completely removed all traces of View and Sequence.

2015-08-30 version 0.10.1 Added Xform and moved Transformable and Realizable into the new xform
package.  Xform should replace Sequence and View altogether.

2015-08-25 version 0.10.0 Renamed most methods in StaticImports.  This is why I've been calling this
*alpha*-quality code.  The four methods map(), set(), tup(), and vec() comprise a mini
data-definition language.  It's wordier than JSON, but still brief for Java and fairly brief
over-all.  Of those four methods, only tup() uses overloading to take heterogeneous arguments.
The other three are the only places in this project that use varargs.  The unmod() methods
have all been renamed to unmodSortedSet() or whatever to avoid overloading with the same number
of arguments and bring it in line with Josh Bloch's Item 41.  This project will be *alpha* still,
at least until the new TransDesc code from the One-Off-Examples project is merged into here.
That code should replace Sequence and View.

2015-08-24 version 0.9.14 Made Tuple2 and Tuple3 non-final and made constructors public for extra
and easy inheritance.

2015-08-23 version 0.9.12 Removed `Transformable<T> forEach(Function1<? super T,?> consumer)`.
See reasons in the "Out of Scope" section below.

2015-08-13 version 0.9.11: Added `RangeOfInt` class as an efficient (in both time and memory)
implementation of `List<Integer>`.  If you want to compare a `RangeOfInt` to generic `List<Integer>`, use
`RangeOfInt.LIST_EQUATOR` so that the hashCodes will be compatible.

2015-07-28 version 0.9.10: Changed toTypedArray() to toArray() because the former was not type safe in a way that would blow up only at runtime.  The latter is still provided for backwards compatibility (particularly useful in jUnit tests).

2015-07-25 version 0.9.9: Renamed methods in staticImports imList() to vec(), imSet() to hSet() (think: "hashSet()"),
imSortedSet to tSet() (think: "treeSet()"), imSortedMap to tMap(), etc.  Also removed the telescoping
methods in favor of just passing vec(tup(1, "one"), tup(2, "two"), tup(3, "three"));  It's maybe a little more work,
but a little less cognitive load and a lot less testing!  Also added Mutable.intRef.decrement().
Renamed Sequence.of() to .ofArray() and similarly with View.  I may rename Sequence/View.ofIter() to
just .of() in a future version, but then, I'm probably going to remove Sequence and replace View
with Transform too.  The interface to Transform is not like View without head() and tail() and it's immutable and faster.

2015-06-23 version 0.9.8: Added union(Iterable i) method to ImSet.

2015-06-12 version 0.9.7: Renamed classes and methods so that the unmodifiable prefix is now "unmod" instead of "un".
Changed XxxxOrdered to SortedXxxx to be more compatible with Java naming conventions.
Added Equator to HashMap and HashSet so you can define your own ComparisonContext now.

2015-06-07 version 0.9.6: Added PersistentHashMap and PersistentHashSet from Clojure with some tests for the same.

2015-06-04 version 0.9.5: Renamed everything from Sorted to Ordered.
Added an UnIteratorOrdered that extends UnIterator.  Same methods, just with an ordering guarantee.
Made UnMap and UnSet extend UnIterator, UnMapOrdered and UnSetOrdered extend UnIteratorOrdered.
Deleted some unnecessary wrapping methods in StaticImports.

2015-06-02 version 0.9.4: Renamed methods so that append/prepend means to add one item, while concat/precat means to add many items.
Changed ImList.put() to ImList.replace() to clarify how it's different from inserting (it doesn't push subsequent items to the right).
Made ImList and PersistentVector implement Sequence.
Changed everything that wrapped an Iterator to take an Iterable instead - can't trust an iterator that's been exposed to other code.
Test coverage was above 85% by line at one point.

2015-05-24 version 0.9.3: Made TreeSet and TreeMap.comparator() return null when the default comparator is used (to
match the contract in SortedMap and SortedSet).

2015-05-24 version 0.9.2: Moved experiments to my One-off_Examples project.

2015-05-24 version 0.9.1: Renamed project from J-cicle to UncleJim.

2015-05-13 Release 0.9 alpha which packages type-safe versions of the Clojure collections and sequence abstraction for Java.
- 3 Immutable collections: [PersistentVector](src/main/java/org/organicdesign/fp/collections/PersistentVector.java), [PersistentTreeMap](src/main/java/org/organicdesign/fp/collections/PersistentTreeMap.java), and [PersistentTreeSet](src/main/java/org/organicdesign/fp/collections/PersistentTreeSet.java).  None of these use equals() or hashcode().
Vector doesn't need to and Map and Set take a Comparator.
- Un-collections which are the Java collection interfaces, but unmodifiable.  These interfaces deprecate the mutator methods and implement them to throw exceptions.  Plus, UnMap implements UnIterable<UnMap.UnEntry<K,V>>.
- Im-collections which add functional "mutator" methods that return a new collection reflecting the change, leaving the old collection unchanged.
- Basic sequence abstraction on the Im- versions of the above.
- Function interfaces that manage exceptions and play nicely with java.util.function.* when practical.
- Memoization methods on functional interfaces.

2015-04-05 version 0.8.2:
- Renamed Sequence.first() and .rest() to .head() and .tail() so that they wouldn't conflict with TreeSet.first()
which returns a T instead of an Option<T>.  This was a difficult decision and I actually implemented all of Sequence
except for flatMap with first() returning a T.  All the functions that could return fewer items that were previously
lazy became eager.  Flatmap became eager, but also became very difficult to implement correctly.  View is already eager,
so I renamed the methods to use the more traditional FP names and restored the Option<T>.  If you don't like the names,
just be glad I didn't use car and cdr.

2015-04-05 version 0.8.1:
- Renamed FunctionX.apply_() to just apply() to match java.util.function interfaces.
 Renamed FunctionX.apply() to applyEx() but this is still what you implement and it can throw an exception.
 Made FunctionX.apply() methods rethrow RuntimeExceptions unchanged, but (still) wrap checked Exceptions in RuntimeExceptions.
 They were previously wrapped in IllegalStateExceptions, except for SideEffect which tried to cast the exception which never worked.
- Added all the functions to Sequence that were previously only in View, plus tests for same.
- Re-implemented Sequence abstraction using LazyRef.
- SideEffect has been deprecated because it may not have been used anywhere.
- Added some tests, improved some documentation, and made a bunch of things private or deleted them in experiments.collections.ImVectorImpl.

2015-03-14 version 0.8.0: Removed firstMatching - in your code, replace firstMatching(...) with filter(...).head().
Implemented filter() on Sequence.
