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

  public String handleBrowserAccountLogin(Map<String, Object> model, Account user, HttpServletRequest request) throws Exception {
   
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
      
      HttpSession session = request.getSession();
      
      session.setAttribute("name", rs.getString("name"));
      session.setAttribute("password", rs.getString("password"));
      session.setAttribute("ID", rs.getString("id"));
      session.setAttribute("Type", rs.getString("type"));
      session.setAttribute("Premium", rs.getBoolean("premium"));
      
      
      System.out.println(session.getAttribute("name") + " " + session.getAttribute("password")  + " " + session.getAttribute("ID") + " " + session.getAttribute("Type"));
      
      String type = (String)session.getAttribute("Type");

      if(type.equals("Normal"))
      {
        
        return "redirect:/";
      }

      else if(type.equals("Author"))
      {
        
        return "redirect:/author";
      }

      else if(type.equals("Admin"))
      {
        
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
public String get(Map<String, Object> model) {
  Order order = new Order();
  model.put("order", order);
  return "ordersuccess";
}

// Save the order data into the database
@PostMapping(
  path = "/order",
  consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
)
public String handleBrowserOrderSubmit(Map<String, Object> model, Order order) throws Exception {
  try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS accounts (id serial, productID varchar(20), sellerID varchar(20), buyerID varchar(20))");
    String sql = "INSERT INTO accounts (name,password,type,premium) VALUES ('" + order.getID() + "','" + order.getProductID()  + "','"  + order.getSellerID() + "','" + order.getBuyerID() + "')";
    stmt.executeUpdate(sql);
    System.out.println(order.getID() + " " + order.getProductID() + " " + order.getSellerID() + " " + order.getBuyerID());
    return "redirect:/ordersuccess";
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

public String handleBrowserProductSubmit(Map<String, Object> model, Product product, HttpServletRequest request) throws Exception {
  try (Connection connection = dataSource.getConnection()) {
    Statement stmt = connection.createStatement();
    HttpSession session = request.getSession();
    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (productId serial, sellerId varchar(20), title varchar(80), image varchar(50), status bool, price varchar(10), author varchar(80), subject varchar(40), description varchar(400), address01 varchar(200), address02 varchar(100), city varchar(20), province varchar(40), postal varchar(10))");
    String sql = "INSERT INTO products (sellerid, title, status, price, author, subject, description, address01, city, province, postal) VALUES ('"+session.getAttribute("ID")+"','"+product.getTitle() +"','"+ "TRUE"+ "','"+ product.getPrice()+ "','" + product.getAuthor() + "','"+ product.getSubject() +"','"+ product.getDescription() + "','" + product.getAddress01()+ "','" + product.getCity()+ "','" + product.getProvince()+ "','" + product.getPostal()+"')";
    stmt.executeUpdate(sql);
    // System.out.println(account.getName() + " " + account.getPassword());
    return "redirect:/success";
  }
  catch (Exception e) {
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

  @RequestMapping("/admin")
  String admin(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if(session == null) { // If not logged in
      return "redirect:/invalid";
    }

    else {
      String type = (String)session.getAttribute("Type");
      if(type.equals("Admin")) {
        return "redirect:/admin";
      }
      else {
        return "redirect:/access";
      }
    }
  }

  @RequestMapping("/access")
  String access() {
    return "access";
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
