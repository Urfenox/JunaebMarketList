package com.crizacio.sodexomarketlist;

public class Producto {
    private String nombre;
    private int cantidad;
    private int precio;
    private String imagen;

    public Producto() {}
    public Producto(String nombre, int cantidad, int precio, String imagen) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.imagen = imagen;
    }
    public String getNombre() {
        return nombre;
    }
    public int getCantidad() {
        return cantidad;
    }
    public int getPrecio() {
        return precio;
    }
    public String getImagen() {
        return imagen;
    }
}