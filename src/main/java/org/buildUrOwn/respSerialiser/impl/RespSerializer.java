package org.buildUrOwn.respSerialiser.impl;
import org.buildUrOwn.respSerialiser.RespSerialiser;

public class RespSerializer implements RespSerialiser {

    private static final String SIMPLE_STRING_PREFIX = "+";
    private static final String INTEGER_PREFIX = ":";
    private static final String BULK_STRING_PREFIX = "$";
    private static final String ARRAY_PREFIX = "*";

    @Override
    public String serialise(Object input) {
        String result;
        switch (input.getClass().getSimpleName()) {
            case "String":
                result = this.serialiseSimpleString((String) input);
                break;
            default:
                throw new IllegalArgumentException("Unsupported input type for serialization");
        }
        return result;
    }

    @Override
    public String serialiseSimpleString(String input) {
        if (input instanceof String) {
            return SIMPLE_STRING_PREFIX + input + "\r\n";
        }
        throw new IllegalArgumentException("Input is not a valid RESP Simple String");
    }

}
