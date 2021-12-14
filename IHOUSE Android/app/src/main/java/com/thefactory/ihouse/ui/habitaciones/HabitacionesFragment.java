package com.thefactory.ihouse.ui.habitaciones;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.thefactory.ihouse.LoginActivity;
import com.thefactory.ihouse.R;
import com.thefactory.ihouse.databinding.FragmentHabitacionesBinding;
import com.thefactory.ihouse.databinding.PopupEliminarBinding;
import com.thefactory.ihouse.databinding.PopupHabitacionBinding;
import com.thefactory.ihouse.modelo.Componente;
import com.thefactory.ihouse.modelo.Habitacion;
import com.thefactory.ihouse.modelo.TipoUsuario;
import com.thefactory.ihouse.servicios.ServiciosComponentes;
import com.thefactory.ihouse.servicios.ServiciosHabitaciones;
import com.thefactory.ihouse.ui.items.ItemHabitacionAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class HabitacionesFragment extends Fragment {

    @Getter
    private List<Habitacion> habitacionesChecked;
    private List<Habitacion> habitacionesItem;
    private HabitacionesViewModel habitacionesViewModel;

    @Getter
    private FragmentHabitacionesBinding binding;

    private Map<Habitacion, List<Componente>> componentesHabitacion;
    private ItemHabitacionAdapter itemHabitacionAdapter;

    private ServiciosHabitaciones sh;
    private ServiciosComponentes sc;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        habitacionesViewModel = new ViewModelProvider(this).get(HabitacionesViewModel.class);
        sh = new ServiciosHabitaciones();
        sc = new ServiciosComponentes();
        habitacionesChecked = new ArrayList<>();
        componentesHabitacion = new HashMap<>();
        binding = FragmentHabitacionesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    @SuppressLint("CheckResult")
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Si no eres un administrador no puedes editar nada
        if (!comprobarTipoUsuario()) {
            binding.menuFlotanteHabitaciones.setVisibility(View.GONE);
        } else {
            binding.botonMenuDeleteHabitacion.setEnabled(false);
            binding.botonMenuUpdateHabitacion.setEnabled(false);
            binding.botonMenuAddHabitacion.setEnabled(false);
        }

        activarProgressBar();

        Single.fromCallable(() -> sh.getHabitaciones())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> {
                    habitacionesViewModel.getHabitaciones().observe(getViewLifecycleOwner(), new Observer<List<Habitacion>>() {
                        @Override
                        public void onChanged(List<Habitacion> habitacionList) {
                            habitacionesItem = habitacionList;
                            itemHabitacionAdapter.notifyDataSetChanged();
                        }
                    });
                    habitacionesViewModel.getHabitacionesComponentes().observe(getViewLifecycleOwner(), new Observer<Map<Habitacion, List<Componente>>>() {
                        @Override
                        public void onChanged(Map<Habitacion, List<Componente>> habitacionListMap) {
                            componentesHabitacion = habitacionListMap;
                            itemHabitacionAdapter.notifyDataSetChanged();
                        }
                    });
                })
                .subscribe(habitacionesEither -> {
                    habitacionesEither
                            .peek(habitacionesList -> {
                                habitacionesItem = habitacionesList;
                                habitacionesList.forEach(habitacion -> {
                                    Single.fromCallable(() -> sc.getComponentes(habitacion))
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .doFinally(() -> desactivarProgressBar())
                                            .subscribe(componentesEither -> {
                                                componentesEither
                                                        .peek(componentes -> {
                                                            if (componentes != null) {
                                                                componentesHabitacion.put(habitacion, componentes);
                                                            } else {
                                                                componentesHabitacion.put(habitacion, new ArrayList<>());
                                                            }
                                                            habitacionesViewModel.setHabitacionesComponentes(componentesHabitacion);
                                                            habitacionesViewModel.setHabitaciones(habitacionesItem);
                                                        })
                                                        .peekLeft(apiError -> {
                                                            Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                                            if (apiError.getCode() == 401) {
                                                                volverPantallaLogin();
                                                            }
                                                        });
                                            }, throwable -> {
                                                showToast(throwable.getMessage());
                                                volverPantallaLogin();
                                            });
                                });
                                iniciarInterfaz(habitacionesItem);
                                itemHabitacionAdapter.setListenerFloatingMenu();
                            })
                            .peekLeft(apiError -> {
                                Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                if (apiError.getCode() == 401) {
                                    volverPantallaLogin();
                                }
                            });
                }, throwable -> {
                    showToast(throwable.getMessage());
                    volverPantallaLogin();
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void activarProgressBar() {
        binding.layoutProgressBarHabitaciones.setVisibility(View.VISIBLE);
        binding.progressBarHabitaciones.setVisibility(View.VISIBLE);
    }

    private void desactivarProgressBar() {
        binding.layoutProgressBarHabitaciones.setVisibility(View.INVISIBLE);
        binding.progressBarHabitaciones.setVisibility(View.GONE);
    }

    private void showToast(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void iniciarInterfaz(List<Habitacion> habitaciones) {
        binding.botonMenuAddHabitacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarPopupHabitacion(null);
            }
        });

        binding.botonMenuUpdateHabitacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (habitacionesChecked.size() == 1) {
                    mostrarPopupHabitacion(habitacionesChecked.get(0));

                } else {
                    showToast("Selecciona una sola habitacion para editarla");

                }
            }
        });

        binding.botonMenuDeleteHabitacion.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                mostrarPopupEliminar();
            }
        });

        itemHabitacionAdapter = new ItemHabitacionAdapter(getContext(), habitaciones, componentesHabitacion, this);
        binding.expandableListViewHabitaciones.setAdapter(itemHabitacionAdapter);
    }

    private void mostrarPopupHabitacion(Habitacion habitacionSelecionada) {

        final View popupHabitacion = getLayoutInflater().inflate(R.layout.popup_habitacion, null);
        PopupHabitacionBinding popupHabitacionBinding = PopupHabitacionBinding.bind(popupHabitacion);

        alertDialogBuilder = new AlertDialog.Builder(getContext());

        popupHabitacionBinding.botonAddHabitacion.setVisibility(View.INVISIBLE);
        popupHabitacionBinding.botonActualizarHabitacion.setVisibility(View.INVISIBLE);

        popupHabitacionBinding.exitPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        if (habitacionSelecionada == null) {
            popupHabitacionBinding.botonAddHabitacion.setVisibility(View.VISIBLE);
            popupHabitacionBinding.botonAddHabitacion.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("CheckResult")
                @Override
                public void onClick(View v) {
                    if (!popupHabitacionBinding.nombreHabitacion.getText().toString().isEmpty() && !popupHabitacionBinding.lugarHabitacionPopup.getText().toString().isEmpty()) {
                        Habitacion h = Habitacion.builder()
                                .nombreHabitacion(popupHabitacionBinding.nombreHabitacion.getText().toString())
                                .lugar(popupHabitacionBinding.lugarHabitacionPopup.getText().toString()).build();

                        popupHabitacionBinding.botonAddHabitacion.setEnabled(false);

                        activarProgressBar();
                        Single.fromCallable(() -> sh.addHabitacion(h))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doFinally(() -> {
                                    desactivarProgressBar();
                                    binding.menuFlotanteHabitaciones.collapse();
                                })
                                .subscribe(habitacionesEither -> {
                                    habitacionesEither
                                            .peek(habitacion -> {
                                                habitacionesViewModel.addHabitacion(habitacion);
                                                alertDialog.dismiss();
                                                actualizarExpandableList();
                                            })
                                            .peekLeft(apiError -> {
                                                Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                                if (apiError.getCode() == 401) {
                                                    alertDialog.dismiss();
                                                    volverPantallaLogin();
                                                }
                                            });
                                }, throwable -> {
                                    showToast(throwable.getMessage());
                                    alertDialog.dismiss();
                                    volverPantallaLogin();
                                });

                    } else {
                        showToast("Hay campos vacios");
                    }
                }
            });
        } else {
            popupHabitacionBinding.nombreHabitacion.setText(habitacionSelecionada.getNombreHabitacion());
            popupHabitacionBinding.lugarHabitacionPopup.setText(habitacionSelecionada.getLugar());
            popupHabitacionBinding.botonActualizarHabitacion.setVisibility(View.VISIBLE);
            popupHabitacionBinding.botonActualizarHabitacion.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("CheckResult")
                @Override
                public void onClick(View v) {
                    if (!popupHabitacionBinding.nombreHabitacion.getText().toString().isEmpty() && !popupHabitacionBinding.lugarHabitacionPopup.getText().toString().isEmpty()) {
                        Habitacion habitacionActualizada = Habitacion.builder()
                                .lugar(popupHabitacionBinding.lugarHabitacionPopup.getText().toString())
                                .nombreHabitacion(popupHabitacionBinding.nombreHabitacion.getText().toString())
                                .id(habitacionSelecionada.getId()).build();

                        popupHabitacionBinding.botonActualizarHabitacion.setEnabled(false);

                        activarProgressBar();
                        Single.fromCallable(() -> sh.updateHabitacion(habitacionActualizada))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doFinally(() -> {
                                    desactivarProgressBar();
                                    binding.menuFlotanteHabitaciones.collapse();
                                    habitacionesChecked.clear();
                                })
                                .subscribe(habitacionesEither -> {
                                    habitacionesEither
                                            .peek(habitacion -> {
                                                habitacionesViewModel.actualizarHabitacion(habitacionSelecionada, habitacionActualizada);
                                                alertDialog.dismiss();
                                                actualizarExpandableList();
                                            })
                                            .peekLeft(apiError -> {
                                                Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                                if (apiError.getCode() == 401) {
                                                    alertDialog.dismiss();
                                                    volverPantallaLogin();
                                                }
                                            });
                                }, throwable -> {
                                    showToast(throwable.getMessage());
                                    alertDialog.dismiss();
                                    volverPantallaLogin();
                                });

                    } else {
                        showToast("Hay campos vacios");
                    }
                }
            });

        }

        alertDialogBuilder.setView(popupHabitacionBinding.getRoot());
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void mostrarPopupEliminar() {
        alertDialogBuilder = new AlertDialog.Builder(getContext());
        final View popupEliminar = getLayoutInflater().inflate(R.layout.popup_eliminar, null);
        PopupEliminarBinding popupEliminarBinding = PopupEliminarBinding.bind(popupEliminar);

        popupEliminarBinding.botonAceptarPopup2.setVisibility(View.GONE);


        popupEliminarBinding.mensajeBorrado.setText("¿Esta seguro de que quiere eliminar la habitacion?");

        popupEliminarBinding.botonAceptarPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (habitacionesChecked.size() != 0) {
                    activarProgressBar();
                    popupEliminarBinding.botonAceptarPopup.setEnabled(false);
                    habitacionesChecked.forEach(habitacion ->
                            Single.fromCallable(() -> sh.deleteHabitacion(habitacion))
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doFinally(() -> {
                                        desactivarProgressBar();
                                        binding.menuFlotanteHabitaciones.collapse();
                                    })
                                    .subscribe(habitacionesEither -> {

                                        habitacionesEither
                                                .peek(habitacion1 -> {
                                                    habitacionesViewModel.deleteHabitacion(habitacion);
                                                    alertDialog.dismiss();
                                                    actualizarExpandableList();
                                                })
                                                .peekLeft(apiError -> {
                                                    Toast.makeText(getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                                    if (apiError.getCode() == 401) {
                                                        alertDialog.dismiss();
                                                        volverPantallaLogin();
                                                    }
                                                });
                                    }, throwable -> {
                                        showToast(throwable.getMessage());
                                        alertDialog.dismiss();
                                        volverPantallaLogin();
                                    }));
                    habitacionesChecked.clear();
                } else {
                    showToast("Debe selecionar minimo una habitacion para eliminarla");
                }
            }
        });

        popupEliminarBinding.botonCancelarPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialogBuilder.setView(popupEliminarBinding.getRoot());
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void actualizarExpandableList() {
        componentesHabitacion = habitacionesViewModel.getHabitacionesComponentes().getValue();
        habitacionesItem = habitacionesViewModel.getHabitaciones().getValue();
        itemHabitacionAdapter.notifyDataSetChanged();
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

}
