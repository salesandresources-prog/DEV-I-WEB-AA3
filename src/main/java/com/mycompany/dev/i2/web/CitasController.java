package com.mycompany.dev.i2.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador (Managed Bean) para la gestion de citas.
 * Implementa las operaciones CRUD (Crear, Leer, Actualizar, Eliminar).
 * Utiliza JDBC para la conexion directa con la base de datos MySQL.
 * 
 * @author Andres Aguiar
 * @version 2.0
 */
@Named("citasController")
@SessionScoped
public class CitasController implements Serializable {

    // Inyeccion del controlador de usuarios para obtener datos de sesion
    @Inject
    private UsuariosController usuariosController;

    // Campos del formulario de gestion de citas
    private Integer idCita;
    private String clienteNombre;
    private String correo;
    private String telefono;
    private String documento;
    private String fechaCita;
    private String descripcion;
    private String estado;

    // Lista de citas cargadas desde la base de datos
    private List<Cita> listaCitas;

    /**
     * Clase interna que representa una cita en el sistema.
     * Encapsula todos los datos de una cita para mostrarlos en la vista.
     */
    public static class Cita implements Serializable {
        private int id;
        private String clienteNombre;
        private String correo;
        private String telefono;
        private String documento;
        private String fechaCita;
        private String descripcion;
        private String estado;

        // Getters y Setters de la clase Cita
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getClienteNombre() { return clienteNombre; }
        public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        public String getDocumento() { return documento; }
        public void setDocumento(String documento) { this.documento = documento; }
        public String getFechaCita() { return fechaCita; }
        public void setFechaCita(String fechaCita) { this.fechaCita = fechaCita; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }

    /**
     * Guarda una nueva cita o actualiza una existente en la base de datos.
     * Si idCita es null, inserta un nuevo registro (CREATE).
     * Si idCita tiene valor, actualiza el registro existente (UPDATE).
     */
    public void guardar() {
        if (idCita == null) {
            // CREAR nueva cita
            String sql = "INSERT INTO citas (cliente_nombre, correo, telefono, documento, fecha_cita, descripcion, estado, id_agente) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = Conexion.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, clienteNombre);
                ps.setString(2, correo);
                ps.setString(3, telefono);
                ps.setString(4, documento);
                ps.setString(5, fechaCita);
                ps.setString(6, descripcion);
                ps.setString(7, "Pendiente");
                ps.setInt(8, usuariosController.getIdUsuarioLogueado());
                ps.executeUpdate();
                // Mensaje de exito para el usuario
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Cita registrada correctamente"));
                System.out.println("[DEV-I 2.0] Cita registrada para cliente: " + clienteNombre);
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo registrar: " + e.getMessage()));
                e.printStackTrace();
            }
        } else {
            // ACTUALIZAR cita existente
            String sql = "UPDATE citas SET cliente_nombre=?, correo=?, telefono=?, documento=?, fecha_cita=?, descripcion=?, estado=? WHERE id=?";
            try (Connection conn = Conexion.obtenerConexion();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, clienteNombre);
                ps.setString(2, correo);
                ps.setString(3, telefono);
                ps.setString(4, documento);
                ps.setString(5, fechaCita);
                ps.setString(6, descripcion);
                ps.setString(7, estado);
                ps.setInt(8, idCita);
                ps.executeUpdate();
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Cita actualizada correctamente"));
                System.out.println("[DEV-I 2.0] Cita #" + idCita + " actualizada");
            } catch (SQLException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar: " + e.getMessage()));
                e.printStackTrace();
            }
        }
        // Limpiar el formulario y recargar la lista de citas
        limpiar();
        cargarCitas();
    }

    /**
     * Elimina una cita de la base de datos por su ID (DELETE).
     * 
     * @param id Identificador unico de la cita a eliminar
     */
    public void eliminar(int id) {
        String sql = "DELETE FROM citas WHERE id = ?";
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito", "Cita eliminada"));
            System.out.println("[DEV-I 2.0] Cita #" + id + " eliminada");
        } catch (SQLException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar: " + e.getMessage()));
            e.printStackTrace();
        }
        // Recargar la lista de citas despues de eliminar
        cargarCitas();
    }

    /**
     * Carga los datos de una cita existente en el formulario para edicion.
     * 
     * @param cita Objeto Cita con los datos a cargar en el formulario
     */
    public void editar(Cita cita) {
        this.idCita = cita.getId();
        this.clienteNombre = cita.getClienteNombre();
        this.correo = cita.getCorreo();
        this.telefono = cita.getTelefono();
        this.documento = cita.getDocumento();
        this.fechaCita = cita.getFechaCita();
        this.descripcion = cita.getDescripcion();
        this.estado = cita.getEstado();
    }

    /**
     * Limpia todos los campos del formulario de citas.
     * Reinicia el formulario para una nueva entrada.
     */
    public void limpiar() {
        idCita = null;
        clienteNombre = "";
        correo = "";
        telefono = "";
        documento = "";
        fechaCita = "";
        descripcion = "";
        estado = "";
    }

    /**
     * Consulta todas las citas de la base de datos y las almacena en listaCitas.
     * Se ejecuta al cargar la pagina y despues de cada operacion CRUD.
     */
    public void cargarCitas() {
        listaCitas = new ArrayList<>();
        String sql = "SELECT * FROM citas ORDER BY id DESC";
        try (Connection conn = Conexion.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Cita c = new Cita();
                c.setId(rs.getInt("id"));
                c.setClienteNombre(rs.getString("cliente_nombre"));
                c.setCorreo(rs.getString("correo"));
                c.setTelefono(rs.getString("telefono"));
                c.setDocumento(rs.getString("documento"));
                c.setFechaCita(rs.getString("fecha_cita"));
                c.setDescripcion(rs.getString("descripcion"));
                c.setEstado(rs.getString("estado"));
                listaCitas.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getter que retorna la lista de citas.
     * Si la lista no ha sido cargada, la carga automaticamente.
     * 
     * @return List de objetos Cita
     */
    public List<Cita> getListaCitas() {
        if (listaCitas == null) {
            cargarCitas();
        }
        return listaCitas;
    }

    // ===================== GETTERS Y SETTERS =====================
    
    public Integer getIdCita() { return idCita; }
    public void setIdCita(Integer idCita) { this.idCita = idCita; }
    
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    
    public String getFechaCita() { return fechaCita; }
    public void setFechaCita(String fechaCita) { this.fechaCita = fechaCita; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
