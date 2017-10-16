package entities;

/**
 * Created by Dani_ on 16/10/2017.
    Clase de Ejemplo
 */



public class Cliente {

    private int idCliente;
    private String nombre;
    private String apellido;

    private String razonSocial;
    private String direccion;
    private String localidad;
    private String telefono;
    private String email;

    public Cliente() {

    }

    public Cliente(int idCliente, String nombre, String apellido, long cuit, String razonSocial) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellido = apellido;
        this.razonSocial = razonSocial;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

}
