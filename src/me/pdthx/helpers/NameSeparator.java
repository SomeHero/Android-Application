package me.pdthx.Helpers;

public class NameSeparator {
	public static String[] separateName(String name) {

		String trimmedName = name.trim();

		String[] names = new String[2];

		int indexOfFirstSpace;
		int indexOfLastSpace;
		int indexOfFirstParenthesis = trimmedName.indexOf("(");
		int indexOfSecondParenthesis = trimmedName.indexOf(")");
		int indexOfHyphen = trimmedName.indexOf("-");
		if(indexOfFirstParenthesis == 0 )
			{
				String areaCode = trimmedName.substring(indexOfFirstParenthesis + 1,indexOfSecondParenthesis);
				String part1 = trimmedName.substring(indexOfSecondParenthesis + 1, indexOfHyphen);
				String part2 = trimmedName.substring(indexOfHyphen + 1, trimmedName.length());
				trimmedName = areaCode + part1 + part2;
			}
		
		indexOfFirstSpace = trimmedName.indexOf(" ");
		indexOfLastSpace = trimmedName.lastIndexOf(" ");
		
		if (indexOfFirstSpace != -1) {
			names[0] = trimmedName.substring(0, indexOfFirstSpace);
		} else {
			names[0] = trimmedName;
		}
		if (indexOfLastSpace != -1) {
			names[1] = trimmedName.substring(indexOfLastSpace + 1);
		} else {
			names[1] = "";
		}

		return names;
	}
}
