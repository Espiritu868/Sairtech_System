package modelo;

public class Cliente {
    
    private int idCliente;
    private String numeroIdentidad;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;

    public Cliente() {
    }

    public Cliente(String numeroIdentidad, String nombre, String apellido, String telefono, String correo) {
        this.numeroIdentidad = numeroIdentidad;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    public Cliente(int idCliente, String numeroIdentidad, String nombre, String apellido, String telefono, String correo) {
        this.idCliente = idCliente;
        this.numeroIdentidad = numeroIdentidad;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    
    public String getNumeroIdentidad() { return numeroIdentidad; }
    public void setNumeroIdentidad(String numeroIdentidad) { this.numeroIdentidad = numeroIdentidad; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}