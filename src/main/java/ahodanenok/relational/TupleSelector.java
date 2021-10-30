package ahodanenok.relational;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class TupleSelector {

    private final Map<String, Object> values = new HashMap<>();

    public TupleSelector withValue(String attributeName, Object value) {
        // todo: name not null or empty
        // todo: name not exists
        // todo: null value allowed?
        values.put(attributeName, value);
        return this;
    }

    public Tuple select() {
        Set<Attribute> attributes = new HashSet<>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            // todo: if value is null, then type can't be determined
            attributes.add(new Attribute(entry.getKey(), entry.getValue().getClass()));
        }

        return new Tuple(new TupleSchema(attributes), values);
    }
}
