**2016-03-13 Release 1.0.2**:
 - Improved speed of Transformable.toImList() by about 5x by using the Mutable version of the persistent vector internally.  This is thanks to a discussion with Peter Niederwieser @pniederw.  Thank you Peter!

**2016-03-13 Release 1.0.1**:
 - Improved some documentation of the toMap methods, used K and V for the key and value types.
 - Otherwise, this has performed well without changes for 4 months - it's stable.
 - Added test dependency on TestUtils project instead of duplicating that code here.

**2015-11-22 0.12.1**:
 - Renamed overloaded toString() methods on FunctionUtils to mapToString() and arrayToString().

**2015-11-16 0.12.0**:
 - Removed Mutable class because it wasn't thread-safe.  Use java.util.concurrent.atomic.AtomicInteger and AtomicReference instead.
 - Added a comparator parameter to Transformable.toMutableSortedMap().

**2015-11-15 0.11.0**: There are many changes in this point release, but unless you are writing your own collections by subclassing the Unmod interfaces, you probably won't notice.
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
 - Removed .toArray() from Transformable.  I'm not promoting array use with UncleJim, only providing it for backwards compatibility.
 I could bring it back for the right reason.
 - Added tests, tests, tests.  CodeCov now reports 86%, IntelliJ 92% coverage.

**2015-10-28 0.10.18**: Removed default implementation of UnmodSortedSet.tailSet() because correct implementation there was not possible.
Fixed implementation of UnmodSortedSet.headSet() to be correct.
Removed UnmodList.insert because it was O(n).
Replacing it with a default implementation that's O(log2 n) will take time (a rough implementation is commented out).
Fixed some return types in UnmodSortedMap to be Unmod*Sorted*Sets/Collections instead of UnmodSets/Collections.
Changed which methods have default implementations in UnmodSortedMap to make it easier to subclass.
Improved test coverage by maybe 5%.

**2015-10-28 0.10.17**: Made UnmodList implement listIterator(int i)
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
