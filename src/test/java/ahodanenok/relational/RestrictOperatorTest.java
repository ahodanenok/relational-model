package ahodanenok.relational;

import ahodanenok.relational.algebra.RestrictOperator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RestrictOperatorTest {

    @Test
    public void shouldRestrict0Relation() {
        assertEquals(Relation.EMPTY, new RestrictOperator(Relation.EMPTY, (r, t) -> true).execute());
        assertEquals(Relation.EMPTY, new RestrictOperator(Relation.EMPTY, (r, t) -> false).execute());
    }

    @Test
    public void shouldRestrictEmptyRelation() {
        RelationSchema schema = new RelationSchemaGenerator()
                .withAttribute("a", Integer.class)
                .withAttribute("b", Boolean.class)
                .generate();
        Relation relation = new RelationSelector().withSchema(schema).select();

        assertEquals(relation, new RestrictOperator(relation, (r, t) -> true).execute());
        assertEquals(relation, new RestrictOperator(relation, (r, t) -> false).execute());
    }

    @Test
    public void shouldRestrictRelationSingleTuple() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 1).withValue("b", 11).select())
                .select();

        assertEquals(relation, new RestrictOperator(relation, (r, t) -> true).execute());
        assertEquals(
                new RelationSelector().withSchema(relation.schema()).select(),
                new RestrictOperator(relation, (r, t) -> false).execute());
    }

    @Test
    public void shouldRestrictRelationMultipleTuples() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 1).withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", 2).withValue("b", 22).select())
                .addTuple(new TupleSelector().withValue("a", 3).withValue("b", 33).select())
                .addTuple(new TupleSelector().withValue("a", 4).withValue("b", 44).select())
                .addTuple(new TupleSelector().withValue("a", 5).withValue("b", 55).select())
                .addTuple(new TupleSelector().withValue("a", 6).withValue("b", 66).select())
                .addTuple(new TupleSelector().withValue("a", 7).withValue("b", 77).select())
                .select();

        assertEquals(relation, new RestrictOperator(relation, (r, t) -> r.cardinality() == 7).execute());
        assertEquals(
                new RelationSelector().withSchema(relation.schema()).select(),
                new RestrictOperator(relation, (r, t) -> (int) t.getValue("a") > 7).execute());

        Relation odd = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 1).withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", 3).withValue("b", 33).select())
                .addTuple(new TupleSelector().withValue("a", 5).withValue("b", 55).select())
                .addTuple(new TupleSelector().withValue("a", 7).withValue("b", 77).select())
                .select();
        assertEquals(odd, new RestrictOperator(relation, (r, t) -> (int) t.getValue("b") % 2 == 1).execute());

        Relation even = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 2).withValue("b", 22).select())
                .addTuple(new TupleSelector().withValue("a", 4).withValue("b", 44).select())
                .addTuple(new TupleSelector().withValue("a", 6).withValue("b", 66).select())
                .select();
        assertEquals(even, new RestrictOperator(relation, (r, t) -> (int) t.getValue("b") % 2 == 0).execute());
    }

    @Test
    public void shouldThrowErrorIfRelationOrPredicateIsNull() {
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> new RestrictOperator(null, (r, t) -> true));
        assertEquals("Relation can't be null", e1.getMessage());

        NullPointerException e2 = assertThrows(NullPointerException.class, () -> new RestrictOperator(Relation.EMPTY, null));
        assertEquals("Predicate can't be null", e2.getMessage());
    }
}
