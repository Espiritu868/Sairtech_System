package modelo;

public class PantallaKnijico {
    
    private int idPantalla;
    private int idLote;
    private String nombreLote; 
    private String modeloEquipo;
    private double precioCompra;
    private double precioCliente;
    private double precioTecnico;
    private int stock;
    private String estado;
    private int numeroCaja; // <--- NUEVO CAMPO

    public PantallaKnijico() {
    }

    public PantallaKnijico(int idLote, String modeloEquipo, double precioCompra, double precioCliente, double precioTecnico, int stock, int numeroCaja) {
        this.idLote = idLote;
        this.modeloEquipo = modeloEquipo;
        this.precioCompra = precioCompra;
        this.precioCliente = precioCliente;
        this.precioTecnico = precioTecnico;
        this.stock = stock;
        this.numeroCaja = numeroCaja;
        this.estado = "Activo"; 
    }

    // Getters y Setters
    public int getIdPantalla() { return idPantalla; }
    public void setIdPantalla(int idPantalla) { this.idPantalla = idPantalla; }

    public int getIdLote() { return idLote; }
    public void setIdLote(int idLote) { this.idLote = idLote; }

    public String getNombreLote() { return nombreLote; }
    public void setNombreLote(String nombreLote) { this.nombreLote = nombreLote; }

    public String getModeloEquipo() { return modeloEquipo; }
    public void setModeloEquipo(String modeloEquipo) { this.modeloEquipo = modeloEquipo; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioCliente() { return precioCliente; }
    public void setPrecioCliente(double precioCliente) { this.precioCliente = precioCliente; }

    public double getPrecioTecnico() { return precioTecnico; }
    public void setPrecioTecnico(double precioTecnico) { this.precioTecnico = precioTecnico; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getNumeroCaja() { return numeroCaja; }
    public void setNumeroCaja(int numeroCaja) { this.numeroCaja = numeroCaja; }
}