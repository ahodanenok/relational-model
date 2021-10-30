package ahodanenok.relational;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TupleTest {

    @Test
    public void shouldSelectTupleWithoutAttributes() {
        Tuple tuple = new TupleSelector().select();
        assertEquals(0, tuple.degree());
        assertEquals(0, tuple.getAttributes().size());
    }

    @Test
    public void shouldSelectTupleWithOneAttribute() {
        Tuple tuple = new TupleSelector().withValue("test", 100).select();
        assertEquals(1, tuple.degree());
        assertEquals(1, tuple.getAttributes().size());

        Attribute attribute = tuple.getAttributes().iterator().next();
        assertEquals("test", attribute.getName());
        assertEquals(Integer.class, attribute.getType());

        assertEquals(100, tuple.getValue("test"));
    }

    @Test
    public void shouldSelectTupleWithMultipleAttributes() {
        Tuple tuple = new TupleSelector()
                .withValue("a", 100L)
                .withValue("b", "hello!")
                .withValue("c", true)
                .withValue("d", new int[] { 1, 2, 3 })
                .select();

        assertEquals(4, tuple.degree());
        assertEquals(4, tuple.getAttributes().size());

        Set<Attribute> expectedAttributes = new HashSet<>();
        expectedAttributes.add(new Attribute("a", Long.class));
        expectedAttributes.add(new Attribute("b", String.class));
        expectedAttributes.add(new Attribute("c", Boolean.class));
        expectedAttributes.add(new Attribute("d", int[].class));
        assertEquals(expectedAttributes, tuple.getAttributes());

        assertEquals(100L, tuple.getValue("a"));
        assertEquals("hello!", tuple.getValue("b"));
        assertEquals(true, tuple.getValue("c"));
        assertArrayEquals(new int[] { 1, 2, 3 }, (int[]) tuple.getValue("d"));
    }


    @Test
    public void shouldBeEqualWithTheSameAttributesAndValues() {
        Tuple tupleA = new TupleSelector()
                .withValue("a", '&')
                .withValue("b", "hello!")
                .withValue("c", true)
                .select();

        Tuple tupleB = new TupleSelector()
                .withValue("c", true)
                .withValue("b", "hello!")
                .withValue("a", '&')
                .select();

        assertEquals(tupleA, tupleB);
        assertEquals(tupleB, tupleA);
        assertEquals(tupleA.hashCode(), tupleB.hashCode());
    }
}
