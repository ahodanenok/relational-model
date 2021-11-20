package ahodanenok.relational;

import ahodanenok.relational.algebra.DifferenceOperator;
import ahodanenok.relational.exception.RelationSchemaMismatchException;
import ahodanenok.relational.expression.IdentityExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DifferenceOperatorTest {

    @Test
    public void shouldSubtract0Relation() {
        assertEquals(Relation.NULLARY_EMPTY, new DifferenceOperator(new IdentityExpression(Relation.NULLARY_TUPLE), new IdentityExpression(Relation.NULLARY_TUPLE)).execute());
        assertEquals(Relation.NULLARY_TUPLE, new DifferenceOperator(new IdentityExpression(Relation.NULLARY_TUPLE), new IdentityExpression(Relation.NULLARY_EMPTY)).execute());
        assertEquals(Relation.NULLARY_EMPTY, new DifferenceOperator(new IdentityExpression(Relation.NULLARY_EMPTY), new IdentityExpression(Relation.NULLARY_TUPLE)).execute());
        assertEquals(Relation.NULLARY_EMPTY, new DifferenceOperator(new IdentityExpression(Relation.NULLARY_EMPTY), new IdentityExpression(Relation.NULLARY_EMPTY)).execute());
    }

    @Test
    public void shouldSubtractEmptyRelations() {
        RelationSchema schema = new RelationSchemaGenerator()
                .withAttribute("a", Integer.class)
                .withAttribute("b", Boolean.class)
                .generate();
        Relation a = new RelationSelector().withSchema(schema).select();
        Relation b = new RelationSelector().withSchema(schema).select();

        Relation result = new DifferenceOperator(new IdentityExpression(a), new IdentityExpression(b)).execute();

        assertEquals(a, result);
        assertEquals(b, result);
    }

    @Test
    public void shouldSubtractTwoEqualRelations() {
        Relation a = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", "22").select())
                .select();
        Relation b = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", "22").select())
                .select();

        Relation result = new DifferenceOperator(new IdentityExpression(a), new IdentityExpression(b)).execute();

        Relation expected = new RelationSelector().withSchema(a.schema()).select();
        assertEquals(expected, result);
    }

    @Test
    public void shouldSubtractTwoDifferentRelationsWithSomeTuplesInCommon() {
        Relation a = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", "22").select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", "33").select())
                .addTuple(new TupleSelector().withValue("a", "4").withValue("b", "44").select())
                .select();
        Relation b = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", "22").select())
                .addTuple(new TupleSelector().withValue("a", "4").withValue("b", "44").select())
                .addTuple(new TupleSelector().withValue("a", "5").withValue("b", "55").select())
                .select();

        Relation result = new DifferenceOperator(new IdentityExpression(a), new IdentityExpression(b)).execute();

        Relation expected = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", "33").select())
                .select();
        assertEquals(expected, result);
    }

    @Test
    public void shouldSubtractTwoDifferentRelationsWithNoTuplesInCommon() {
        Relation a = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", "22").select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", "33").select())
                .select();
        Relation b = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "5").withValue("b", "55").select())
                .addTuple(new TupleSelector().withValue("a", "7").withValue("b", "77").select())
                .addTuple(new TupleSelector().withValue("a", "9").withValue("b", "99").select())
                .select();

        Relation result = new DifferenceOperator(new IdentityExpression(a), new IdentityExpression(b)).execute();

        Relation expected = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", "22").select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", "33").select())
                .select();
        assertEquals(expected, result);
    }

    @Test
    public void shouldThrowErrorIfSchemasAreDifferent() {
        Relation a = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .select();
        Relation b = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "2").withValue("c", "22").select())
                .select();

        DifferenceOperator op1 = new DifferenceOperator(new IdentityExpression(a), new IdentityExpression(b));
        RelationSchemaMismatchException e1 = assertThrows(RelationSchemaMismatchException.class, op1::execute);
        assertEquals(b, e1.getMismatchedRelation());
        assertEquals(a.schema(), e1.getTargetSchema());

        DifferenceOperator op2 = new DifferenceOperator(new IdentityExpression(b), new IdentityExpression(a));
        RelationSchemaMismatchException e2 = assertThrows(RelationSchemaMismatchException.class, op2::execute);
        assertEquals(a, e2.getMismatchedRelation());
        assertEquals(b.schema(), e2.getTargetSchema());
    }

    @Test
    public void shouldThrowErrorIfRelationIsNull() {
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> new DifferenceOperator(null, new IdentityExpression(Relation.NULLARY_TUPLE)));
        assertEquals("Expression can't be null: left", e1.getMessage());

        NullPointerException e2 = assertThrows(NullPointerException.class, () -> new DifferenceOperator(new IdentityExpression(Relation.NULLARY_TUPLE), null));
        assertEquals("Expression can't be null: right", e2.getMessage());
    }
}
