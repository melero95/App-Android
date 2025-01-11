package com.productos.luis.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.nio.channels.Channel;

public class SQLiteHelper extends SQLiteOpenHelper {

    // Nombre y versión de la base de datos
    private static final String DATABASE_NAME = "productosDB";
    private static final int DATABASE_VERSION = 1;

    private SQLiteDatabase db;

    // Sentencia SQL para crear la tabla 'usuarios'
    private static final String CREATE_TABLE_USUARIOS =
            "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "login TEXT UNIQUE, " +
                    "password TEXT, " +
                    "saldo REAL DEFAULT 1000.0, " +
                    "es_admin INTEGER DEFAULT 0);";

    // Usuario administrador por defecto
    private static final String INSERT_ADMIN =
            "INSERT OR REPLACE INTO usuarios (id, login, password, saldo, es_admin) " +
                    "VALUES (1, 'admin', 'admin', 1000.0, 1);";

    // Sentencia SQL para crear la tabla de productos
    private static final String CREATE_TABLE_PRODUCTOS =
            "CREATE TABLE IF NOT EXISTS productos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "precio REAL NOT NULL, " +
                    "stock INTEGER NOT NULL);";

    // Inserción de productos iniciales (5 productos)
    private static final String INSERT_PRODUCTOS =
            "INSERT INTO productos (nombre, precio, stock) VALUES " +
                    "('Escritorio', 20.5, 10), " +
                    "('Silla oficina', 35.0, 15), " +
                    "('Ordenador', 50.0, 8), " +
                    "('Monitor', 12.75, 25), " +
                    "('Lampara', 100.0, 5);";

    // Sentencia SQL para crear la tabla de compras
    private static final String CREATE_TABLE_COMPRAS =
            "CREATE TABLE IF NOT EXISTS compras (" +
                    "usuario TEXT NOT NULL, " +
                    "producto TEXT NOT NULL, " +
                    "cantidad INTEGER NOT NULL)";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIOS);
        db.execSQL(INSERT_ADMIN);

        db.execSQL(CREATE_TABLE_PRODUCTOS);
        db.execSQL(INSERT_PRODUCTOS);  // Inserción de productos iniciales

        db.execSQL(CREATE_TABLE_COMPRAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aquí se agregara lógica para actualizaciones futuras
        // Por ejemplo: ALTER TABLE o migraciones
        /*
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE productos ADD COLUMN descripcion TEXT;");
        }
        */
    }

    // Abrir base de datos (cuando se necesite)
    public void abrirBaseDatos() {

        if (db == null || !db.isOpen()) {
            db = getWritableDatabase();
        }
    }

    // Cerrar base de datos
    public void cerrarBaseDatos() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public boolean estaBaseDatosAbierta() {
        return db != null && db.isOpen();
    }


    // Método para regenerar productos (llamado manualmente si es necesario)
    public void regenerarProductos(SQLiteDatabase db) {
        db.execSQL("DELETE FROM productos");  // Borra productos existentes
        db.execSQL(INSERT_PRODUCTOS);         // Inserta nuevos productos
    }
}
