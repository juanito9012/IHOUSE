package com.thefactory.ihouse;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.thefactory.ihouse.databinding.ActivityComponentesBinding;
import com.thefactory.ihouse.databinding.PopupEliminarBinding;
import com.thefactory.ihouse.modelo.Componente;
import com.thefactory.ihouse.modelo.Habitacion;
import com.thefactory.ihouse.modelo.TipoComponente;
import com.thefactory.ihouse.servicios.ServiciosComponentes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.thefactory.ihouse.modelo.TipoComponente.MOTOR;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ComponentesActivity extends AppCompatActivity {

    private ServiciosComponentes sc;

    private Componente componente;
    private Habitacion habitacion;


    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    private ActivityComponentesBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = ActivityComponentesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Gson gson = new Gson();
        sc = new ServiciosComponentes();


        binding.spinnerPin1.setVisibility(View.INVISIBLE);
        binding.spinnerPin2.setVisibility(View.INVISIBLE);


        binding.imageEliminarComponente.setVisibility(View.INVISIBLE);
        binding.botonActualizarComponente.setVisibility(View.INVISIBLE);
        binding.botonAddComponente.setVisibility(View.INVISIBLE);

        rellenarSpinners();

        desactivarProgressBar();

        SharedPreferences componenteInfo = getSharedPreferences("componenteInfo", Context.MODE_PRIVATE);
        String componenteJson = componenteInfo.getString("componenteJson", null);


        SharedPreferences habitacionInfo = getSharedPreferences("habitacionInfo", Context.MODE_PRIVATE);
        String habitacionJson = habitacionInfo.getString("habitacionJson", null);

        if (componenteJson != null) {
            this.componente = gson.fromJson(componenteJson, Componente.class);
            this.habitacion = this.componente.getHabitacion();

            binding.nombreComponenteComponentes.setText(componente.getNombreComponente());

            if (componente.getTipoComponente().equals(MOTOR)) {
                binding.spinnerTipoComponente.setSelection(0);
            } else {
                binding.spinnerTipoComponente.setSelection(1);
            }

            binding.imageEliminarComponente.setVisibility(View.VISIBLE);
            binding.botonActualizarComponente.setVisibility(View.VISIBLE);

            binding.imageEliminarComponente.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("CheckResult")
                @Override
                public void onClick(View v) {
                    mostrarPopupEliminar();
                }
            });


            binding.botonActualizarComponente.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("CheckResult")
                @Override
                public void onClick(View v) {
                    if (comprobarCamposVacios()) {
                        binding.botonActualizarComponente.setEnabled(false);
                        componente.setNombreComponente(binding.nombreComponenteComponentes.getText().toString());
                        TipoComponente tipoComponente = (TipoComponente) binding.spinnerTipoComponente.getSelectedItem();
                        componente.setTipoComponente(tipoComponente);
                        if (tipoComponente.equals(MOTOR)) {
                            componente.setGpiopinMotorSubir((String) binding.spinnerPin1.getSelectedItem());
                            componente.setGpiopinMotorBajar((String) binding.spinnerPin2.getSelectedItem());
                            componente.setGpioPinLED(null);
                        } else {
                            componente.setGpiopinMotorSubir(null);
                            componente.setGpiopinMotorBajar(null);
                            componente.setGpioPinLED((String) binding.spinnerPin1.getSelectedItem());
                        }
                        activarProgressBar();
                        Single.fromCallable(() -> sc.updateComponente(componente))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doFinally(() -> desactivarProgressBar())
                                .subscribe(componenteEither -> {
                                    componenteEither
                                            .peek(componente1 -> {
                                                Toast.makeText(getApplicationContext(), "Componente " + componente.getNombreComponente() + " Actualizado", Toast.LENGTH_SHORT).show();
                                                startMainActivity();
                                            })
                                            .peekLeft(apiError -> {
                                                Toast.makeText(getApplicationContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                                if (apiError.getCode() == 401) {
                                                    volverPantallaLogin();
                                                }
                                            });
                                }, throwable -> {
                                    showToast(throwable.getMessage());
                                    volverPantallaLogin();
                                });
                    }
                }
            });

        } else if (habitacionJson != null) {
            this.habitacion = gson.fromJson(habitacionJson, Habitacion.class);
            binding.botonAddComponente.setVisibility(View.VISIBLE);

            binding.botonAddComponente.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("CheckResult")
                @Override
                public void onClick(View v) {
                    if (comprobarCamposVacios()) {
                        binding.botonAddComponente.setEnabled(false);
                        TipoComponente tipoComponente = (TipoComponente) binding.spinnerTipoComponente.getSelectedItem();
                        if (tipoComponente.equals(MOTOR)) {
                            componente = Componente.builder()
                                    .nombreComponente(binding.nombreComponenteComponentes.getText().toString())
                                    .tipoComponente(tipoComponente)
                                    .fechaInstalacion(LocalDate.now())
                                    .gpiopinMotorSubir((String) binding.spinnerPin1.getSelectedItem())
                                    .gpiopinMotorBajar((String) binding.spinnerPin2.getSelectedItem())
                                    .gpioPinLED(null)
                                    .habitacion(habitacion)
                                    .build();
                        } else {
                            componente = Componente.builder()
                                    .nombreComponente(binding.nombreComponenteComponentes.getText().toString())
                                    .tipoComponente(tipoComponente)
                                    .fechaInstalacion(LocalDate.now())
                                    .gpioPinLED((String) binding.spinnerPin1.getSelectedItem())
                                    .gpiopinMotorBajar(null)
                                    .gpiopinMotorSubir(null)
                                    .habitacion(habitacion)
                                    .build();
                        }
                        activarProgressBar();
                        Single.fromCallable(() -> sc.addComponente(componente))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doFinally(() -> desactivarProgressBar())
                                .subscribe(componenteEither -> {
                                    componenteEither
                                            .peek(componente1 -> {
                                                Toast.makeText(getApplicationContext(), "Componente " + componente.getNombreComponente() + " Creado", Toast.LENGTH_SHORT).show();
                                                startMainActivity();
                                            })
                                            .peekLeft(apiError -> {
                                                Toast.makeText(getApplicationContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                                if (apiError.getCode() == 401) {
                                                    volverPantallaLogin();
                                                }
                                            });
                                }, throwable -> {
                                    showToast(throwable.getMessage());
                                    volverPantallaLogin();
                                });
                    }
                }
            });
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        startMainActivity();
    }

    private void activarProgressBar() {
        binding.botonAddComponente.setEnabled(false);
        binding.botonActualizarComponente.setEnabled(false);
        binding.imageEliminarComponente.setEnabled(false);

        binding.spinnerTipoComponente.setEnabled(false);
        binding.spinnerPin1.setEnabled(false);
        binding.spinnerPin2.setEnabled(false);

        binding.nombreComponenteComponentes.setEnabled(false);

        binding.layoutProgressBarComponentes.setVisibility(View.VISIBLE);
        binding.progressBarComponentes.setVisibility(View.VISIBLE);
    }

    private void desactivarProgressBar() {
        binding.botonAddComponente.setEnabled(true);
        binding.botonActualizarComponente.setEnabled(true);
        binding.imageEliminarComponente.setEnabled(true);

        binding.spinnerTipoComponente.setEnabled(true);
        binding.spinnerPin1.setEnabled(true);
        binding.spinnerPin2.setEnabled(true);

        binding.nombreComponenteComponentes.setEnabled(true);

        binding.layoutProgressBarComponentes.setVisibility(View.INVISIBLE);
        binding.progressBarComponentes.setVisibility(View.GONE);
    }

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void startMainActivity() {
        desactivarProgressBar();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void rellenarSpinners() {
        List<TipoComponente> tipoComponentes = Arrays.asList(TipoComponente.values());

        ArrayAdapter<CharSequence> adapterSpinnerTipoComponente = new ArrayAdapter(this, android.R.layout.simple_spinner_item, tipoComponentes);
        binding.spinnerTipoComponente.setAdapter(adapterSpinnerTipoComponente);

        binding.spinnerTipoComponente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List gpioPin = listGPIOPin();
                binding.spinnerPin1.setVisibility(View.VISIBLE);
                if (MOTOR.equals(binding.spinnerTipoComponente.getSelectedItem())) {
                    gpioPin.add(0, "Pin Subir");
                    ArrayAdapter<CharSequence> adapterSpinnerPin1 = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, gpioPin);
                    binding.spinnerPin1.setAdapter(adapterSpinnerPin1);
                } else {
                    gpioPin.add(0, "Pin LED");
                }
                ArrayAdapter<CharSequence> adapterSpinnerPin1 = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, gpioPin);
                binding.spinnerPin1.setAdapter(adapterSpinnerPin1);

                if (componente != null) {
                    if (componente.getTipoComponente().equals(MOTOR)) {
                        binding.spinnerPin1.setSelection(Integer.parseInt(componente.getGpiopinMotorSubir()));
                    } else {
                        binding.spinnerPin1.setSelection(Integer.parseInt(componente.getGpioPinLED()));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.spinnerPin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List gpioPin = listGPIOPin();
                gpioPin.add(0, "Pin Bajar");
                gpioPin.remove(binding.spinnerPin1.getSelectedItem());
                ArrayAdapter adapterSpinnerPin2 = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, gpioPin);
                binding.spinnerPin2.setAdapter(adapterSpinnerPin2);
                if (MOTOR.equals(binding.spinnerTipoComponente.getSelectedItem())) {
                    binding.spinnerPin2.setVisibility(View.VISIBLE);
                    if (componente != null)
                        if (Integer.parseInt(componente.getGpiopinMotorBajar()) > Integer.parseInt(componente.getGpiopinMotorSubir())) {
                            binding.spinnerPin2.setSelection(Integer.parseInt(componente.getGpiopinMotorBajar()) - 1);
                        } else {
                            binding.spinnerPin2.setSelection(Integer.parseInt(componente.getGpiopinMotorBajar()));
                        }
                } else {
                    binding.spinnerPin2.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private List listGPIOPin() {
        List gpioPin = new ArrayList<>();

        for (int i = 1; i < 32; i++) {
            gpioPin.add(String.valueOf(i));
        }
        return gpioPin;
    }

    private boolean comprobarCamposVacios() {
        if (binding.spinnerTipoComponente.getSelectedItem().equals(MOTOR)) {
            if (!binding.spinnerPin1.getSelectedItem().equals("Pin Subir") && !binding.spinnerPin2.getSelectedItem().equals("Pin Bajar") &&
                    !binding.nombreComponenteComponentes.getText().toString().isEmpty()) {
                return true;
            }
        } else {
            if (!binding.spinnerPin1.getSelectedItem().equals("Pin LED") &&
                    !binding.nombreComponenteComponentes.getText().toString().isEmpty()) {
                return true;
            }
        }
        showToast("Faltan campos por rellenar");
        return false;
    }

    private void mostrarPopupEliminar() {
        alertDialogBuilder = new AlertDialog.Builder(this);
        final View popupEliminar = getLayoutInflater().inflate(R.layout.popup_eliminar, null);
        PopupEliminarBinding popupEliminarBinding = PopupEliminarBinding.bind(popupEliminar);

        popupEliminarBinding.botonAceptarPopup2.setVisibility(View.GONE);

        popupEliminarBinding.mensajeBorrado.setText("¿Esta seguro que quiere eliminar el componente?");

        popupEliminarBinding.botonAceptarPopup.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View v) {
                activarProgressBar();
                popupEliminarBinding.botonAceptarPopup.setEnabled(false);
                Single.fromCallable(() -> sc.deleteComponente(componente))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> desactivarProgressBar())
                        .subscribe(componenteEither -> {
                            componenteEither
                                    .peek(c -> {
                                        Toast.makeText(getApplicationContext(), "Componente " + componente.getNombreComponente() + " Borrado", Toast.LENGTH_SHORT).show();
                                        startMainActivity();
                                    })
                                    .peekLeft(apiError -> {
                                        Toast.makeText(getApplicationContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                        if (apiError.getCode() == 401) {
                                            volverPantallaLogin();
                                        }
                                    });
                        }, throwable -> {
                            showToast(throwable.getMessage());
                            volverPantallaLogin();
                        });
            }
        });

        popupEliminarBinding.botonCancelarPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialogBuilder.setView(popupEliminar);
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void volverPantallaLogin() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        alertDialogBuilder = new AlertDialog.Builder(this);
        final View popupEliminar = getLayoutInflater().inflate(R.layout.popup_eliminar, null);
        PopupEliminarBinding popupEliminarBinding = PopupEliminarBinding.bind(popupEliminar);

        popupEliminarBinding.botonCancelarPopup.setVisibility(View.GONE);
        popupEliminarBinding.botonAceptarPopup.setVisibility(View.GONE);


        popupEliminarBinding.mensajeBorrado.setText("Error vuelve a intentar iniciar Sesion");


        popupEliminarBinding.botonAceptarPopup2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy();
                startActivity(new Intent(getBaseContext(), LoginActivity.class));
                alertDialog.dismiss();
            }
        });

        alertDialogBuilder.setView(popupEliminarBinding.getRoot());
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}