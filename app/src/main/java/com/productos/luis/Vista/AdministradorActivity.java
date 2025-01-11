package com.productos.luis.Vista;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.productos.luis.R;
import com.productos.luis.adaptadores.ListUsuariosAdapter;
import com.productos.luis.controlador.ControladorAdministrador;
import com.productos.luis.modelo.Usuario;
import com.productos.luis.sqlite.SQLiteHelper;

import java.util.List;

public class AdministradorActivity extends AppCompatActivity {

    private ControladorAdministrador controladorAdministrador;
    private SQLiteHelper helper;
    private ListView listViewUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AdministradorActivity", "onCreate - Inicio");
        setContentView(R.layout.vista_listview_administradores);
        Log.d("AdministradorActivity", "onCreate - Vista inflada correctamente");

        // Inicializa el controlador y la vista
        controladorAdministrador = new ControladorAdministrador(this);
        listViewUsuarios = findViewById(R.id.listView);
        Log.d("AdministradorActivity", "onCreate - Controlador y ListView inicializados");

        // Configura la lista de usuarios
        configurarListaUsuarios();
    }


    private void configurarListaUsuarios() {
        try {
            // Obtén la lista de usuarios
            List<Usuario> listaUsuarios = controladorAdministrador.obtenerTodosLosUsuarios();
            if (listaUsuarios != null && !listaUsuarios.isEmpty()) {
                Log.d("AdministradorActivity", "Usuarios obtenidos: " + listaUsuarios.size());

                // Configura el adaptador
                ListUsuariosAdapter adapter = new ListUsuariosAdapter(this, listaUsuarios);
                listViewUsuarios.setAdapter(adapter);
            } else {
                Log.d("AdministradorActivity", "No se encontraron usuarios.");
                Toast.makeText(this, "No se encontraron usuarios en la base de datos.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("AdministradorActivity", "Error al configurar la lista de usuarios", e);
            Toast.makeText(this, "Error al cargar la lista de usuarios.", Toast.LENGTH_SHORT).show();
        }
    }

    public void cambiarRol(String login) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = helper.getReadableDatabase();
            cursor = db.rawQuery("SELECT rol FROM usuarios WHERE login=?", new String[]{login});

            if (cursor.moveToFirst()) { // Si encuentra el usuario
                int rolActual = cursor.getInt(0); // Obtiene el rol actual (1 o 0)

                if (rolActual == 1) { // Si es administrador
                    if (controladorAdministrador.quitarRolAdministrador(login)) {
                        mostrarMensaje("El usuario " + login + " ha sido degradado a usuario ordinario.");
                    } else {
                        mostrarMensaje("Error al degradar al usuario " + login + ".");
                    }
                } else { // Si es usuario ordinario
                    if (controladorAdministrador.darRolAdministrador(login)) {
                        mostrarMensaje("El usuario " + login + " ha sido ascendido a administrador.");
                    } else {
                        mostrarMensaje("Error al ascender al usuario " + login + ".");
                    }
                }
            } else {
                mostrarMensaje("Usuario no encontrado.");
            }
        } catch (Exception e) {
            mostrarMensaje("Error al cambiar el rol del usuario: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
