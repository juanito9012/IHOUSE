package com.thefactory.ihouse.ui.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.thefactory.ihouse.ComponentesActivity;
import com.thefactory.ihouse.R;
import com.thefactory.ihouse.modelo.Componente;
import com.thefactory.ihouse.modelo.Habitacion;
import com.thefactory.ihouse.modelo.Posicion;
import com.thefactory.ihouse.servicios.ServiciosComponentes;
import com.thefactory.ihouse.ui.habitaciones.HabitacionesFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ItemHabitacionAdapter extends BaseExpandableListAdapter {

    private List<Habitacion> habitaciones;
    private Map<Habitacion, List<Componente>> componentesHabitacion;
    private LayoutInflater layoutInflater;
    private ServiciosComponentes sc;
    private List<CheckBox> checkBoxes;
    private HabitacionesFragment habitacionesFragment;


    public ItemHabitacionAdapter(Context context, List<Habitacion> habitaciones, Map<Habitacion, List<Componente>> componentesHabitacion, HabitacionesFragment habitacionesFragment) {
        this.habitaciones = habitaciones;
        this.layoutInflater = layoutInflater.from(context);
        this.componentesHabitacion = componentesHabitacion;
        sc = new ServiciosComponentes();
        checkBoxes = new ArrayList<>();
        this.habitacionesFragment = habitacionesFragment;

    }

    @Override
    public int getGroupCount() {
        return habitaciones.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return componentesHabitacion.get(habitaciones.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return habitaciones.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return componentesHabitacion.get(habitaciones.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_habitacion, null);
        String nombreHabitacion = ((Habitacion) getGroup(groupPosition)).getNombreHabitacion();
        TextView textViewNombreHabitacion = convertView.findViewById(R.id.nombreMiembro);
        textViewNombreHabitacion.setText(nombreHabitacion);
        CheckBox checkBox = convertView.findViewById(R.id.checkBoxHabitacion);
        ImageView imageAddComponente = convertView.findViewById(R.id.imageAddComponente);

        //Si no eres un administrador no puedes editar nada
        if (!habitacionesFragment.comprobarTipoUsuario()) {
            imageAddComponente.setVisibility(View.GONE);
            checkBox.setVisibility(View.GONE);
        } else {
            if (isExpanded) {
                imageAddComponente.setVisibility(View.VISIBLE);
            } else {
                imageAddComponente.setVisibility(View.INVISIBLE);
            }
            imageAddComponente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();
                    SharedPreferences habitacionInfo = habitacionesFragment.getContext().getSharedPreferences("habitacionInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor habitacionEditor = habitacionInfo.edit();
                    habitacionEditor.putString("habitacionJson", gson.toJson(habitaciones.get(groupPosition)));
                    habitacionEditor.apply();

                    SharedPreferences componenteInfo = habitacionesFragment.getContext().getSharedPreferences("componenteInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor componenteEditor = componenteInfo.edit();
                    componenteEditor.putString("componenteJson", null);
                    componenteEditor.apply();

                    habitacionesFragment.getContext().startActivity(new Intent(habitacionesFragment.getContext(), ComponentesActivity.class));
                }
            });

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        habitacionesFragment.getHabitacionesChecked().add(habitaciones.get(groupPosition));
                    } else {
                        habitacionesFragment.getHabitacionesChecked().remove(habitaciones.get(groupPosition));
                    }
                }
            });

            checkBox.setVisibility(View.INVISIBLE);
            checkBoxes.add(checkBox);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.item_componente, null);
        Componente c = (Componente) getChild(groupPosition, childPosition);
        TextView nombreComponente = convertView.findViewById(R.id.nombreMiembro);
        Button botonOn = convertView.findViewById(R.id.botonON);
        Button botonOff = convertView.findViewById(R.id.botonOff);
        Button botonSubir = convertView.findViewById(R.id.botonSubir);
        Button botonBajar = convertView.findViewById(R.id.botonBajar);
        Button botonParar = convertView.findViewById(R.id.botonParar);
        ImageView imageEditComponente = convertView.findViewById(R.id.imageEditComponente);


        //Si no eres un administrador no puedes editar nada
        if (!habitacionesFragment.comprobarTipoUsuario()) {
            imageEditComponente.setVisibility(View.GONE);
        } else {
            imageEditComponente.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Gson gson = new Gson();

                    SharedPreferences componenteInfo = habitacionesFragment.getContext().getSharedPreferences("componenteInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor componenteEditor = componenteInfo.edit();
                    Componente c = componentesHabitacion.get(habitaciones.get(groupPosition)).get(childPosition);
                    c.setHabitacion(habitaciones.get(groupPosition));
                    componenteEditor.putString("componenteJson", gson.toJson(c));
                    componenteEditor.apply();

                    SharedPreferences habitacionInfo = habitacionesFragment.getContext().getSharedPreferences("habitacionInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor habitacionEditor = habitacionInfo.edit();
                    habitacionEditor.putString("habitacionJson", null);
                    habitacionEditor.apply();

                    habitacionesFragment.getContext().startActivity(new Intent(habitacionesFragment.getContext(), ComponentesActivity.class));
                }
            });
        }

        botonParar.setVisibility(View.GONE);

        switch (c.getTipoComponente()) {
            case LED:
                botonSubir.setVisibility(View.INVISIBLE);
                botonBajar.setVisibility(View.INVISIBLE);
                botonOn.setVisibility(View.VISIBLE);
                botonOff.setVisibility(View.VISIBLE);
                break;
            case MOTOR:
                botonSubir.setVisibility(View.VISIBLE);
                botonBajar.setVisibility(View.VISIBLE);
                botonOn.setVisibility(View.INVISIBLE);
                botonOff.setVisibility(View.INVISIBLE);
                break;
        }

        View finalConvertView = convertView;
        botonOn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                c.setPosicion(Posicion.ON);
                Single.fromCallable(() -> sc.moverComponente(c))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(componentes -> {
                            componentes
                                    .peek(componente -> {
                                        Toast.makeText(finalConvertView.getContext(), c.getNombreComponente() + " ENCENDIDO", Toast.LENGTH_SHORT).show();
                                    })
                                    .peekLeft(apiError -> {
                                        Toast.makeText(finalConvertView.getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                        habitacionesFragment.volverPantallaLogin();
                                    });
                        }, throwable -> {
                            Toast.makeText(finalConvertView.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            habitacionesFragment.volverPantallaLogin();
                        });
            }
        });
        botonOff.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                c.setPosicion(Posicion.OFF);
                Single.fromCallable(() -> sc.moverComponente(c))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(componentes -> {
                            componentes
                                    .peek(componente -> {
                                        Toast.makeText(finalConvertView.getContext(), c.getNombreComponente() + " APAGADO", Toast.LENGTH_SHORT).show();
                                    })
                                    .peekLeft(apiError -> {
                                        Toast.makeText(finalConvertView.getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                        habitacionesFragment.volverPantallaLogin();
                                    });
                        }, throwable -> {
                            Toast.makeText(finalConvertView.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            habitacionesFragment.volverPantallaLogin();
                        });
            }
        });


        botonSubir.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                c.setPosicion(Posicion.SUBIDO);
                Single.fromCallable(() -> sc.moverComponente(c))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> {
                            botonOn.setVisibility(View.INVISIBLE);
                            botonOff.setVisibility(View.INVISIBLE);
                            botonParar.setVisibility(View.VISIBLE);
                        })
                        .subscribe(componentes -> {
                            componentes
                                    .peek(componente -> {
                                        Toast.makeText(finalConvertView.getContext(), c.getNombreComponente() + " SUBIENDO", Toast.LENGTH_SHORT).show();
                                    })
                                    .peekLeft(apiError -> {
                                        Toast.makeText(finalConvertView.getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                        habitacionesFragment.volverPantallaLogin();
                                    });
                        }, throwable -> {
                            Toast.makeText(finalConvertView.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            habitacionesFragment.volverPantallaLogin();
                        });
            }
        });

        botonBajar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                c.setPosicion(Posicion.ABAJO);
                Single.fromCallable(() -> sc.moverComponente(c))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> {
                            botonOn.setVisibility(View.INVISIBLE);
                            botonOff.setVisibility(View.INVISIBLE);
                            botonParar.setVisibility(View.VISIBLE);
                        })
                        .subscribe(componentes -> {
                            componentes
                                    .peek(componente -> {
                                        Toast.makeText(finalConvertView.getContext(), c.getNombreComponente() + " BAJANDO", Toast.LENGTH_SHORT).show();
                                    })
                                    .peekLeft(apiError -> {
                                        Toast.makeText(finalConvertView.getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                        habitacionesFragment.volverPantallaLogin();
                                    });
                        }, throwable -> {
                            Toast.makeText(finalConvertView.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            habitacionesFragment.volverPantallaLogin();
                        });
            }
        });

        botonParar.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                if (c.getPosicion().equals(Posicion.ABAJO)) {
                    c.setPosicion(Posicion.PARADO_ABAJO);
                } else if (c.getPosicion().equals(Posicion.SUBIDO)) {
                    c.setPosicion(Posicion.PARADO_ARRIBA);
                }
                Single.fromCallable(() -> sc.moverComponente(c))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> {
                            botonParar.setVisibility(View.GONE);
                            botonBajar.setVisibility(View.VISIBLE);
                            botonSubir.setVisibility(View.VISIBLE);
                        })
                        .subscribe(componentes -> {
                            componentes
                                    .peek(componente -> {
                                        Toast.makeText(finalConvertView.getContext(), c.getNombreComponente() + " PARADO", Toast.LENGTH_SHORT).show();
                                    })
                                    .peekLeft(apiError -> {
                                        Toast.makeText(finalConvertView.getContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                        habitacionesFragment.volverPantallaLogin();
                                    });
                        }, throwable -> {
                            Toast.makeText(finalConvertView.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            habitacionesFragment.volverPantallaLogin();
                        });
            }
        });


        nombreComponente.setText(c.getNombreComponente());
        return convertView;
    }

    public void setListenerFloatingMenu() {
        habitacionesFragment.getBinding().menuFlotanteHabitaciones.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                checkBoxes.forEach(checkBox -> checkBox.setVisibility(View.VISIBLE));
                habitacionesFragment.getBinding().botonMenuDeleteHabitacion.setEnabled(true);
                habitacionesFragment.getBinding().botonMenuUpdateHabitacion.setEnabled(true);
                habitacionesFragment.getBinding().botonMenuAddHabitacion.setEnabled(true);

            }

            @Override
            public void onMenuCollapsed() {
                checkBoxes.forEach(checkBox -> checkBox.setVisibility(View.INVISIBLE));
                habitacionesFragment.getBinding().botonMenuDeleteHabitacion.setEnabled(false);
                habitacionesFragment.getBinding().botonMenuUpdateHabitacion.setEnabled(false);
                habitacionesFragment.getBinding().botonMenuAddHabitacion.setEnabled(false);

            }
        });
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}