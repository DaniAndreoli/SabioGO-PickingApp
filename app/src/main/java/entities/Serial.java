package entities;

/**
 * Created by Federico on 28/10/2017.
 */

public class Serial {
    private String numero;
    private int idSerial;
    //private int codigoArticulo;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    /*
    public int getCodigoArticulo() {
        return codigoArticulo;
    }

    public void setCodigoArticulo(int codigoArticulo) {
        this.codigoArticulo = codigoArticulo;
    }*/

    public Serial() {
        this.idSerial = 0;
    }

    public Serial(String numero){
        this.idSerial = 0;
        this.numero= numero;
    }

}
