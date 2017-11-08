package entities;

/**
 * Created by Federico on 28/10/2017.
 */

public class Serial {
    private String numero;
    private int idSerial;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }


    public Serial() {
        this.idSerial = 0;
    }

    public Serial(String numero){
        this.idSerial = 0;
        this.numero= numero;
    }

}
