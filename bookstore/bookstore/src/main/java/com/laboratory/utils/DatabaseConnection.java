package com.laboratory.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bookstore"; // 数据库连接
    private static final String USER = "root"; // 用户名
    private static final String PASSWORD = "mmq183193"; // 密码

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
