package com.mycompany.dev.i2.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controlador (Managed Bean) para la gestion de usuarios.
 * Maneja el inicio de sesion, registro y recuperacion de contrasena.
 * Utiliza CDI (@Named) y es de ambito de sesion (@SessionScoped).
 * 
 * @author Andres Aguiar
 * @version 2.0
 */
@Named("usuariosController")
@SessionScoped
public class UsuariosController implements Serializable {

    // Campos del formulario de login
    private String usuario;
    private String password;
    
    // Campos del formulario de registro
    private String regUsuario;
    private String regPassword;
    private String regConfirmar;
    
    // Datos del usuario logueado en la sesion
    private Integer idUsuarioLogueado;
    private String nombreUsuarioLogueado;
    private boolean logueado = false;

    /**
     * Metodo que valida las credenciales del usuario contra la base de datos.
     * Si las credenciales son correctas, redirige al dashboard de citas.
     * Si son incorrectas, muestra un mensaje de error en la misma pagina.
     * 
     * @return String ruta de navegacion JSF (citas.xhtml o null para quedarse)
     */
    public String validarLogin() {
        // Consulta SQL para buscar al usuario por nombre de usuario y contrasena
        String sql = "SELECT * FROM usuarios WHERE user = ? AND password = ?";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Establecer los parametros de la consulta
            ps.setString(1, usuario);
            ps.setString(2, password);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Login exitoso: guardar datos del usuario en la sesion
                    idUsuarioLogueado = rs.getInt("id");
                    nombreUsuarioLogueado = rs.getString("user");
                    logueado = true;
                    System.out.println("[DEV-I 2.0] Login exitoso para: " + nombreUsuarioLogueado);
                    // Redirigir al dashboard de gestion de citas
                    return "citas?faces-redirect=true";
                } else {
                    // Login fallido: mostrar mensaje de error en la pantalla
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                            "Error", "Usuario o contrasena incorrectos"));
                    return null;
                }
            }
        } catch (SQLException e) {
            // Error de conexion con la base de datos
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Error", "Error de conexion con la base de datos: " + e.getMessage()));
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Metodo que registra un nuevo usuario en la base de datos.
     * Valida que las contrasenas coincidan antes de insertar.
     * 
     * @return String ruta de navegacion JSF
     */
    public String registrarUsuario() {
        // Validar que las contrasenas coincidan
        if (!regPassword.equals(regConfirmar)) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Error", "Las contrasenas no coinciden"));
            return null;
        }
        
        // Consulta SQL para insertar el nuevo usuario
        String sql = "INSERT INTO usuarios (user, password) VALUES (?, ?)";
        
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, regUsuario);
            ps.setString(2, regPassword);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                System.out.println("[DEV-I 2.0] Usuario registrado: " + regUsuario);
                // Redirigir al login con mensaje de exito
                return "login?faces-redirect=true";
            }
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                    "Error", "No se pudo registrar: " + e.getMessage()));
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cierra la sesion del usuario y redirige al login.
     * 
     * @return String ruta de navegacion JSF al login
     */
    public String cerrarSesion() {
        // Invalidar la sesion HTTP del usuario
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        logueado = false;
        return "login?faces-redirect=true";
    }

    // ===================== GETTERS Y SETTERS =====================
    
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRegUsuario() { return regUsuario; }
    public void setRegUsuario(String regUsuario) { this.regUsuario = regUsuario; }
    
    public String getRegPassword() { return regPassword; }
    public void setRegPassword(String regPassword) { this.regPassword = regPassword; }
    
    public String getRegConfirmar() { return regConfirmar; }
    public void setRegConfirmar(String regConfirmar) { this.regConfirmar = regConfirmar; }
    
    public Integer getIdUsuarioLogueado() { return idUsuarioLogueado; }
    public String getNombreUsuarioLogueado() { return nombreUsuarioLogueado; }
    public boolean isLogueado() { return logueado; }
}