package com.productos.luis.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.productos.luis.R;
import com.productos.luis.controlador.ControladorUsuario;

public class MainActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword;
    private Button btnIniciarSesion, btnCrearCuenta;
    private ControladorUsuario controladorUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_usuario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicialización de vistas
        etUsuario = findViewById(R.id.etLogUsuario);
        etPassword = findViewById(R.id.pswLogPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        btnCrearCuenta = findViewById(R.id.btnIrACrearCuenta);

        // Inicialización del controlador
        controladorUsuario = new ControladorUsuario(this);

        // Abrimos la base de datos al iniciar
        controladorUsuario.abrirBaseDatos();

        // Configuración de botones
        btnIniciarSesion.setOnClickListener(v -> iniciarSesion());
        btnCrearCuenta.setOnClickListener(v -> irACrearCuenta());
    }

    @Override
    protected void onDestroy() {
        // Cerramos la base de datos al destruir la actividad
        controladorUsuario.cerrarBaseDatos();
        super.onDestroy();
    }

    // Método para iniciar sesión
    private void iniciarSesion() {
        String usuario = etUsuario.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validación de campos vacíos
        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el usuario existe primero
        if (!controladorUsuario.usuarioExiste(usuario)) {
            Toast.makeText(this, "El usuario no existe. Inténtalo de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si la contraseña es correcta
        if (!controladorUsuario.verificarUsuario(usuario, password)) {
            Toast.makeText(this, "Contraseña incorrecta. Inténtalo de nuevo", Toast.LENGTH_SHORT).show();
            return;
        }

        // Comprobar si es administrador
        boolean esAdmin = controladorUsuario.esAdmin(usuario);

        // Lanzar la actividad correspondiente
        Intent intent;
        if (esAdmin) {
            intent = new Intent(this, AdministradorActivity.class);
            // Pasar datos necesarios a la actividad
            intent.putExtra("usuario", usuario);
            startActivity(intent);

        } else {
            intent = new Intent(this, UsuarioActivity.class);

            // Pasar datos necesarios a la actividad
            intent.putExtra("usuario", usuario);
            startActivity(intent);
        }

        // Finaliza la actividad de login
        finish();  // Finaliza la actividad de login
    }


    // Método para ir a la pantalla de registro
    private void irACrearCuenta() {
        Intent intent = new Intent(this, RegistroActivity.class);
        startActivity(intent);
    }
}
