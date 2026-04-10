package modelo;

public class TipoEquipo {
    private int idTipo;
    private String nombreTipo;

    public TipoEquipo() {}

    public TipoEquipo(int idTipo, String nombreTipo) {
        this.idTipo = idTipo;
        this.nombreTipo = nombreTipo;
    }

    public TipoEquipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }
    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }
}