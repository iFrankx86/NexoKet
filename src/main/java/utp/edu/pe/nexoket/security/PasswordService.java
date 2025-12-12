package utp.edu.pe.nexoket.security;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio de encriptación de contraseñas usando BCrypt
 * Proporciona métodos seguros para hash y verificación de contraseñas
 * 
 * @author NexoKet Team
 */
public class PasswordService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordService.class);
    private static final int SALT_ROUNDS = 12;
    
    /**
     * Encripta una contraseña usando BCrypt
     * @param plainPassword Contraseña en texto plano
     * @return Hash de la contraseña
     * @throws IllegalArgumentException si la contraseña está vacía
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        
        try {
            String hashed = BCrypt.hashpw(plainPassword, BCrypt.gensalt(SALT_ROUNDS));
            logger.debug("Contraseña encriptada exitosamente");
            return hashed;
        } catch (Exception e) {
            logger.error("Error al encriptar contraseña", e);
            throw new RuntimeException("Error en encriptación", e);
        }
    }
    
    /**
     * Verifica si una contraseña coincide con su hash
     * @param plainPassword Contraseña en texto plano
     * @param hashedPassword Hash almacenado
     * @return true si coinciden, false en caso contrario
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            boolean matches = BCrypt.checkpw(plainPassword, hashedPassword);
            logger.debug("Verificación de contraseña: {}", matches ? "exitosa" : "fallida");
            return matches;
        } catch (Exception e) {
            logger.error("Error al verificar contraseña", e);
            return false;
        }
    }
    
    /**
     * Valida si una contraseña cumple con los requisitos de fortaleza
     * Requisitos: Mínimo 8 caracteres, mayúsculas, minúsculas, números y símbolos
     * 
     * @param password Contraseña a validar
     * @return true si es fuerte, false en caso contrario
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>/?].*");
        
        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
    
    /**
     * Obtiene un mensaje descriptivo de los requisitos de contraseña
     * @return Mensaje con los requisitos
     */
    public static String getPasswordRequirements() {
        return "La contraseña debe tener:\n" +
               "- Mínimo 8 caracteres\n" +
               "- Al menos una letra mayúscula\n" +
               "- Al menos una letra minúscula\n" +
               "- Al menos un número\n" +
               "- Al menos un símbolo especial (!@#$%^&*...)";
    }
    
    /**
     * Valida una contraseña y retorna mensaje de error si no cumple requisitos
     * @param password Contraseña a validar
     * @return null si es válida, mensaje de error en caso contrario
     */
    public static String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "La contraseña no puede estar vacía";
        }
        
        if (password.length() < 8) {
            return "La contraseña debe tener al menos 8 caracteres";
        }
        
        if (!password.matches(".*[A-Z].*")) {
            return "La contraseña debe contener al menos una letra mayúscula";
        }
        
        if (!password.matches(".*[a-z].*")) {
            return "La contraseña debe contener al menos una letra minúscula";
        }
        
        if (!password.matches(".*\\d.*")) {
            return "La contraseña debe contener al menos un número";
        }
        
        if (!password.matches(".*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return "La contraseña debe contener al menos un símbolo especial";
        }
        
        return null; // Contraseña válida
    }
}
