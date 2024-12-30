package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloContainsIgnoreCase(String name);

    List<Serie> findByAtoresContainsIgnoreCaseAndAvaliacaoGreaterThanEqual(
        String nomeAtor,
        Double avaliacao
    );

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    List<Serie> findByTotalTemporadasLessThanEqualAndAvaliacaoGreaterThanEqual(
        int totalTemporadas,
        double avaliacao
    );

    @Query(
        "SELECT serie FROM Serie serie WHERE serie.totalTemporadas <= :totalTemporadas AND serie.avaliacao >= :avaliacao"
    )
    List<Serie> seriesPorTemporadaEAvaliacao(
        int totalTemporadas,
        double avaliacao
    );

    @Query("SELECT episodio FROM Serie serie JOIN serie.episodios episodio WHERE episodio.titulo ILIKE %:trechoEpisodio%")
    List<Episodio> episodiosPorTrecho(String trechoEpisodio);

    @Query("SELECT episodio FROM Serie serie JOIN serie.episodios episodio WHERE serie = :serie ORDER BY episodio.avaliacao DESC LIMIT 5")
    List<Episodio> top5EpisodiosPorSerie(Serie serie);

    @Query("SELECT episodio FROM Serie serie JOIN serie.episodios episodio WHERE serie = :serie AND YEAR(episodio.dataLancamento) >= :anoDeBusca")
    List<Episodio> episodiosPorSerieEAno(Serie serie, int anoDeBusca);

    List<Serie> findTop5ByOrderByEpisodiosDataLancamentoDesc();

    @Query("SELECT s FROM Serie s " +
            "JOIN s.episodios e " +
            "GROUP BY s " +
            "ORDER BY MAX(e.dataLancamento) DESC LIMIT 5")
    List<Serie> encontrarEpisodiosMaisRecentes();

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.id = :id AND e.temporada = :numero")
    List<Episodio> obterEpisodiosPorTemporada(Long id, Long numero);

}
