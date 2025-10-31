// === PRODUCTOS ABARROTES ===
package utp.edu.pe.nexoket.modelo;

public class ProductoAbarrote extends ProductoBase {
    
    public ProductoAbarrote(String codigo, String nombre, String marca) {
        super(codigo, nombre, marca);
    }
    
    @Override
    public String getCategoria() {
        return "Abarrotes";
    }
    
    @Override
    public double getMargenGananciaSugerido() {
        return 0.18; // 18% de ganancia para abarrotes
    }
    
    @Override
    public boolean necesitaIGV() {
        return false; // Abarrotes básicos NO pagan IGV
    }
}