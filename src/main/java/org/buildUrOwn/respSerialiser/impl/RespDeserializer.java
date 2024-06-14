package org.buildUrOwn.respSerialiser.impl;

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
    


    public String[] deSerialiseArray(String input) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deSerialiseArray'");
    }
    
}
