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
        assertEquals("nAmE", new Attribute("nAmE", String.class).getName());;
        assertEquals("NAME", new Attribute("NAME", String.class).getName());;
        assertEquals(" N a M e ", new Attribute(" N a M e ", String.class).getName());;
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
