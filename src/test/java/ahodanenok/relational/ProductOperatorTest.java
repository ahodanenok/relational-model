package ahodanenok.relational;

import ahodanenok.relational.algebra.ProductOperator;
import ahodanenok.relational.algebra.UnionOperator;
import ahodanenok.relational.exception.AttributeAlreadyExistsException;
import ahodanenok.relational.exception.RelationSchemaMismatchException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProductOperatorTest {

    @Test
    public void shouldMultiply0Relation() {
        assertEquals(Relation.EMPTY, new ProductOperator(Relation.EMPTY, Relation.EMPTY).execute());
    }

    @Test
    public void shouldMultiplyEmptyRelations() {
        RelationSchema schemaA = new RelationSchemaGenerator()
                .withAttribute("a", Integer.class)
                .withAttribute("b", Boolean.class)
                .generate();
        Relation a = new RelationSelector().withSchema(schemaA).select();

        RelationSchema schemaB = new RelationSchemaGenerator()
                .withAttribute("c", Integer.class)
                .withAttribute("d", Boolean.class)
                .generate();
        Relation b = new RelationSelector().withSchema(schemaB).select();

        Relation result = new ProductOperator(a, b).execute();

        RelationSchema resultSchema = new RelationSchemaGenerator()
                .withAttribute("a", Integer.class)
                .withAttribute("b", Boolean.class)
                .withAttribute("c", Integer.class)
                .withAttribute("d", Boolean.class)
                .generate();
        Relation expected = new RelationSelector().withSchema(resultSchema).select();

        assertEquals(expected, result);
    }

    @Test
    public void shouldMultiplyRelations() {
        Relation a = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "a1").withValue("b", "b11").select())
                .addTuple(new TupleSelector().withValue("a", "a2").withValue("b", "b22").select())
                .select();
        Relation b = new RelationSelector()
                .addTuple(new TupleSelector().withValue("c", "c1").withValue("d", "d11").select())
                .addTuple(new TupleSelector().withValue("c", "c2").withValue("d", "d22").select())
                .select();

        Relation result = new ProductOperator(a, b).execute();

        Relation expected = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "a1").withValue("b", "b11").withValue("c", "c1").withValue("d", "d11").select())
                .addTuple(new TupleSelector().withValue("a", "a1").withValue("b", "b11").withValue("c", "c2").withValue("d", "d22").select())
                .addTuple(new TupleSelector().withValue("a", "a2").withValue("b", "b22").withValue("c", "c1").withValue("d", "d11").select())
                .addTuple(new TupleSelector().withValue("a", "a2").withValue("b", "b22").withValue("c", "c2").withValue("d", "d22").select())
                .select();

        assertEquals(expected, result);
    }

    @Test
    public void shouldThrowErrorIfCommonAttributes() {
        Relation a = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .select();
        Relation b = new RelationSelector()
                .addTuple(new TupleSelector().withValue("b", "2").withValue("c", "22").select())
                .select();

        ProductOperator op1 = new ProductOperator(a, b);
        AttributeAlreadyExistsException e1 = assertThrows(AttributeAlreadyExistsException.class, op1::execute);
        assertEquals("b", e1.getName());

        ProductOperator op2 = new ProductOperator(b, a);
        AttributeAlreadyExistsException e2 = assertThrows(AttributeAlreadyExistsException.class, op2::execute);
        assertEquals("b", e2.getName());
    }

    @Test
    public void shouldThrowErrorIfRelationIsNull() {
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> new ProductOperator(null, Relation.EMPTY));
        assertEquals("relation 'a' can't be null", e1.getMessage());

        NullPointerException e2 = assertThrows(NullPointerException.class, () -> new ProductOperator(Relation.EMPTY, null));
        assertEquals("relation 'b' can't be null", e2.getMessage());
    }
}
