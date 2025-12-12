package utp.edu.pe.nexoket.dao;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import utp.edu.pe.nexoket.db.MongoDBConnection;
import utp.edu.pe.nexoket.modelo.Producto;

public class ProductoDAO {
    private final MongoCollection<Document> collection;

    public ProductoDAO() {
        // Conectar directamente a la colección "Productos" (plural)
        collection = MongoDBConnection.getInstance().getDatabase().getCollection("Productos");
        
        try {
            long count = collection.countDocuments();
            System.out.println("DEBUG DAO: Conectado a colección 'Productos'. Documentos encontrados: " + count);
        } catch (Exception e) {
            System.out.println("DEBUG DAO: Error al verificar colección: " + e.getMessage());
        }
    }

    // Registrar producto
    public void registrarProducto(Producto producto) {
        Document doc = new Document("codigo", producto.getCodigo())
                .append("nombre", producto.getNombre())
                .append("descripcion", producto.getDescripcion())
                .append("marca", producto.getMarca())
                .append("categoria", producto.getCategoria())
                .append("subcategoria", producto.getSubcategoria())
                .append("unidadMedida", producto.getUnidadMedida())
                .append("cantidadPorUnidad", producto.getCantidadPorUnidad())
                .append("precio", producto.getPrecio())
                .append("stock", producto.getStock())
                .append("stockMinimo", producto.getStockMinimo())
                .append("proveedor", producto.getProveedor())
                .append("fechaVencimiento", producto.getFechaVencimiento())
                .append("ubicacion", producto.getUbicacion())
                .append("aplicaIGV", producto.isAplicaIGV())
                .append("activo", producto.isActivo())
                .append("fechaCreacion", producto.getFechaCreacion())
                .append("fechaActualizacion", producto.getFechaActualizacion());
        collection.insertOne(doc);
    }

    // Consultar producto por código
    public Producto consultarProducto(String codigo) {
        Document query = new Document("codigo", codigo);
        Document resultado = collection.find(query).first();

        if (resultado != null) {
            return documentToProducto(resultado);
        }
        return null;
    }
    
    // Alias para compatibilidad con VentaFacade
    public Producto buscarPorCodigo(String codigo) {
        return consultarProducto(codigo);
    }

    // Consultar producto por nombre
    public Producto consultarProductoPorNombre(String nombre) {
        Document query = new Document("nombre", new Document("$regex", nombre).append("$options", "i"));
        Document resultado = collection.find(query).first();

        if (resultado != null) {
            return documentToProducto(resultado);
        }
        return null;
    }

    // Obtener todos los productos
    public List<Producto> obtenerTodosLosProductos() {
        List<Producto> productos = new ArrayList<>();
        try {
            System.out.println("DEBUG DAO: Iniciando búsqueda en colección 'Productos'...");
            
            // Verificar que la colección existe y tiene datos
            long totalDocs = collection.countDocuments();
            System.out.println("DEBUG DAO: Total de documentos en colección: " + totalDocs);
            
            if (totalDocs == 0) {
                System.out.println("DEBUG DAO: No se encontraron documentos en la colección");
                return productos;
            }
            
            FindIterable<Document> documentos = collection.find();
            
            int contador = 0;
            for (Document doc : documentos) {
                contador++;
                System.out.println("DEBUG DAO: Procesando documento " + contador + ": " + doc.getString("codigo"));
                
                Producto producto = documentToProducto(doc);
                if (producto != null) {
                    productos.add(producto);
                    System.out.println("DEBUG DAO: Producto agregado exitosamente: " + producto.getCodigo());
                } else {
                    System.out.println("DEBUG DAO: Error al convertir documento " + contador);
                }
            }
            
            System.out.println("DEBUG DAO: Total productos procesados: " + contador);
            System.out.println("DEBUG DAO: Total productos convertidos exitosamente: " + productos.size());
            
        } catch (Exception e) {
            System.out.println("DEBUG DAO: Error al obtener todos los productos: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    // Obtener productos activos
    public List<Producto> obtenerProductosActivos() {
        List<Producto> productos = new ArrayList<>();
        try {
            Document query = new Document("activo", true);
            FindIterable<Document> documentos = collection.find(query);
            
            System.out.println("DEBUG DAO: Buscando productos activos...");
            
            for (Document doc : documentos) {
                Producto producto = documentToProducto(doc);
                if (producto != null) {
                    productos.add(producto);
                    System.out.println("DEBUG DAO: Producto activo encontrado: " + producto.getCodigo());
                }
            }
            System.out.println("DEBUG DAO: Productos activos encontrados: " + productos.size());
        } catch (Exception e) {
            System.out.println("DEBUG DAO: Error al obtener productos activos: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    // Obtener productos por categoría
    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        List<Producto> productos = new ArrayList<>();
        Document query = new Document("categoria", new Document("$regex", categoria).append("$options", "i"))
                .append("activo", true);
        FindIterable<Document> documentos = collection.find(query);
        
        for (Document doc : documentos) {
            productos.add(documentToProducto(doc));
        }
        return productos;
    }

    // Obtener productos por categoría y subcategoría
    public List<Producto> obtenerProductosPorCategoriaYSubcategoria(String categoria, String subcategoria) {
        List<Producto> productos = new ArrayList<>();
        Document query = new Document("categoria", new Document("$regex", categoria).append("$options", "i"))
                .append("activo", true);
        
        if (subcategoria != null && !subcategoria.trim().isEmpty()) {
            query.append("subcategoria", new Document("$regex", subcategoria).append("$options", "i"));
        }
        
        FindIterable<Document> documentos = collection.find(query);
        for (Document doc : documentos) {
            productos.add(documentToProducto(doc));
        }
        return productos;
    }

    // Obtener productos por marca
    public List<Producto> obtenerProductosPorMarca(String marca) {
        List<Producto> productos = new ArrayList<>();
        Document query = new Document("marca", new Document("$regex", marca).append("$options", "i"))
                .append("activo", true);
        FindIterable<Document> documentos = collection.find(query);
        
        for (Document doc : documentos) {
            productos.add(documentToProducto(doc));
        }
        return productos;
    }

    // Obtener productos con stock bajo
    public List<Producto> obtenerProductosStockBajo() {
        List<Producto> productos = new ArrayList<>();
        List<Document> pipeline = new ArrayList<>();
        pipeline.add(new Document("$match", new Document("activo", true)));
        pipeline.add(new Document("$match", new Document("$expr", 
            new Document("$lte", List.of("$stock", "$stockMinimo")))));
        
        for (Document doc : collection.aggregate(pipeline)) {
            productos.add(documentToProducto(doc));
        }
        return productos;
    }

    // Actualizar producto
    public void actualizarProducto(String codigo, Producto productoActualizado) {
        System.out.println("=== DAO: Actualizando producto " + codigo + " ===");
        System.out.println("DAO: aplicaIGV = " + productoActualizado.isAplicaIGV());
        System.out.println("DAO: activo = " + productoActualizado.isActivo());
        
        Document query = new Document("codigo", codigo);
        Document nuevosDatos = new Document("nombre", productoActualizado.getNombre())
                .append("descripcion", productoActualizado.getDescripcion())
                .append("marca", productoActualizado.getMarca())
                .append("categoria", productoActualizado.getCategoria())
                .append("subcategoria", productoActualizado.getSubcategoria())
                .append("unidadMedida", productoActualizado.getUnidadMedida())
                .append("cantidadPorUnidad", productoActualizado.getCantidadPorUnidad())
                .append("precio", productoActualizado.getPrecio())
                .append("stock", productoActualizado.getStock())
                .append("stockMinimo", productoActualizado.getStockMinimo())
                .append("proveedor", productoActualizado.getProveedor())
                .append("fechaVencimiento", productoActualizado.getFechaVencimiento())
                .append("ubicacion", productoActualizado.getUbicacion())
                .append("aplicaIGV", productoActualizado.isAplicaIGV())
                .append("activo", productoActualizado.isActivo())
                .append("fechaActualizacion", new Date());
        
        System.out.println("DAO: Documento ANTES de enviar a MongoDB:");
        System.out.println("  - aplicaIGV en documento: " + nuevosDatos.get("aplicaIGV"));
        System.out.println("  - activo en documento: " + nuevosDatos.get("activo"));
        System.out.println("DAO: JSON completo: " + nuevosDatos.toJson());
        
        collection.updateOne(query, new Document("$set", nuevosDatos));
        System.out.println("DAO: updateOne() ejecutado");
        
        // VERIFICACIÓN INMEDIATA: Leer el documento que acabamos de actualizar
        Document docVerificacion = collection.find(query).first();
        if (docVerificacion != null) {
            System.out.println("DAO VERIFICACIÓN: Documento DESPUÉS de actualizar en MongoDB:");
            System.out.println("  - aplicaIGV en MongoDB: " + docVerificacion.get("aplicaIGV"));
            System.out.println("  - activo en MongoDB: " + docVerificacion.get("activo"));
        } else {
            System.err.println("DAO ERROR: No se pudo leer el documento después de actualizar");
        }
        
        System.out.println("DAO: Actualización completada");
    }

    // Actualizar solo el stock
    public void actualizarStock(String codigo, int nuevoStock) {
        Document query = new Document("codigo", codigo);
        Document nuevosDatos = new Document("stock", nuevoStock)
                .append("fechaActualizacion", new Date());
        collection.updateOne(query, new Document("$set", nuevosDatos));
    }

    // Reducir stock (para ventas)
    public boolean reducirStock(String codigo, int cantidad) {
        Producto producto = consultarProducto(codigo);
        if (producto != null && producto.tieneStockSuficiente(cantidad)) {
            actualizarStock(codigo, producto.getStock() - cantidad);
            return true;
        }
        return false;
    }

    // Aumentar stock (para compras)
    public void aumentarStock(String codigo, int cantidad) {
        Producto producto = consultarProducto(codigo);
        if (producto != null) {
            actualizarStock(codigo, producto.getStock() + cantidad);
        }
    }

    // Eliminar producto (lógico)
    public void eliminarProducto(String codigo) {
        Document query = new Document("codigo", codigo);
        Document nuevosDatos = new Document("activo", false)
                .append("fechaActualizacion", new Date());
        collection.updateOne(query, new Document("$set", nuevosDatos));
    }

    // Buscar productos por texto
    public List<Producto> buscarProductos(String texto) {
        List<Producto> productos = new ArrayList<>();
        Document query = new Document("$and", List.of(
            new Document("activo", true),
            new Document("$or", List.of(
                new Document("nombre", new Document("$regex", texto).append("$options", "i")),
                new Document("codigo", new Document("$regex", texto).append("$options", "i")),
                new Document("descripcion", new Document("$regex", texto).append("$options", "i")),
                new Document("marca", new Document("$regex", texto).append("$options", "i"))
            ))
        ));
        
        FindIterable<Document> documentos = collection.find(query);
        for (Document doc : documentos) {
            productos.add(documentToProducto(doc));
        }
        return productos;
    }

    // Obtener categorías distintas
    public List<String> obtenerCategorias() {
        List<String> categorias = new ArrayList<>();
        collection.distinct("categoria", String.class).into(categorias);
        categorias.removeIf(cat -> cat == null || cat.trim().isEmpty());
        return categorias;
    }

    // Obtener marcas distintas
    public List<String> obtenerMarcas() {
        List<String> marcas = new ArrayList<>();
        collection.distinct("marca", String.class).into(marcas);
        marcas.removeIf(marca -> marca == null || marca.trim().isEmpty());
        return marcas;
    }

    // Obtener subcategorías por categoría
    public List<String> obtenerSubcategoriasPorCategoria(String categoria) {
        List<String> subcategorias = new ArrayList<>();
        Document query = new Document("categoria", new Document("$regex", categoria).append("$options", "i"))
                .append("activo", true);
        collection.distinct("subcategoria", query, String.class).into(subcategorias);
        subcategorias.removeIf(sub -> sub == null || sub.trim().isEmpty());
        return subcategorias;
    }

    // Obtener unidades de medida distintas
    public List<String> obtenerUnidadesMedida() {
        List<String> unidades = new ArrayList<>();
        collection.distinct("unidadMedida", String.class).into(unidades);
        unidades.removeIf(unidad -> unidad == null || unidad.trim().isEmpty());
        return unidades;
    }

    // Método auxiliar para convertir Document a Producto
    private Producto documentToProducto(Document doc) {
        try {
            System.out.println("DEBUG DAO: Convirtiendo documento: " + doc.toJson());
            
            // Extraer campos con manejo de diferentes nombres y tipos
            String codigo = doc.getString("codigo");
            String nombre = doc.getString("nombre");
            String descripcion = doc.getString("descripcion");
            String marca = doc.getString("marca");
            String categoria = doc.getString("categoria");
            String subcategoria = doc.getString("subcategoria");
            String unidadMedida = doc.getString("unidadMedida");
            
            // Manejar cantidadPorUnidad como Integer o Double
            Integer cantidadPorUnidad = 1;
            Object cantidadObj = doc.get("cantidadPorUnidad");
            if (cantidadObj instanceof Integer) {
                cantidadPorUnidad = (Integer) cantidadObj;
            } else if (cantidadObj instanceof Double) {
                cantidadPorUnidad = ((Double) cantidadObj).intValue();
            }
            
            // Manejar precio como Double o Integer
            Double precio = 0.0;
            Object precioObj = doc.get("precioVenta");
            if (precioObj == null) {
                precioObj = doc.get("precio");
            }
            if (precioObj instanceof Double) {
                precio = (Double) precioObj;
            } else if (precioObj instanceof Integer) {
                precio = ((Integer) precioObj).doubleValue();
            }
            
            // Manejar stock como Integer o Double
            Integer stock = 0;
            Object stockObj = doc.get("stock");
            if (stockObj instanceof Integer) {
                stock = (Integer) stockObj;
            } else if (stockObj instanceof Double) {
                stock = ((Double) stockObj).intValue();
            }
            
            // Manejar stockMinimo
            Integer stockMinimo = 0;
            Object stockMinimoObj = doc.get("stockMinimo");
            if (stockMinimoObj == null) {
                stockMinimoObj = doc.get("stockminimo");
            }
            if (stockMinimoObj instanceof Integer) {
                stockMinimo = (Integer) stockMinimoObj;
            } else if (stockMinimoObj instanceof Double) {
                stockMinimo = ((Double) stockMinimoObj).intValue();
            }
            
            String proveedor = doc.getString("proveedor");
            String ubicacion = doc.getString("ubicacion");
            Date fechaVencimiento = doc.getDate("fechaVencimiento");
            Boolean aplicaIGV = doc.getBoolean("aplicaIGV");
            if (aplicaIGV == null) {
                Object igvObj = doc.get("aplicaIGV");
                if (igvObj instanceof String) {
                    String igvStr = ((String) igvObj).trim().toLowerCase();
                    aplicaIGV = !(igvStr.equals("deshabilitado") || igvStr.equals("false") || igvStr.equals("0"));
                } else {
                    aplicaIGV = true; // Por defecto aplica IGV si no se especifica
                }
            }
            
            System.out.println("DEBUG DAO: Datos extraídos - Código: " + codigo + ", Nombre: " + nombre + ", Precio: " + precio);
            
            Producto producto = new Producto(
                codigo, nombre, descripcion, marca, categoria,
                subcategoria, unidadMedida, cantidadPorUnidad,
                precio, stock, stockMinimo, proveedor,
                fechaVencimiento, ubicacion
            );
            
            // Manejar campo activo (aceptar boolean o string heredado)
            Boolean activo = doc.getBoolean("activo");
            if (activo == null) {
                Object actObj = doc.get("activo");
                if (actObj instanceof String) {
                    String actStr = ((String) actObj).trim().toLowerCase();
                    activo = !(actStr.equals("inactivo") || actStr.equals("false") || actStr.equals("0"));
                } else {
                    activo = true; // Por defecto activo si no está especificado
                }
            }
            producto.setActivo(activo);
            producto.setAplicaIGV(aplicaIGV);
            
            producto.setFechaCreacion(doc.getDate("fechaCreacion"));
            producto.setFechaActualizacion(doc.getDate("fechaActualizacion"));
            
            System.out.println("DEBUG DAO: Producto convertido exitosamente: " + producto.getCodigo());
            return producto;
            
        } catch (Exception e) {
            System.out.println("DEBUG DAO: Error al convertir documento: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}