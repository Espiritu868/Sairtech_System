package modelo;

public class Estadisticas {
    private int equiposEntregados;
    private int ordenesPendientes;
    private double gananciasTotales;

    // Getters y Setters
    public int getEquiposEntregados() { return equiposEntregados; }
    public void setEquiposEntregados(int equiposEntregados) { this.equiposEntregados = equiposEntregados; }

    public int getOrdenesPendientes() { return ordenesPendientes; }
    public void setOrdenesPendientes(int ordenesPendientes) { this.ordenesPendientes = ordenesPendientes; }

    public double getGananciasTotales() { return gananciasTotales; }
    public void setGananciasTotales(double gananciasTotales) { this.gananciasTotales = gananciasTotales; }
}