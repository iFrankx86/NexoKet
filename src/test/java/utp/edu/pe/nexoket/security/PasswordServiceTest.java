package utp.edu.pe.nexoket.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests unitarios para PasswordService
 * Valida encriptación, verificación y validación de contraseñas
 * 
 * @author NexoKet Team
 */
@DisplayName("Tests para PasswordService")
class PasswordServiceTest {
    
    @Test
    @DisplayName("Debe encriptar contraseña correctamente")
    void testHashPassword() {
        String plainPassword = "Password123!";
        String hashed = PasswordService.hashPassword(plainPassword);
        
        assertNotNull(hashed);
        assertNotEquals(plainPassword, hashed);
        assertTrue(hashed.startsWith("$2a$")); // BCrypt prefix
    }
    
    @Test
    @DisplayName("Debe generar hashes diferentes para la misma contraseña")
    void testHashPasswordGeneraDiferentesSalts() {
        String plainPassword = "Password123!";
        String hash1 = PasswordService.hashPassword(plainPassword);
        String hash2 = PasswordService.hashPassword(plainPassword);
        
        assertNotEquals(hash1, hash2, "Cada hash debe tener un salt único");
    }
    
    @Test
    @DisplayName("Debe verificar contraseña correcta")
    void testVerifyPasswordCorrecto() {
        String plainPassword = "Password123!";
        String hashed = PasswordService.hashPassword(plainPassword);
        
        assertTrue(PasswordService.verifyPassword(plainPassword, hashed));
    }
    
    @Test
    @DisplayName("Debe rechazar contraseña incorrecta")
    void testVerifyPasswordIncorrecto() {
        String plainPassword = "Password123!";
        String hashed = PasswordService.hashPassword(plainPassword);
        
        assertFalse(PasswordService.verifyPassword("WrongPassword", hashed));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si contraseña está vacía")
    void testHashPasswordVacia() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordService.hashPassword("");
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordService.hashPassword(null);
        });
    }
    
    @Test
    @DisplayName("Debe validar contraseña fuerte correctamente")
    void testPasswordFuerte() {
        assertTrue(PasswordService.isStrongPassword("Password123!"));
        assertTrue(PasswordService.isStrongPassword("MyS3cur3P@ss"));
        assertTrue(PasswordService.isStrongPassword("Abcd1234#"));
    }
    
    @Test
    @DisplayName("Debe rechazar contraseña sin mayúsculas")
    void testPasswordSinMayusculas() {
        assertFalse(PasswordService.isStrongPassword("password123!"));
    }
    
    @Test
    @DisplayName("Debe rechazar contraseña sin minúsculas")
    void testPasswordSinMinusculas() {
        assertFalse(PasswordService.isStrongPassword("PASSWORD123!"));
    }
    
    @Test
    @DisplayName("Debe rechazar contraseña sin números")
    void testPasswordSinNumeros() {
        assertFalse(PasswordService.isStrongPassword("Password!"));
    }
    
    @Test
    @DisplayName("Debe rechazar contraseña sin símbolos")
    void testPasswordSinSimbolos() {
        assertFalse(PasswordService.isStrongPassword("Password123"));
    }
    
    @Test
    @DisplayName("Debe rechazar contraseña menor a 8 caracteres")
    void testPasswordCorta() {
        assertFalse(PasswordService.isStrongPassword("Pass1!"));
    }
    
    @Test
    @DisplayName("Debe validar contraseña y retornar null si es válida")
    void testValidatePasswordValida() {
        String resultado = PasswordService.validatePassword("Password123!");
        assertNull(resultado, "Contraseña válida debe retornar null");
    }
    
    @Test
    @DisplayName("Debe retornar mensaje de error para contraseña inválida")
    void testValidatePasswordInvalida() {
        String resultado = PasswordService.validatePassword("weak");
        assertNotNull(resultado);
        assertTrue(resultado.contains("8 caracteres"));
    }
    
    @Test
    @DisplayName("Debe retornar requisitos de contraseña")
    void testGetPasswordRequirements() {
        String requisitos = PasswordService.getPasswordRequirements();
        assertNotNull(requisitos);
        assertTrue(requisitos.contains("8 caracteres"));
        assertTrue(requisitos.contains("mayúscula"));
        assertTrue(requisitos.contains("minúscula"));
        assertTrue(requisitos.contains("número"));
        assertTrue(requisitos.contains("símbolo"));
    }
}
