package br.com.voltz.util;

public class CpfCnpjUtil {

    public static boolean isCpf(String cpfOuCnpj) {
        if (cpfOuCnpj == null) return false;
        String numeros = cpfOuCnpj.replaceAll("\\D", "");
        return numeros.length() == 11;
    }

    public static boolean isCnpj(String cpfOuCnpj) {
        if (cpfOuCnpj == null) return false;
        String numeros = cpfOuCnpj.replaceAll("\\D", "");
        return numeros.length() == 14;
    }
}
