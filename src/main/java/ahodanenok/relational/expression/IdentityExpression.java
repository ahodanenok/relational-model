package ahodanenok.relational.expression;

import ahodanenok.relational.Relation;

import java.util.Objects;

/**
 * Expression which always produces the given relation.
 */
public class IdentityExpression implements RelationalExpression {

    private final Relation relation;

    public IdentityExpression(Relation relation) {
        Objects.requireNonNull(relation, "Relation can't be null");
        this.relation = relation;
    }

    @Override
    public Relation execute() {
        return relation;
    }
}
