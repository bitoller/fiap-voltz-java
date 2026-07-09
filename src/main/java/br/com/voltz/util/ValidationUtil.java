package br.com.voltz.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isStrongPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static String getPasswordRequirements() {
        return """
                The password must contain:
                - Minimum of 8 characters
                - At least one uppercase letter
                - At least one lowercase letter
                - At least one number
                - At least one special character (@#$%^&+=!)
                """;
    }

    public static boolean validateCpf(String cpf) {
        if (cpf == null || cpf.length() != 11 || !cpf.matches("\\d+")) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int sum = 0;

        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }

        int firstDigit = 11 - (sum % 11);

        if (firstDigit > 9)
            firstDigit = 0;

        if (firstDigit != (cpf.charAt(9) - '0')) {
            return false;
        }

        sum = 0;

        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }

        int secondDigit = 11 - (sum % 11);

        if (secondDigit > 9)
            secondDigit = 0;

        if (secondDigit != (cpf.charAt(10) - '0')) {
            return false;
        }

        return true;
    }

    public static boolean validateCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() != 14 || !cnpj.matches("\\d+")) {
            return false;
        }

        if (cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        int[] weights = { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

        int sum = 0;

        for (int i = 0; i < 12; i++) {
            sum += (cnpj.charAt(i) - '0') * weights[i];
        }

        int firstDigit = 11 - (sum % 11);

        if (firstDigit > 9)
            firstDigit = 0;

        if (firstDigit != (cnpj.charAt(12) - '0')) {
            return false;
        }

        weights = new int[] { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };

        sum = 0;

        for (int i = 0; i < 13; i++) {
            sum += (cnpj.charAt(i) - '0') * weights[i];
        }

        int secondDigit = 11 - (sum % 11);

        if (secondDigit > 9)
            secondDigit = 0;

        if (secondDigit != (cnpj.charAt(13) - '0')) {
            return false;
        }

        return true;
    }

    public static boolean validateCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null) {
            return false;
        }

        if (cpfCnpj.length() == 11) {
            return validateCpf(cpfCnpj);
        } else if (cpfCnpj.length() == 14) {
            return validateCnpj(cpfCnpj);
        }

        return false;
    }

    public static String formatCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.isEmpty()) {
            return cpfCnpj;
        }

        if (cpfCnpj.length() == 11) {
            return cpfCnpj.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        } else if (cpfCnpj.length() == 14) {
            return cpfCnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
        }

        return cpfCnpj;
    }
}