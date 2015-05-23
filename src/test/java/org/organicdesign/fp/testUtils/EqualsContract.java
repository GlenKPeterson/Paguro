package org.organicdesign.fp.testUtils;

import java.util.Arrays;
import java.util.List;

import org.organicdesign.fp.collections.UnList;

import static org.junit.Assert.*;

public class EqualsContract {
    /**
     Tests Reflexive, Symmetric, Transitive, Consistent, and non-nullity properties
     of the equals() contract.  If you think this is confusing, realize that there is no
     way to implement a one-sided equals() correctly with inheritence - it's a broken concept, but it's
     still used so often that you have to do your best with it.
     @param equiv1 First equivalent (but unique) object
     @param equiv2 Second equivalent (but unique) object (could be a different class)
     @param equiv3 Third equivalent (but unique) object (could be a different class)
     @param different Non-equivalent object with a different hashCode (should be a compatible class)
     @param <S> The super-class of all these objects - could be an interface that these should be equal within.
     */
    public static <S, T1 extends S, T2 extends S, T3 extends S, T4 extends S>
    void equalsHashCode(T1 equiv1, T2 equiv2, T3 equiv3, T4 different) {
        if ( (equiv1 == equiv2) ||
             (equiv1 == equiv3) ||
             (equiv1 == different) ||
             (equiv2 == equiv3) ||
             (equiv2 == different) ||
             (equiv3 == different) ) {
            throw new IllegalArgumentException("You must provide four different (having different memory locations) but 3 equivalent objects");
        }
        List<S> equivs = Arrays.asList(equiv1, equiv2, equiv3);

        //noinspection ObjectEqualsNull
        assertFalse(different.equals(null));
        assertEquals(different.hashCode(), different.hashCode());
        //noinspection EqualsWithItself
        assertTrue(different.equals(different));

        // Reflexive
        for(S equiv : equivs) {
            assertEquals(equiv.hashCode(), equiv.hashCode());
            assertNotEquals(equiv.hashCode(), different.hashCode());
            //noinspection EqualsWithItself
            assertTrue(equiv.equals(equiv));
            assertFalse(equiv.equals(different));
            assertFalse(different.equals(equiv));

            // Check null
            //noinspection ObjectEqualsNull
            assertFalse(equiv.equals(null));
        };

        // Symmetric (effectively covers Transitive as well)
        UnList.permutations(equivs, (a, b) -> {
            assertEquals(a.hashCode(), b.hashCode());
            assertTrue(a.equals(b));
            assertTrue(b.equals(a));
            return null;
        });
    }
}
