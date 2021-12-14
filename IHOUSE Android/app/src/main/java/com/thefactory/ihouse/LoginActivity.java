package com.thefactory.ihouse;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.thefactory.ihouse.databinding.LoginBinding;
import com.thefactory.ihouse.modelo.Usuario;
import com.thefactory.ihouse.servicios.ServiciosUsuarios;
import com.thefactory.ihouse.utils.ConfigurationSingleton_Retrofit_Login;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@RequiresApi(api = Build.VERSION_CODES.O)
public class LoginActivity extends AppCompatActivity {
    private ServiciosUsuarios su;
    private LoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        binding = LoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        su = new ServiciosUsuarios();
        obtenerComponentes();
        intentarLogin();
    }


    private void obtenerComponentes() {

        desactivarProgressBar();

        binding.botonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.nombreUsuarioLogin.getText().toString().isEmpty() && !binding.psswdUsuario.getText().toString().isEmpty() && !binding.url.getText().toString().isEmpty()) {
                    Usuario u = Usuario.builder()
                            .nombreUsuario(binding.nombreUsuarioLogin.getText().toString())
                            .psswd(binding.psswdUsuario.getText().toString())
                            .build();

                    SharedPreferences urlPreferences = getSharedPreferences("urlInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = urlPreferences.edit();

                    editor.putString("url", binding.url.getText().toString());
                    editor.apply();

                    ConfigurationSingleton_Retrofit_Login.setUrl(binding.url.getText().toString());
                    login(u);
                }else {
                    showToast("Faltan campos por rellenar");
                }
            }
        });
    }

    private void startMainActivity() {
        desactivarProgressBar();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("CheckResult")
    private void login(Usuario u) {
        activarProgressBar();
        Single.fromCallable(() -> su.loginUsuario(u))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::desactivarProgressBar)
                .subscribe(jwtEither -> {
                    jwtEither
                            .peek(jwt -> {
                                Single.fromCallable(() -> su.getUsuario(u))
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(usuarioMappersEither ->
                                                usuarioMappersEither
                                                        .peek(usuarioMapper -> {

                                                            SharedPreferences userPreferences = getSharedPreferences("usuarioInfo", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor userEditor = userPreferences.edit();
                                                            userEditor.putString("tipoUsuario", usuarioMapper.getTipoUsuario().toString());
                                                            userEditor.putString("idUsuario", usuarioMapper.getIdUsuario());
                                                            userEditor.putString("nombreUsuario", usuarioMapper.getNombreUsuario());
                                                            userEditor.putString("psswd", binding.psswdUsuario.getText().toString());
                                                            userEditor.apply();

                                                            startMainActivity();
                                                        }).peekLeft(apiError -> {
                                                    Toast.makeText(getApplicationContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                                                }), throwable -> showToast(throwable.getMessage()));
                            })
                            .peekLeft(apiError -> {
                                Toast.makeText(getApplicationContext(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }, throwable -> showToast(throwable.getMessage()));
    }

    public void intentarLogin() {
        SharedPreferences userPreferences = getSharedPreferences("usuarioInfo", Context.MODE_PRIVATE);

        String nombreUsuario = userPreferences.getString("nombreUsuario", null);
        String psswdUsuario = userPreferences.getString("psswd", null);

        SharedPreferences urlPreferences = getSharedPreferences("urlInfo", Context.MODE_PRIVATE);
        String url = urlPreferences.getString("url", null);

        if (url != null) {
            this.binding.url.setText(url);
        } else {
            this.binding.url.setText("http://");
        }

        if (nombreUsuario != null && psswdUsuario != null && url != null) {
            ConfigurationSingleton_Retrofit_Login.setUrl(url);

            this.binding.psswdUsuario.setText(psswdUsuario);
            this.binding.nombreUsuarioLogin.setText(nombreUsuario);


            Usuario u = Usuario.builder()
                    .nombreUsuario(nombreUsuario)
                    .psswd(psswdUsuario)
                    .build();

            login(u);
        }
    }

    private void activarProgressBar() {
        binding.nombreUsuarioLogin.setEnabled(false);
        binding.psswdUsuario.setEnabled(false);
        binding.url.setEnabled(false);

        binding.layoutProgressBarMiembros.setVisibility(View.VISIBLE);
        binding.progressBarMiembros.setVisibility(View.VISIBLE);
    }

    private void desactivarProgressBar() {
        binding.nombreUsuarioLogin.setEnabled(true);
        binding.psswdUsuario.setEnabled(true);
        binding.url.setEnabled(true);

        binding.layoutProgressBarMiembros.setVisibility(View.INVISIBLE);
        binding.progressBarMiembros.setVisibility(View.GONE);
    }
}
