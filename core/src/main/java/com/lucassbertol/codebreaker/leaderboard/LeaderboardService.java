package com.lucassbertol.codebreaker.leaderboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpStatus;

public class LeaderboardService {

    public interface SubmitCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface CheckNameCallback {
        void onNameExists(boolean exists);
        void onError(String message);
    }

    public LeaderboardService() {
    }

    public void submitScore(String nome, int pontos, SubmitCallback callback) {
        if (nome == null || nome.trim().isEmpty()) {
            if (callback != null) {
                callback.onError("Nome invalido");
            }
            return;
        }

        // Monta JSON
        String jsonBody = "{\"nome\":\"" + escapeJson(nome) + "\",\"score\":" + pontos + "}";
        String url = LeaderboardConfig.getAppendUrl();

        Gdx.app.log("LeaderboardService", "=== ENVIANDO REQUEST ===");
        Gdx.app.log("LeaderboardService", "URL: " + url);
        Gdx.app.log("LeaderboardService", "JSON: " + jsonBody);

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(url);
        request.setHeader("Content-Type", "application/json; charset=utf-8");
        request.setFollowRedirects(true);
        request.setTimeOut(5000);
        request.setContent(jsonBody);

        try {
            byte[] contentBytes = jsonBody.getBytes("UTF-8");
            request.setHeader("Content-Length", String.valueOf(contentBytes.length));
        } catch (Exception e) {
            Gdx.app.error("LeaderboardService", "Erro ao calcular Content-Length", e);
        }

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {

                Gdx.app.log("LeaderboardService", "RESPOSTA RECEBIDA");
                Gdx.app.log("LeaderboardService", "Status: " + httpResponse.getStatus().getStatusCode());
                Gdx.app.log("LeaderboardService", "Body: " + httpResponse.getResultAsString());

                HttpStatus status = httpResponse.getStatus();
                int statusCode = status.getStatusCode();

                if (statusCode >= 200 && statusCode < 300) {
                    if (callback != null) {
                        Gdx.app.postRunnable(callback::onSuccess);
                    }
                } else {
                    if (callback != null) {
                        Gdx.app.postRunnable(() ->
                            callback.onError("Erro HTTP: " + statusCode));
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("LeaderboardService", "ERRO NA REQUISIÇÃO");
                Gdx.app.error("LeaderboardService", "Mensagem: " + t.getMessage());
                Gdx.app.error("LeaderboardService", "Classe: " + t.getClass().getName());
                Gdx.app.error("LeaderboardService",
                    "Falha ao enviar score: " + t.getMessage(), t);
                if (callback != null) {
                    Gdx.app.postRunnable(() ->
                        callback.onError("Falha na requisição: " + t.getMessage()));
                }
            }

            @Override
            public void cancelled() {
                if (callback != null) {
                    Gdx.app.postRunnable(() ->
                        callback.onError("Requisição cancelada"));
                }
            }
        });
    }

    public void checkNameExists(String nome, CheckNameCallback callback) {
        if (nome == null || nome.trim().isEmpty()) {
            if (callback != null) {
                callback.onError("Nome invalido");
            }
            return;
        }

        String url = LeaderboardConfig.getFetchUrl();

        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.GET);
        request.setUrl(url);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                HttpStatus status = httpResponse.getStatus();
                int statusCode = status.getStatusCode();

                if (statusCode >= 200 && statusCode < 300) {
                    String result = httpResponse.getResultAsString();

                    // Verifica se é HTML de erro
                    if (result.trim().startsWith("<!DOCTYPE") || result.trim().startsWith("<html")) {
                        Gdx.app.error("LeaderboardService", "ERRO");
                        if (callback != null) {
                            Gdx.app.postRunnable(() ->
                                callback.onError("Script não configurado corretamente"));
                        }
                        return;
                    }

                    boolean nameExists = checkIfNameExistsInResponse(result, nome);


                    if (callback != null) {
                        Gdx.app.postRunnable(() -> callback.onNameExists(nameExists));
                    }
                } else {
                    if (callback != null) {
                        Gdx.app.postRunnable(() ->
                            callback.onError("Erro ao verificar nome: HTTP " + statusCode));
                    }
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("LeaderboardService",
                    "Falha ao verificar nome: " + t.getMessage(), t);
                if (callback != null) {
                    Gdx.app.postRunnable(() ->
                        callback.onError("Falha na verificação: " + t.getMessage()));
                }
            }

            @Override
            public void cancelled() {
                if (callback != null) {
                    Gdx.app.postRunnable(() ->
                        callback.onError("Verificação cancelada"));
                }
            }
        });
    }

    private boolean checkIfNameExistsInResponse(String jsonResponse, String nome) {
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            return false;
        }

        try {

            // Verifica se a resposta é HTML de erro ao invés de JSON
            if (jsonResponse.trim().startsWith("<!DOCTYPE") || jsonResponse.trim().startsWith("<html")) {
                return false;
            }

            // [{"nome":"João","score":100}]
            String nomeNormalizado = nome.trim().toLowerCase();

            // Remove espaços e quebras de linha para facilitar o parse
            String jsonCleaned = jsonResponse.replaceAll("\\s+", " ").trim();
            String jsonLower = jsonCleaned.toLowerCase();


            // Procura pelo padrão "nome":"valor"
            String pattern = "\"nome\":";
            int index = 0;

            while ((index = jsonLower.indexOf(pattern, index)) != -1) {
                // Move para depois de "nome":
                index += pattern.length();

                // Pula espaços em branco
                while (index < jsonLower.length() && (jsonLower.charAt(index) == ' ' || jsonLower.charAt(index) == '\t')) {
                    index++;
                }

                // Verifica se tem aspas
                if (index < jsonLower.length() && jsonLower.charAt(index) == '"') {
                    index++; // Pula a aspas de abertura

                    // Encontra a aspas de fechamento
                    int endQuote = jsonLower.indexOf('"', index);

                    if (endQuote != -1) {
                        // Extrai o nome encontrado (da string original para manter case)
                        String foundNameOriginal = jsonCleaned.substring(index, endQuote).trim();
                        String foundName = foundNameOriginal.toLowerCase();

                        if (foundName.equals(nomeNormalizado)) {
                            return true;
                        }

                        index = endQuote + 1;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }

            return false;

        } catch (Exception e) {
            Gdx.app.error("LeaderboardService",
                "Erro ao processar resposta JSON: " + e.getMessage(), e);
            return false;
        }
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"");
    }
}
