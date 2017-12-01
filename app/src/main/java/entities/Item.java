package entities;

import java.util.List;

/**
 * Created by Dani_ on 24/10/2017.
 */

public class Item {

    private int id_item;
    private String codigoArticulo;
    private String descripcion;
    private int unidad;
    private double cantidad;
    private double kilos;
    private double puedePickear;
    private double saldo;
    private int faltaPickear;
    private List<Serial> seriales;


    public Item(){

    }

    public String getCodigoArticulo() {
        return codigoArticulo;
    }

    public void setCodigoArticulo(String codigoArticulo) {
        this.codigoArticulo = codigoArticulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getUnidad() {
        return unidad;
    }

    public void setUnidad(int unidad) {
        this.unidad = unidad;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getKilos() {
        return kilos;
    }

    public void setKilos(double kilos) {
        this.kilos = kilos;
    }

    public double getPuedePickear() {
        return puedePickear;
    }

    public void setPuedePickear(double puedePickear) {
        this.puedePickear = puedePickear;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public List<Serial> getSeriales() {
        return seriales;
    }

    public void setSeriales(List<Serial> seriales) {
        this.seriales = seriales;
    }

    public int getId_item() {
        return id_item;
    }

    public void setId_item(int id_item) {
        this.id_item = id_item;
    }

    public int getFaltaPickear() {
        return faltaPickear;
    }

    public void setFaltaPickear(int faltaPickear) {
        this.faltaPickear = faltaPickear;
    }
}
