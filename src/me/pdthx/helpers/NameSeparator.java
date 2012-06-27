package me.pdthx.helpers;

public class NameSeparator
{
    public static String[] separateName(String name) {

        String trimmedName = name.trim();

        String[] names = new String[2];

        int indexOfFirstSpace = trimmedName.indexOf(" ");
        int indexOfLastSpace = trimmedName.lastIndexOf(" ");

        if (indexOfFirstSpace != -1) {
            names[0] = trimmedName.substring(0, indexOfFirstSpace);
        }
        else {
            names[0] = trimmedName;
        }
        if (indexOfLastSpace != -1) {
            names[1] = trimmedName.substring(indexOfLastSpace + 1);
        }
        else {
            names[1] = "";
        }

        return names;
    }
}
