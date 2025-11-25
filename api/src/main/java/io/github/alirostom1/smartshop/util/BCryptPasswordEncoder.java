package io.github.alirostom1.smartshop.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoder {
    private final BCrypt.Hasher hasher = BCrypt.withDefaults();

    public String encode(String rawPassword){
        return hasher.hashToString(12,rawPassword.toCharArray());
    }


    public boolean matches(String rawPassword,String hashedPassword){
        return BCrypt.verifyer().verify(rawPassword.toCharArray(),hashedPassword).verified;
    }
}
