package org.buildUrOwn;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.buildUrOwn.respSerialiser.RespDeserialiser;
import org.buildUrOwn.respSerialiser.RespSerialiser;
import org.buildUrOwn.respSerialiser.impl.RespSerializer;
import org.buildUrOwn.respSerialiser.impl.RespDeserializer;

public class RespTest extends TestCase {
    public RespTest( String testName )
    {
        super( testName );
    }
    public static Test suite()
    {
        return new TestSuite( RespTest.class );
    }

    private RespSerialiser respSerialiser = new RespSerializer();
    private RespDeserialiser respDeserialiser = new RespDeserializer();

    public void testValidDeserSimpleString(){
        String strtoDeser = "+hello world\r\n";
        String output = String.valueOf(this.respDeserialiser.deserialise(strtoDeser));
        assertEquals(output, "hello world");
    }

    public void testInvalidDeserSimpleString(){
        String strtoDeser = "+test\ntest\r\n";
        String output =  String.valueOf(this.respDeserialiser.deserialise(strtoDeser));
        assertEquals(output, "-Error Simple string contains CR or LF characters\r\n");
    }

    public void testValidDeserNull(){
        String strtoDeser = "_\r\n";
        Object output = this.respDeserialiser.deserialise(strtoDeser);
        assertNull(output);
    }

    public void testInvalidDeserNull(){
        String strtoDeser = "_1s\r\n";
        Object output = this.respDeserialiser.deserialise(strtoDeser);
        assertEquals(output, "-Error Not Valid Null\r\n");
    }

    public void testValidDeserInteger(){
        String strtoDeser = ":200\r\n";
        Object output = this.respDeserialiser.deserialise(strtoDeser);
        assertEquals(output, Integer.valueOf(200));
        String strtoDeser1 = ":-200\r\n";
        Object output1 = this.respDeserialiser.deserialise(strtoDeser1);
        assertEquals(output1, Integer.valueOf(-200));
    }

    public void testInvalidDeserInterger(){
        String strToDeser = ":84rg\r\n";
        Object output = this.respDeserialiser.deserialise(strToDeser);
        System.out.println("outpur: "+output);
        assertEquals(output, "-Error Not Valid Integer\r\n");
    }
}
