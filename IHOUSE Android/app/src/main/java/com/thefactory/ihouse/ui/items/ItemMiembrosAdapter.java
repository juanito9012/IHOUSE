package com.thefactory.ihouse.ui.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.thefactory.ihouse.R;
import com.thefactory.ihouse.modelo.TipoUsuario;
import com.thefactory.ihouse.modelo.UsuarioMapper;
import com.thefactory.ihouse.ui.usuarios.MiembrosFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemMiembrosAdapter extends RecyclerView.Adapter<ItemMiembrosAdapter.ItemMiembrosViewHolder> {

    private List<UsuarioMapper> usuarios;
    private LayoutInflater layoutInflater;
    private MiembrosFragment miembrosFragment;

    public ItemMiembrosAdapter(Context context, List<UsuarioMapper> usuarios, MiembrosFragment miembrosFragment) {
        this.usuarios = usuarios;
        this.layoutInflater = LayoutInflater.from(context);
        this.miembrosFragment = miembrosFragment;
    }

    @NotNull
    @Override
    public ItemMiembrosViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_miembro, parent, false);
        return new ItemMiembrosViewHolder(itemView, this);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull ItemMiembrosViewHolder holder, int position) {
        if (!usuarios.isEmpty()) {
            holder.setNombreMiembro(usuarios.get(position));
        }

    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }


    class ItemMiembrosViewHolder extends RecyclerView.ViewHolder {
        private final View itemLayout;
        private TextView nombreMiembro;
        private TextView textViewAdmin;


        public ItemMiembrosViewHolder(@NonNull View itemView, ItemMiembrosAdapter itemMiembrosAdapter) {
            super(itemView);
            this.itemLayout = itemView.findViewById(R.id.layoutMiembro);
            this.nombreMiembro = itemView.findViewById(R.id.nombreMiembro);
            this.textViewAdmin = itemView.findViewById(R.id.textViewAdmin);
            itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    if (miembrosFragment.comprobarTipoUsuario()) {
                        miembrosFragment.mostrarPopupMiembros(usuarios.get(getAdapterPosition()));
                    }
                }
            });

        }

        @SuppressLint("ResourceAsColor")
        public void setNombreMiembro(UsuarioMapper u) {
            textViewAdmin.setVisibility(View.GONE);
            nombreMiembro.setText(u.getNombreUsuario());
            if (u.getTipoUsuario().equals(TipoUsuario.ADMIN)){
                textViewAdmin.setVisibility(View.VISIBLE);
            }
        }
    }
}
