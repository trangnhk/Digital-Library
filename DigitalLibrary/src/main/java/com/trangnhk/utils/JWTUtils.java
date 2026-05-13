/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.trangnhk.utils;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class JWTUtils {
    // Secret key
    private static final String SECRET = "DigitalLibrary_TEAM10_KieuTrang_ThanhVy";
    
    private static final long EXPIRATION_MS = 86400000;
    
    // ISSUER
    private static final String ISSUER = "smart-library-system";
    
    // generate token
    public static String generateToken(String username, String role) throws Exception{
        JWSSigner signer = new MACSigner(SECRET);
        
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("role", role)
                .issuer(ISSUER)
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .build();
        
        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
        
        signedJWT.sign(signer);
        
        return signedJWT.serialize();
        
    }
    
    public static boolean validateToken(String token) throws Exception{
        SignedJWT signedJWT = SignedJWT.parse(token);
        
        JWSVerifier verifier = new MACVerifier(SECRET);
        
        boolean verified = signedJWT.verify(verifier);
        
        if (!verified){
            return false;
        }
        
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        
        return expiration.after(new Date());
    }
    
    // GET Username
    public static String getUsername(String token)throws Exception{
        SignedJWT signedJWT = SignedJWT.parse(token);
        
        return signedJWT.getJWTClaimsSet().getSubject();
    }
    
    // GET Role
    public static String getRole(String token) throws Exception{
        SignedJWT signedJWT = SignedJWT.parse(token);
        
        return signedJWT.getJWTClaimsSet().getStringClaim("role");
    }
    
    // Validate
    public static String validateTokenAndGetUsername(String token) throws Exception{
        if (validateToken(token))
            return getUsername(token);
        
        return null;
    }
    
}









