package org.buildUrOwn.respSerialiser;


public interface RespDeserialiser {
    public Object deserialise(String input);
    public String deSerialiseSimpleString(String input);
    public Integer deSerialiseInteger(String input);
    public String deSerializeBulkString(String input);
    public String[] deSerialiseArray(String input);

    public Object deSerializeNull(String input);
}
