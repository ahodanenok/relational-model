package ahodanenok.relational;

import ahodanenok.relational.algebra.DifferenceOperator;
import ahodanenok.relational.algebra.UnionOperator;
import ahodanenok.relational.exception.RelationSchemaMismatchException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DifferenceOperatorTest {

    @Test
    public void shouldSubtract0Relation() {
        assertEquals(Relation.EMPTY, new DifferenceOperator(Relation.EMPTY, Relation.EMPTY).execute());
    }

    @Test
    public void shouldSubtractEmptyRelations() {
        RelationSchema schema = new RelationSchemaGenerator()
                .withAttribute("a", Integer.class)
                .withAttribute("b", Boolean.class)
                .generate();
        Relation a = new RelationSelector().withSchema(schema).select();
        Relation b = new RelationSelector().withSchema(schema).select();

        Relation result = new DifferenceOperator(a, b).execute();

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

        Relation result = new DifferenceOperator(a, b).execute();

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

        Relation result = new DifferenceOperator(a, b).execute();

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

        Relation result = new DifferenceOperator(a, b).execute();

        Relation expected = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", "22").select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", "33").select())
                .select();
        assertEquals(expected, result);
    }

    @Test
    public void shouldSubtractMultipleRelations() {
        Relation a = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", "22").select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", "33").select())
                .addTuple(new TupleSelector().withValue("a", "4").withValue("b", "44").select())
                .addTuple(new TupleSelector().withValue("a", "5").withValue("b", "55").select())
                .addTuple(new TupleSelector().withValue("a", "6").withValue("b", "66").select())
                .addTuple(new TupleSelector().withValue("a", "7").withValue("b", "77").select())
                .select();
        Relation b = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "5").withValue("b", "55").select())
                .addTuple(new TupleSelector().withValue("a", "7").withValue("b", "77").select())
                .addTuple(new TupleSelector().withValue("a", "9").withValue("b", "99").select())
                .select();
        Relation c = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .addTuple(new TupleSelector().withValue("a", "5").withValue("b", "55").select())
                .select();
        Relation d = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "4").withValue("b", "44").select())
                .addTuple(new TupleSelector().withValue("a", "7").withValue("b", "77").select())
                .addTuple(new TupleSelector().withValue("a", "0").withValue("b", "00").select())
                .select();

        Relation result = new DifferenceOperator(a, b).addRelation(c).addRelation(d).execute();

        Relation expected = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", "22").select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", "33").select())
                .addTuple(new TupleSelector().withValue("a", "6").withValue("b", "66").select())
                .select();
        assertEquals(expected, result);
    }

    @Test
    public void shouldThrowErrorIfNotAllTuplesHaveSameSchema() {
        Relation a = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .select();
        Relation b = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "2").withValue("c", "22").select())
                .select();

        DifferenceOperator op1 = new DifferenceOperator(a, b);
        RelationSchemaMismatchException e1 = assertThrows(RelationSchemaMismatchException.class, op1::execute);
        assertEquals(b, e1.getMismatchedRelation());
        assertEquals(a.schema(), e1.getTargetSchema());

        DifferenceOperator op2 = new DifferenceOperator(b, a);
        RelationSchemaMismatchException e2 = assertThrows(RelationSchemaMismatchException.class, op2::execute);
        assertEquals(a, e2.getMismatchedRelation());
        assertEquals(b.schema(), e2.getTargetSchema());

        DifferenceOperator op3 = new DifferenceOperator(a, a).addRelation(b);
        RelationSchemaMismatchException e3 = assertThrows(RelationSchemaMismatchException.class, op3::execute);
        assertEquals(b, e3.getMismatchedRelation());
        assertEquals(a.schema(), e3.getTargetSchema());
    }

    @Test
    public void shouldThrowErrorIfRelationIsNull() {
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> new DifferenceOperator(null, Relation.EMPTY));
        assertEquals("relation 'a' can't be null", e1.getMessage());

        NullPointerException e2 = assertThrows(NullPointerException.class, () -> new DifferenceOperator(Relation.EMPTY, null));
        assertEquals("relation 'b' can't be null", e2.getMessage());

        NullPointerException e3 = assertThrows(NullPointerException.class, () -> new DifferenceOperator(Relation.EMPTY, Relation.EMPTY).addRelation(null));
        assertEquals("relation can't be null", e3.getMessage());
    }
}
