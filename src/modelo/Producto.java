package modelo;

public class Producto {
    private int idProducto;
    private String codigoBarras;
    private String nombreProducto;
    private int idCategoria;
    private double precioCompra;
    private double precioVenta;
    private int stock;
    private int stockMinimo;
    private int idProveedor; 
    
    private boolean aplicaPrecioTecnico;
    private double precioTecnico;
    
    // --- NUEVA VARIABLE ---
    private String ubicacion;

    public Producto() {}

    public Producto(String codigoBarras, String nombreProducto, int idCategoria, double precioCompra, double precioVenta, int stock, int stockMinimo, int idProveedor, boolean aplicaPrecioTecnico, double precioTecnico, String ubicacion) {
        this.codigoBarras = codigoBarras;
        this.nombreProducto = nombreProducto;
        this.idCategoria = idCategoria;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.idProveedor = idProveedor;
        this.aplicaPrecioTecnico = aplicaPrecioTecnico;
        this.precioTecnico = precioTecnico;
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public boolean isAplicaPrecioTecnico() { return aplicaPrecioTecnico; }
    public void setAplicaPrecioTecnico(boolean aplicaPrecioTecnico) { this.aplicaPrecioTecnico = aplicaPrecioTecnico; }

    public double getPrecioTecnico() { return precioTecnico; }
    public void setPrecioTecnico(double precioTecnico) { this.precioTecnico = precioTecnico; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}