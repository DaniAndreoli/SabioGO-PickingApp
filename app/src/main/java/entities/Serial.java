package entities;

/**
 * Created by Federico on 28/10/2017.
 */

public class Serial {
    private String numero;
    private int codigoArticulo;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public int getCodigoArticulo() {
        return codigoArticulo;
    }

    public void setCodigoArticulo(int codigoArticulo) {
        this.codigoArticulo = codigoArticulo;
    }

    public Serial() {

    }

    public Serial(int codigoArticulo, String numero){
        this.codigoArticulo = codigoArticulo;
        this.numero= numero;
    }

}
