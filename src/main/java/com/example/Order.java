package com.example;

public class Order {

    private String ID;
    private String productID;
    private String sellerID;
    private String buyerID;
    private String cost;

    public String getID()
    {
        return this.ID;
    }

    public String getProductID()
    {
        return this.productID;
    }

    public String getSellerID()
    {
        return this.sellerID;
    }

    public String getBuyerID()
    {
        return this.buyerID;
    }

    public String getCost()
    {
        return this.cost;
    }

    public void setID(String i)
    {
        this.ID = i;
    }

    public void setProductID(String p)
    {
        this.productID = p;
    }

    public void setSellerID(String s)
    {
        this.sellerID = s;
    }

    public void setBuyerID(String b)
    {
        this.buyerID = b;
    }

    public void setCost(String c)
    {
        this.cost = c;
    }
}
