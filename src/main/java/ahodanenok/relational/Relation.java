package ahodanenok.relational;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Relation is an unordered set of tuples, which have the same schema.
 */
public final class Relation {

    public static final Relation EMPTY = new Relation(RelationSchema.EMPTY, Collections.emptySet());

    private final RelationSchema schema;
    private final Set<Tuple> tuples;

    Relation(RelationSchema schema, Set<Tuple> tuples) {
        this.schema = schema;
        this.tuples = Collections.unmodifiableSet(tuples);
    }

    /**
     * Schema of the relation.
     */
    public RelationSchema schema() {
        return schema;
    }

    /**
     * Get number of attributes in the relation.
     */
    public int degree() {
        return schema.degree();
    }

    /**
     * Get number of tuples in the relation.
     */
    public int cardinality() {
        return tuples.size();
    }

    public boolean isEmpty() {
        return cardinality() == 0;
    }

    /**
     * Extract a single tuple from the relation.
     *
     * @throws IllegalStateException if the relation is empty or contains more than one tuple
     */
    public Tuple getSingleTuple() {
        if (tuples.size() != 1) {
            throw new IllegalStateException(
                    "To extract a single tuple it must exist and be the only tuple in the relation");
        }

        return tuples.iterator().next();
    }

    /**
     * Stream of tuples in the relation without any particular order.
     * If the relation is empty, then an empty stream will be returned.
     */
    public Stream<Tuple> tuples() {
        return tuples.stream();
    }

    /**
     * Check if the relation is superset of the given relation.
     */
    public boolean isSupersetOf(Relation relation) {
        return isSupersetOf(relation, false);
    }

    /**
     * Check if the relation is superset of the given relation.
     * @param proper check if proper superset
     */
    public boolean isSupersetOf(Relation relation, boolean proper) {
        boolean superset = tuples.containsAll(relation.tuples);
        if (proper) {
            return superset && cardinality() > relation.cardinality();
        } else {
            return superset;
        }
    }

    @Override
    public int hashCode() {
        return 31 * schema.hashCode() + tuples.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }

        // relations are equal iff they have the same attributes and the same tuples
        Relation other = (Relation) obj;
        if (!schema.equals(other.schema)) {
            return false;
        }

        return tuples.equals(other.tuples);
    }
}
