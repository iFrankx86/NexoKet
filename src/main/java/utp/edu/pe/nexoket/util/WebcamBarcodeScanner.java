package utp.edu.pe.nexoket.util;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Escáner de códigos de barras y QR usando la webcam
 * Utiliza ZXing para la decodificación y Webcam Capture para el acceso a la cámara
 * 
 * @author User
 */
public class WebcamBarcodeScanner extends JPanel implements Runnable, ThreadFactory {

    private static final long serialVersionUID = 1L;
    
    private Webcam webcam = null;
    private Executor executor = Executors.newSingleThreadExecutor(this);
    private MultiFormatReader reader = new MultiFormatReader();
    private String lastScannedCode = null;
    private ScannerCallback callback;
    private boolean scanning = false;

    /**
     * Interface para callback cuando se detecta un código
     */
    public interface ScannerCallback {
        void onCodeScanned(String code, BarcodeFormat format);
    }

    /**
     * Constructor del escáner
     */
    public WebcamBarcodeScanner() {
        super();
        setLayout(new BorderLayout());
        // Usar java.awt.Dimension explícitamente para evitar conflicto con com.google.zxing.Dimension
        setPreferredSize(new java.awt.Dimension(640, 480));
    }

    /**
     * Inicia el escáner con la webcam
     */
    public void startScanner(ScannerCallback callback) {
        this.callback = callback;
        
        try {
            // Obtener la webcam predeterminada
            webcam = Webcam.getDefault();
            
            if (webcam == null) {
                JOptionPane.showMessageDialog(this,
                    "No se detectó ninguna webcam en el sistema.\n" +
                    "Por favor, conecte una cámara e intente nuevamente.",
                    "Webcam no encontrada",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Configurar resolución
            webcam.setViewSize(WebcamResolution.VGA.getSize());
            
            // Abrir la webcam
            webcam.open();
            
            // Iniciar el hilo de escaneo
            scanning = true;
            executor.execute(this);
            
            System.out.println("Webcam iniciada: " + webcam.getName());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al iniciar la webcam:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Detiene el escáner y libera la webcam
     */
    public void stopScanner() {
        scanning = false;
        
        if (webcam != null && webcam.isOpen()) {
            try {
                webcam.close();
                System.out.println("Webcam cerrada correctamente");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hilo principal de escaneo
     */
    @Override
    public void run() {
        do {
            try {
                Thread.sleep(100); // Delay para no saturar CPU
                
                if (webcam != null && webcam.isOpen()) {
                    // Capturar imagen de la webcam
                    BufferedImage image = webcam.getImage();
                    
                    if (image == null) {
                        continue;
                    }
                    
                    // Mostrar la imagen en el panel
                    paintImage(image);
                    
                    // Intentar decodificar código de barras
                    Result result = decodeImage(image);
                    
                    if (result != null) {
                        String code = result.getText();
                        BarcodeFormat format = result.getBarcodeFormat();
                        
                        // Solo procesar si es un código diferente al último
                        if (!code.equals(lastScannedCode)) {
                            lastScannedCode = code;
                            
                            System.out.println("Código detectado: " + code + " [" + format + "]");
                            
                            // Llamar al callback en el EDT
                            SwingUtilities.invokeLater(() -> {
                                if (callback != null) {
                                    callback.onCodeScanned(code, format);
                                }
                            });
                            
                            // Pequeña pausa después de detectar un código
                            Thread.sleep(2000);
                        }
                    }
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            
        } while (scanning);
    }

    /**
     * Decodifica un código de barras de una imagen
     */
    private Result decodeImage(BufferedImage image) {
        if (image == null) {
            return null;
        }
        
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            
            return reader.decode(bitmap);
            
        } catch (NotFoundException e) {
            // No se encontró código en esta imagen (normal)
            return null;
        } catch (Exception e) {
            // Otros errores
            return null;
        }
    }

    /**
     * Dibuja la imagen de la webcam en el panel
     */
    private void paintImage(BufferedImage image) {
        if (image != null) {
            Graphics g = getGraphics();
            if (g != null) {
                // Escalar imagen al tamaño del panel
                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            }
        }
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, "Webcam Scanner Thread");
        t.setDaemon(true);
        return t;
    }

    /**
     * Verifica si hay webcams disponibles
     */
    public static boolean hasWebcam() {
        return Webcam.getDefault() != null;
    }

    /**
     * Obtiene la lista de webcams disponibles
     */
    public static java.util.List<Webcam> getAvailableWebcams() {
        return Webcam.getWebcams();
    }

    /**
     * Reinicia el último código escaneado (para permitir re-escanear el mismo)
     */
    public void resetLastCode() {
        lastScannedCode = null;
    }

    /**
     * Verifica si el escáner está activo
     */
    public boolean isScanning() {
        return scanning;
    }

    /**
     * Obtiene la webcam actual
     */
    public Webcam getWebcam() {
        return webcam;
    }
}