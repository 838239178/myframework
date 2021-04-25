package org.shijh.myframework.framework.dao;


import org.shijh.myframework.framework.bean.JdbcConfig;

import java.sql.*;

public class ConnectionManager {

//    private static final String DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//    private static final String DATABASE_URL = "jdbc:sqlserver://localhost:1433;database=xscj";
//    private static final String DATABASE_USRE = "sa";
//    private static final String DATABASE_PASSWORD = "sa";

    private static JdbcConfig jdbcConfig;

    /** 返回连接 */
    public static Connection getConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(jdbcConfig.getDriver());
            dbConnection = DriverManager.getConnection(jdbcConfig.getUrl(),
                    jdbcConfig.getUsername(), jdbcConfig.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbConnection;
    }

    /** 关闭连接*/
    public static void closeConnection(Connection dbConnection) {
        try {
            if (dbConnection != null && (!dbConnection.isClosed())) {
                dbConnection.close();
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

    }

    /**关闭结果集*/
    public static void closeResultSet(ResultSet res) {
        try {
            if (res != null) {
                res.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** 关闭语句*/
    public static void closeStatement(PreparedStatement pStatement) {
        try {
            if (pStatement != null) {
                pStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void closeStatement(Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setJdbcConfig(JdbcConfig jdbcConfig) {
        ConnectionManager.jdbcConfig = jdbcConfig;
    }
}
