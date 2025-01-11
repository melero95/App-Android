package com.productos.luis.controlador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.productos.luis.modelo.Producto;
import com.productos.luis.sqlite.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class ControladorProducto {
    private SQLiteHelper dbHelper;
    private SQLiteDatabase db;

    //constructor de la clase
    public ControladorProducto(Context context) {
        dbHelper = new SQLiteHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Obtener todos los productos de la base de datos
    public List<Producto> obtenerTodosLosProductos() {
        List<Producto> listaProductos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM productos", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                int stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"));

                listaProductos.add(new Producto(id, nombre, precio, stock));

                // Verificación temporal
                System.out.println("Producto: " + nombre + ", Precio: " + precio);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaProductos;
    }

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

    //Metodo para registrar las compras en la tabla compras
    public boolean registrarCompra(String usuario, String producto, int cantidad) {
        ContentValues values = new ContentValues();
        values.put("usuario", usuario);
        values.put("producto", producto);
        values.put("cantidad", cantidad);

        long resultado = db.insert("compras", null, values);
        return resultado != -1; // Devuelve true si la inserción fue exitosa
    }

    // Método para actualizar el precio de un producto
    public boolean actualizarPrecio(int id, double nuevoPrecio) {
        ContentValues values = new ContentValues();
        values.put("precio", nuevoPrecio);

        int filasActualizadas = db.update("productos", values, "id = ?", new String[]{String.valueOf(id)});
        return filasActualizadas > 0;
    }

    // Método para actualizar el stock de un producto
    public boolean actualizarStock(int id, int nuevoStock) {
        ContentValues values = new ContentValues();
        values.put("stock", nuevoStock);

        int filasActualizadas = db.update("productos", values, "id = ?", new String[]{String.valueOf(id)});
        return filasActualizadas > 0;
    }

    public boolean reducirStock(String nombreProducto, int cantidad) {

        Cursor cursor = db.rawQuery("SELECT stock FROM productos WHERE nombre = ?", new String[]{nombreProducto});
        if (cursor.moveToFirst()) {
            int stockActual = cursor.getInt(0);

            if (stockActual >= cantidad) {
                ContentValues values = new ContentValues();
                values.put("stock", stockActual - cantidad);
                db.update("productos", values, "nombre = ?", new String[]{nombreProducto});
                cursor.close();
                return true;
            } else {
                cursor.close();
                return false; // No hay suficiente stock
            }
        }

        cursor.close();
        return false; // Producto no encontrado
    }


}
