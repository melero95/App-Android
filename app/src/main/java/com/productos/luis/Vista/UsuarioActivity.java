package com.productos.luis.Vista;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.productos.luis.R;
import com.productos.luis.adaptadores.GridAdapter;
import com.productos.luis.adaptadores.ListProductoAdquiridoAdapter;
import com.productos.luis.controlador.ControladorProducto;
import com.productos.luis.controlador.ControladorProductoAdquirido;
import com.productos.luis.controlador.ControladorUsuario;
import com.productos.luis.modelo.Producto;
import com.productos.luis.modelo.ProductoAdquirido;

import java.util.List;

public class UsuarioActivity extends AppCompatActivity {

    private ListView listViewProductosAdquiridos;
    private GridView gridViewProductosDisponibles;

    private Button btnIrAComprar;
    private Button btnIrAProductosAdquiridos;

    private ControladorUsuario controladorUsuario;
    private ControladorProducto controladorProducto;

    private GridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_productos_adquiridos);

        // Configurar la Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarUsuario);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Productos Adquiridos");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar los controladores
        controladorUsuario = new ControladorUsuario(this);
        controladorProducto = new ControladorProducto(this);

        // Verificar si la base de datos está abierta
        if (!controladorUsuario.estaBaseDatosAbierta()) {
            controladorUsuario.abrirBaseDatos();
        }

        // Configurar el usuario actual desde el Intent
        String usuario = getIntent().getStringExtra("usuario");
        if (usuario != null) {
            controladorUsuario.setUsuarioActual(usuario);
            Toast.makeText(this, "Bienvenido, " + usuario, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: Usuario no encontrado en el Intent.", Toast.LENGTH_SHORT).show();
        }

        // Mostrar la vista inicial de productos adquiridos
        mostrarVistaProductosAdquiridos();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @NonNull ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == R.id.gridViewProductosDisponibles) {
            getMenuInflater().inflate(R.menu.menu_producto_disponible, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position; // Obtener la posición del producto seleccionado

        if (item.getItemId() == R.id.action_comprar) {
            gestionarCompra(position);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Finaliza la actividad y vuelve a la anterior
        return true;
    }

    private void actualizarSaldo() {
        TextView saldoTextView = findViewById(R.id.txtSaldo);
        if (saldoTextView != null) {
            double saldo = controladorUsuario.obtenerSaldoActual();
            saldoTextView.setText(String.format("Saldo: %.2f €", saldo));
        }
    }

    private void mostrarVistaProductosAdquiridos() {
        setContentView(R.layout.vista_productos_adquiridos);

        listViewProductosAdquiridos = findViewById(R.id.listViewProductosAdquiridos);
        btnIrAComprar = findViewById(R.id.btnIrAProductosDisponibles);

        // Inicializar el controlador de productos adquiridos
        ControladorProductoAdquirido controladorProductoAdquirido = new ControladorProductoAdquirido(this);

        // Obtener los productos adquiridos del usuario actual
        List<ProductoAdquirido> productosAdquiridos = controladorProductoAdquirido.obtenerProductosAdquiridos(controladorUsuario.getUsuarioActual());

        // Configurar el adaptador personalizado para productos adquiridos
        ListProductoAdquiridoAdapter adapter = new ListProductoAdquiridoAdapter(this, productosAdquiridos);
        listViewProductosAdquiridos.setAdapter(adapter);

        btnIrAComprar.setOnClickListener(v -> mostrarVistaProductosDisponibles());

        actualizarSaldo();
    }


    private void mostrarVistaProductosDisponibles() {
        setContentView(R.layout.vista_productos_disponibles);

        gridViewProductosDisponibles = findViewById(R.id.gridViewProductosDisponibles);
        btnIrAProductosAdquiridos = findViewById(R.id.btnIrAProductosAdquiridos);

        // Obtener productos desde la base de datos
        List<Producto> productosDisponibles = controladorProducto.obtenerTodosLosProductos();

        // Configurar el adaptador para el GridView
        gridAdapter = new GridAdapter(this, productosDisponibles);
        gridViewProductosDisponibles.setAdapter(gridAdapter);

        // Registrar el menú contextual
        registerForContextMenu(gridViewProductosDisponibles);

        btnIrAProductosAdquiridos.setOnClickListener(v -> mostrarVistaProductosAdquiridos());

        actualizarSaldo();
    }

private void gestionarCompra(int position) {
    // Obtener el producto seleccionado
    Producto productoSeleccionado = controladorProducto.obtenerTodosLosProductos().get(position);

    String nombreProducto = productoSeleccionado.getNombreProducto();
    double precioProducto = productoSeleccionado.getPrecio();

    // Verificar si el usuario tiene saldo suficiente
    double saldoActual = controladorUsuario.obtenerSaldoActual();
    if (saldoActual < precioProducto) {
        Toast.makeText(this, "Saldo insuficiente para comprar este producto", Toast.LENGTH_SHORT).show();
        return;
    }

    // Restar saldo al usuario
    controladorUsuario.aumentarSaldo(controladorUsuario.getUsuarioActual(), -precioProducto);

    // Reducir stock del producto
    boolean stockActualizado = controladorProducto.reducirStock(nombreProducto, 1);
    if (!stockActualizado) {
        Toast.makeText(this, "Error al reducir el stock", Toast.LENGTH_SHORT).show();
        return;
    }

    // Registrar la compra
    boolean compraRegistrada = controladorProducto.registrarCompra(controladorUsuario.getUsuarioActual(), nombreProducto, 1);
    if (compraRegistrada) {
        Toast.makeText(this, "Compra registrada: " + nombreProducto, Toast.LENGTH_SHORT).show();
    } else {
        Toast.makeText(this, "Error al registrar la compra", Toast.LENGTH_SHORT).show();
    }

    // Actualizar la interfaz
    actualizarSaldo();
    //actualizarVistaProductos();
    mostrarVistaProductosDisponibles();
}

}
