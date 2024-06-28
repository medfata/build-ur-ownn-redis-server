package org.buildUrOwn.respSerialiser.impl;
import org.buildUrOwn.respSerialiser.RespSerialiser;

public class RespSerializer implements RespSerialiser {

    private static final String SIMPLE_STRING_PREFIX = "+";
    private static final String INTEGER_PREFIX = ":";
    private static final String BULK_STRING_PREFIX = "$";
    private static final String ARRAY_PREFIX = "*";

    @Override
    public String serialise(Object input) {
        String result = "";
        System.out.println("----> type: "+input.getClass().getSimpleName());
        switch (input.getClass().getSimpleName()) {
            case "String":
                result = this.serialiseSimpleString((String) input);
                break;
            default:
                break;
                //throw new IllegalArgumentException("Unsupported input type for serialization");
        }
        if(input.getClass().getSimpleName().equals("String[]")){
            result = this.serializeArray((String[]) input);
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

    @Override
    public String serializeArray(String[] input) {
        if (input == null || input.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(ARRAY_PREFIX).append(input.length).append("\r\n");
        for (String element : input) {
            sb.append(BULK_STRING_PREFIX).append(element.length()).append("\r\n");
            sb.append(element).append("\r\n");
        }
        return sb.toString();
    }

}
