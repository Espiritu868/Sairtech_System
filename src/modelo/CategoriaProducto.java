package modelo;

public class CategoriaProducto {
    private int idCategoria;
    private String nombreCategoria;
    private String descripcion;
    private int diasGarantia; // <--- NUEVA VARIABLE

    public CategoriaProducto() {
    }

    public CategoriaProducto(int idCategoria, String nombreCategoria, String descripcion, int diasGarantia) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
        this.diasGarantia = diasGarantia;
    }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getDiasGarantia() { return diasGarantia; }
    public void setDiasGarantia(int diasGarantia) { this.diasGarantia = diasGarantia; }
    
    // Este toString ayuda mucho al llenar ComboBox en el futuro
    @Override
    public String toString() {
        return nombreCategoria;
    }
}