package me.pdthx.helpers;

import junit.framework.TestCase;

public class NameSeparatorTest extends TestCase
{
    private String name1 = "Edward Mitchell";
    private String name2 = "Edward Blake Mitchell";
    private String name3 = "Edward Blake Awesome Mitchell";
    private String name4 = "Edward";
    private String name5 = " Edward";
    private String name6 = "Edward ";
    private String name7 = " Edward ";
    private String[] output;


    public void setUp() {
        output = new String[2];
    }

    public void testName1() {
        output = NameSeparator.separateName(name1);
        assertEquals("Edward", output[0]);
        assertEquals("Mitchell", output[1]);
    }

    public void testName2() {
        output = NameSeparator.separateName(name2);
        assertEquals("Edward", output[0]);
        assertEquals("Mitchell", output[1]);
    }

    public void testName3() {
        output = NameSeparator.separateName(name3);
        assertEquals("Edward", output[0]);
        assertEquals("Mitchell", output[1]);
    }

    public void testName4() {
        output = NameSeparator.separateName(name4);
        assertEquals("Edward", output[0]);
        assertEquals("", output[1]);
    }

    public void testName5() {
        output = NameSeparator.separateName(name5);
        assertEquals("Edward", output[0]);
        assertEquals("", output[1]);
    }

    public void testName6() {
        output = NameSeparator.separateName(name6);
        assertEquals("Edward", output[0]);
        assertEquals("", output[1]);
    }

    public void testName7() {
        output = NameSeparator.separateName(name7);
        assertEquals("Edward", output[0]);
        assertEquals("", output[1]);
    }
}
