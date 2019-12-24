package com.Vedma.Roleplay.Vedma_Alpha.POJO.API;

public class UserCreditals {
    private String UserName;
    private   String Password;
    public  UserCreditals(String UserName, String Password)
    {
        this.UserName=UserName;
        this.Password=Password;
    }

    public String getUserName() {
        return UserName;
    }

    public String getPassword() {
        return Password;
    }
}
