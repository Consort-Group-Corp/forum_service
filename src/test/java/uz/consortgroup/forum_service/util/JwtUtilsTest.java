package uz.consortgroup.forum_service.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    private final String secretKey = "dGVzdFNlY3JldEtleVRlc3RTZWNyZXRLZXlUZXN0U2VjcmV0S2V5";
    private final String validToken = generateValidToken();
    private final String invalidToken = "invalid.token";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtils, "jetSecret", secretKey);
    }

    @Test
    void getUserIdFromJwt_Success() {
        UUID userId = jwtUtils.getUserIdFromJwt(validToken);
        assertNotNull(userId);
    }

    @Test
    void getUserIdFromJwt_InvalidToken() {
        assertThrows(io.jsonwebtoken.JwtException.class, () -> {
            jwtUtils.getUserIdFromJwt(invalidToken);
        });
    }

    @Test
    void getUsernameFromJwt_Success() {
        String username = jwtUtils.getUsernameFromJwt(validToken);
        assertNotNull(username);
    }

    @Test
    void getUsernameFromJwt_InvalidToken() {
        assertThrows(io.jsonwebtoken.JwtException.class, () -> {
            jwtUtils.getUsernameFromJwt(invalidToken);
        });
    }

    @Test
    void validateJwtToken_ValidToken() {
        assertTrue(jwtUtils.validateJwtToken(validToken));
    }

    @Test
    void validateJwtToken_InvalidToken() {
        assertFalse(jwtUtils.validateJwtToken(invalidToken));
    }

    @Test
    void validateJwtToken_NullToken() {
        assertFalse(jwtUtils.validateJwtToken(null));
    }

    @Test
    void validateJwtToken_EmptyToken() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    private String generateValidToken() {
        UUID userId = UUID.randomUUID();
        String username = "testuser";

        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId.toString())
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .compact();
    }
}