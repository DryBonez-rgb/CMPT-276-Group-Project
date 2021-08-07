/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at  
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import java.io.*;  
import javax.servlet.*;  
import javax.servlet.http.*;  

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index() {
    return "index";
  }
 // ==============================
 // SIGN UP
 // ==============================
  
 @RequestMapping("/success")
 String success() {
   return "success";
 }

  @GetMapping(
    path = "/signup"
  )
  public String getSignUpForm(Map<String, Object> model) {
    Account account = new Account();
    model.put("account", account);
    return "signup";
  }

  // Save the account data into the database
  @PostMapping(
    path = "/signup",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleBrowserPersonSubmit(Map<String, Object> model, Account account) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS accounts (id serial, name varchar(20), password varchar(20), type varchar(20), premium bool)");
      String sql = "INSERT INTO accounts (name,password,type,premium) VALUES ('" + account.getName() + "','" + account.getPassword()  + "','"  + account.getType() + "','" + account.getPremium() + "')";
      stmt.executeUpdate(sql);
      System.out.println(account.getName() + " " + account.getPassword());
      return "redirect:/success";
    }
    catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }


@RequestMapping("/invalid")
 String invalid() {
   return "invalid";
 }

//================================
// LOGIN
//================================

  @GetMapping(
    path = "/login"
  )
  public String getLoginForm(Map<String, Object> model) {
    Account account = new Account();
    model.put("account", account);
    return "login";
  }

  @PostMapping (
    path = "/login",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )

  public String handleBrowserAccountLogin(Map<String, Object> model, Account user) throws Exception {
   
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE name='" + user.getName() + "'");
      
      if(rs.next() == false) // checks for invalid username.
      {
        return "redirect:/invalid";
      }

      String passcheck = user.getPassword();

      if(passcheck.equals(rs.getString("password"))) // check for valid password
      {
  
      Account CurrentUser = new Account();
      CurrentUser.setName(rs.getString("name"));
      CurrentUser.setID(rs.getString("id"));
      CurrentUser.setPassword(rs.getString("password"));
      CurrentUser.setType(rs.getString("type"));
      CurrentUser.setPremium(rs.getBoolean("premium"));
      
      System.out.println(CurrentUser.getName() + " " + CurrentUser.getPassword() + " " + CurrentUser.getID() + " " + CurrentUser.getType());
      
      String type = CurrentUser.getType();

      if(type.equals("Normal"))
      {
        model.put("user", CurrentUser);
        return "redirect:/";
      }

      else if(type.equals("Author"))
      {
        model.put("user", CurrentUser);
        return "redirect:/author";
      }

      else if(type.equals("Admin"))
      {
        model.put("user", CurrentUser);
        return "redirect:/admin";
      }

      
      return "redirect:/";
      
    }

      else {
        return "redirect:/invalid"; // If password check fails.
      }
    }

    catch (Exception e) {
    model.put("message", e.getMessage());
    return "error";
    }
  }

//================================
// ORDER DATABASE
//================================
  
@GetMapping(
  path = "/order"
)
public String getOrderForm(Map<String, Object> model, HttpServletRequest request) {
  HttpSession session = request.getSession(false);
  if(session == null) { // If not logged in
      return "redirect:/invalid";
    }
  else {
    Order order = new Order();
    model.put("order", order);
    return "order";
  }
}

// Save the order data into the database
@PostMapping(
  path = "/order",
  consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
)
public String handleBrowserOrderSubmit(Map<String, Object> model, Order order, HttpServletRequest request) throws Exception {
  try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    HttpSession session = request.getSession();
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS orders (id serial, productID varchar(20), sellerID varchar(20), buyerID varchar(20), cost varchar(20))");
    String sql = "INSERT INTO orders (productID,sellerID,buyerID,cost) VALUES ('" + order.getProductID()  + "','"  + order.getSellerID() + "','" + session.getAttribute("ID") + "','" + order.getCost() + "')"; 
    stmt.executeUpdate(sql);
    System.out.println(order.getProductID() + " " + order.getSellerID() + " " + session.getAttribute("ID") + " " + order.getCost());
    return "redirect:/success";
  }
  catch (Exception e) {
    model.put("message", e.getMessage());
    return "error";
  }
}

//================================
// SUBMIT PRODUCT
//================================

@GetMapping(
  path = "/upload"
)
public String getProductForm(Map<String, Object> model) {
  Product product = new Product();
  model.put("product", product);
  return "upload";
}

@PostMapping (
  path = "/upload",
  consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
)

public String handleBrowserProductSubmit(Map<String, Object> model, Product product) throws Exception {
  try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (productId serial, sellerId varchar(20), title varchar(80), image varchar(50), status bool, price varchar(10), author varchar(80), subject varchar(40), description varchar(400), address01 varchar(200), address02 varchar(100), city varchar(20), province varchar(40), postal varchar(10))");
    String sql = "INSERT INTO products (title, status, price, author, subject, description, address01, city, province, postal) VALUES ('"+ product.getTitle() +"','"+ "TRUE"+ "','"+ product.getPrice()+ "','" + product.getAuthor() + "','"+ product.getSubject() +"','"+ product.getDescription() + "','" + product.getAddress01()+ "','" + product.getCity()+ "','" + product.getProvince()+ "','" + product.getPostal()+"')";
    stmt.executeUpdate(sql);
    // System.out.println(account.getName() + " " + account.getPassword());
    return "redirect:/success";
  }
  catch (Exception e) {
    model.put("message", e.getMessage());
    return "error";
  }
}

//================================
// SUBMIT EBOOK
//================================

@GetMapping(
  path = "/uploadebook"
)
public String getEbookForm(Map<String, Object> model) {
  Product product = new Product();
  model.put("product", product);
  return "uploadebook";
}

@PostMapping (
  path = "/uploadebook",
  consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
)

public String handleBrowserEbookSubmit(Map<String, Object> model, Product product, HttpServletRequest request) throws Exception {
  try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    HttpSession session = request.getSession(); 
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (productId serial, sellerId varchar(20), title varchar(80), image varchar(50), status bool, price varchar(10), author varchar(80), subject varchar(40), description varchar(400), address01 varchar(200), address02 varchar(100), city varchar(20), province varchar(40), postal varchar(10))");
    String sql = "INSERT INTO products (title, status, price, author, subject, description, address01, city, province, postal) VALUES ('"+ product.getTitle() +"','"+ "TRUE"+ "','"+ product.getPrice()+ "','" + session.getAttribute("name") + "','"+ product.getSubject() +"','"+ product.getDescription() + "','" + product.getAddress01()+ "','" + product.getCity()+ "','" + product.getProvince()+ "','" + product.getPostal()+"')";
    stmt.executeUpdate(sql);
    // System.out.println(account.getName() + " " + account.getPassword());
    return "redirect:/success";
  }
  catch (Exception e) {
    model.put("message", e.getMessage());
    return "error";
  }
}

@RequestMapping("/productdisplay/{pid}")
String getProduct(Map<String, Object> model, @PathVariable String pid) {
  try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM products WHERE productId=" + Integer.parseInt(pid));
  
    Product productDisplayed = new Product(rs.getString("title"), rs.getString("author"), rs.getString("price"), rs.getString("sellerID"), rs.getString("image"));

    model.put("output", productDisplayed);
    return "productdisplay";
  } catch (Exception e) {
    model.put("message", e.getMessage());
    return "error";
  }
}

@RequestMapping("/author/{aid}")
String getAuthor(Map<String, Object> model, @PathVariable String aid) {
  try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE id=" + Integer.parseInt(aid));
    ResultSet rs2 = stmt.executeQuery("SELECT title FROM products WHERE author='" + rs.getString("name"));
    ResultSet rs3 = stmt.executeQuery("SELECT productID, buyerID, cost WHERE sellerID=" + Integer.parseInt(aid));
  
    Account authorDisplayed = new Account(rs.getString("name"));

    ArrayList<String> outputproduct = new ArrayList<String>();
    while (rs2.next()) {
      outputproduct.add(rs2.getString("title"));
    }

    ArrayList<Order> outputorder = new ArrayList<Order>();
    while (rs3.next()) {
      Order order = new Order(rs.getString("productID"), rs.getString("buyerID"), rs.getString("cost"));
      outputorder.add(order);
    }

    model.put("outputauthor", authorDisplayed);
    model.put("outputproduct", outputproduct);
    model.put("outputorder", outputorder);

    return "author";
  } catch (Exception e) {
    model.put("message", e.getMessage());
    return "error";
  }
}

  @RequestMapping("/db")
  String db(Map<String, Object> model) {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
      stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
      ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getTimestamp("tick"));
      }

      model.put("records", output);
      return "db";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }


  @RequestMapping("/author")
  String author() {
    return "author";
  }
  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

}
