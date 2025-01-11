package com.productos.luis.controlador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.productos.luis.modelo.Usuario;
import com.productos.luis.sqlite.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class ControladorAdministrador {
    private SQLiteHelper dbHelper;
    private SQLiteDatabase db;

    public ControladorAdministrador(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    // Método para inicializar la base de datos si es necesario
    private SQLiteDatabase getDatabase() {
        if (db == null || !db.isOpen()) {
            db = dbHelper.getReadableDatabase(); // Usa getWritableDatabase si necesitas escritura
        }
        return db;
    }

    // Método para cerrar la base de datos
    public void cerrarBaseDatos() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    // Obtener todos los usuarios de la base de datos
    public List<Usuario> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("SELECT * FROM usuarios", null);
            Log.d("ControladorAdmin", "Número de filas en el cursor: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                    String login = cursor.getString(cursor.getColumnIndexOrThrow("login")); // Cambiado de 'nombre' a 'login'
                    String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                    double saldo = cursor.getDouble(cursor.getColumnIndexOrThrow("saldo"));
                    int esAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("es_admin")); // Cambiado de 'rol' a 'es_admin'

                    Log.d("ControladorAdmin", "Usuario encontrado: " + login + ", Saldo: " + saldo + ", esAdmin: " + esAdmin);

                    // Crea el objeto Usuario
                    Usuario usuario = new Usuario(id, login, password, saldo, esAdmin == 1);
                    usuarios.add(usuario);

                } while (cursor.moveToNext());
            } else {
                Log.d("ControladorAdmin", "El cursor está vacío.");
            }
        } catch (Exception e) {
            Log.e("ControladorAdmin", "Error al procesar el cursor", e);
        } finally {
            if (cursor != null) cursor.close();
        }

        return usuarios;
    }



    // Dar rol de administrador a un usuario
    public boolean darRolAdministrador(String login) {
        SQLiteDatabase db = getDatabase();
        ContentValues valores = new ContentValues();
        valores.put("rol", 1); // 1 indica administrador
        return db.update("usuarios", valores, "nombre=?", new String[]{login}) > 0;
    }

    // Quitar rol de administrador a un usuario
    public boolean quitarRolAdministrador(String login) {
        SQLiteDatabase db = getDatabase();
        ContentValues valores = new ContentValues();
        valores.put("rol", 0); // 0 indica usuario ordinario
        return db.update("usuarios", valores, "nombre=?", new String[]{login}) > 0;
    }

    // Ajustar el saldo de un usuario
    public boolean ajustarSaldoUsuario(String login, double cantidad) {
        SQLiteDatabase db = getDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT saldo FROM usuarios WHERE nombre=?", new String[]{login});
            if (cursor.moveToFirst()) {
                double saldoActual = cursor.getDouble(0);
                double nuevoSaldo = Math.max(saldoActual + cantidad, 0); // Saldo mínimo 0

                ContentValues valores = new ContentValues();
                valores.put("saldo", nuevoSaldo);
                return db.update("usuarios", valores, "nombre=?", new String[]{login}) > 0;
            }
        } catch (Exception e) {
            Log.e("ControladorAdmin", "Error al ajustar saldo", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return false;
    }

    // Eliminar un usuario de la base de datos
    public boolean eliminarUsuario(String login) {
        SQLiteDatabase db = getDatabase();
        return db.delete("usuarios", "nombre=?", new String[]{login}) > 0;
    }
}
