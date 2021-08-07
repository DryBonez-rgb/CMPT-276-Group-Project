package com.example;

public class Account 
{
    private String name;
    private String password;
    private String ID;
    private String type;
    private boolean premium;

    public Account() {}

    public Account(String name) {
        this.name = name;
    }

    public String getName()
    {
        return this.name;
    }

    public String getPassword()
    {
        return this.password;
    }

    public String getID()
    {
        return this.ID;
    }

    public String getType()
    {
        return this.type;
    }

    public boolean getPremium()
    {
        return this.premium;
    }

    public void setName(String n)
    {
        this.name = n;
    }

    public void setPassword(String p)
    {
        this.password = p;
    }

    public void setID(String i)
    {
        this.ID = i;
    }

    public void setType(String t)
    {
        this.type = t;
    }

    public void setPremium(boolean p)
    {
        this.premium = p;
    }



}