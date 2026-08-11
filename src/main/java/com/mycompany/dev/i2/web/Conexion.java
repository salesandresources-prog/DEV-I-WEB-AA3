package com.mycompany.dev.i2.web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para la conexion con la base de datos MySQL.
 * Utiliza JDBC para establecer la conexion directa con el servidor de base de datos.
 * Configurada para funcionar con XAMPP (MySQL en puerto 3306).
 * 
 * @author Andres Aguiar
 * @version 2.0
 */
public class Conexion {

    // URL de conexion a la base de datos 'dev_i2_web' en MySQL local (exclusiva para este proyecto)
    private static final String URL = "jdbc:mysql://localhost:3306/dev_i2_web?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    // Usuario por defecto de XAMPP
    private static final String USER = "root";
    // Contrasena vacia por defecto en XAMPP
    private static final String PASSWORD = "";

    /**
     * Obtiene una conexion activa con la base de datos MySQL.
     * Carga el driver JDBC de MySQL y establece la conexion.
     * 
     * @return Connection objeto de conexion activa, o null si falla
     */
    public static Connection obtenerConexion() {
        Connection conectar = null;
        try {
            // Cargar el driver JDBC de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establecer la conexion con los parametros configurados
            conectar = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("====== [DEV-I 2.0] Conexion Exitosa con base 'devi_db' ======");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("[Error DEV-I 2.0] Falla de conexion: " + e.getMessage());
        }
        return conectar;
    }
}
