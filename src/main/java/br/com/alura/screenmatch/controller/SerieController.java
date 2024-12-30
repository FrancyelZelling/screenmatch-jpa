package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import retrofit2.http.Path;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/series")
public class SerieController {
    @Autowired
    private SerieService servico;

    @GetMapping
    @ResponseBody
    public List<SerieDTO> obterSeries(){
        return servico.obterTodasAsSeries();
    }

    @GetMapping("/top5")
    @ResponseBody
    public List<SerieDTO> obterTop5Series(){
        return servico.obterTop5Series();
    }

    @GetMapping("/lancamentos")
    @ResponseBody
    public List<SerieDTO> obterLancamentos(){
        return servico.obterLancamentos();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public SerieDTO obterPorId(@PathVariable Long id){
        return servico.obterPorId(id);
    }

    @GetMapping("/{id}/temporadas/todas")
    @ResponseBody
    public List<EpisodioDTO> obterTodasTemporadas(@PathVariable Long id){
        return servico.obterTodasTemporadas(id);
    }

    @GetMapping("/{id}/temporadas/{numero}")
    @ResponseBody
    public List<EpisodioDTO> obterTodasTemporadas(@PathVariable Long id, @PathVariable Long numero){
        return servico.obterTemporadasPorNumero(id, numero);
    }

    @GetMapping("/categoria/{categoria}")
    @ResponseBody
    public List<SerieDTO> obterSeriesPorCategoria(@PathVariable String categoria){
        return servico.obterSeriesPorCategoria(categoria);
    }
}
