package com.productos.luis.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.productos.luis.R;
import com.productos.luis.controlador.ControladorUsuario;

public class RegistroActivity extends AppCompatActivity {

    private EditText etUsuario, etPassword, etConfirmarPassword;
    private Button btnRegistrar, btnLogin;
    private ControladorUsuario controladorUsuario;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_usuario);  //esta clase usa registro_usuario.xml


        // Habilitar ActionBar y botón atrás
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        //Inicializo las variables de campos de texto y contraseñas
        etUsuario = findViewById(R.id.etRegUsuario);
        etPassword = findViewById(R.id.pswRegPassword);
        etConfirmarPassword = findViewById(R.id.pswConfirmarPassword);

        //Inicializo los botones
        btnRegistrar = findViewById(R.id.btnCrearCuenta);
        btnLogin = findViewById(R.id.btnIrAIniciarSesion);

        //Inicializo el controlador para manejar los datos
        controladorUsuario = new ControladorUsuario(this);

        //  Configuro los setOnClick de los botones
        //boton de registrar usuario
        btnRegistrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               if  (registrarUsuario()) {
                    irAIniciarSesion();
               }
            }
        });

        //boton de ir a inicio de sesion
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irAIniciarSesion();
            }
        });

    }   //fin onCreate      **********************************

    // Capturar evento del botón de retroceso (Up Button)
    @Override
    public boolean onSupportNavigateUp() {
        finish();  // Finaliza la actividad actual y vuelve a MainActivity
        return true;
    }

    // Método para registrar usuario con validaciones para evitar fallos
    private boolean registrarUsuario() {
        String usuario = etUsuario.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmarPassword.getText().toString();
        boolean resultado = true;

        //Hago validaciones. si alguna falla resutado sera false y no se ejecuta la accion de añadir usuario
        //Comprueba que no haya campos vacios
        if (usuario.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            resultado = false;
        }

        // Validación para la longitud de la contraseña, no puede ser inferior a 6
        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            resultado = false;
        }

        //Comprueba que las contraseñas introducidas sean iguales
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            resultado = false;
        }

        //Una vez que pasa lascomprobaciones, si todas son correctas ( no cambian resultado a false) se añade el usuario

        if (resultado) {
            // Intenta insertar el usuario solo si pasó todas las validaciones
            boolean insercion = controladorUsuario.insertarUsuario(usuario, password);
            if (insercion) {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                // Solo se muestra este mensaje si realmente existe un duplicado
                Toast.makeText(this, "El usuario ya existe", Toast.LENGTH_SHORT).show();
            }
        }
// Si hubo fallos en las validaciones previas, no se intenta insertar
        return false;

    }


    // Método para ir a la actividad de inicio de sesión
    private void irAIniciarSesion() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
