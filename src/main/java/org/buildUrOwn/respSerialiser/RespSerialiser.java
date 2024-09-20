package org.buildUrOwn.respSerialiser;
public interface RespSerialiser {
    public String serialise(Object input);
    public String serialiseSimpleString(String input);
    public String serializeArray(String[] input);
    public String serialiseInteger(Integer input);
}
