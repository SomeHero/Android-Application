package me.pdthx.Helpers;

import java.util.ArrayList;

public class PhoneNumberFormatter
{
    public static String stripNumber(String phoneNumber) {

        String strippedNumber = phoneNumber.replaceAll("[^0-9]", "");
        String regex = "[2-9][0-9]{2}[2-9][0-9]{2}[0-9]{4}";
        return strippedNumber.matches(regex) ? strippedNumber : null;
    }

    public static String formatNumber(String phoneNumber) {
        String fixedNumber = "";
        String number = "";
        String extras = "";

        String[] invalidAreaCodesArray = new String[] { "800", "866", "877", "888", "855", "844", "833", "822"};
        ArrayList<String> invalidAreaCodes = new ArrayList<String>();

        for (int i = 0; i < invalidAreaCodesArray.length; i++) {
            invalidAreaCodes.add(invalidAreaCodesArray[i]);
        }

        fixedNumber = phoneNumber.replaceAll("[^0-9]", "");
        int fixedNumberLength = fixedNumber.length();

        if (fixedNumberLength >= 10) {

            if (fixedNumberLength == 10) {
                number = fixedNumber;
            }
            else if (fixedNumberLength > 10) {
                //extras = fixedNumber.substring(0, fixedNumberLength - 10) + " ";
                number = fixedNumber.substring(fixedNumberLength - 10);
            }

            if (invalidAreaCodes.contains(number.substring(0, 3))) {
                return null;
            }

            number = "(" + number.substring(0, 3) + ") " +
                    number.substring(3, 6) + "-" + number.substring(6);

            fixedNumber = extras + number;

            return fixedNumber;
        }
        else if (fixedNumberLength == 7)
        {
        	return fixedNumber.substring(0, 3) + "-" + fixedNumber.substring(3);
        }

        return null;
    }
}
