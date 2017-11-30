package entities;

import java.util.List;

import data_access.CodigoBarraDAO;

/**
 * Created by Dani_ on 24/10/2017.
 */

public class Comprobante {

    private int id_comprobante;
    private int numeroPick;
    private int orden;
    private String observaciones;
    private int puedeUsuario;
    private String idUsuario;
    private List<Item> items;

    public Comprobante(){

    }

    public int getNumeroPick() {
        return numeroPick;
    }

    public void setNumeroPick(int numeroPick) {
        this.numeroPick = numeroPick;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getPuedeUsuario() {
        return puedeUsuario;
    }

    public void setPuedeUsuario(int puedeUsuario) {
        this.puedeUsuario = puedeUsuario;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }


    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getId_comprobante() {
        return id_comprobante;
    }

    public void setId_comprobante(int id_comprobante) {
        this.id_comprobante = id_comprobante;
    }

    public Boolean perteneceAlComprobante(String codArt) {

        for (Item item: getItems()) {
            if (item.getCodigoArticulo() == codArt) {
                return true;
            }
        }
        return false;
    }
}
