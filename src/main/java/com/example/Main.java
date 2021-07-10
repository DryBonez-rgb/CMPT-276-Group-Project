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
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS accounts (name varchar(20), password varchar(20), type varchar(20), premium bool, id serial)");
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


//================================
// LOGIN
//================================

  @RequestMapping("/login")
  String login() {
    return "login";
  }

  @PostMapping (
    path = "/login",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleBrowserAccountLogin(Map<String, Object> model, Account user) throws Exception {
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM accounts WHERE password="+(user.getPassword()));
      ResultSet ns = stmt.executeQuery("SELECT * FROM accounts WHERE name="+(user.getName()));
      rs.next();
      ns.next();
      if(rs.getString("id") == ns.getString("id")) // make sure the username and the password are for the same account id
      {

        Account CurrentUser = new Account();
        CurrentUser.setName(rs.getString("name"));
        CurrentUser.setID(rs.getString("id"));
        CurrentUser.setPassword(rs.getString("password"));
        CurrentUser.setType(rs.getString("type"));
        CurrentUser.setPremium(rs.getBoolean("premium"));
      
        if(CurrentUser.getType() == "Normal")
        {
          model.put("user", CurrentUser);
          return "index";
        }

        if(CurrentUser.getType() == "Author")
        {
          model.put("user", CurrentUser);
          return "author";
        }

        if(CurrentUser.getType() == "Publisher")
        {
          model.put("user", CurrentUser);
         return "publisher";
        }

        if(CurrentUser.getType() == "Admin")
        {
          model.put("user", CurrentUser);
          return "admin";
        }
      
        return "error"; // Shouldn't get here under normal circumstances.
      }
        else {
        return "error"; // Shouldn't get here under normal circumstances.
        }
    
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
