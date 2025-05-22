package com.example.catalogo.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

// Adicione os imports de Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.ExpiredJwtException; // Importe explicitamente
import io.jsonwebtoken.MalformedJwtException; // Importe explicitamente
import io.jsonwebtoken.security.SignatureException; // Importe explicitamente

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    // Carrega a chave do application.properties
    @Value("${jwt.secret}")
    private String SECRET_STRING_FROM_PROPERTIES; // Nome para diferenciar do atributo final

    private final Key SIGNING_KEY; // A chave real usada para assinar/validar

    private final long EXPIRACAO = 1000 * 60 * 60 * 24; // 24 horas

    // Construtor que recebe a chave injetada pelo Spring
    // Esta é a forma correta de inicializar a chave uma única vez ao iniciar o serviço
    public JwtService(@Value("${jwt.secret}") String secretFromProperties) {
        this.SECRET_STRING_FROM_PROPERTIES = secretFromProperties; // Atribui para o campo
        try {
            // Decodifica a string Base64 para bytes e gera a chave HMAC
            this.SIGNING_KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretFromProperties));
            logger.info("JWT Service inicializado com chave secreta carregada do application.properties.");
        } catch (IllegalArgumentException e) {
            logger.error("ERRO GRAVE: Chave secreta JWT em application.properties é inválida (não é Base64 válida ou está corrompida).", e);
            // É crítico que a aplicação não inicie se a chave for inválida.
            throw new RuntimeException("Falha ao inicializar JwtService: Chave secreta JWT inválida.", e);
        }
    }

    public String gerarToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRACAO))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256) // USANDO A CHAVE INICIALIZADA
                .compact();
    }

    public String validarToken(String token) {
        try {
            logger.info("Tentando validar token: {}", token);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY) // USANDO A CHAVE INICIALIZADA
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            logger.info("Token validado com sucesso. Username: {}", username);
            return username;
        } catch (ExpiredJwtException e) {
            logger.error("ERRO JWT: Token expirado. Detalhes: {}", e.getMessage());
            return null;
        } catch (MalformedJwtException e) {
            logger.error("ERRO JWT: Token malformado. Detalhes: {}", e.getMessage());
            return null;
        } catch (SignatureException e) {
            logger.error("ERRO JWT: Assinatura inválida. Certifique-se que a chave no application.properties é a mesma usada para gerar o token. Detalhes: {}", e.getMessage());
            logger.error("Chave secreta carregada: {}", SECRET_STRING_FROM_PROPERTIES); // Loga a chave para verificação
            return null;
        } catch (IllegalArgumentException e) {
            logger.error("ERRO JWT: Token vazio ou nulo. Detalhes: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("ERRO JWT: Erro inesperado durante a validação do token. Detalhes: {}", e.getMessage());
            return null;
        }
    }
}