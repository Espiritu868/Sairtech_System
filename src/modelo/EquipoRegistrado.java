package modelo;

public class EquipoRegistrado {
    private int idEquipo;
    private int idCliente;
    private int idTipo;
    private int idMarca;
    private String modelo;
    private String imeiSerie;

    public EquipoRegistrado() {}

    public EquipoRegistrado(int idCliente, int idTipo, int idMarca, String modelo, String imeiSerie) {
        this.idCliente = idCliente;
        this.idTipo = idTipo;
        this.idMarca = idMarca;
        this.modelo = modelo;
        this.imeiSerie = imeiSerie;
    }

    public EquipoRegistrado(int idEquipo, int idCliente, int idTipo, int idMarca, String modelo, String imeiSerie) {
        this.idEquipo = idEquipo;
        this.idCliente = idCliente;
        this.idTipo = idTipo;
        this.idMarca = idMarca;
        this.modelo = modelo;
        this.imeiSerie = imeiSerie;
    }

    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }
    public int getIdMarca() { return idMarca; }
    public void setIdMarca(int idMarca) { this.idMarca = idMarca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getImeiSerie() { return imeiSerie; }
    public void setImeiSerie(String imeiSerie) { this.imeiSerie = imeiSerie; }
}