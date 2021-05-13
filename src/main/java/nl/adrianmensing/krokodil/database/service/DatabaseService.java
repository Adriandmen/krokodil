package nl.adrianmensing.krokodil.database.service;

import nl.adrianmensing.krokodil.database.Storable;

import java.sql.ResultSet;

public interface DatabaseService<T extends Storable> {
    T readColumnFromDatabase(ResultSet result);

    T writeColumnToDatabase(T column);
}
