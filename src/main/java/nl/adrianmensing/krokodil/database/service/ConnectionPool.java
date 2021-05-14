package nl.adrianmensing.krokodil.database.service;

import java.sql.Connection;

public interface ConnectionPool {
    Connection getConnection();


}
