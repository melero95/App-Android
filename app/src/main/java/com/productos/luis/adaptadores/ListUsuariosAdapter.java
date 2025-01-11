package com.productos.luis.adaptadores;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.productos.luis.R;
import com.productos.luis.modelo.Usuario;

import java.util.List;

public class ListUsuariosAdapter extends BaseAdapter {

    private Context context;
    private List<Usuario> usuarios;

    public ListUsuariosAdapter(Context context, List<Usuario> usuarios) {
        this.context = context;
        this.usuarios = usuarios;
    }

    @Override
    public int getCount() {
        return usuarios.size(); // Número de usuarios en la lista
    }

    @Override
    public Object getItem(int position) {
        return usuarios.get(position); // Usuario en la posición dada
    }

    @Override
    public long getItemId(int position) {
        return position; // Puedes devolver un ID único del usuario aquí si existe
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        }

        // Obtén los elementos de la vista
        ImageView icUsuario = convertView.findViewById(R.id.icUsuario);
        TextView txtNombreUsuario = convertView.findViewById(R.id.txtNombreUsuario);
        TextView txtSaldoUsuario = convertView.findViewById(R.id.txtSaldoUsuario);

        // Obtén el usuario actual
        Usuario usuario = usuarios.get(position);

        // Log para depurar
        Log.d("ListUsuariosAdapter", "Inflando vista para: " + usuario.getNombre());

        // Asigna los valores
        txtNombreUsuario.setText(usuario.getNombre());
        txtSaldoUsuario.setText(String.format("%.2f €", usuario.getSaldo()));

        // Asigna íconos diferentes para usuarios normales y administradores
        if (usuario.isAdmin()) {
            icUsuario.setImageResource(R.drawable.ic_admin); // Asegúrate de que esta imagen exista
        } else {
            icUsuario.setImageResource(R.drawable.ic_usuario); // Asegúrate de que esta imagen exista
        }

        return convertView;
    }
}
