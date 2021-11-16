package ahodanenok.relational;

import ahodanenok.relational.algebra.JoinOperator;
import ahodanenok.relational.exception.RelationalException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JoinOperatorTest {

    @Test
    public void shouldJoin0Relation() {
        assertEquals(Relation.NULLARY_TUPLE, new JoinOperator(Relation.NULLARY_TUPLE, Relation.NULLARY_TUPLE).execute());
        assertEquals(Relation.NULLARY_EMPTY, new JoinOperator(Relation.NULLARY_TUPLE, Relation.NULLARY_EMPTY).execute());
        assertEquals(Relation.NULLARY_EMPTY, new JoinOperator(Relation.NULLARY_EMPTY, Relation.NULLARY_TUPLE).execute());
        assertEquals(Relation.NULLARY_EMPTY, new JoinOperator(Relation.NULLARY_EMPTY, Relation.NULLARY_EMPTY).execute());
    }

    @Test
    public void shouldJoinEmptyRelations() {
        RelationSchema schemaA = new RelationSchemaGenerator()
                .withAttribute("a", Integer.class)
                .withAttribute("b", Boolean.class)
                .generate();
        Relation a = new RelationSelector().withSchema(schemaA).select();

        RelationSchema schemaB = new RelationSchemaGenerator()
                .withAttribute("b", Boolean.class)
                .withAttribute("d", String.class)
                .generate();
        Relation b = new RelationSelector().withSchema(schemaB).select();

        Relation result = new JoinOperator(a, b).execute();

        RelationSchema resultSchema = new RelationSchemaGenerator()
                .withAttribute("a", Integer.class)
                .withAttribute("b", Boolean.class)
                .withAttribute("d", String.class)
                .generate();
        Relation expected = new RelationSelector().withSchema(resultSchema).select();

        assertEquals(expected, result);
    }

    @Test
    public void shouldThrowErrorIfCommonAttributeTypesNotEqual() {
        RelationSchema schemaA = new RelationSchemaGenerator()
                .withAttribute("a", Integer.class)
                .withAttribute("b", Boolean.class)
                .generate();
        Relation a = new RelationSelector().withSchema(schemaA).select();

        RelationSchema schemaB = new RelationSchemaGenerator()
                .withAttribute("b", Integer.class)
                .withAttribute("d", String.class)
                .generate();
        Relation b = new RelationSelector().withSchema(schemaB).select();

        JoinOperator op = new JoinOperator(a, b);

        RelationalException e = assertThrows(RelationalException.class, op::execute);
        assertEquals("Can't add attribute 'b' of type 'java.lang.Integer' as it has already been added with type 'java.lang.Boolean'", e.getMessage());
    }

    @Test
    public void shouldJoinRelationsWithoutCommonAttributes() {
        Relation a = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "a1").withValue("b", "b11").select())
                .addTuple(new TupleSelector().withValue("a", "a2").withValue("b", "b22").select())
                .select();
        Relation b = new RelationSelector()
                .addTuple(new TupleSelector().withValue("c", "c1").withValue("d", "d11").select())
                .addTuple(new TupleSelector().withValue("c", "c2").withValue("d", "d22").select())
                .select();

        Relation result = new JoinOperator(a, b).execute();

        Relation expected = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "a1").withValue("b", "b11").withValue("c", "c1").withValue("d", "d11").select())
                .addTuple(new TupleSelector().withValue("a", "a1").withValue("b", "b11").withValue("c", "c2").withValue("d", "d22").select())
                .addTuple(new TupleSelector().withValue("a", "a2").withValue("b", "b22").withValue("c", "c1").withValue("d", "d11").select())
                .addTuple(new TupleSelector().withValue("a", "a2").withValue("b", "b22").withValue("c", "c2").withValue("d", "d22").select())
                .select();

        assertEquals(expected, result);
    }

    @Test
    public void shouldJoinRelationsWithSomeCommonAttributes() {
        Relation a = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "a1").withValue("b", "b1").select())
                .addTuple(new TupleSelector().withValue("a", "a2").withValue("b", "b2").select())
                .addTuple(new TupleSelector().withValue("a", "a3").withValue("b", "b3").select())
                .addTuple(new TupleSelector().withValue("a", "a4").withValue("b", "b4").select())
                .select();
        Relation b = new RelationSelector()
                .addTuple(new TupleSelector().withValue("b", "b5").withValue("c", "c5").select())
                .addTuple(new TupleSelector().withValue("b", "b4").withValue("c", "c4").select())
                .addTuple(new TupleSelector().withValue("b", "b1").withValue("c", "c1").select())
                .select();

        Relation result = new JoinOperator(a, b).execute();

        Relation expected = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "a1").withValue("b", "b1").withValue("c", "c1").select())
                .addTuple(new TupleSelector().withValue("a", "a4").withValue("b", "b4").withValue("c", "c4").select())
                .select();

        assertEquals(expected, result);
    }

    @Test
    public void shouldJoinRelationsWithoutAllCommonAttributes() {
        Relation a = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "a1").withValue("b", "b1").select())
                .addTuple(new TupleSelector().withValue("a", "a2").withValue("b", "b2").select())
                .addTuple(new TupleSelector().withValue("a", "a3").withValue("b", "b3").select())
                .select();
        Relation b = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "a0").withValue("b", "b0").select())
                .addTuple(new TupleSelector().withValue("a", "a2").withValue("b", "b2").select())
                .addTuple(new TupleSelector().withValue("a", "a3").withValue("b", "b3").select())
                .addTuple(new TupleSelector().withValue("a", "a5").withValue("b", "b5").select())
                .select();

        Relation result = new JoinOperator(a, b).execute();

        Relation expected = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "a2").withValue("b", "b2").select())
                .addTuple(new TupleSelector().withValue("a", "a3").withValue("b", "b3").select())
                .select();

        assertEquals(expected, result);
    }

    @Test
    public void shouldThrowErrorIfRelationIsNull() {
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> new JoinOperator(null, Relation.NULLARY_TUPLE));
        assertEquals("Relation can't be null: left", e1.getMessage());

        NullPointerException e2 = assertThrows(NullPointerException.class, () -> new JoinOperator(Relation.NULLARY_TUPLE, null));
        assertEquals("Relation can't be null: right", e2.getMessage());
    }
}
