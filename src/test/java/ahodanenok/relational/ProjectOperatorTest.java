package ahodanenok.relational;

import ahodanenok.relational.algebra.ProjectOperator;
import ahodanenok.relational.exception.AttributeNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectOperatorTest {

    @Test
    public void shouldProject0Relation() {
        assertEquals(Relation.NULLARY_TUPLE, new ProjectOperator(Relation.NULLARY_TUPLE).includeAttributes().execute());
        assertEquals(Relation.NULLARY_TUPLE, new ProjectOperator(Relation.NULLARY_TUPLE).withoutAttributes().execute());
        assertEquals(Relation.NULLARY_EMPTY, new ProjectOperator(Relation.NULLARY_EMPTY).includeAttributes().execute());
        assertEquals(Relation.NULLARY_EMPTY, new ProjectOperator(Relation.NULLARY_EMPTY).withoutAttributes().execute());
    }

    @Test
    public void shouldProjectEmptyRelation() {
        RelationSchema schema = new RelationSchemaGenerator()
                .withAttribute("a", Integer.class)
                .withAttribute("b", Boolean.class)
                .withAttribute("c", String.class)
                .generate();

        Relation relation = new RelationSelector().withSchema(schema).select();

        RelationSchema expectedIncluded = new RelationSchemaGenerator()
                .withAttribute("b", Boolean.class)
                .generate();
        assertEquals(
                new RelationSelector().withSchema(expectedIncluded).select(),
                new ProjectOperator(relation, "b").execute());

        RelationSchema expectedExcluded = new RelationSchemaGenerator()
                .withAttribute("a", Integer.class)
                .withAttribute("c", String.class)
                .generate();
        assertEquals(
                new RelationSelector().withSchema(expectedExcluded).select(),
                new ProjectOperator(relation, "b").withoutAttributes().execute());
    }

    @Test
    public void shouldProjectRelationWithOneTuple() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").withValue("c", "111").withValue("d", "1111").select())
                .select();

        Relation expectedIncluded = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("d", "1111").select())
                .select();
        assertEquals(expectedIncluded, new ProjectOperator(relation, "a", "d").execute());

        Relation expectedExcluded = new RelationSelector()
                .addTuple(new TupleSelector().withValue("b", "11").withValue("c", "111").select())
                .select();
        assertEquals(expectedExcluded, new ProjectOperator(relation, "a", "d").withoutAttributes().execute());
    }

    @Test
    public void shouldProjectRelationWithMultipleTuples() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").withValue("c", "111").withValue("d", "1111").select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", "22").withValue("c", "222").withValue("d", "2222").select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", "33").withValue("c", "333").withValue("d", "3333").select())
                .addTuple(new TupleSelector().withValue("a", "4").withValue("b", "44").withValue("c", "444").withValue("d", "4444").select())
                .select();

        Relation expectedIncluded = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("c", "111").withValue("d", "1111").select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("c", "222").withValue("d", "2222").select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("c", "333").withValue("d", "3333").select())
                .addTuple(new TupleSelector().withValue("a", "4").withValue("c", "444").withValue("d", "4444").select())
                .select();
        assertEquals(expectedIncluded, new ProjectOperator(relation, "a", "c", "d").execute());

        Relation expectedExcluded = new RelationSelector()
                .addTuple(new TupleSelector().withValue("b", "11").select())
                .addTuple(new TupleSelector().withValue("b", "22").select())
                .addTuple(new TupleSelector().withValue("b", "33").select())
                .addTuple(new TupleSelector().withValue("b", "44").select())
                .select();
        assertEquals(expectedExcluded, new ProjectOperator(relation, "a", "c", "d").withoutAttributes().execute());

        Relation expectedExcludedAll = new RelationSelector().withSchema(RelationSchema.EMPTY).addTuple(Tuple.EMPTY).select();
        assertEquals(expectedExcludedAll, new ProjectOperator(relation, "a", "b", "c", "d").withoutAttributes().execute());
    }

    @Test
    public void shouldSwitchBetweenModes() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").withValue("c", "111").select())
                .select();

        Relation expectedIncluded = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").select())
                .select();

        Relation expectedExcluded = new RelationSelector()
                .addTuple(new TupleSelector().withValue("b", "11").withValue("c", "111").select())
                .select();

        ProjectOperator op = new ProjectOperator(relation, "a");
        assertEquals(expectedIncluded, op.execute());
        assertEquals(expectedExcluded, op.withoutAttributes().execute());
        assertEquals(expectedIncluded, op.includeAttributes().execute());
        assertEquals(expectedExcluded, op.withoutAttributes().execute());
        assertEquals(expectedIncluded, op.includeAttributes().execute());
    }

    @Test
    public void shouldThrowErrorIfAttributeNotFound() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", "11").select())
                .select();

        ProjectOperator op = new ProjectOperator(relation, "b", "c");
        AttributeNotFoundException e = assertThrows(AttributeNotFoundException.class, op::execute);
        assertEquals("c", e.getName());
    }

    @Test
    public void shouldThrowErrorIfNullParameters() {
        NullPointerException e1 = assertThrows(NullPointerException.class, () -> new ProjectOperator(null, Collections.emptyList()));
        assertEquals("Relation can't be null", e1.getMessage());

        NullPointerException e2 = assertThrows(NullPointerException.class, () -> new ProjectOperator(Relation.NULLARY_TUPLE, (List<String>) null));
        assertEquals("Attribute names can't be null", e2.getMessage());
    }
}
