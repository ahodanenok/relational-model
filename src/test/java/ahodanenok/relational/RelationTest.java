package ahodanenok.relational;

import ahodanenok.relational.exception.TupleSchemaMismatchException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RelationTest {

    @Test
    public void shouldSelectRelationWithoutAttributes() {
        Relation relation = new RelationSelector()
                .addTuple(Tuple.EMPTY)
                .select();

        assertEquals(0, relation.degree());
        assertEquals(1, relation.cardinality());
        assertEquals(1, relation.tuples().count());
        assertEquals(Tuple.EMPTY, relation.tuples().findFirst().orElse(null));
    }

    @Test
    public void shouldSelectEmptyRelationWithoutAttributes() {
        Relation relation = new RelationSelector().withSchema(RelationSchema.EMPTY).select();

        assertEquals(0, relation.degree());
        assertEquals(0, relation.cardinality());
        assertEquals(0, relation.tuples().count());
    }

    @Test
    public void shouldSelectEmptyRelation() {
        RelationSchema schema = new RelationSchemaGenerator().withAttribute("a", String.class).generate();
        Relation relation = new RelationSelector().withSchema(schema).select();

        assertEquals(1, relation.degree());
        assertEquals(0, relation.cardinality());
        assertEquals(0, relation.tuples().count());
    }

    @Test
    public void schemaIsRequiredWhenRelationIsEmpty() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> new RelationSelector().select());
        assertEquals("Schema must be specified explicitly if the relation is empty", e.getMessage());
    }

    @Test
    public void shouldSelectRelationWithOneTupleAndInferSchema() {
        Tuple tuple = new TupleSelector()
                .withValue("name", "abc")
                .withValue("seen", true)
                .withValue("count", 10)
                .select();
        Relation relation = new RelationSelector().addTuple(tuple).select();

        assertEquals(3, relation.degree());
        assertEquals(1, relation.cardinality());
        assertEquals(1, relation.tuples().count());
        assertEquals(tuple, relation.tuples().findFirst().orElse(null));
        assertEquals(tuple, relation.getSingleTuple());

        RelationSchema expectedSchema = new RelationSchemaGenerator()
                .withAttribute("name", String.class)
                .withAttribute("seen", Boolean.class)
                .withAttribute("count", Integer.class)
                .generate();
        assertEquals(expectedSchema, relation.schema());
    }

    @Test
    public void shouldSelectRelationWithOneTupleWithPredefinedSchema() {
        RelationSchema schema = new RelationSchemaGenerator()
                .withAttribute("a", Character.class)
                .withAttribute("b", String.class)
                .withAttribute("c", Long.class)
                .withAttribute("d", Error.class)
                .generate();
        Tuple tuple = new TupleSelector()
                .withValue("d", new Error("error!"))
                .withValue("c", 100L)
                .withValue("b", "abc")
                .withValue("a", '@')
                .select();
        Relation relation = new RelationSelector().withSchema(schema).addTuple(tuple).select();

        assertEquals(4, relation.degree());
        assertEquals(1, relation.cardinality());
        assertEquals(schema, relation.schema());
        assertEquals(tuple, relation.tuples().findFirst().orElse(null));
        assertEquals(tuple, relation.getSingleTuple());
    }

    @Test
    public void shouldThrowErrorIfTupleDoesNotMatchInferredSchema_DifferentTypes() {
        TupleSchemaMismatchException e = assertThrows(TupleSchemaMismatchException.class, () ->
                new RelationSelector()
                        .addTuple(new TupleSelector().withValue("id", 5).withValue("count", 100L).select())
                        .addTuple(new TupleSelector().withValue("id", 6).withValue("count", 200L).select())
                        .addTuple(new TupleSelector().withValue("id", 7).withValue("count", true).select()));

        assertEquals("Tuple's schema is not compatible with relation's schema.", e.getMessage());

        RelationSchema expectedSchema = new RelationSchemaGenerator()
                .withAttribute("id", Integer.class)
                .withAttribute("count", Long.class)
                .generate();
        assertEquals(expectedSchema,e.getTargetSchema());

        Tuple expectedTuple = new TupleSelector().withValue("id", 7).withValue("count", true).select();
        assertEquals(expectedTuple, e.getMismatchedTuple());
    }

    @Test
    public void shouldThrowErrorIfTupleDoesNotMatchInferredSchema_DifferentAttributes() {
        TupleSchemaMismatchException e = assertThrows(TupleSchemaMismatchException.class, () ->
                new RelationSelector()
                        .addTuple(new TupleSelector().withValue("id", 5).withValue("visible", true).select())
                        .addTuple(new TupleSelector().withValue("id", 6).withValue("visible", false).select())
                        .addTuple(new TupleSelector().withValue("id", 7).withValue("visible", true).select())
                        .addTuple(new TupleSelector().withValue("id", 8).withValue("hidden", false).select()));

        assertEquals("Tuple's schema is not compatible with relation's schema.", e.getMessage());

        RelationSchema expectedSchema = new RelationSchemaGenerator()
                .withAttribute("id", Integer.class)
                .withAttribute("visible", Boolean.class)
                .generate();
        assertEquals(expectedSchema,e.getTargetSchema());

        Tuple expectedTuple = new TupleSelector().withValue("id", 8).withValue("hidden", false).select();
        assertEquals(expectedTuple, e.getMismatchedTuple());
    }

    @Test
    public void shouldThrowErrorIfTupleDoesNotMatchExplicitSchema_DifferentTypes() {
        RelationSchema schema = new RelationSchemaGenerator()
                .withAttribute("name", String.class)
                .withAttribute("count", Integer.class)
                .generate();

        TupleSchemaMismatchException e = assertThrows(TupleSchemaMismatchException.class, () ->
                new RelationSelector()
                        .withSchema(schema)
                        .addTuple(new TupleSelector().withValue("name", 5).withValue("count", 11).select()));

        assertEquals("Tuple's schema is not compatible with relation's schema.", e.getMessage());
        assertEquals(schema, e.getTargetSchema());
        assertEquals(new TupleSelector().withValue("name", 5).withValue("count", 11).select(), e.getMismatchedTuple());
    }

    @Test
    public void shouldThrowErrorIfTupleDoesNotMatchExplicitSchema_DifferentAttributes() {
        RelationSchema schema = new RelationSchemaGenerator()
                .withAttribute("name", String.class)
                .withAttribute("count", Integer.class)
                .generate();

        TupleSchemaMismatchException e = assertThrows(TupleSchemaMismatchException.class, () ->
                new RelationSelector()
                        .withSchema(schema)
                        .addTuple(new TupleSelector().withValue("name", "ABC").withValue("value", 11).select()));

        assertEquals("Tuple's schema is not compatible with relation's schema.", e.getMessage());
        assertEquals(schema, e.getTargetSchema());

        Tuple expectedTuple = new TupleSelector().withValue("name", "ABC").withValue("value", 11).select();
        assertEquals(expectedTuple, e.getMismatchedTuple());
    }

    @Test
    public void shouldSelectRelationWithMultipleTuplesAndInferSchema() {
        Tuple tuple1 = new TupleSelector().withValue("a", "1").withValue("b", '!').select();
        Tuple tuple2 = new TupleSelector().withValue("a", "2").withValue("b", '@').select();
        Tuple tuple3 = new TupleSelector().withValue("a", "3").withValue("b", '#').select();
        Tuple tuple4 = new TupleSelector().withValue("a", "4").withValue("b", '$').select();
        Relation relation = new RelationSelector()
                .addTuple(tuple1)
                .addTuple(tuple2)
                .addTuple(tuple3)
                .addTuple(tuple4)
                .select();

        assertEquals(2, relation.degree());
        assertEquals(4, relation.cardinality());
        RelationSchema expectedSchema = new RelationSchemaGenerator()
                .withAttribute("a", String.class)
                .withAttribute("b", Character.class)
                .generate();
        assertEquals(expectedSchema, relation.schema());
        assertEquals(4, relation.tuples().count());
        assertEquals(tuple1, relation.tuples().filter(t -> t.getValue("a").equals("1")).findFirst().orElse(null));
        assertEquals(tuple2, relation.tuples().filter(t -> t.getValue("a").equals("2")).findFirst().orElse(null));
        assertEquals(tuple3, relation.tuples().filter(t -> t.getValue("a").equals("3")).findFirst().orElse(null));
        assertEquals(tuple4, relation.tuples().filter(t -> t.getValue("a").equals("4")).findFirst().orElse(null));
    }

    @Test
    public void shouldSelectRelationWithMultipleTuplesWithPredefinedSchema() {
        RelationSchema schema = new RelationSchemaGenerator()
                .withAttribute("a", Integer.class)
                .withAttribute("b", Long.class)
                .generate();
        Tuple tuple1 = new TupleSelector().withValue("a", 1).withValue("b", 100L).select();
        Tuple tuple2 = new TupleSelector().withValue("a", 2).withValue("b", 200L).select();
        Tuple tuple3 = new TupleSelector().withValue("a", 3).withValue("b", 300L).select();
        Relation relation = new RelationSelector()
                .withSchema(schema)
                .addTuple(tuple1)
                .addTuple(tuple2)
                .addTuple(tuple3)
                .select();

        assertEquals(2, relation.degree());
        assertEquals(3, relation.cardinality());
        assertEquals(schema, relation.schema());
        assertEquals(3, relation.tuples().count());
        assertEquals(tuple1, relation.tuples().filter(t -> t.getValue("a").equals(1)).findFirst().orElse(null));
        assertEquals(tuple2, relation.tuples().filter(t -> t.getValue("a").equals(2)).findFirst().orElse(null));
        assertEquals(tuple3, relation.tuples().filter(t -> t.getValue("a").equals(3)).findFirst().orElse(null));
    }

    @Test
    public void shouldNotContainDuplicates() {
        Tuple tuple1 = new TupleSelector().withValue("a", "1").withValue("b", '!').select();
        Tuple tuple2 = new TupleSelector().withValue("a", "2").withValue("b", '@').select();
        Tuple tuple3 = new TupleSelector().withValue("a", "1").withValue("b", '!').select();
        Tuple tuple4 = new TupleSelector().withValue("a", "2").withValue("b", '@').select();
        Relation relation = new RelationSelector()
                .addTuple(tuple1)
                .addTuple(tuple2)
                .addTuple(tuple3)
                .addTuple(tuple4)
                .select();

        assertEquals(2, relation.degree());
        assertEquals(2, relation.cardinality());
        assertEquals(2, relation.tuples().count());
        assertEquals(tuple1, relation.tuples().filter(t -> t.getValue("a").equals("1")).findFirst().orElse(null));
        assertEquals(tuple2, relation.tuples().filter(t -> t.getValue("a").equals("2")).findFirst().orElse(null));
        assertEquals(tuple3, relation.tuples().filter(t -> t.getValue("a").equals("1")).findFirst().orElse(null));
        assertEquals(tuple4, relation.tuples().filter(t -> t.getValue("a").equals("2")).findFirst().orElse(null));
    }

    @Test
    public void shouldBeEqualIfSchemaAndTuplesMatch() {
        Relation relationA = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", 22).select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", 33).select())
                .addTuple(new TupleSelector().withValue("a", "4").withValue("b", 44).select())
                .select();
        Relation relationB = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", 22).select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", 33).select())
                .addTuple(new TupleSelector().withValue("a", "4").withValue("b", 44).select())
                .select();

        assertEquals(relationA, relationB);
        assertEquals(relationB, relationA);
        assertEquals(relationA.hashCode(), relationB.hashCode());
    }

    @Test
    public void shouldNotBeEqualIfSchemaIsDifferent() {
        Relation relationA = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", 22).select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", 33).select())
                .select();
        Relation relationB = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("c", 11).select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("c", 22).select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("c", 33).select())
                .select();

        assertNotEquals(relationA, relationB);
        assertNotEquals(relationB, relationA);
    }

    @Test
    public void shouldNotBeEqualIfTuplesDifferent() {
        Relation relationA = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", 22).select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", 33).select())
                .addTuple(new TupleSelector().withValue("a", "4").withValue("b", 44).select())
                .select();
        Relation relationB = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", "1").withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", "2").withValue("b", 22).select())
                .addTuple(new TupleSelector().withValue("a", "3").withValue("b", 33).select())
                .select();

        assertNotEquals(relationA, relationB);
        assertNotEquals(relationB, relationA);
    }
}
