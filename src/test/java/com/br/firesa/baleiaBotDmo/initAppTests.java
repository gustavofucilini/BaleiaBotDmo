package com.br.firesa.baleiaBotDmo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class initAppTests {

	public static void main(String[] args) {
        String url = "https://dmo.gameking.com/Sign/SignUpWrite.aspx?vf=797436e2f4ed50f4fd0bbd05d1b727ce";
        
        // Faz uma requisição HTTP para obter o conteúdo da página
        String pageContent = getContentFromUrl(url);

        // Extrai o valor de jid usando uma expressão regular simples
        String jid = extractJid(pageContent);

        // Imprime o valor de jid
        System.out.println("Valor de jid: " + jid);
    }

    private static String getContentFromUrl(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extractJid(String pageContent) {
        // Use uma expressão regular para encontrar o valor de jid na página
        Pattern pattern = Pattern.compile("name=\"jid\" value=\"(.*?)\"");
        Matcher matcher = pattern.matcher(pageContent);

        // Se encontrar correspondência, retorna o valor de jid
        if (matcher.find()) {
            return matcher.group(1);
        }

        // Retorna nulo se não encontrar
        return null;
    }

}
