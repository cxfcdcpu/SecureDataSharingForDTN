package com.example.securedatasharingfordtn.login;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserInfo {
    private String username;
    private String password;
    private List<String> attributes;
    private String firstname;
    private String lastname;
    private String registerationTime;
    private String expirationDate;
    private int userID;

    public String getString(){
        String ret = "";
        ret+= "username: "+username;
        ret+= "password: " + password;
        ret+= "firstname: " + firstname;
        ret+= "lastname: " + lastname;
        ret+= "registrationTime: " + registerationTime.toString();
        ret+= "expirationDate: " + expirationDate.toString();
        ret+= "userID: " + userID;
        return ret;
    }

    public String getExpirationDate() {
        return this.expirationDate;
    }

    public String getRegisterTime() {
        return this.registerationTime;
    }

    public String getHashedPassword() {
        return this.password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getAttributesString() {
        StringBuilder sb = new StringBuilder();
        for(String attr:attributes) {
            sb.append(attr.trim());
            sb.append(",");
        }
        if (sb.length()<=0) {
            return "";
        }
        return sb.substring(0, sb.length()-1);
    }

    public List<String> getAttributes(){
        return this.attributes;
    }

    public int getUserID() {
        return this.userID;
    }


}