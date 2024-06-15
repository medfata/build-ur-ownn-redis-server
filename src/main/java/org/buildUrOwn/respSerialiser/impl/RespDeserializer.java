package org.buildUrOwn.respSerialiser.impl;

import java.util.ArrayList;
import java.util.List;

import org.buildUrOwn.respSerialiser.RespDeserialiser;

public class RespDeserializer implements RespDeserialiser {

    public Object deserialise(String input) {
        try{
            switch (input.charAt(0)) {
                case '+':
                    return deSerialiseSimpleString(input);
                case ':':
                    return deSerialiseInteger(input);
                case '$':
                    return deSerializeBulkString(input);
                case '*':
                    return deSerialiseArray(input);
                case '_':
                    return deSerializeNull(input);
                default:
                    throw new IllegalArgumentException("Unknown RESP type");
            }
        }catch(IllegalArgumentException  e){
            StringBuilder sb = new StringBuilder();
            sb.append("-Error ").append(e.getMessage()).append("\r\n");
            return sb.toString();
        }catch(Exception e){
            e.printStackTrace();
            return "-Error unexpected exception happend!";
        }
    }

    public String deSerialiseSimpleString(String input) {
        if (input.charAt(0) != '+') {
            throw new IllegalArgumentException("Not a simple string");
        }
        String extrInput = input.substring(1, input.length() - 2); // Remove '+' and CRLF
        if (extrInput.contains("\r") || extrInput.contains("\n")) {
            throw new IllegalArgumentException("Simple string contains CR or LF characters");
        }
        return extrInput;
    }

    public Object deSerializeNull(String input){
        if(input.charAt(0) != '_'){
            throw new IllegalArgumentException("Not a Null");
        }
        String extrInput = input.substring(1, input.length() - 2);
        if(extrInput.equals("") == false){
            throw new IllegalArgumentException("Not Valid Null");
        }
        return null;
    }

    public Integer deSerialiseInteger(String input) {
       if(input.charAt(0) != ':'){
           throw new IllegalArgumentException("Not an Integer");
       }
       String extrInput = input.substring(1, input.length() - 2);
       try{
           return Integer.valueOf(extrInput);
       }catch(NumberFormatException e){
           throw new IllegalArgumentException("Not Valid Integer");
       }
    }

    @Override
    public String deSerializeBulkString(String input) {
        if (input.charAt(0) != '$') {
            throw new IllegalArgumentException("Not a bulk string");
        }
    
        int lengthCrlfIndex = input.indexOf("\r\n");
        int endCrlnfIndex = input.indexOf("\r\n", lengthCrlfIndex + 2);
        if (endCrlnfIndex != (input.length() - 2)) {
            throw new IllegalArgumentException("Malformed bulk string: Missing CRLF after length");
        }
    
        int length;
        try {
            length = Integer.parseInt(input.substring(1, lengthCrlfIndex));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid bulk string length", e);
        }

        if (length == -1) {
            return null; // Null bulk string
        }
    
        int startOfString = lengthCrlfIndex + 2;
        int endOfString = startOfString + length;
    
        if (endOfString + 2 > input.length() || !input.substring(endOfString, endOfString + 2).equals("\r\n")) {
            throw new IllegalArgumentException("Malformed bulk string: Content length mismatch or missing final CRLF");
        }
    
        return input.substring(startOfString, endOfString);
    }

    @Override
    public String[] deSerialiseArray(String input) {
        if (input.charAt(0) != '*') {
            throw new IllegalArgumentException("Not an array");
        }

        int length;
        try {
            length = Integer.parseInt(input.substring(1, input.indexOf("\r\n")));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid array length");
        }

        if (length == -1) {
            return null; // Null array
        }

        List<String> elements = new ArrayList<>();
        int index = input.indexOf("\r\n") + 2;
        for (int i = 0; i < length; i++) {
            char type = input.charAt(index);
            switch (type) {
                case '+':
                    int endSimpleString = input.indexOf("\r\n", index);
                    elements.add(deSerialiseSimpleString(input.substring(index, endSimpleString + 2)));
                    index = endSimpleString + 2;
                    break;
                case ':':
                    int endInteger = input.indexOf("\r\n", index);
                    elements.add(String.valueOf(deSerialiseInteger(input.substring(index, endInteger + 2))));
                    index = endInteger + 2;
                    break;
                case '$':
                    int endBulkStringLength = input.indexOf("\r\n", index);
                    int bulkStringLength = Integer.parseInt(input.substring(index + 1, endBulkStringLength));
                    if (bulkStringLength == -1) {
                        elements.add(null); // Null bulk string
                        index = endBulkStringLength + 2;
                    } else {
                        String bulkString = input.substring(endBulkStringLength + 2, endBulkStringLength + 2 + bulkStringLength);
                        elements.add(bulkString);
                        index = endBulkStringLength + 2 + bulkStringLength + 2;
                    }
                    break;
                case '*':
                    throw new IllegalArgumentException("nested array is not supported");
                default:
                    throw new IllegalArgumentException("Unknown RESP type in array");
            }
        }

        return elements.toArray(new String[0]);
    }
    
}
