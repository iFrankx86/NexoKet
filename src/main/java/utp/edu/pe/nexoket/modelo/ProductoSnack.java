// === PRODUCTOS SNACKS ===
package utp.edu.pe.nexoket.modelo;

public class ProductoSnack extends ProductoBase {
    
    public ProductoSnack(String codigo, String nombre, String marca) {
        super(codigo, nombre, marca);
    }
    
    @Override
    public String getCategoria() {
        return "Snacks";
    }
    
    @Override
    public double getMargenGananciaSugerido() {
        return 0.40; // 40% de ganancia para snacks
    }
    
    @Override
    public boolean necesitaIGV() {
        return true; // Snacks SÍ pagan IGV
    }
}
