package utils;

import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import dao.modelo.Password;
import dao.modelo.Usuario;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Log4j2
public class UtilsCifrado {

    public static Usuario descifrarContraseña(Usuario u) {
        try {
            String claveSimetrica = new String(Base64.getUrlDecoder().decode(u.getClaveSimetrica()));

            byte[] iv = Base64.getUrlDecoder().decode(u.getIv());
            byte[] salt = Base64.getUrlDecoder().decode(u.getSalt());
            int interaciones = u.getInteraciones();

            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(claveSimetrica.toCharArray(), salt, interaciones, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher descifrador = Cipher.getInstance("AES/GCM/noPADDING");
            descifrador.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            String psswdDescifrada = new String(descifrador.doFinal(Base64.getUrlDecoder().decode(u.getPsswd())));

            u.setPsswd(psswdDescifrada);
            return u;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static Password descifrarContraseña(Password p) {
        try {
            String claveSimetrica = new String(Base64.getUrlDecoder().decode(p.getClaveSimetrica()));
            byte[] iv = Base64.getUrlDecoder().decode(p.getIv());
            byte[] salt = Base64.getUrlDecoder().decode(p.getSalt());
            int interaciones = p.getInteraciones();

            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(claveSimetrica.toCharArray(), salt, interaciones, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher descifrador = Cipher.getInstance("AES/GCM/noPADDING");
            descifrador.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            String psswdDescifrada = new String(descifrador.doFinal(Base64.getUrlDecoder().decode(p.getPassword())));

            p.setPassword(psswdDescifrada);
            return p;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static void crearClaves(String path) {
        try {
            SecureRandom sr = new SecureRandom();
            byte[] psswdPFXBytes = new byte[16];

            sr.nextBytes(psswdPFXBytes);

            String psswdPFX = new String(Base64.getUrlEncoder().encode(psswdPFXBytes));

            Security.addProvider(new BouncyCastleProvider());
            KeyPairGenerator generadorRSA = KeyPairGenerator.getInstance("RSA");
            generadorRSA.initialize(2048);
            KeyPair clavesRSA = generadorRSA.generateKeyPair();
            PrivateKey clavePrivada = clavesRSA.getPrivate();
            PublicKey clavePublica = clavesRSA.getPublic();

            X509V3CertificateGenerator cert = new X509V3CertificateGenerator();
            cert.setSerialNumber(BigInteger.valueOf(1));
            cert.setSubjectDN(new X509Principal("CN=IHOUSE_SERVER"));
            cert.setIssuerDN(new X509Principal("CN=IHOUSE_SERVER"));
            cert.setPublicKey(clavePublica);
            cert.setNotBefore(Date.from(LocalDate.now().plus(365, ChronoUnit.DAYS).atStartOfDay().toInstant(ZoneOffset.UTC)));
            cert.setNotAfter(new Date());
            cert.setSignatureAlgorithm("SHA1WithRSAEncryption");

            X509Certificate certificate = cert.generateX509Certificate(clavePrivada);
            KeyStore ks = KeyStore.getInstance("PKCS12");

            ks.load(null, null);
            ks.setCertificateEntry("publica", certificate);
            ks.setKeyEntry("privada", clavePrivada, psswdPFX.toCharArray(), new Certificate[]{certificate});
            FileOutputStream fos = new FileOutputStream(path + "/server_cert.pfx");
            ks.store(fos, "".toCharArray());
            fos.close();

            byte[] iv = new byte[12];
            byte[] salt = new byte[16];
            byte[] claveSimetrica = new byte[16];
            int interaciones = sr.nextInt(100000);


            //Rellanamos las variables necesarias para hacer nuestro cifrado
            sr.nextBytes(iv);
            sr.nextBytes(salt);
            sr.nextBytes(claveSimetrica);
            String claveSimetricaString = Base64.getUrlEncoder().encodeToString(claveSimetrica);

            //Ciframos nuestras variables
            String ivString = Base64.getUrlEncoder().encodeToString(iv);
            String saltString = Base64.getUrlEncoder().encodeToString(salt);
            String claveSimetricaCifrada  = Base64.getUrlEncoder().encodeToString(claveSimetricaString.getBytes());

            //Creamos nuestro metodo de cifrado rellenando todo con nuestras variables aleatorias
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(claveSimetricaString.toCharArray(), salt, interaciones, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cifradorAES = Cipher.getInstance("AES/GCM/noPadding");
            //Ponemos el cipher en modo encriptar
            cifradorAES.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            //Ciframos el mensaje
            String psswdPFXCifrada = new String(Base64.getUrlEncoder().encode(cifradorAES.doFinal(psswdPFX.getBytes())));

            Password psswd = Password.builder()
                    .password(psswdPFXCifrada)
                    .claveSimetrica(claveSimetricaCifrada)
                    .iv(ivString)
                    .salt(saltString)
                    .interaciones(interaciones)
                    .build();

            Gson gson = new Gson();

            String psswdJson = gson.toJson(psswd);
            FileOutputStream fos1 = new FileOutputStream(path + "/psswd.json");
            fos1.write(psswdJson.getBytes());
            fos1.close();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

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
            log.error(e.getMessage(), e);
            return null;
        }
        return u;
    }
}
