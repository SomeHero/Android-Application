package me.pdthx.Models;

import junit.framework.TestCase;

public class FriendTest
    extends TestCase
{
    private Friend friend;

    public void setUp() {
        friend = new Friend();
        friend.setName("Edward Mitchell");
        friend.getPaypoints().add("edeesis@yahoo.com");
        friend.setFBContact(true);
    }

    public void testMasterSearch() {
        assertTrue(friend.masterSearch("ed"));
        assertTrue(friend.masterSearch("edw"));
        assertTrue(friend.masterSearch("mi"));
        friend.getPaypoints().add("7574691582");
        assertTrue(friend.masterSearch("757"));
        assertFalse(friend.masterSearch("ward"));
        assertTrue(friend.masterSearch("edward mi"));
        assertTrue(friend.masterSearch("edward mitchell"));
        assertFalse(friend.masterSearch("edward mic"));
        assertTrue(friend.masterSearch("Mitchell ed"));
        assertTrue(friend.masterSearch("Mitchell edward"));
        assertFalse(friend.masterSearch("Mitchell eda"));
        assertTrue(friend.masterSearch("edward "));
    }

}
