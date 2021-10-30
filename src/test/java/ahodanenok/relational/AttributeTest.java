package ahodanenok.relational;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AttributeTest {

    @Test
    public void shouldCreateAttribute() {
        Attribute a = new Attribute("test", Integer.class);
        assertEquals("test", a.getName());
        assertEquals(Integer.class, a.getType());
    }

    @Test
    public void shouldNotChangeName() {
        assertEquals("nAmE", new Attribute("nAmE", String.class).getName());
        assertEquals("NAME", new Attribute("NAME", String.class).getName());
        assertEquals("N a M e", new Attribute("N a M e", String.class).getName());
    }

    @Test
    public void shouldTrimName() {
        assertEquals("A", new Attribute(" A ", String.class).getName());
        assertEquals("b", new Attribute("\tb\r", String.class).getName());
        assertEquals("c", new Attribute("c\n", String.class).getName());
    }

    @Test
    public void shouldThrowErrorIfNameIsNotValid() {
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> new Attribute(null, String.class));
        assertEquals("name can't be null", e1.getMessage());

        IllegalArgumentException e2 = assertThrows(IllegalArgumentException.class, () -> new Attribute("   ", String.class));
        assertEquals("name can't be empty", e2.getMessage());
    }

    @Test
    public void shouldThrowErrorIfTypeIsNotValid() {
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> new Attribute("abc", null));
        assertEquals("type can't be null", e1.getMessage());
    }

    @Test
    public void testEquals() {
        assertEquals(new Attribute("a", Number.class), new Attribute("a", Number.class));
        assertEquals(new Attribute("a", Number.class).hashCode(), new Attribute("a", Number.class).hashCode());

        assertNotEquals(new Attribute("a", Number.class), null);
        assertNotEquals(new Attribute("a", Number.class), 10);
        assertNotEquals(new Attribute("a", Number.class), new Attribute("a", Object.class));
        assertNotEquals(new Attribute("a", Number.class), new Attribute("a", Long.class));
        assertNotEquals(new Attribute("A", Number.class), new Attribute("a", Number.class));
        assertNotEquals(new Attribute("b", Number.class), new Attribute("a", Number.class));
    }
}
