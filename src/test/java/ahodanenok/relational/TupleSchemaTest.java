package ahodanenok.relational;

import ahodanenok.relational.exception.AttributeNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TupleSchemaTest {

    @Test
    public void shouldGenerateSchemaWithoutAttributes() {
        TupleSchema schema = new TupleSchemaGenerator().generate();
        assertEquals(0, schema.degree());
        assertEquals(0, schema.getAttributes().size());
    }

    @Test
    public void shouldGenerateSchemaWithOneAttribute() {
        TupleSchema schema = new TupleSchemaGenerator()
                .withAttribute(new Attribute("a", int.class))
                .generate();

        assertEquals(1, schema.degree());
        assertEquals(1, schema.getAttributes().size());
        assertEquals(Collections.singleton(new Attribute("a", int.class)), schema.getAttributes());
        assertTrue(schema.hasAttribute("a"));
        assertEquals(new Attribute("a", int.class), schema.getAttribute("a"));
    }

    @Test
    public void shouldGenerateSchemaWithMultipleAttributes() {
        TupleSchema schema = new TupleSchemaGenerator()
                .withAttribute("a", int.class)
                .withAttribute("b", String.class)
                .withAttribute("c", Byte.class)
                .withAttribute("d", char.class)
                .withAttribute("e", Boolean.class)
                .generate();

        assertEquals(5, schema.degree());
        assertEquals(5, schema.getAttributes().size());

        assertTrue(schema.hasAttribute("a"));
        assertEquals(new Attribute("a", int.class), schema.getAttribute("a"));

        assertTrue(schema.hasAttribute("b"));
        assertEquals(new Attribute("b", String.class), schema.getAttribute("b"));

        assertTrue(schema.hasAttribute("c"));
        assertEquals(new Attribute("c", Byte.class), schema.getAttribute("c"));

        assertTrue(schema.hasAttribute("d"));
        assertEquals(new Attribute("d", char.class), schema.getAttribute("d"));

        assertTrue(schema.hasAttribute("e"));
        assertEquals(new Attribute("e", Boolean.class), schema.getAttribute("e"));

        Set<Attribute> expectedAttributes = new HashSet<>();
        expectedAttributes.add(new Attribute("e", Boolean.class));
        expectedAttributes.add(new Attribute("d", char.class));
        expectedAttributes.add(new Attribute("c", Byte.class));
        expectedAttributes.add(new Attribute("b", String.class));
        expectedAttributes.add(new Attribute("a", int.class));
        assertEquals(expectedAttributes, schema.getAttributes());
    }

    @Test
    public void shouldGenerateSchemaWithRepeatingAttributes() {
        TupleSchema schema = new TupleSchemaGenerator()
                .withAttribute("a", Long.class)
                .withAttribute("b", Boolean.class)
                .withAttribute("a", Long.class)
                .generate();

        assertEquals(2, schema.degree());
        assertEquals(2, schema.getAttributes().size());

        Set<Attribute> expectedAttributes = new HashSet<>();
        expectedAttributes.add(new Attribute("a", Long.class));
        expectedAttributes.add(new Attribute("b", Boolean.class));
        assertEquals(expectedAttributes, schema.getAttributes());

        assertTrue(schema.hasAttribute("a"));
        assertEquals(new Attribute("a", Long.class), schema.getAttribute("a"));

        assertTrue(schema.hasAttribute("b"));
        assertEquals(new Attribute("b", Boolean.class), schema.getAttribute("b"));
    }

    @Test
    public void shouldBeEqualIfAttributesMatch() {
        TupleSchema schemaA = new TupleSchemaGenerator()
                .withAttribute(new Attribute("a", Integer.class))
                .withAttribute("b", String.class)
                .withAttribute(new Attribute("c", Byte.class))
                .generate();

        TupleSchema schemaB = new TupleSchemaGenerator()
                .withAttribute(new Attribute("a", Integer.class))
                .withAttribute("b", String.class)
                .withAttribute(new Attribute("c", Byte.class))
                .generate();

        assertEquals(schemaA, schemaB);
        assertEquals(schemaB, schemaA);
        assertEquals(schemaA.hashCode(), schemaB.hashCode());
    }

    @Test
    public void shouldNotBeEqualIfDegreesDiffer() {
        TupleSchema schemaA = new TupleSchemaGenerator()
                .withAttribute(new Attribute("a", Integer.class))
                .withAttribute("b", String.class)
                .withAttribute(new Attribute("c", Byte.class))
                .generate();

        TupleSchema schemaB = new TupleSchemaGenerator()
                .withAttribute(new Attribute("a", Integer.class))
                .withAttribute("b", String.class)
                .generate();

        assertNotEquals(schemaA, schemaB);
        assertNotEquals(schemaB, schemaA);
    }

    @Test
    public void shouldNotBeEqualIfTypesDiffer() {
        TupleSchema schemaA = new TupleSchemaGenerator()
                .withAttribute(new Attribute("a", Integer.class))
                .withAttribute("b", String.class)
                .withAttribute(new Attribute("c", Byte.class))
                .generate();

        TupleSchema schemaB = new TupleSchemaGenerator()
                .withAttribute(new Attribute("a", Long.class))
                .withAttribute("b", String.class)
                .withAttribute("c", Byte.class)
                .generate();

        assertNotEquals(schemaA, schemaB);
        assertNotEquals(schemaB, schemaA);
    }

    @Test
    public void shouldNotBeEqualIfNamesDiffer() {
        TupleSchema schemaA = new TupleSchemaGenerator()
                .withAttribute(new Attribute("a", Integer.class))
                .withAttribute(new Attribute("b", String.class))
                .withAttribute(new Attribute("n", Byte.class))
                .generate();

        TupleSchema schemaB = new TupleSchemaGenerator()
                .withAttribute(new Attribute("a", Integer.class))
                .withAttribute(new Attribute("b", String.class))
                .withAttribute(new Attribute("N", Byte.class))
                .generate();

        assertNotEquals(schemaA, schemaB);
        assertNotEquals(schemaB, schemaA);
    }

    @Test
    public void shouldTrimAttributeNameDuringLookup() {
        TupleSchema schema = new TupleSchemaGenerator().withAttribute("phone", String.class).generate();
        assertEquals(new Attribute("phone", String.class), schema.getAttribute("\n  phone \t"));
    }

    @Test
    public void shouldThrowErrorIfAttributeNameToLookupIsNull() {
        TupleSchema schema = new TupleSchemaGenerator().withAttribute("123", boolean.class).generate();

        NullPointerException e1 = assertThrows(NullPointerException.class, () -> schema.getAttribute(null));
        assertEquals("Attribute name can't be null", e1.getMessage());

        NullPointerException e2 = assertThrows(NullPointerException.class, () -> schema.hasAttribute(null));
        assertEquals("Attribute name can't be null", e2.getMessage());
    }

    @Test
    public void shouldThrowErrorIfAttributeNotFound() {
        TupleSchema schema = new TupleSchemaGenerator().withAttribute("123", boolean.class).generate();

        AttributeNotFoundException e = assertThrows(AttributeNotFoundException.class, () -> schema.getAttribute("abc"));
        assertEquals("Attribute 'abc' not found", e.getMessage());
        assertEquals("abc", e.getName());
    }
}
