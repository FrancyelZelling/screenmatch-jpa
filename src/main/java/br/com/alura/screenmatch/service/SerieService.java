package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodioDTO;
import br.com.alura.screenmatch.dto.SerieDTO;
import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {
    @Autowired
    private SerieRepository repositorio;

    private List<SerieDTO> converteDados(List<Serie> series) {
        return series.stream()
                .map(s -> new SerieDTO(
                        s.getId(),
                        s.getTitulo(),
                        s.getTotalTemporadas(),
                        s.getAtores(),
                        s.getSinopse(),
                        s.getGenero(),
                        s.getPosterUrl(),
                        s.getAvaliacao()
                ))
                .collect(Collectors.toList());
    }

    public SerieDTO obterPorId(Long id) {
        var serie = repositorio.findById(id);
        if (serie.isPresent()) {
            var s = serie.get();
            return new SerieDTO(
                    s.getId(),
                    s.getTitulo(),
                    s.getTotalTemporadas(),
                    s.getAtores(),
                    s.getSinopse(),
                    s.getGenero(),
                    s.getPosterUrl(),
                    s.getAvaliacao()
            );
        }

        return null;
    }

    public List<SerieDTO> obterTodasAsSeries() {
        return converteDados(repositorio.findAll());
    }

    public List<SerieDTO> obterTop5Series() {
        return converteDados(repositorio.findTop5ByOrderByAvaliacaoDesc());
    }

    public List<SerieDTO> obterLancamentos() {
        return converteDados(repositorio.encontrarEpisodiosMaisRecentes());
    }

    public List<EpisodioDTO> obterTodasTemporadas(Long id) {
        Optional<Serie> serie = repositorio.findById(id);

        if (serie.isPresent()){
            var s = serie.get();

            return s.getEpisodios().stream().map(e -> new EpisodioDTO(
                    e.getTemporada(),
                    e.getTitulo(),
                    e.getNumeroEpisodio()
            )).collect(Collectors.toList());
        }

        return null;
    }

    public List<EpisodioDTO> obterTemporadasPorNumero(Long id, Long numero) {
        var episodios = repositorio.obterEpisodiosPorTemporada(id, numero);

        return episodios.stream().map(e -> new EpisodioDTO(
                e.getTemporada(),
                e.getTitulo(),
                e.getNumeroEpisodio()
        )).collect(Collectors.toList());
    }

    public List<SerieDTO> obterSeriesPorCategoria(String categoria) {
        Categoria categoriaEnum = Categoria.fromPortugues(categoria);

        return converteDados(repositorio.findByGenero(categoriaEnum));
    }

}
