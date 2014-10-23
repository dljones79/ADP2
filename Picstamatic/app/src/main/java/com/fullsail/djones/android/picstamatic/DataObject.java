package com.fullsail.djones.android.picstamatic;

import java.io.Serializable;

/**
 * Created by David on 10/15/14.
 */
public class DataObject implements Serializable {

    private static final long serialVersionUID = 738493756273847562L;

    private String mFirstName;
    private String mLastName;
    private String mStreetAddress;
    private String mCity;
    private String mState;
    private String mZipCode;
    private String mEmail;
    private String mPhone;

    public DataObject(){}

    public DataObject(String _first, String _last, String _street, String _city, String _state, String _zip, String _email, String _phone) {
        mFirstName = _first;
        mLastName = _last;
        mStreetAddress = _street;
        mCity = _city;
        mState = _state;
        mZipCode = _zip;
        mEmail = _email;
        mPhone = _phone;
    }

    public String getmFirstName() { return mFirstName; }

    public String getmLastName() { return mLastName; }

    public String getmStreetAddress() { return mStreetAddress; }

    public String getmCity() { return mCity; }

    public String getmState() { return mState; }

    public String getmZipCode() { return mZipCode; }

    public String getmEmail() { return mEmail; }

    public String getmPhone() { return mPhone; }

    public void setmFirstName(String mFirstName) { this.mFirstName = mFirstName; }

    public void setmLastName(String mLastName) { this.mLastName = mLastName; }

    public void setmStreetAddress(String mStreetAddress) { this.mStreetAddress = mStreetAddress; }

    public void setmCity(String mCity) { this.mCity = mCity; }

    public void setmState(String mState) { this.mState = mState; }

    public void setmZipCode(String mZipCode) { this.mZipCode = mZipCode; }

    public void setmEmail(String mEmail) { this.mEmail = mEmail; }

    public void setmPhone(String mPhone) { this.mPhone = mPhone; }
}
