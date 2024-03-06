package com.br.firesa.baleiaBotDmo;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.Month;
import java.util.Random;

public class Generators {
	
	private static final String ALPHANUMERIC_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static String generatePassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Adiciona as primeiras três letras alfanuméricas
        for (int i = 0; i < 3; i++) {
            password.append(ALPHANUMERIC_CHARS.charAt(random.nextInt(ALPHANUMERIC_CHARS.length())));
        }

        // Adiciona caracteres aleatórios até atingir o comprimento desejado
        int passwordLength = random.nextInt(11) + 6; // Entre 6 e 16 caracteres
        for (int i = 3; i < passwordLength; i++) {
            char randomChar = ALPHANUMERIC_CHARS.charAt(random.nextInt(ALPHANUMERIC_CHARS.length()));
            password.append(randomChar);
        }

        return password.toString();
    }
	
	public static LocalDate generateBirthday() {
        Random random = new Random();
        int year = random.nextInt(21) + 1985; // Entre 1985 e 2005
        int monthValue = random.nextInt(12) + 1; // Mês de 1 a 12
        int dayOfMonth = random.nextInt(28) + 1; // Dia do mês

        return LocalDate.of(year, monthValue, dayOfMonth);
    }
	
	public static String generateUser(String nickname) {
        // Adiciona um caractere alfanumérico se o nickname tiver menos de 6 caracteres
        if (nickname.length() < 6) {
            Random random = new Random();
            char randomChar = (char) (random.nextInt(26) + 'a'); // Caractere aleatório de 'a' a 'z'
            nickname += randomChar;
        }

        // Remove caracteres se o nickname tiver mais de 10 caracteres
        if (nickname.length() > 10) {
            int removeCount = nickname.length() - 9; // Calcula a quantidade de caracteres a serem removidos
            nickname = nickname.substring(0, nickname.length() - removeCount);
        }

        // Garante que o usuário resultante não tenha mais de 16 caracteres
        if (nickname.length() > 16) {
            nickname = nickname.substring(0, 16);
        }

        return nickname;
    }

}
