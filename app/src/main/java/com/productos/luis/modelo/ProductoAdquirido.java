package com.productos.luis.modelo;

public class ProductoAdquirido {
    private String nombreProductoAd;
    private int cantidadAdquirida;
    private Producto producto; // Instancia de Producto

    // Constructor
    public ProductoAdquirido(String nombreProducto, int cantidadAdquirida, Producto producto) {
        this.nombreProductoAd = nombreProducto;
        this.cantidadAdquirida = cantidadAdquirida;
        this.producto = producto;
    }

    // Getters y setters
    public String getNombreProducto() {
        return nombreProductoAd;
    }

    public int getCantidadAdquirida() {
        return cantidadAdquirida;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setCantidadAdquirida(int cantidadAdquirida) {
        this.cantidadAdquirida = cantidadAdquirida;
    }
}
