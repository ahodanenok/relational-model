package ahodanenok.relational;

import ahodanenok.relational.exception.RelationalException;
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
    public void shouldOverwriteAttributeValue() {
        Tuple tuple = new TupleSelector()
                .withValue("a", 100L)
                .withValue("b", "hello!")
                .withValue("a", 11L)
                .select();

        assertEquals(2, tuple.degree());
        assertEquals(2, tuple.getAttributes().size());

        Set<Attribute> expectedAttributes = new HashSet<>();
        expectedAttributes.add(new Attribute("a", Long.class));
        expectedAttributes.add(new Attribute("b", String.class));
        assertEquals(expectedAttributes, tuple.getAttributes());

        assertEquals(11L, tuple.getValue("a"));
        assertEquals("hello!", tuple.getValue("b"));
    }

    @Test
    public void shouldThrowErrorIfAttributeTypesDifferent() {
        RelationalException e = assertThrows(RelationalException.class, () -> {
            new TupleSelector()
                    .withValue("a", 100L)
                    .withValue("b", "hello!")
                    .withValue("a", 11);
        });

        assertEquals("Can't change attribute type for 'a', initial type is 'java.lang.Long', current type is 'java.lang.Integer'", e.getMessage());
    }

    @Test
    public void shouldThrowErrorIfAttributeValueDiffersFromAttributeType() {
        RelationalException e = assertThrows(RelationalException.class, () -> {
            new TupleSelector().withValue(new Attribute("test", Integer.class), 100L);
        });

        assertEquals("Value type 'java.lang.Long' for attribute 'test' doesn't equal its type 'java.lang.Integer'", e.getMessage());
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
