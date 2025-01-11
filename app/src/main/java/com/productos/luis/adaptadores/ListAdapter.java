package com.productos.luis.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.productos.luis.R;
import com.productos.luis.modelo.Usuario;

import java.util.List;

public class ListAdapter extends BaseAdapter {
    private Context context;
    private List<Usuario> usuarios;
    private LayoutInflater inflater;

    // Constructor que recibe el contexto y la lista de usuarios
    public ListAdapter(Context context, List<Usuario> usuarios) {
        this.context = context;
        this.usuarios = usuarios;
        this.inflater = LayoutInflater.from(context);
    }

    // Devuelve el número total de usuarios en la lista
    @Override
    public int getCount() {
        return usuarios != null ? usuarios.size() : 0;
    }

    // Devuelve el usuario en una posición específica
    @Override
    public Object getItem(int position) {
        return usuarios.get(position);
    }

    // Devuelve el ID del ítem (usamos la posición como ID)
    @Override
    public long getItemId(int position) {
        return position;
    }

    // Infla y devuelve la vista para cada elemento de la lista
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // Reutilizamos la vista existente si no es nula (optimización con ViewHolder)
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list, parent, false);

            // Inicializamos el ViewHolder y almacenamos las referencias a los elementos de la vista
            holder = new ViewHolder();
            holder.iconoUsuario = convertView.findViewById(R.id.imageView4);
            holder.nombreUsuario = convertView.findViewById(R.id.txtNombreUsuario);
            holder.saldoUsuario = convertView.findViewById(R.id.txtSaldo);

            // Guardamos el ViewHolder en la vista para futuras reutilizaciones
            convertView.setTag(holder);
        } else {
            // Recuperamos el ViewHolder existente si la vista ya fue inflada
            holder = (ViewHolder) convertView.getTag();
        }

        // Obtenemos el usuario en la posición actual
        Usuario usuario = usuarios.get(position);

        // Asignamos los valores de nombre y saldo a los elementos de la vista
        holder.iconoUsuario.setImageResource(R.drawable.ic_persona);  // Icono de usuario
        holder.nombreUsuario.setText(usuario.getNombre());
        holder.saldoUsuario.setText(String.format("Saldo: %.2f €", usuario.getSaldo()));

        return convertView;
    }

    // Clase interna ViewHolder para optimizar el rendimiento de getView()
    static class ViewHolder {
        ImageView iconoUsuario;
        TextView nombreUsuario;
        TextView saldoUsuario;
    }

    // Método para actualizar la lista de usuarios y refrescar el ListView
    public void actualizarDatos(List<Usuario> nuevosUsuarios) {
        this.usuarios.clear();  // Limpiamos la lista actual
        this.usuarios.addAll(nuevosUsuarios);  // Añadimos los nuevos datos
        notifyDataSetChanged();  // Notificamos al ListView que los datos han cambiado
    }
}
