package entities;

/**
 * Created by Federico on 28/10/2017.
 */

public class Serial {

    private int id_serial, idSerial;
    private String codigoArticulo;
    private String numero;
    private String tipoComprobante;//Stock o Entrada/Salida
    private int idItem;

    public Serial() {

    }

    public Serial(String codigoArt, String nroSerial, String tipoComprobante) {
        this.codigoArticulo = codigoArt;
        this.numero = nroSerial;
        this.tipoComprobante = tipoComprobante;
        this.idSerial = 0;
    }

    public String getCodigoArticulo() {
        return codigoArticulo;
    }

    public void setCodigoArticulo(String codigoArticulo) {
        this.codigoArticulo = codigoArticulo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipoComprobante() {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante) {
        this.tipoComprobante = tipoComprobante;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public int getId_serial() {
        return id_serial;
    }

    public void setId_serial(int id_serial) {
        this.id_serial = id_serial;
    }
}
