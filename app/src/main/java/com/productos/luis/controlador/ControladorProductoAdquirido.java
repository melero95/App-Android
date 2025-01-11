package com.productos.luis.controlador;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.productos.luis.modelo.Producto;
import com.productos.luis.modelo.ProductoAdquirido;
import com.productos.luis.sqlite.SQLiteHelper;

import java.util.ArrayList;
import java.util.List;

public class ControladorProductoAdquirido {

    private SQLiteHelper dbHelper;
    private SQLiteDatabase db;

    // Constructor
    public ControladorProductoAdquirido(Context context) {
        dbHelper = new SQLiteHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    // Obtener todos los productos adquiridos de un usuario
    public List<ProductoAdquirido> obtenerProductosAdquiridos(String usuario) {
        List<ProductoAdquirido> listaProductosAdquiridos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT producto, cantidad FROM compras WHERE usuario = ?",
                new String[]{usuario});

        if (cursor.moveToFirst()) {
            do {
                String nombreProducto = cursor.getString(cursor.getColumnIndexOrThrow("producto"));
                int cantidadAdquirida = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));

                // Crear una instancia de Producto (basado en su nombre)
                Producto producto = obtenerProductoPorNombre(nombreProducto);

                listaProductosAdquiridos.add(new ProductoAdquirido(nombreProducto, cantidadAdquirida, producto));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return listaProductosAdquiridos;
    }

    /**
     * Método para obtener un Producto basado en su nombre.
     */
    private Producto obtenerProductoPorNombre(String nombreProducto) {
        Cursor cursor = db.rawQuery("SELECT id, nombre, precio, stock FROM productos WHERE nombre = ?", new String[]{nombreProducto});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
            int stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"));
            cursor.close();

            return new Producto(id, nombre, precio, stock);
        }
        cursor.close();
        return null; // Retorna null si no se encuentra el producto
    }



    // Actualizar la cantidad de un producto adquirido
    public boolean actualizarCantidad(String usuario, String nombreProducto, int nuevaCantidad) {
        ContentValues values = new ContentValues();
        values.put("cantidad", nuevaCantidad);

        int filasActualizadas = db.update("compras", values, "usuario = ? AND producto = ?",
                new String[]{usuario, nombreProducto});
        return filasActualizadas > 0;
    }

    // Eliminar un producto adquirido
    public boolean eliminarProductoAdquirido(String usuario, String nombreProducto) {
        int filasEliminadas = db.delete("compras", "usuario = ? AND producto = ?",
                new String[]{usuario, nombreProducto});
        return filasEliminadas > 0;
    }

    // Cerrar base de datos
    public void cerrarBaseDatos() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }


}
