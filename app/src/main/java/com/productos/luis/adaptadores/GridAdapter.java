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

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private List<Producto> productos;
    private LayoutInflater inflater;

    public GridAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.productos = productos;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return productos.size();
    }

    @Override
    public Object getItem(int position) {
        return productos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_grid, parent, false);
            holder = new ViewHolder();
            holder.imgProducto = convertView.findViewById(R.id.imgProducto);
            holder.nombreProducto = convertView.findViewById(R.id.nombreProducto);
            holder.precioProducto = convertView.findViewById(R.id.precioProducto);
            holder.stockProducto = convertView.findViewById(R.id.stockProducto);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Producto producto = productos.get(position);

        // Cargar dinámicamente la imagen basada en el ID del producto
        int resId = context.getResources().getIdentifier("img_" + producto.getId(), "drawable", context.getPackageName());
        if (resId != 0) {
            holder.imgProducto.setImageResource(resId);
        } else {
            holder.imgProducto.setImageResource(R.drawable.ic_launcher_foreground); // Imagen por defecto si no se encuentra
        }

        // Configurar los datos del producto
        holder.nombreProducto.setText(producto.getNombreProducto());
        holder.precioProducto.setText(String.format("Precio: %.2f €", producto.getPrecio()));
        holder.stockProducto.setText("Stock: " + producto.getStock());

        return convertView;
    }

    // Método para actualizar el GridView dinámicamente
    public void actualizarDatos(List<Producto> nuevosProductos) {
        this.productos.clear();
        this.productos.addAll(nuevosProductos);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        ImageView imgProducto;
        TextView nombreProducto;
        TextView precioProducto;
        TextView stockProducto;
    }
}
