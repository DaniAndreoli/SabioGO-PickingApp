package entities;

/**
 * Created by Dani_ on 24/10/2017.
 */

public class CodigoBarra {
    private int numero;
    private String nombre;
    private String descripcion;
    private int largoTotal;
    private int ubicacionCodProd;
    private int largoCodProd;
    private int ubicacionCantidad;
    private int largoCantidad;
    private int ubicacionPeso;
    private int largoPeso;
    private int ubicacionPrecio;
    private int largoPrecio;
    private int ubicacionFechaElab;
    private int largoFechaElab;
    private int ubicacionFechaVenc;
    private int largoFechaVenc;
    private int ubicacionDigitoVer;
    private int largoDigitoVer;
    private int ubicacionIdUsuario;
    private int largoIdUsuario;
    private int cantidadDecPeso;

    public CodigoBarra(){

    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getLargoTotal() {
        return largoTotal;
    }

    public void setLargoTotal(int largoTotal) {
        this.largoTotal = largoTotal;
    }

    public int getUbicacionCodProd() {
        return ubicacionCodProd;
    }

    public void setUbicacionCodProd(int ubicacionCodProd) {
        this.ubicacionCodProd = ubicacionCodProd;
    }

    public int getLargoCodProd() {
        return largoCodProd;
    }

    public void setLargoCodProd(int largoCodProd) {
        this.largoCodProd = largoCodProd;
    }

    public int getUbicacionCantidad() {
        return ubicacionCantidad;
    }

    public void setUbicacionCantidad(int ubicacionCantidad) {
        this.ubicacionCantidad = ubicacionCantidad;
    }

    public int getLargoCantidad() {
        return largoCantidad;
    }

    public void setLargoCantidad(int largoCantidad) {
        this.largoCantidad = largoCantidad;
    }

    public int getUbicacionPeso() {
        return ubicacionPeso;
    }

    public void setUbicacionPeso(int ubicacionPeso) {
        this.ubicacionPeso = ubicacionPeso;
    }

    public int getLargoPeso() {
        return largoPeso;
    }

    public void setLargoPeso(int largoPeso) {
        this.largoPeso = largoPeso;
    }

    public int getUbicacionPrecio() {
        return ubicacionPrecio;
    }

    public void setUbicacionPrecio(int ubicacionPrecio) {
        this.ubicacionPrecio = ubicacionPrecio;
    }

    public int getLargoPrecio() {
        return largoPrecio;
    }

    public void setLargoPrecio(int largoPrecio) {
        this.largoPrecio = largoPrecio;
    }

    public int getUbicacionFechaElab() {
        return ubicacionFechaElab;
    }

    public void setUbicacionFechaElab(int ubicacionFechaElab) {
        this.ubicacionFechaElab = ubicacionFechaElab;
    }

    public int getLargoFechaElab() {
        return largoFechaElab;
    }

    public void setLargoFechaElab(int largoFechaElab) {
        this.largoFechaElab = largoFechaElab;
    }

    public int getUbicacionFechaVenc() {
        return ubicacionFechaVenc;
    }

    public void setUbicacionFechaVenc(int ubicacionFechaVenc) {
        this.ubicacionFechaVenc = ubicacionFechaVenc;
    }

    public int getLargoFechaVenc() {
        return largoFechaVenc;
    }

    public void setLargoFechaVenc(int largoFechaVenc) {
        this.largoFechaVenc = largoFechaVenc;
    }

    public int getUbicacionDigitoVer() {
        return ubicacionDigitoVer;
    }

    public void setUbicacionDigitoVer(int ubicacionDigitoVer) {
        this.ubicacionDigitoVer = ubicacionDigitoVer;
    }

    public int getLargoDigitoVer() {
        return largoDigitoVer;
    }

    public void setLargoDigitoVer(int largoDigitoVer) {
        this.largoDigitoVer = largoDigitoVer;
    }

    public int getUbicacionIdUsuario() {
        return ubicacionIdUsuario;
    }

    public void setUbicacionIdUsuario(int ubicacionIdUsuario) {
        this.ubicacionIdUsuario = ubicacionIdUsuario;
    }

    public int getLargoIdUsuario() {
        return largoIdUsuario;
    }

    public void setLargoIdUsuario(int largoIdUsuario) {
        this.largoIdUsuario = largoIdUsuario;
    }

    public int getCantidadDecPeso() {
        return cantidadDecPeso;
    }

    public void setCantidadDecPeso(int cantidadDecPeso) {
        this.cantidadDecPeso = cantidadDecPeso;
    }

    //TODO Agregar metodos get para atributos: codArt, cantidad, kilos, unidades, obteniendo un numero de serie como parametro
}
