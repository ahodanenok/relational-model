package ahodanenok.relational.algebra;

import ahodanenok.relational.Relation;

/**
 * Relational operator take relations as their arguments
 * and produce a new relation when executed.
 */
public interface RelationalOperator {

    Relation execute();
}
