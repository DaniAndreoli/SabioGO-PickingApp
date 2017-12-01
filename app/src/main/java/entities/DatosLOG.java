package entities;

/**
 * Created by Federico on 28/11/2017.
 */

public class DatosLOG {
    private String ubicacion, porcentaje_bateria, modelo_dispositivo, marca_dispositivo;

    public DatosLOG(String ubicacion, String porcentaje_bateria, String modelo_dispositivo, String marca_dispositivo) {
        this.ubicacion = ubicacion;
        this.porcentaje_bateria = porcentaje_bateria;
        this.modelo_dispositivo = modelo_dispositivo;
        this.marca_dispositivo = marca_dispositivo;
    }

    public DatosLOG() {
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getPorcentaje_bateria() {
        return porcentaje_bateria;
    }

    public void setPorcentaje_bateria(String porcentaje_bateria) {
        this.porcentaje_bateria = porcentaje_bateria;
    }

    public String getModelo_dispositivo() {
        return modelo_dispositivo;
    }

    public void setModelo_dispositivo(String modelo_dispositivo) {
        this.modelo_dispositivo = modelo_dispositivo;
    }

    public String getMarca_dispositivo() {
        return marca_dispositivo;
    }

    public void setMarca_dispositivo(String marca_dispositivo) {
        this.marca_dispositivo = marca_dispositivo;
    }


    @Override
    public String toString() {
        return "DatosLOG{" +
                "ubicacion='" + ubicacion +
                ", porcentaje_bateria='" + porcentaje_bateria +
                ", modelo_dispositivo='" + modelo_dispositivo +
                ", marca_dispositivo='" + marca_dispositivo +
                '}';
    }
}
