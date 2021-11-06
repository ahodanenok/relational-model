package ahodanenok.relational.util;

public final class Strings {

    private Strings() { }

    public static void requireNotEmpty(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}
