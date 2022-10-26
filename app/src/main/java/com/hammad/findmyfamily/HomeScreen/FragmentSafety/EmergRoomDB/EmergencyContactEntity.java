package com.hammad.findmyfamily.HomeScreen.FragmentSafety.EmergRoomDB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.hammad.findmyfamily.Util.Constants;

import java.io.Serializable;

@Entity (tableName = Constants.EMERGENCY_CONTACT)
public class EmergencyContactEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id ;

    @ColumnInfo(name = Constants.OWNER_EMAIL)
    private String ownerEmail;

    @ColumnInfo(name = Constants.OWNER_PHONE_NO)
    private String OwnerPhoneNo;

    @ColumnInfo(name = Constants.CONTACT_NAME)
    private String contactName;

    @ColumnInfo(name = Constants.CONTACT_NO)
    private String ContactNo;

    @ColumnInfo(name = Constants.IS_CONTACT_APPROVED)
    private boolean isContactApproved;

    public EmergencyContactEntity() {}

    public EmergencyContactEntity(String ownerEmail, String ownerPhoneNo, String contactName, String contactNo, boolean isContactApproved) {
        this.ownerEmail = ownerEmail;
        OwnerPhoneNo = ownerPhoneNo;
        this.contactName = contactName;
        ContactNo = contactNo;
        this.isContactApproved = isContactApproved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhoneNo() {
        return OwnerPhoneNo;
    }

    public void setOwnerPhoneNo(String ownerPhoneNo) {
        OwnerPhoneNo = ownerPhoneNo;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNo() {
        return ContactNo;
    }

    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }

    public boolean isContactApproved() {
        return isContactApproved;
    }

    public void setContactApproved(boolean contactApproved) {
        isContactApproved = contactApproved;
    }
}
