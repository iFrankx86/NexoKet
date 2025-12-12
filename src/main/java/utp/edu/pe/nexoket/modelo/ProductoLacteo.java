package utp.edu.pe.nexoket.modelo;

public class ProductoLacteo extends ProductoBase {
    
    public ProductoLacteo(String codigo, String nombre, String marca) {
        super(codigo, nombre, marca);
    }
    
    @Override
    public String getCategoria() {
        return "Lácteos";
    }
    
    @Override
    public double getMargenGananciaSugerido() {
        return 0.22; // 22% de ganancia para lácteos
    }
    
    @Override
    public boolean necesitaIGV() {
        return false; // Lácteos NO pagan IGV en Perú
    }
}