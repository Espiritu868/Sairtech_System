package modelo;

import java.sql.Timestamp;

public class OrdenReparacion {
    private int idOrden;
    private int idEquipo;
    private Timestamp fechaIngreso;
    private String problemaReportado;
    private String trabajoRealizado;
    private double costo;
    private String estado;
    
    // --- VARIABLE PARA CUMPLIR LA 3FN ---
    private int idUsuarioEntrega;

    // --- NUEVA VARIABLE PARA CLAVE/PATRÓN DEL DISPOSITIVO ---
    private String seguridadDispositivo; 

    public OrdenReparacion() {}

    public OrdenReparacion(int idEquipo, String problemaReportado, String trabajoRealizado, double costo, String estado) {
        this.idEquipo = idEquipo;
        this.problemaReportado = problemaReportado;
        this.trabajoRealizado = trabajoRealizado;
        this.costo = costo;
        this.estado = estado;
    }

    public OrdenReparacion(int idOrden, int idEquipo, Timestamp fechaIngreso, String problemaReportado, String trabajoRealizado, double costo, String estado) {
        this.idOrden = idOrden;
        this.idEquipo = idEquipo;
        this.fechaIngreso = fechaIngreso;
        this.problemaReportado = problemaReportado;
        this.trabajoRealizado = trabajoRealizado;
        this.costo = costo;
        this.estado = estado;
    }

    // Getters y Setters existentes
    public int getIdOrden() { return idOrden; }
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }
    
    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }
    
    public Timestamp getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Timestamp fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    
    public String getProblemaReportado() { return problemaReportado; }
    public void setProblemaReportado(String problemaReportado) { this.problemaReportado = problemaReportado; }
    
    public String getTrabajoRealizado() { return trabajoRealizado; }
    public void setTrabajoRealizado(String trabajoRealizado) { this.trabajoRealizado = trabajoRealizado; }
    
    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdUsuarioEntrega() { return idUsuarioEntrega; }
    public void setIdUsuarioEntrega(int idUsuarioEntrega) { this.idUsuarioEntrega = idUsuarioEntrega; }

    // --- NUEVOS MÉTODOS GET Y SET PARA LA SEGURIDAD ---
    public String getSeguridadDispositivo() { return seguridadDispositivo; }
    public void setSeguridadDispositivo(String seguridadDispositivo) { this.seguridadDispositivo = seguridadDispositivo; }
}