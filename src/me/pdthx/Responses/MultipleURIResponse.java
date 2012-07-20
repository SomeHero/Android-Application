package me.pdthx.Responses;

public class MultipleURIResponse
{
    public String UserUri;
    public String FirstName;
    public String LastName;

    public String toString()
    {
        return UserUri + ": " + FirstName + " " + LastName;
    }
}
