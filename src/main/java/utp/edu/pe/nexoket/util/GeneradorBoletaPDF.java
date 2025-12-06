package utp.edu.pe.nexoket.util;

import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import utp.edu.pe.nexoket.modelo.DetalleVenta;
import utp.edu.pe.nexoket.modelo.Venta;

// Imports de iText 5
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

/**
 * Clase utilitaria para generar boletas de venta en formato PDF
 * @author User
 */
public class GeneradorBoletaPDF {
    
    private static final Font FONT_TITLE = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font FONT_HEADER = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font FONT_NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font FONT_SMALL = FontFactory.getFont(FontFactory.HELVETICA, 8);
    
    /**
     * Genera un PDF de la boleta de venta
     * @param venta Objeto Venta con toda la información
     * @param rutaDestino Ruta donde se guardará el PDF
     * @return true si se generó correctamente, false en caso contrario
     */
    public static boolean generarBoletaPDF(Venta venta, String rutaDestino) {
        Document document = new Document(PageSize.A4);
        
        try {
            PdfWriter.getInstance(document, new FileOutputStream(rutaDestino));
            document.open();
            
            // Agregar contenido al PDF
            agregarEncabezado(document, venta);
            agregarInformacionCliente(document, venta);
            agregarTablaProductos(document, venta);
            agregarTotales(document, venta);
            agregarPieDePagina(document);
            
            document.close();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            if (document.isOpen()) {
                document.close();
            }
            return false;
        }
    }
    
    /**
     * Agrega el encabezado del documento
     */
    private static void agregarEncabezado(Document document, Venta venta) throws DocumentException {
        // Título principal
        Paragraph titulo = new Paragraph("NEXOKET", FONT_TITLE);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(5);
        document.add(titulo);
        
        // Subtítulo
        Paragraph subtitulo = new Paragraph("BOLETA DE VENTA", FONT_HEADER);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(20);
        document.add(subtitulo);
        
        // Información de la venta
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(15);
        
        agregarCeldaInfo(infoTable, "Número de Boleta:", venta.getNumeroVenta());
        agregarCeldaInfo(infoTable, "Fecha:", venta.getFechaEmision().format(formatter));
        agregarCeldaInfo(infoTable, "Vendedor:", venta.getEmisorBoleta());
        agregarCeldaInfo(infoTable, "Tipo de Pago:", venta.getTipoPago());
        
        document.add(infoTable);
    }
    
    /**
     * Agrega información del cliente si existe
     */
    private static void agregarInformacionCliente(Document document, Venta venta) throws DocumentException {
        if (venta.getDniCliente() != null && !venta.getDniCliente().isEmpty()) {
            // Línea separadora
            LineSeparator line = new LineSeparator();
            document.add(new Chunk(line));
            document.add(Chunk.NEWLINE);
            
            Paragraph clienteTitulo = new Paragraph("INFORMACIÓN DEL CLIENTE", FONT_HEADER);
            clienteTitulo.setSpacingAfter(10);
            document.add(clienteTitulo);
            
            PdfPTable clienteTable = new PdfPTable(2);
            clienteTable.setWidthPercentage(100);
            clienteTable.setSpacingAfter(15);
            
            agregarCeldaInfo(clienteTable, "DNI:", venta.getDniCliente());
            agregarCeldaInfo(clienteTable, "Nombre:", venta.getNombreCliente());
            
            if (venta.getTelefonoCliente() != null && !venta.getTelefonoCliente().isEmpty()) {
                agregarCeldaInfo(clienteTable, "Teléfono:", venta.getTelefonoCliente());
            }
            
            document.add(clienteTable);
        }
    }
    
    /**
     * Agrega la tabla de productos
     */
    private static void agregarTablaProductos(Document document, Venta venta) throws DocumentException {
        // Línea separadora
        LineSeparator line = new LineSeparator();
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
        
        Paragraph productosTitulo = new Paragraph("DETALLE DE PRODUCTOS", FONT_HEADER);
        productosTitulo.setSpacingAfter(10);
        document.add(productosTitulo);
        
        // Crear tabla con 5 columnas
        PdfPTable tabla = new PdfPTable(5);
        tabla.setWidthPercentage(100);
        tabla.setSpacingAfter(15);
        
        // Establecer anchos de columnas
        float[] columnWidths = {1.5f, 3f, 1f, 1.5f, 1.5f};
        tabla.setWidths(columnWidths);
        
        // Encabezados de la tabla
        agregarCeldaEncabezado(tabla, "Código");
        agregarCeldaEncabezado(tabla, "Descripción");
        agregarCeldaEncabezado(tabla, "Cant.");
        agregarCeldaEncabezado(tabla, "P. Unit.");
        agregarCeldaEncabezado(tabla, "Subtotal");
        
        // Agregar productos
        for (DetalleVenta detalle : venta.getDetalles()) {
            agregarCeldaProducto(tabla, detalle.getCodigoProducto());
            agregarCeldaProducto(tabla, detalle.getNombreProducto());
            agregarCeldaProducto(tabla, String.valueOf(detalle.getCantidad()));
            agregarCeldaProducto(tabla, String.format("S/ %.2f", detalle.getPrecioUnitario()));
            agregarCeldaProducto(tabla, String.format("S/ %.2f", detalle.getSubtotal()));
        }
        
        document.add(tabla);
    }
    
    /**
     * Agrega los totales de la venta
     */
    private static void agregarTotales(Document document, Venta venta) throws DocumentException {
        // Línea separadora
        LineSeparator line = new LineSeparator();
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
        
        // Tabla de totales (alineada a la derecha)
        PdfPTable totalesTable = new PdfPTable(2);
        totalesTable.setWidthPercentage(50);
        totalesTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalesTable.setSpacingAfter(15);
        
        float[] columnWidths = {2f, 1.5f};
        totalesTable.setWidths(columnWidths);
        
        agregarCeldaTotal(totalesTable, "Subtotal:", String.format("S/ %.2f", venta.getSubtotal()));
        agregarCeldaTotal(totalesTable, "IGV (18%):", String.format("S/ %.2f", venta.getIgv()));
        
        // Total con fondo
        PdfPCell cellTotalLabel = new PdfPCell(new Phrase("TOTAL:", FONT_HEADER));
        cellTotalLabel.setBorder(Rectangle.NO_BORDER);
        cellTotalLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellTotalLabel.setPadding(5);
        cellTotalLabel.setBackgroundColor(new BaseColor(230, 230, 230));
        totalesTable.addCell(cellTotalLabel);
        
        PdfPCell cellTotalValue = new PdfPCell(new Phrase(String.format("S/ %.2f", venta.getTotal()), FONT_HEADER));
        cellTotalValue.setBorder(Rectangle.NO_BORDER);
        cellTotalValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellTotalValue.setPadding(5);
        cellTotalValue.setBackgroundColor(new BaseColor(230, 230, 230));
        totalesTable.addCell(cellTotalValue);
        
        document.add(totalesTable);
        
        // Información de pago
        document.add(Chunk.NEWLINE);
        PdfPTable pagoTable = new PdfPTable(2);
        pagoTable.setWidthPercentage(50);
        pagoTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        pagoTable.setSpacingAfter(15);
        pagoTable.setWidths(columnWidths);
        
        agregarCeldaTotal(pagoTable, "Efectivo recibido:", String.format("S/ %.2f", venta.getEfectivoRecibido()));
        agregarCeldaTotal(pagoTable, "Vuelto:", String.format("S/ %.2f", venta.getVuelto()));
        
        document.add(pagoTable);
    }
    
    /**
     * Agrega el pie de página
     */
    private static void agregarPieDePagina(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);
        
        // Línea separadora
        LineSeparator line = new LineSeparator();
        document.add(new Chunk(line));
        document.add(Chunk.NEWLINE);
        
        Paragraph gracias = new Paragraph("¡GRACIAS POR SU COMPRA!", FONT_HEADER);
        gracias.setAlignment(Element.ALIGN_CENTER);
        gracias.setSpacingAfter(10);
        document.add(gracias);
        
        Paragraph info = new Paragraph("Este documento es una representación impresa de la boleta de venta", FONT_SMALL);
        info.setAlignment(Element.ALIGN_CENTER);
        document.add(info);
    }
    
    // Métodos auxiliares para agregar celdas
    
    private static void agregarCeldaInfo(PdfPTable tabla, String etiqueta, String valor) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(etiqueta, FONT_HEADER));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setPadding(5);
        tabla.addCell(cellLabel);
        
        PdfPCell cellValue = new PdfPCell(new Phrase(valor, FONT_NORMAL));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setPadding(5);
        tabla.addCell(cellValue);
    }
    
    private static void agregarCeldaEncabezado(PdfPTable tabla, String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FONT_HEADER));
        cell.setBackgroundColor(new BaseColor(52, 152, 219));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        cell.setBorderColor(BaseColor.WHITE);
        tabla.addCell(cell);
    }
    
    private static void agregarCeldaProducto(PdfPTable tabla, String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FONT_NORMAL));
        cell.setPadding(5);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        tabla.addCell(cell);
    }
    
    private static void agregarCeldaTotal(PdfPTable tabla, String etiqueta, String valor) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(etiqueta, FONT_NORMAL));
        cellLabel.setBorder(Rectangle.NO_BORDER);
        cellLabel.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellLabel.setPadding(5);
        tabla.addCell(cellLabel);
        
        PdfPCell cellValue = new PdfPCell(new Phrase(valor, FONT_NORMAL));
        cellValue.setBorder(Rectangle.NO_BORDER);
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellValue.setPadding(5);
        tabla.addCell(cellValue);
    }
}
