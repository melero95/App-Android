package com.productos.luis.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.productos.luis.R;
import com.productos.luis.modelo.Producto;
import com.productos.luis.modelo.ProductoAdquirido;

import java.util.List;

public class ListProductoAdquiridoAdapter extends BaseAdapter {

    private Context context;
    private List<ProductoAdquirido> productos;
    private LayoutInflater inflater;

    // Constructor
    public ListProductoAdquiridoAdapter(Context context, List<ProductoAdquirido> productos) {
        this.context = context;
        this.productos = productos;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return productos.size();  // Número de productos adquiridos
    }

    @Override
    public Object getItem(int position) {
        return productos.get(position);  // Producto en la posición actual
    }

    @Override
    public long getItemId(int position) {
        return position;  // Usamos la posición como ID
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_producto_adquirido, parent, false);
            holder = new ViewHolder();
            holder.imgProducto = convertView.findViewById(R.id.imgProductoAdquirido);
            holder.txtNombre = convertView.findViewById(R.id.txtNombreProducto);
            holder.txtCantidad = convertView.findViewById(R.id.txtCantidadAdquirida);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Obtener el producto adquirido actual
        ProductoAdquirido productoAdquirido = productos.get(position);
        Producto producto = productoAdquirido.getProducto(); // Obtener la instancia de Producto

        if (producto != null) {
            // Generar dinámicamente el nombre del recurso de la imagen
            String nombreRecurso = "img_" + producto.getId(); // Ejemplo: img_1
            int resourceId = context.getResources().getIdentifier(nombreRecurso, "drawable", context.getPackageName());

            // Configurar la imagen
            if (resourceId != 0) { // Si el recurso existe
                holder.imgProducto.setImageResource(resourceId);
            } else {
                holder.imgProducto.setImageResource(R.drawable.ic_launcher_foreground); // Imagen por defecto
            }
        } else {
            holder.imgProducto.setImageResource(R.drawable.ic_launcher_foreground); // Imagen por defecto si no existe Producto
        }

        // Configurar los textos
        holder.txtNombre.setText(productoAdquirido.getNombreProducto());
        holder.txtCantidad.setText("Cantidad: " + productoAdquirido.getCantidadAdquirida());

        return convertView;
    }



    // ViewHolder para mejorar el rendimiento
    static class ViewHolder {
        ImageView imgProducto;
        TextView txtNombre;
        TextView txtCantidad;
    }
}
