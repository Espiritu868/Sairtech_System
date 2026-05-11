package modelo;

public class Despiece {
    private int idDespiece;
    private String modeloDispositivo;
    private String placaBase;
    private String pantallaLcd;
    private String bateria;
    private String marcoChasis;
    private String moduloCarga;
    private String camaras;
    private String comentarios;
    private String rutaImg1;
    private String rutaImg2;
    private String rutaImg3;

    public Despiece() {}

    // Getters y Setters
    public int getIdDespiece() { return idDespiece; }
    public void setIdDespiece(int idDespiece) { this.idDespiece = idDespiece; }

    public String getModeloDispositivo() { return modeloDispositivo; }
    public void setModeloDispositivo(String modeloDispositivo) { this.modeloDispositivo = modeloDispositivo; }

    public String getPlacaBase() { return placaBase; }
    public void setPlacaBase(String placaBase) { this.placaBase = placaBase; }

    public String getPantallaLcd() { return pantallaLcd; }
    public void setPantallaLcd(String pantallaLcd) { this.pantallaLcd = pantallaLcd; }

    public String getBateria() { return bateria; }
    public void setBateria(String bateria) { this.bateria = bateria; }

    public String getMarcoChasis() { return marcoChasis; }
    public void setMarcoChasis(String marcoChasis) { this.marcoChasis = marcoChasis; }

    public String getModuloCarga() { return moduloCarga; }
    public void setModuloCarga(String moduloCarga) { this.moduloCarga = moduloCarga; }

    public String getCamaras() { return camaras; }
    public void setCamaras(String camaras) { this.camaras = camaras; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public String getRutaImg1() { return rutaImg1; }
    public void setRutaImg1(String rutaImg1) { this.rutaImg1 = rutaImg1; }

    public String getRutaImg2() { return rutaImg2; }
    public void setRutaImg2(String rutaImg2) { this.rutaImg2 = rutaImg2; }

    public String getRutaImg3() { return rutaImg3; }
    public void setRutaImg3(String rutaImg3) { this.rutaImg3 = rutaImg3; }
}