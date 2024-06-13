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

    public String[] deSerializeBulkString(String input) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deSerializeBulkString'");
    }

    public String[] deSerialiseArray(String input) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deSerialiseArray'");
    }
    
}
