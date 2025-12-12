// === PRODUCTOS BEBIDAS ===
package utp.edu.pe.nexoket.modelo;

public class ProductoBebida extends ProductoBase {
    
    public ProductoBebida (String codigo, String nombre, String marca) {
        super(codigo, nombre, marca);
    }
    
    @Override
    public String getCategoria() {
        return "Bebidas";
    }
    
    @Override
    public double getMargenGananciaSugerido() {
        return 0.33; // 33% de ganancia para bebidas
    }
    
    @Override
    public boolean necesitaIGV() {
        return true; // Bebidas SÍ pagan IGV
    }
}