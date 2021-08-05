package com.example;

import javax.swing.text.StyledEditorKit.BoldAction;

public class Product 
{
    private String title;
    private String productID;
    private String sellerID;
    private Boolean status;
    private String image;
    private String price;
    private String author;
    private String subject;
    private String description;
    private String address01;
    private String address02;
    private String city;
    private String province;
    private String postal;
    private String isbn;




    public String getTitle()
    {
        return this.title;
    }

    public String getProductID()
    {
        return this.productID;
    }

    public String getSellerID()
    {
        return this.sellerID;
    }

    public Boolean getStatus()
    {
        return this.status;
    }

    public String getImage()
    {
        return this.image;
    }

    public String getAuthor()
    {
        return this.author;
    }

    public String getPrice()
    {
        return this.price;
    }

    public String getSubject()
    {
        return this.subject;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getAddress01()
    {
        return this.address01;
    }

    public String getAddress02()
    {
        return this.address02;
    }
    public String getCity()
    {
        return this.city;
    }
    public String getProvince()
    {
        return this.province;
    }
    public String getPostal()
    {
        return this.postal;
    }
    public String getIsbn()
    {
        return this.isbn;
    }

    public void setTitle(String a)
    {
        this.title = a;
    }

    public void setStatus(Boolean b)
    {
        this.status = b;
    }

    public void setImage(String c)
    {
        this.image = c;
    }

    public void setPrice(String c)
    {
        this.price = c;
    }

    public void setAuthor(String c)
    {
        this.author = c;
    }

    public void setSubject(String d)
    {
        this.subject = d;
    }

    public void setDescription(String e)
    {
        this.description = e;
    }

    public void setAddress01(String f)
    {
        this.address01 = f;
    }

    public void setAddress02(String g)
    {
        this.address02 = g;
    }

    public void setCity(String h)
    {
        this.city = h;
    }

    public void setProvince(String i)
    {
        this.province = i;
    }

    public void setPostal(String j)
    {
        this.postal = j;
    }

    public void setIsbn(String k)
    {
        this.isbn = k;
    }


}