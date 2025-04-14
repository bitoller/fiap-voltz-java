package br.com.voltz.util;

import org.mindrot.jbcrypt.BCrypt;

public class CriptografiaUtil {

    public static String hashSenha(String senha) {
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    public static boolean verificarSenha(String senha, String hash) {
        return BCrypt.checkpw(senha, hash);
    }
}
