package org.buildUrOwn;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.util.Arrays;
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
        assertEquals(
            "hello world", 
            this.respDeserialiser.deserialise("+hello world\r\n")
        );
    }

    public void testInvalidDeserSimpleString(){
        assertEquals(
            "-Error Simple string contains CR or LF characters\r\n",
            this.respDeserialiser.deserialise("+test\ntest\r\n")
        );
    }

    public void testValidDeserNull(){
        String strtoDeser = "_\r\n";
        Object output = this.respDeserialiser.deserialise(strtoDeser);
        assertNull(output);
    }

    public void testInvalidDeserNull(){
        assertEquals(
            "-Error Not Valid Null\r\n", 
            this.respDeserialiser.deserialise("_1s\r\n")
        );
    }

    public void testValidDeserInteger(){
        assertEquals(Integer.valueOf(200),this.respDeserialiser.deserialise(":200\r\n"));
        assertEquals(Integer.valueOf(-200),this.respDeserialiser.deserialise(":-200\r\n"));
    }

    public void testInvalidDeserInterger(){
        String strToDeser = ":84rg\r\n";
        Object output = this.respDeserialiser.deserialise(strToDeser);
        assertEquals("-Error Not Valid Integer\r\n", output);
    }

    public void testValidDeserBulkString(){
        assertEquals("hello", this.respDeserialiser.deserialise("$5\r\nhello\r\n"));
        assertEquals("", this.respDeserialiser.deserialise("$0\r\n\r\n"));
    }

    public void testInValidDeserBulkString(){
        assertEquals(
            "-Error Malformed bulk string: Missing CRLF after length\r\n",
            this.respDeserialiser.deserialise("$5hello\r\n")
        );
        assertEquals(
            "-Error Invalid bulk string length\r\n",
            this.respDeserialiser.deserialise("$12k\r\ntest\r\n")
        );
    }

    public void testValidDeserArray(){
        assertEquals(
            Arrays.toString(new String[]{"ping"}),
            Arrays.toString((String[]) this.respDeserialiser.deserialise("*1\r\n$4\r\nping\r\n"))
        );
        assertEquals(
            Arrays.toString(new String[]{"echo", "hello world"}),
            Arrays.toString((String[]) this.respDeserialiser.deserialise("*2\r\n$4\r\necho\r\n$11\r\nhello world\r\n"))
        );
        assertEquals(
            Arrays.toString(new String[]{"get", "key"}),
            Arrays.toString((String[]) this.respDeserialiser.deserialise("*2\r\n$3\r\nget\r\n$3\r\nkey\r\n"))
        );
    }

    public void testInvalidDeserArray(){
        assertEquals(
            "-Error Invalid array length\r\n",
            this.respDeserialiser.deserialise("*2ks\r\n$4\r\nping\r\n")
        );
    }

    public void testValidSeriaSimpleString(){
        assertEquals("+test simple string\r\n", this.respSerialiser.serialise("test simple string"));
    }
}
