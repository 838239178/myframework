package org.shijh.myframework.framework.dao;



import org.shijh.myframework.framework.annotation.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@Component("connectionPool")
public class ConnectionPool {
    private final int MAX_POOL_SIZE = 50;//最大连接数

    private final int MIN_POOL_SIZE = 10;//最小连接数

    private int poolSize = MIN_POOL_SIZE;//已经创建的连接数

    /**
     * 阻塞并发队列，当队列为空时将阻塞直至获取资源
     */
    private final Queue<Connection> pool = new LinkedBlockingQueue<>(MAX_POOL_SIZE);

    public ConnectionPool() {
        for (int i = 0; i < MIN_POOL_SIZE; i++) {
            pool.add(ConnectionManager.getConnection());
        }
    }

    public Connection getConnection() {
        if (pool.isEmpty()) {
            if (poolSize < MAX_POOL_SIZE) {
                pool.add(ConnectionManager.getConnection());
                poolSize++;
            }
        }
        return pool.poll();
    }

    public void returnConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed() && connection.isValid(1)) {
                pool.add(connection);
            } else {
                throw new SQLException("数据库连接以关闭或无法使用");
            }
        } catch (SQLException se) {
            poolSize--;
            se.printStackTrace();
        }
    }
}
