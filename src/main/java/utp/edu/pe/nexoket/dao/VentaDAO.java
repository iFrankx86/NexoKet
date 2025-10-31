package utp.edu.pe.nexoket.dao;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import utp.edu.pe.nexoket.db.MongoDBConnection;
import utp.edu.pe.nexoket.modelo.DetalleVenta;
import utp.edu.pe.nexoket.modelo.Venta;

/**
 * DAO para gestionar operaciones de Ventas en MongoDB
 * @author User
 */
public class VentaDAO {
    private MongoCollection<Document> collection;
    private MongoCollection<Document> registroVentaCollection;
    
    public VentaDAO() {
        MongoDatabase database = MongoDBConnection.getInstance().getDatabase();
        this.collection = database.getCollection("Ventas");
        this.registroVentaCollection = database.getCollection("RegistroVenta");
    }
    
    /**
     * Inserta una nueva venta en la base de datos
     * También guarda en RegistroVenta como historial
     */
    public boolean insertarVenta(Venta venta) {
        try {
            Document doc = ventaToDocument(venta);
            collection.insertOne(doc);
            System.out.println("✅ Venta registrada: " + venta.getNumeroVenta());
            
            // Guardar también en RegistroVenta (historial)
            insertarEnRegistroVenta(venta);
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al insertar venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Inserta la venta en la colección RegistroVenta como historial
     */
    private void insertarEnRegistroVenta(Venta venta) {
        try {
            Document registroDoc = ventaToDocument(venta);
            registroDoc.append("fechaRegistro", new Date());
            registroVentaCollection.insertOne(registroDoc);
            System.out.println("✅ Venta guardada en historial: " + venta.getNumeroVenta());
        } catch (Exception e) {
            System.err.println("⚠️ Error al guardar en RegistroVenta: " + e.getMessage());
            // No lanzamos la excepción para que no afecte la venta principal
        }
    }
    
    /**
     * Busca una venta por número de venta
     */
    public Venta buscarPorNumero(String numeroVenta) {
        try {
            Document doc = collection.find(new Document("numeroVenta", numeroVenta)).first();
            if (doc != null) {
                return documentToVenta(doc);
            }
        } catch (Exception e) {
            System.err.println("❌ Error al buscar venta: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Obtiene todas las ventas
     */
    public List<Venta> obtenerTodas() {
        List<Venta> ventas = new ArrayList<>();
        try {
            for (Document doc : collection.find()) {
                ventas.add(documentToVenta(doc));
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener ventas: " + e.getMessage());
            e.printStackTrace();
        }
        return ventas;
    }
    
    /**
     * Obtiene ventas por fecha
     */
    public List<Venta> obtenerPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        List<Venta> ventas = new ArrayList<>();
        try {
            Date inicio = Date.from(fechaInicio.atZone(ZoneId.systemDefault()).toInstant());
            Date fin = Date.from(fechaFin.atZone(ZoneId.systemDefault()).toInstant());
            
            Document query = new Document("fecha", 
                new Document("$gte", inicio).append("$lte", fin));
            
            for (Document doc : collection.find(query)) {
                ventas.add(documentToVenta(doc));
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener ventas por fecha: " + e.getMessage());
            e.printStackTrace();
        }
        return ventas;
    }
    
    /**
     * Obtiene ventas por cliente
     */
    public List<Venta> obtenerPorCliente(String dniCliente) {
        List<Venta> ventas = new ArrayList<>();
        try {
            Document query = new Document("cliente.dni", dniCliente);
            for (Document doc : collection.find(query)) {
                ventas.add(documentToVenta(doc));
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener ventas por cliente: " + e.getMessage());
            e.printStackTrace();
        }
        return ventas;
    }
    
    /**
     * Genera el siguiente número de venta
     */
    public String generarNumeroVenta() {
        try {
            long count = collection.countDocuments();
            return String.format("V%06d", count + 1);
        } catch (Exception e) {
            System.err.println("❌ Error al generar número de venta: " + e.getMessage());
            return "V000001";
        }
    }
    
    /**
     * Convierte una Venta a Document de MongoDB
     */
    private Document ventaToDocument(Venta venta) {
        Document doc = new Document();
        doc.append("numeroVenta", venta.getNumeroVenta());
        doc.append("fechaEmision", Date.from(venta.getFechaEmision().atZone(ZoneId.systemDefault()).toInstant()));
        doc.append("emisorBoleta", venta.getEmisorBoleta());
        doc.append("tipoPago", venta.getTipoPago());
        doc.append("efectivoRecibido", venta.getEfectivoRecibido());
        doc.append("vuelto", venta.getVuelto());
        doc.append("estado", venta.getEstado());
        
        // Cliente
        doc.append("dniCliente", venta.getDniCliente());
        doc.append("nombreCliente", venta.getNombreCliente());
        doc.append("telefonoCliente", venta.getTelefonoCliente());
        
        // Detalles
        List<Document> detallesDoc = new ArrayList<>();
        for (DetalleVenta detalle : venta.getDetalles()) {
            Document detalleDoc = new Document();
            detalleDoc.append("codigoProducto", detalle.getCodigoProducto());
            detalleDoc.append("nombreProducto", detalle.getNombreProducto());
            detalleDoc.append("categoria", detalle.getCategoria());
            detalleDoc.append("precioUnitario", detalle.getPrecioUnitario());
            detalleDoc.append("cantidad", detalle.getCantidad());
            detalleDoc.append("subtotal", detalle.getSubtotal());
            detallesDoc.add(detalleDoc);
        }
        doc.append("detalles", detallesDoc);
        
        // Totales
        doc.append("subtotal", venta.getSubtotal());
        doc.append("igv", venta.getIgv());
        doc.append("total", venta.getTotal());
        
        return doc;
    }
    
    /**
     * Convierte un Document de MongoDB a Venta
     */
    private Venta documentToVenta(Document doc) {
        Venta venta = new Venta();
        venta.setNumeroVenta(doc.getString("numeroVenta"));
        
        Date fecha = doc.getDate("fechaEmision");
        if (fecha != null) {
            venta.setFechaEmision(LocalDateTime.ofInstant(fecha.toInstant(), ZoneId.systemDefault()));
        }
        
        venta.setEmisorBoleta(doc.getString("emisorBoleta"));
        venta.setTipoPago(doc.getString("tipoPago"));
        venta.setEfectivoRecibido(doc.getDouble("efectivoRecibido"));
        venta.setVuelto(doc.getDouble("vuelto"));
        venta.setEstado(doc.getString("estado"));
        
        // Cliente
        venta.setDniCliente(doc.getString("dniCliente"));
        venta.setNombreCliente(doc.getString("nombreCliente"));
        venta.setTelefonoCliente(doc.getString("telefonoCliente"));
        
        // Detalles
        List<Document> detallesDoc = (List<Document>) doc.get("detalles");
        if (detallesDoc != null) {
            for (Document detalleDoc : detallesDoc) {
                DetalleVenta detalle = new DetalleVenta();
                detalle.setCodigoProducto(detalleDoc.getString("codigoProducto"));
                detalle.setNombreProducto(detalleDoc.getString("nombreProducto"));
                detalle.setCategoria(detalleDoc.getString("categoria"));
                detalle.setPrecioUnitario(detalleDoc.getDouble("precioUnitario"));
                detalle.setCantidad(detalleDoc.getInteger("cantidad"));
                detalle.setSubtotal(detalleDoc.getDouble("subtotal"));
                venta.agregarDetalle(detalle);
            }
        }
        
        // Totales (ya están calculados en agregarDetalle)
        venta.setSubtotal(doc.getDouble("subtotal"));
        venta.setIgv(doc.getDouble("igv"));
        venta.setTotal(doc.getDouble("total"));
        
        return venta;
    }
    
    /**
     * Elimina una venta por número
     */
    public boolean eliminarVenta(String numeroVenta) {
        try {
            collection.deleteOne(new Document("numeroVenta", numeroVenta));
            System.out.println("✅ Venta eliminada: " + numeroVenta);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error al eliminar venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
