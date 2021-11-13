package ahodanenok.relational.algebra;

import ahodanenok.relational.*;
import ahodanenok.relational.exception.AttributeNotFoundException;
import ahodanenok.relational.exception.AttributeAlreadyExistsException;
import ahodanenok.relational.util.Strings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Produces a new relation with the names of some attributes changed.
 */
public final class RenameOperator implements RelationalOperator {

    private final Relation relation;
    private final Map<String, String> mappings;

    public RenameOperator(Relation relation) {
        Objects.requireNonNull(relation, "Relation can't be null");
        this.relation = relation;
        this.mappings = new HashMap<>();
    }

    /**
     * Add renaming mapping.
     *
     * @param fromName attribute to rename
     * @param toName a new name for the attribute
     * @throws IllegalArgumentException if any of the names is null or empty
     */
    public RenameOperator addMapping(String fromName, String toName) {
        Strings.requireNotEmpty(fromName, "fromName can't be null or empty");
        Strings.requireNotEmpty(toName, "toName can't be null or empty");
        this.mappings.put(fromName.trim(), toName.trim());
        return this;
    }

    @Override
    public Relation execute() {
        if (mappings.isEmpty()) {
            return relation;
        }

        RelationSchema sourceSchema = relation.schema();
        Iterator<Map.Entry<String, String>> iterator = mappings.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            // dropping unnecessary work
            if (entry.getKey().equals(entry.getValue())) {
                iterator.remove();
                continue;
            }

            String fromName = entry.getKey();
            // check that the attribute exists in the schema
            if (!sourceSchema.hasAttribute(fromName)) {
                throw new AttributeNotFoundException(fromName);
            }

            String toName = entry.getValue();
            // check that the target name doesn't exist yet in the schema
            if (!mappings.containsKey(toName) && sourceSchema.hasAttribute(toName)) {
                throw new AttributeAlreadyExistsException(sourceSchema.getAttribute(toName));
            }
        }

        RelationSchemaGenerator resultSchemaGenerator = new RelationSchemaGenerator();
        for (Attribute a : sourceSchema.getAttributes()) {
            if (mappings.containsKey(a.getName())) {
                resultSchemaGenerator.withAttribute(new Attribute(mappings.get(a.getName()), a.getType()));
            } else {
                resultSchemaGenerator.withAttribute(a);
            }
        }

        RelationSchema resultSchema = resultSchemaGenerator.generate();
        RelationSelector resultRelationSelector = new RelationSelector().withSchema(resultSchema);
        relation.tuples().map(this::renameTuple).forEach(resultRelationSelector::addTuple);

        return resultRelationSelector.select();
    }

    private Tuple renameTuple(Tuple t) {
        TupleSelector tupleSelector = new TupleSelector();
        for (Attribute a : t.getAttributes()) {
            String resultName = mappings.getOrDefault(a.getName(), a.getName());
            tupleSelector.withValue(resultName, t.getValue(a.getName()));
        }

        return tupleSelector.select();
    }
}
