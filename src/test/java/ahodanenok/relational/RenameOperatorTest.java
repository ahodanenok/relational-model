package ahodanenok.relational;

import ahodanenok.relational.algebra.RenameOperator;
import ahodanenok.relational.exception.AttributeNotFoundException;
import ahodanenok.relational.exception.AttributeAlreadyExistsException;
import ahodanenok.relational.expression.IdentityExpression;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RenameOperatorTest {

    @Test
    public void shouldReturnTheSameRelationWhenNoAttributesRenamed() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 1).withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", 2).withValue("b", 22).select())
                .select();
        Relation resultRelation = new RenameOperator(new IdentityExpression(relation)).execute();
        assertEquals(relation, resultRelation);
    }

    @Test
    public void shouldRenameSingleAttribute() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 1).withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", 2).withValue("b", 22).select())
                .select();
        Relation resultRelation = new RenameOperator(new IdentityExpression(relation)).addMapping("a", "c").execute();
        Relation expectedRelation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("c", 1).withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("c", 2).withValue("b", 22).select())
                .select();
        assertEquals(expectedRelation, resultRelation);
    }

    @Test
    public void shouldRenameMultipleAttributes() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 1).withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", 2).withValue("b", 22).select())
                .select();
        Relation resultRelation = new RenameOperator(new IdentityExpression(relation)).addMapping("b", "!b!").addMapping("a", "_a_").execute();
        Relation expectedRelation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("_a_", 1).withValue("!b!", 11).select())
                .addTuple(new TupleSelector().withValue("_a_", 2).withValue("!b!", 22).select())
                .select();
        assertEquals(expectedRelation, resultRelation);
    }

    @Test
    public void shouldSwapAttributeNames() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 1).withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", 2).withValue("b", 22).select())
                .select();
        Relation resultRelation = new RenameOperator(new IdentityExpression(relation)).addMapping("a", "b").addMapping("b", "a").execute();
        Relation expectedRelation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("b", 1).withValue("a", 11).select())
                .addTuple(new TupleSelector().withValue("b", 2).withValue("a", 22).select())
                .select();
        assertEquals(expectedRelation, resultRelation);
    }

    @Test
    public void shouldRenameToExistingAttributeIfItAlsoRenamed() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 1).withValue("b", 11).withValue("c", 111).select())
                .addTuple(new TupleSelector().withValue("a", 2).withValue("b", 22).withValue("c", 222).select())
                .select();
        Relation resultRelation = new RenameOperator(new IdentityExpression(relation)).addMapping("a", "b").addMapping("b", "d").execute();
        Relation expectedRelation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("b", 1).withValue("d", 11).withValue("c", 111).select())
                .addTuple(new TupleSelector().withValue("b", 2).withValue("d", 22).withValue("c", 222).select())
                .select();
        assertEquals(expectedRelation, resultRelation);
    }

    @Test
    public void shouldThrowErrorIfAttributeNotFound() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 1).withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", 2).withValue("b", 22).select())
                .select();
        RenameOperator op = new RenameOperator(new IdentityExpression(relation)).addMapping("c", "a");
        AttributeNotFoundException e = assertThrows(AttributeNotFoundException.class, op::execute);
        assertEquals("c", e.getName());
    }

    @Test
    public void shouldThrowErrorIfRenamingToExistingAttribute() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 1).withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", 2).withValue("b", 22).select())
                .select();

        RenameOperator op = new RenameOperator(new IdentityExpression(relation)).addMapping("a", "b");

        AttributeAlreadyExistsException e = assertThrows(AttributeAlreadyExistsException.class, op::execute);
        assertEquals("Attribute 'b' already exists", e.getMessage());
        assertEquals(new Attribute("b", Integer.class), e.getExistingAttribute());
    }

    @Test
    public void shouldThrowErrorIfNameIsNotValid() {
        Relation relation = new RelationSelector()
                .addTuple(new TupleSelector().withValue("a", 1).withValue("b", 11).select())
                .addTuple(new TupleSelector().withValue("a", 2).withValue("b", 22).select())
                .select();
        RenameOperator op = new RenameOperator(new IdentityExpression(relation));

        NullPointerException e1 = assertThrows(NullPointerException.class, () -> op.addMapping(null, "a"));
        assertEquals("Name can't be null", e1.getMessage());

        NullPointerException e2 = assertThrows(NullPointerException.class, () -> op.addMapping("a", null));
        assertEquals("Target name can't be null", e2.getMessage());
    }
}
