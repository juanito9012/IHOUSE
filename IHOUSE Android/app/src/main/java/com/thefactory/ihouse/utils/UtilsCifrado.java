package com.thefactory.ihouse.utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.thefactory.ihouse.modelo.Usuario;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class UtilsCifrado {

    private static final String UTILS_CIFRADO_DEBUG = "UTILS_CIFRADO_DEBUG";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Usuario cifrarContraseña(Usuario u) {
        try {
            SecureRandom sr = new SecureRandom();
            byte[] iv = new byte[12];
            byte[] salt = new byte[16];
            byte[] claveSimetrica = new byte[16];
            int interaciones = sr.nextInt(100000);

            sr.nextBytes(iv);
            sr.nextBytes(salt);
            sr.nextBytes(claveSimetrica);

            String ivString;
            ivString = Base64.getUrlEncoder().encodeToString(iv);
            String saltString = Base64.getUrlEncoder().encodeToString(salt);
            String claveSimetricaString = Base64.getUrlEncoder().encodeToString(claveSimetrica);

            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(claveSimetricaString.toCharArray(), salt, interaciones, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cifradorAES = Cipher.getInstance("AES/GCM/noPadding");
            cifradorAES.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            String passwdCifrada = new String(Base64.getUrlEncoder().encode(cifradorAES.doFinal(u.getPsswd().getBytes())));

            u.setClaveSimetrica(Base64.getUrlEncoder().encodeToString(claveSimetricaString.getBytes()));
            u.setPsswd(passwdCifrada);
            u.setIv(ivString);
            u.setSalt(saltString);
            u.setInteraciones(interaciones);

        } catch (Exception e) {
            Log.e(UTILS_CIFRADO_DEBUG, e.getMessage(), e);
            return null;
        }
        return u;
    }
}
