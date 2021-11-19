package ahodanenok.relational.algebra;

import ahodanenok.relational.*;
import ahodanenok.relational.exception.AttributeNotFoundException;
import ahodanenok.relational.exception.AttributeAlreadyExistsException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Rename attributes in a single relation.
 *
 * <p>Produces a new relation with the names of attributes changed according to the given mappings.
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
     * @param name attribute to rename
     * @param targetName a new name for the attribute
     * @throws NullPointerException if any of the names is null
     */
    public RenameOperator addMapping(String name, String targetName) {
        Objects.requireNonNull(name, "Name can't be null");
        Objects.requireNonNull(targetName, "Target name can't be null");
        this.mappings.put(name.trim(), targetName.trim());
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

            String name = entry.getKey();
            // check that the attribute exists in the schema
            if (!sourceSchema.hasAttribute(name)) {
                throw new AttributeNotFoundException(name);
            }

            // todo: check no many-to-one
            String targetName = entry.getValue();
            // check that the target name doesn't exist yet in the schema
            if (!mappings.containsKey(targetName) && sourceSchema.hasAttribute(targetName)) {
                throw new AttributeAlreadyExistsException(sourceSchema.getAttribute(targetName));
            }
        }

        RelationSchemaGenerator resultSchemaGenerator = new RelationSchemaGenerator();
        sourceSchema.attributes().forEach(a -> {
            if (mappings.containsKey(a.getName())) {
                resultSchemaGenerator.withAttribute(new Attribute(mappings.get(a.getName()), a.getType()));
            } else {
                resultSchemaGenerator.withAttribute(a);
            }
        });

        RelationSchema resultSchema = resultSchemaGenerator.generate();
        RelationSelector resultRelationSelector = new RelationSelector().withSchema(resultSchema);
        relation.tuples().map(this::renameTuple).forEach(resultRelationSelector::addTuple);

        return resultRelationSelector.select();
    }

    private Tuple renameTuple(Tuple t) {
        TupleSelector tupleSelector = new TupleSelector();
        t.attributes().forEach(a -> {
            String resultName = mappings.getOrDefault(a.getName(), a.getName());
            tupleSelector.withValue(resultName, t.getValue(a.getName()));
        });

        return tupleSelector.select();
    }
}
