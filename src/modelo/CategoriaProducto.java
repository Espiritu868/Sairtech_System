package modelo;

public class CategoriaProducto {
    private int idCategoria;
    private String nombreCategoria;
    private String descripcion;

    public CategoriaProducto() {
    }

    public CategoriaProducto(int idCategoria, String nombreCategoria, String descripcion) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
    }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public String getNombreCategoria() { return nombreCategoria; }
    public void setNombreCategoria(String nombreCategoria) { this.nombreCategoria = nombreCategoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    // Este toString ayuda mucho al llenar ComboBox en el futuro
    @Override
    public String toString() {
        return nombreCategoria;
    }
}