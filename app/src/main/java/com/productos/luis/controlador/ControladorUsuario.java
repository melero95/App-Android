package com.productos.luis.controlador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.productos.luis.modelo.Usuario;
import com.productos.luis.sqlite.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class ControladorUsuario {

    private SQLiteHelper dbHelper;
    private SQLiteDatabase db;
    private Context context;
    private String usuarioActual; // Usuario actualmente en sesión

    public ControladorUsuario(Context context) {
        dbHelper = new SQLiteHelper(context);
        this.context = context;
    }

    // ---------------------------------------
    // SECCIÓN 0 : CONTROL SOBRE LA BASE DE DATOS
    // ---------------------------------------

    // Abrir base de datos (cuando se necesite)
    public void abrirBaseDatos() {
        if (db == null || !db.isOpen()) {
            db = dbHelper.getWritableDatabase();
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

    // ---------------------------------------
    // SECCIÓN 1: FUNCIONES DE USUARIO NORMAL
    // ---------------------------------------

    // Obtener y establecer el usuario actual
    public String getUsuarioActual() {
        return usuarioActual;
    }

    public void setUsuarioActual(String usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    // Método para comprobar si un usuario ya existe
    public boolean usuarioExiste(String login) {
        abrirBaseDatos(); // Asegura que la base está abierta
        if (db == null) {
            Toast.makeText(context, "Error al abrir la base de datos", Toast.LENGTH_SHORT).show();
            return false;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE login = ?", new String[]{login});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }


    // Método para insertar un nuevo usuario
    public boolean insertarUsuario(String login, String password) {
        dbHelper.abrirBaseDatos(); // Asegura que la base está abierta
        if (usuarioExiste(login)) {
            Toast.makeText(context, "El usuario ya existe", Toast.LENGTH_SHORT).show();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("login", login);
        values.put("password", password);
        values.put("saldo", 1000.0); // Saldo inicial
        values.put("es_admin", 0);

        long resultado = db.insert("usuarios", null, values);

        if (resultado == -1) {
            Toast.makeText(context, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Usuario creado correctamente", Toast.LENGTH_SHORT).show();
        }

        return resultado != -1;
    }

    // Método para verificar credenciales
    public boolean verificarUsuario(String login, String password) {
        dbHelper.abrirBaseDatos(); // Asegura que la base está abierta
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE login = ? AND password = ?",
                new String[]{login, password});

        boolean coincide = cursor.getCount() > 0;
        cursor.close();
        return coincide;
    }

    // Verificar si es admin
    public boolean esAdmin(String login) {
        dbHelper.abrirBaseDatos(); // Asegura que la base está abierta
        Cursor cursor = db.rawQuery("SELECT es_admin FROM usuarios WHERE login = ?", new String[]{login});
        boolean esAdmin = false;

        if (cursor.moveToFirst()) {
            esAdmin = cursor.getInt(0) == 1; // Verifica si es administrador
        }

        cursor.close();
        return esAdmin;
    }

    // Método para obtener el saldo del usuario actual
    public double obtenerSaldoActual() {
        dbHelper.abrirBaseDatos(); // Asegura que la base está abierta
        double saldo = 0.0;

        if (usuarioActual == null || usuarioActual.isEmpty()) {
            Toast.makeText(context, "Usuario actual no definido", Toast.LENGTH_SHORT).show();
            return saldo;
        }

        // Consulta para obtener el saldo del usuario actual
        String query = "SELECT saldo FROM usuarios WHERE login = ?";
        Cursor cursor = db.rawQuery(query, new String[]{usuarioActual});

        if (cursor.moveToFirst()) {
            saldo = cursor.getDouble(0); // Obtiene el saldo desde la primera columna
        } else {
            Toast.makeText(context, "No se encontró saldo para el usuario", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        return saldo;
    }

    // ---------------------------------------
    // SECCIÓN 2: FUNCIONES DE ADMINISTRADOR
    // ---------------------------------------

    // Método para ascender a administrador
    public boolean ascenderUsuario(String login) {
        dbHelper.abrirBaseDatos(); // Asegura que la base está abierta
        ContentValues values = new ContentValues();
        values.put("es_admin", 1);

        int filasActualizadas = db.update("usuarios", values, "login = ?", new String[]{login});
        return filasActualizadas > 0;
    }

    // Método para degradar a usuario normal
    public boolean degradarUsuario(String login) {
        dbHelper.abrirBaseDatos(); // Asegura que la base está abierta
        ContentValues values = new ContentValues();
        values.put("es_admin", 0);

        int filasActualizadas = db.update("usuarios", values, "login = ?", new String[]{login});
        return filasActualizadas > 0;
    }

    // Método para aumentar saldo
    public boolean aumentarSaldo(String login, double cantidad) {
        dbHelper.abrirBaseDatos(); // Asegura que la base está abierta
        Cursor cursor = db.rawQuery("SELECT saldo FROM usuarios WHERE login = ?", new String[]{login});
        if (cursor.moveToFirst()) {
            double saldoActual = cursor.getDouble(0);
            double nuevoSaldo = saldoActual + cantidad;

            if (nuevoSaldo < 0) {
                nuevoSaldo = 0;
            }

            ContentValues values = new ContentValues();
            values.put("saldo", nuevoSaldo);

            int filasActualizadas = db.update("usuarios", values, "login = ?", new String[]{login});
            cursor.close();
            return filasActualizadas > 0;
        }
        cursor.close();
        return false;
    }

    // Eliminar usuario
    public boolean eliminarUsuario(String login) {
        dbHelper.abrirBaseDatos(); // Asegura que la base está abierta
        int filasEliminadas = db.delete("usuarios", "login = ?", new String[]{login});
        return filasEliminadas > 0;
    }

    // Obtener lista de todos los usuarios
    public List<Usuario> obtenerTodosLosUsuarios() {
        dbHelper.abrirBaseDatos(); // Asegura que la base está abierta
        List<Usuario> listaUsuarios = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM usuarios", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                double saldo = cursor.getDouble(cursor.getColumnIndexOrThrow("saldo"));
                int esAdminInt = cursor.getInt(cursor.getColumnIndexOrThrow("es_admin"));
                boolean esAdmin = (esAdminInt == 1);

                Usuario usuario = new Usuario(id, nombre, password, saldo, esAdmin);
                listaUsuarios.add(usuario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return listaUsuarios;
    }
}
