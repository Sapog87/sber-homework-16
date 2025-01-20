package org.sber;

import org.sber.proxy.CachedInvocationHandler;
import org.sber.storage.DatabaseStorage;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseCacheProxy {
    private final String url;
    private final String user;
    private final String password;

    public DatabaseCacheProxy(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Object cache(Object delegate) {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println(connection);
            return Proxy.newProxyInstance(
                    ClassLoader.getSystemClassLoader(),
                    delegate.getClass().getInterfaces(),
                    new CachedInvocationHandler(
                            delegate,
                            new DatabaseStorage(connection)
                    )
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}