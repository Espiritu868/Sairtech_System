package modelo;

public class Proveedor {
    
    private int idProveedor;
    private String empresa;
    private String nombreContacto;
    private String telefono;
    private String direccion;
    private String tipoRepuestos;
    private String estado;

    public Proveedor() {}

    public Proveedor(int idProveedor, String empresa, String nombreContacto, String telefono, String direccion, String tipoRepuestos, String estado) {
        this.idProveedor = idProveedor;
        this.empresa = empresa;
        this.nombreContacto = nombreContacto;
        this.telefono = telefono;
        this.direccion = direccion;
        this.tipoRepuestos = tipoRepuestos;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTipoRepuestos() { return tipoRepuestos; }
    public void setTipoRepuestos(String tipoRepuestos) { this.tipoRepuestos = tipoRepuestos; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // ¡TRUCO DE MAGIA PARA EL COMBOBOX!
    // Al hacer esto, cuando metamos el objeto al ComboBox, Java mostrará automáticamente el nombre de la empresa.
    @Override
    public String toString() {
        return this.empresa;
    }
}