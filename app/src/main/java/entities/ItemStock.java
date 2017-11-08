package entities;

/**
 * Created by Dani_ on 31/10/2017.
 */

public class ItemStock {
    private String codigoArticulo;
    private float cantidad;
    private float unidad;
    private float kilos;

    public ItemStock() {

    }

    public ItemStock(String codigoArticulo, float cantidad, float unidad, float kilos){
        this.codigoArticulo = codigoArticulo;
        this.cantidad = cantidad;
        this.unidad = unidad;
        this.kilos = kilos;
    }

    public String getCodigoArticulo() {
        return codigoArticulo;
    }

    public void setCodigoArticulo(String codigoArticulo) {
        this.codigoArticulo = codigoArticulo;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public float getUnidad() {
        return unidad;
    }

    public void setUnidad(int unidad) {
        this.unidad = unidad;
    }

    public float getKilos() {
        return kilos;
    }

    public void setKilos(int kilos) {
        this.kilos = kilos;
    }
}
