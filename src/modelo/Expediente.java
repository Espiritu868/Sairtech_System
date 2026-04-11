package modelo;

import java.sql.Timestamp;

public class Expediente {
    
    private int idOrden;
    private Timestamp fechaIngreso;
    private Timestamp fechaEntrega;
    private String estado;
    private String nombreCliente;
    private String telefonoCliente;
    private String modeloEquipo;
    private String problemaReportado;
    private String trabajoRealizado;
    private double costo;
    private String tecnicoEntrega;

    public Expediente() {}

    public int getIdOrden() { return idOrden; }
    public void setIdOrden(int idOrden) { this.idOrden = idOrden; }

    public Timestamp getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Timestamp fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public Timestamp getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(Timestamp fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getTelefonoCliente() { return telefonoCliente; }
    public void setTelefonoCliente(String telefonoCliente) { this.telefonoCliente = telefonoCliente; }

    public String getModeloEquipo() { return modeloEquipo; }
    public void setModeloEquipo(String modeloEquipo) { this.modeloEquipo = modeloEquipo; }

    public String getProblemaReportado() { return problemaReportado; }
    public void setProblemaReportado(String problemaReportado) { this.problemaReportado = problemaReportado; }

    public String getTrabajoRealizado() { return trabajoRealizado; }
    public void setTrabajoRealizado(String trabajoRealizado) { this.trabajoRealizado = trabajoRealizado; }

    public double getCosto() { return costo; }
    public void setCosto(double costo) { this.costo = costo; }

    public String getTecnicoEntrega() { return tecnicoEntrega; }
    public void setTecnicoEntrega(String tecnicoEntrega) { this.tecnicoEntrega = tecnicoEntrega; }
}