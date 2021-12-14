package config;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtilsSingleton {

    private SessionFactory ourSessionFactory;

    private static HibernateUtilsSingleton instance;

    private HibernateUtilsSingleton() {}

    public static HibernateUtilsSingleton getInstance() {
        if (instance == null || instance.ourSessionFactory == null) {
            instance = new HibernateUtilsSingleton();
            try {
                Configuration configuration = new Configuration();

                configuration.configure()
                        .setProperty("hibernate.connection.url", ConfigurationSingleton_Server.getInstance().getRuta())
                        .setProperty("hibernate.connection.username", ConfigurationSingleton_Server.getInstance().getUser())
                        .setProperty("hibernate.connection.password", ConfigurationSingleton_Server.getInstance().getPassword())
                        .setProperty("hibernate.connection.driver_class", ConfigurationSingleton_Server.getInstance().getDriver());

                instance.ourSessionFactory = configuration.buildSessionFactory();
            } catch (Throwable ex) {
                throw new ExceptionInInitializerError(ex);
            }
        }
        return instance;
    }

    public Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }
}