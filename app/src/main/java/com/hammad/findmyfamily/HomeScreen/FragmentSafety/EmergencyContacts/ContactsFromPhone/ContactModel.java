package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergencyContacts.ContactsFromPhone;

public class ContactModel {

    private String contactId;
    private String contactName;
    private String contactNumber;

    public ContactModel(String contactId, String contactName, String contactNumber) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.contactNumber = contactNumber;
    }

    public String getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

}
