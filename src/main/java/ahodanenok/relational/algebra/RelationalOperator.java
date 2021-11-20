package ahodanenok.relational.algebra;

import ahodanenok.relational.Relation;
import ahodanenok.relational.expression.RelationalExpression;

/**
 * Relational operator take relations as their arguments
 * and produce a new relation when executed.
 */
public interface RelationalOperator extends RelationalExpression {

    @Override
    Relation execute();
}
