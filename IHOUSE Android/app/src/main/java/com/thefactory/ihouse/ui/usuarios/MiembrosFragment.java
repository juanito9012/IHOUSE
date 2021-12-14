package com.thefactory.ihouse.ui.usuarios;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.thefactory.ihouse.LoginActivity;
import com.thefactory.ihouse.R;
import com.thefactory.ihouse.databinding.FragmentUsuariosBinding;
import com.thefactory.ihouse.databinding.PopupEliminarBinding;
import com.thefactory.ihouse.databinding.PopupUsuarioBinding;
import com.thefactory.ihouse.modelo.TipoUsuario;
import com.thefactory.ihouse.modelo.Usuario;
import com.thefactory.ihouse.modelo.UsuarioMapper;
import com.thefactory.ihouse.servicios.ServiciosUsuarios;
import com.thefactory.ihouse.ui.items.ItemMiembrosAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MiembrosFragment extends Fragment {

    private MiembrosViewModel miembrosViewModel;
    private FragmentUsuariosBinding binding;
    private ItemMiembrosAdapter itemMiembrosAdapter;

    @Getter
    private List<UsuarioMapper> miembrosCasa;
    private ServiciosUsuarios su;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        miembrosViewModel = new ViewModelProvider(this).get(MiembrosViewModel.class);

        su = new ServiciosUsuarios();

        binding = FragmentUsuariosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }


    @Override
    @SuppressLint("CheckResult")
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.botonMenuAddMiembros.setEnabled(false);

        activarProgressBar();
        Single.fromCallable(() -> su.getMiembros())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    desactivarProgressBar();
                    miembrosViewModel.getMiembros().observe(getViewLifecycleOwner(), new Observer<List<UsuarioMapper>>() {
                        @Override
                        public void onChanged(List<UsuarioMapper> usuarios) {
                            miembrosCasa = usuarios;
                            itemMiembrosAdapter.notifyDataSetChanged();
                        }
                    });
                    miembrosViewModel.setUsuarios(miembrosCasa);
                })
                .subscribe(miembrosEither ->
                                miembrosEither
                                        .peek(usuarioMappers -> {
                                            miembrosCasa = usuarioMappers;
                                            iniciarInterfaz();
                                        })
                                        .peekLeft(apiError -> {
                                            Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                            if (apiError.getCode() == 401) {
                                                volverPantallaLogin();
                                            }
                                        }),
                        throwable -> {
                            showToast(throwable.getMessage());
                            volverPantallaLogin();
                        });

        if (!comprobarTipoUsuario()) {
            binding.menuFlotanteMiembros.setVisibility(View.GONE);
        } else {
            binding.menuFlotanteMiembros.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
                @Override
                public void onMenuExpanded() {
                    binding.botonMenuAddMiembros.setEnabled(true);
                }

                @Override
                public void onMenuCollapsed() {
                    binding.botonMenuAddMiembros.setEnabled(false);
                }
            });

            binding.botonMenuAddMiembros.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarPopupMiembros(null);
                }
            });
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void iniciarInterfaz() {
        itemMiembrosAdapter = new ItemMiembrosAdapter(getContext(), miembrosCasa, this);
        binding.recyclerViewMiembros.setAdapter(itemMiembrosAdapter);
        binding.recyclerViewMiembros.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void activarProgressBar() {
        binding.layoutProgressBarMiembros.setVisibility(View.VISIBLE);
        binding.progressBarMiembros.setVisibility(View.VISIBLE);
    }

    private void desactivarProgressBar() {
        binding.layoutProgressBarMiembros.setVisibility(View.INVISIBLE);
        binding.progressBarMiembros.setVisibility(View.GONE);
    }

    private void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    public void mostrarPopupMiembros(UsuarioMapper um) {
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        final View popupMiembros = getLayoutInflater().inflate(R.layout.popup_usuario, null);

        PopupUsuarioBinding popupUsuarioBinding = PopupUsuarioBinding.bind(popupMiembros);
        SharedPreferences userPreferences = getActivity().getSharedPreferences("usuarioInfo", Context.MODE_PRIVATE);
        String nombreUsuario = userPreferences.getString("nombreUsuario", null);

        if (um != null) {
            if (nombreUsuario.equals(um.getNombreUsuario())) {
                popupUsuarioBinding.imageEliminarUsuario.setVisibility(View.GONE);
            }
        }

        List<TipoUsuario> tipoUsuarios = Arrays.asList(TipoUsuario.values());
        ArrayAdapter<CharSequence> adapterSpinnerTipoUsuario = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, tipoUsuarios);
        popupUsuarioBinding.spinnerTipoUsuario.setAdapter(adapterSpinnerTipoUsuario);

        popupUsuarioBinding.exitPopupUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        popupUsuarioBinding.imageEliminarUsuario.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                activarProgressBar();
                popupUsuarioBinding.imageEliminarUsuario.setEnabled(false);
                Single.fromCallable(() -> su.eliminarUsuario(um))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> {
                            desactivarProgressBar();
                        })
                        .subscribe(usuarioEither ->
                                        usuarioEither
                                                .peek(usuarioMapper -> {
                                                    miembrosViewModel.eliminarUsuario(um);
                                                    alertDialog.dismiss();
                                                    actualizarRecyclerView();
                                                })
                                                .peekLeft(apiError -> {
                                                    Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                                    if (apiError.getCode() == 401) {
                                                        alertDialog.dismiss();
                                                        volverPantallaLogin();
                                                    }
                                                })
                                , throwable -> {
                                    showToast(throwable.getMessage());
                                    alertDialog.dismiss();
                                    volverPantallaLogin();
                                });
            }
        });


        popupUsuarioBinding.botonAddMiembro.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                if (!popupUsuarioBinding.nombreMiembro.getText().toString().isEmpty() && !popupUsuarioBinding.emailUsuarioMiembros.getText().toString().isEmpty()
                        && !popupUsuarioBinding.psswdUsuarioMiembros.getText().toString().isEmpty()) {
                    Usuario u = Usuario.builder()
                            .nombreUsuario(popupUsuarioBinding.nombreMiembro.getText().toString())
                            .email(popupUsuarioBinding.emailUsuarioMiembros.getText().toString())
                            .psswd(popupUsuarioBinding.psswdUsuarioMiembros.getText().toString())
                            .tipoUsuario((TipoUsuario) popupUsuarioBinding.spinnerTipoUsuario.getSelectedItem())
                            .build();

                    popupUsuarioBinding.botonAddMiembro.setEnabled(false);
                    activarProgressBar();
                    Single.fromCallable(() -> su.registrarUsuario(u))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally(() -> {
                                desactivarProgressBar();
                            })
                            .subscribe(usuarioEither ->
                                            usuarioEither
                                                    .peek(usuarioMapper -> {
                                                        miembrosViewModel.addUsuario(usuarioMapper);
                                                        alertDialog.dismiss();
                                                        actualizarRecyclerView();
                                                    })
                                                    .peekLeft(apiError -> {
                                                        Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                                        if (apiError.getCode() == 401) {
                                                            alertDialog.dismiss();
                                                            volverPantallaLogin();
                                                        }
                                                    })
                                    , throwable -> {
                                        showToast(throwable.getMessage());
                                        alertDialog.dismiss();
                                        volverPantallaLogin();
                                    });
                } else {
                    showToast("Faltan campos por rellenar");
                }
            }
        });

        popupUsuarioBinding.botonActualizarMiembro.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                if (!popupUsuarioBinding.nombreMiembro.getText().toString().isEmpty()
                        && !popupUsuarioBinding.emailUsuarioMiembros.getText().toString().isEmpty()) {
                    popupUsuarioBinding.botonActualizarMiembro.setEnabled(false);

                    Usuario u = Usuario.builder()
                            .idUsuario(um.getIdUsuario())
                            .nombreUsuario(popupUsuarioBinding.nombreMiembro.getText().toString())
                            .email(popupUsuarioBinding.emailUsuarioMiembros.getText().toString())
                            .tipoUsuario((TipoUsuario) popupUsuarioBinding.spinnerTipoUsuario.getSelectedItem())
                            .build();

                    if (!popupUsuarioBinding.psswdUsuarioMiembros.getText().toString().isEmpty()) {
                        u.setPsswd(popupUsuarioBinding.psswdUsuarioMiembros.getText().toString());
                    }

                    activarProgressBar();
                    Single.fromCallable(() -> su.actulizarUsuario(u))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally(() -> {
                                desactivarProgressBar();
                            })
                            .subscribe(usuarioEither ->
                                            usuarioEither
                                                    .peek(usuarioMapper -> {
                                                        miembrosViewModel.actualizarUsuario(um, usuarioMapper);
                                                        alertDialog.dismiss();
                                                        actualizarRecyclerView();
                                                    })
                                                    .peekLeft(apiError -> {
                                                        Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                                        if (apiError.getCode() == 401) {
                                                            alertDialog.dismiss();
                                                            volverPantallaLogin();
                                                        }
                                                    })
                                    , throwable -> {
                                        showToast(throwable.getMessage());
                                        alertDialog.dismiss();
                                        volverPantallaLogin();
                                    });
                } else {
                    showToast("Faltan campos por rellenar");
                }
            }
        });

        if (um != null) {
            popupUsuarioBinding.botonAddMiembro.setVisibility(View.GONE);

            popupUsuarioBinding.nombreMiembro.setText(um.getNombreUsuario());
            popupUsuarioBinding.emailUsuarioMiembros.setText(um.getEmail());
            popupUsuarioBinding.spinnerTipoUsuario.setSelection(um.getTipoUsuario().ordinal());
        } else {
            popupUsuarioBinding.botonActualizarMiembro.setVisibility(View.GONE);
            popupUsuarioBinding.imageEliminarUsuario.setVisibility(View.GONE);
        }

        alertDialogBuilder.setView(popupUsuarioBinding.getRoot());
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public boolean comprobarTipoUsuario() {
        SharedPreferences userPreferences = getContext().getSharedPreferences("usuarioInfo", Context.MODE_PRIVATE);
        String nombreUsuario = userPreferences.getString("tipoUsuario", null);
        TipoUsuario tp = TipoUsuario.valueOf(nombreUsuario);

        if (tp.equals(TipoUsuario.ADMIN)) {
            return true;
        }
        return false;
    }

    public void volverPantallaLogin() {
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        final View popupEliminar = getLayoutInflater().inflate(R.layout.popup_eliminar, null);

        PopupEliminarBinding popupEliminarBinding = PopupEliminarBinding.bind(popupEliminar);

        popupEliminarBinding.botonCancelarPopup.setVisibility(View.GONE);
        popupEliminarBinding.botonAceptarPopup.setVisibility(View.GONE);

        popupEliminarBinding.mensajeBorrado.setText("Error vuelve a intentar iniciar Sesion");

        popupEliminarBinding.botonAceptarPopup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroyView();
                startActivity(new Intent(getContext(), LoginActivity.class));
                alertDialog.dismiss();
            }
        });

        alertDialogBuilder.setView(popupEliminarBinding.getRoot());
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void actualizarRecyclerView() {
        miembrosCasa = miembrosViewModel.getMiembros().getValue();
        itemMiembrosAdapter.notifyDataSetChanged();
    }

}