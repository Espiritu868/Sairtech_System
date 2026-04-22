package modelo;

public class DetalleVenta {
    private int idDetalle;
    private int idVenta;
    private int idProducto; // 0 si es un servicio manual o reparación
    private String descripcion;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;
    
    // --- NUEVAS VARIABLES DE GARANTÍA ---
    private String imei;
    private int diasGarantia;

    public DetalleVenta() {
        this.imei = ""; // Seguro por defecto
        this.diasGarantia = 0;
    }

    // Getters y Setters
    public int getIdDetalle() { return idDetalle; }
    public void setIdDetalle(int idDetalle) { this.idDetalle = idDetalle; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public String getImei() { return imei; }
    public void setImei(String imei) { this.imei = imei; }

    public int getDiasGarantia() { return diasGarantia; }
    public void setDiasGarantia(int diasGarantia) { this.diasGarantia = diasGarantia; }
}