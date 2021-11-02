package ahodanenok.relational;

import ahodanenok.relational.exception.AttributeNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TupleSchemaTest {

    @Test
    public void shouldGenerateEmptySchema() {
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
        assertEquals(new Attribute("a", int.class), schema.getAttribute("a"));
    }

    @Test
    public void shouldGenerateSchemaWithMultipleAttributes() {
        TupleSchema schema = new TupleSchemaGenerator()
                .withAttribute(new Attribute("a", int.class))
                .withAttribute("b", String.class)
                .withAttribute(new Attribute("c", Byte.class))
                .withAttribute("d", char.class)
                .withAttribute(new Attribute("e", Boolean.class))
                .generate();

        assertEquals(5, schema.degree());
        assertEquals(5, schema.getAttributes().size());

        assertEquals(new Attribute("a", int.class), schema.getAttribute("a"));
        assertEquals(new Attribute("b", String.class), schema.getAttribute("b"));
        assertEquals(new Attribute("c", Byte.class), schema.getAttribute("c"));
        assertEquals(new Attribute("d", char.class), schema.getAttribute("d"));
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

        NullPointerException e = assertThrows(NullPointerException.class, () -> schema.getAttribute(null));
        assertEquals("name can't be null", e.getMessage());
    }

    @Test
    public void shouldThrowErrorIfAttributeNotFound() {
        TupleSchema schema = new TupleSchemaGenerator().withAttribute("123", boolean.class).generate();

        AttributeNotFoundException e = assertThrows(AttributeNotFoundException.class, () -> schema.getAttribute("abc"));
        assertEquals("Attribute 'abc' not found", e.getMessage());
        assertEquals("abc", e.getName());
    }
}
