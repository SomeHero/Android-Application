package me.pdthx.helpers;

import junit.framework.TestCase;

public class PhoneNumberFormatterTest extends TestCase
{
    private String phoneNumber1 = "+17574691582";
    private String phoneNumber2 = "7574691582";
    private String phoneNumber3 = "1-(757)-469-1582";
    private String phoneNumber4 = "1-757-4691582";
    private String phoneNumber5 = "1(800)4691582";
    private String phoneNumber6 = "8664691582";
    private String phoneNumber7 = "411";
    private String phoneNumber8 = "4961582";


    public void setUp() {
    }

    public void testPhoneNumberFormatter() {

        assertEquals("(757) 469-1582", PhoneNumberFormatter.formatNumber(phoneNumber1));
        assertEquals("496-1582", PhoneNumberFormatter.formatNumber(phoneNumber8));
    }

    public void testPhoneNumberFormatter2() {
        assertEquals("(757) 469-1582", PhoneNumberFormatter.formatNumber(phoneNumber2));
    }

    public void testPhoneNumberFormatter3() {
        assertEquals("(757) 469-1582", PhoneNumberFormatter.formatNumber(phoneNumber3));
    }

    public void testPhoneNumberFormatter4() {
        assertEquals("(757) 469-1582", PhoneNumberFormatter.formatNumber(phoneNumber4));
    }

    public void testPhoneNumberFormatter5() {
        assertNull(PhoneNumberFormatter.formatNumber(phoneNumber5));
    }

    public void testPhoneNumberFormatter6() {
        assertNull(PhoneNumberFormatter.formatNumber(phoneNumber6));
    }

    public void testPhoneNumberFormatter7() {
        assertNull(PhoneNumberFormatter.formatNumber(phoneNumber7));
    }
}
