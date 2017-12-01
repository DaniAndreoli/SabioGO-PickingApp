package entities;

/**
 * Created by Federico on 28/10/2017.
 */

public class Serial {

    private int id_serial;
    private String codigoArticulo;
    private String serial;
    private String tipoComprobante;//Stock o Entrada/Salida
    private int idItem;

    public Serial() {

    }

    public Serial(String codigoArt, String nroSerie, String tipoComprobante) {
        this.codigoArticulo = codigoArt;
        this.serial = nroSerie;
        this.tipoComprobante = tipoComprobante;
    }

    public String getCodigoArticulo() {
        return codigoArticulo;
    }

    public void setCodigoArticulo(String codigoArticulo) {
        this.codigoArticulo = codigoArticulo;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
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
