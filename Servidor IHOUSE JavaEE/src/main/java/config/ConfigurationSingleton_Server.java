/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public class ConfigurationSingleton_Server {

    private String ruta;
    private String user;
    private String password;
    private String driver;
    private static ConfigurationSingleton_Server config;

    public static ConfigurationSingleton_Server cargarInstance(InputStream file) {
        if (config == null) {
            try {
                Yaml yaml = new Yaml();
                config = yaml.loadAs(file, ConfigurationSingleton_Server.class);
            } catch (Exception ex) {
                Logger.getLogger(ConfigurationSingleton_Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return config;
    }
    public static ConfigurationSingleton_Server getInstance() {
        return config;
    }

}
