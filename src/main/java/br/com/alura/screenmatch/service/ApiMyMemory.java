package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.model.DadosTraducao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import java.net.URLEncoder;

public class ApiMyMemory {
    public static String obterTraducao(String text) {
        ObjectMapper mapper = new ObjectMapper();
        ConsumoApi api = new ConsumoApi();

        String texto = URLEncoder.encode(text);
        String langPair = URLEncoder.encode("en|pt-br");
        String url = "https://api.mymemory.translated.net/get?q=" + texto + "&langpair=" + langPair;

        DadosTraducao dadosTraducao;
        String json = api.obterDados(url);

        try {
            dadosTraducao = mapper.readValue(json, DadosTraducao.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

       return dadosTraducao.resposta().textoTraduzido();
    }
}
