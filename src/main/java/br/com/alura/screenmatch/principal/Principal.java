package br.com.alura.screenmatch.principal;

import br.com.alura.screenmatch.model.Categoria;
import br.com.alura.screenmatch.model.DadosSerie;
import br.com.alura.screenmatch.model.DadosTemporada;
import br.com.alura.screenmatch.model.Episodio;
import br.com.alura.screenmatch.model.Serie;
import br.com.alura.screenmatch.repository.SerieRepository;
import br.com.alura.screenmatch.service.ConsumoApi;
import br.com.alura.screenmatch.service.ConverteDados;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";
    private List<DadosSerie> dadosSerie = new ArrayList<>();

    private SerieRepository repositorio;
    private Optional<Serie> serieBusca;

    private List<Serie> series;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu =
                """
                ----------------------------------------------------------------
                                            Menu
                ----------------------------------------------------------------
                1 - Buscar séries
                2 - Buscar episódios
                3 - Listar séries procuradas
                4 - Buscar série por nome
                5 - Buscar série por ator
                6 - Buscar top 5 séries
                7 - Buscar séries por categoria
                8 - Filtrar séries
                9 - Buscar episódio por trecho
                10 - Top 5 episódios por série
                11 - Buscar episódios a partir de uma data

                0 - Sair
                """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorNome();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaEAvaliação();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    top5EpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosAPartirDeData();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarSeriePorNome() {
        System.out.println("Digite o nome da série que deseja procurar: ");
        var nome = leitura.nextLine();

        serieBusca = repositorio.findByTituloContainsIgnoreCase(nome);

        if (serieBusca.isPresent()) {
            System.out.println("Série encontrada!");
            System.out.println(serieBusca.get().getTitulo());
        } else {
            System.out.println("Série não encontrada.");
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(
            ENDERECO + nomeSerie.replace(" ", "+") + API_KEY
        );
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma série pelo nome: ");
        var nomeSerie = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainsIgnoreCase(
            nomeSerie
        );

        if (serie.isPresent()) {
            List<DadosTemporada> temporadas = new ArrayList<>();
            var serieEncontrada = serie.get();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(
                    ENDERECO +
                    serieEncontrada.getTitulo().replace(" ", "+") +
                    "&season=" +
                    i +
                    API_KEY
                );
                DadosTemporada dadosTemporada = conversor.obterDados(
                    json,
                    DadosTemporada.class
                );
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas
                .stream()
                .flatMap(t ->
                    t.episodios().stream().map(e -> new Episodio(t.numero(), e))
                )
                .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada.");
        }
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series
            .stream()
            .sorted(Comparator.comparing(Serie::getGenero))
            .forEach(System.out::println);
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator: ");
        var nomeAtor = leitura.nextLine();

        System.out.println("avaliações a partir de qual valor?");
        var avaliacao = leitura.nextDouble();

        List<Serie> seriesEncontradas =
            repositorio.findByAtoresContainsIgnoreCaseAndAvaliacaoGreaterThanEqual(
                nomeAtor,
                avaliacao
            );
        System.out.println("Series em que " + nomeAtor + " trabalhou: ");
        seriesEncontradas.forEach(s ->
            System.out.println(
                s.getTitulo() + ", avaliação: " + s.getAvaliacao()
            )
        );
    }

    private void buscarTop5Series() {
        List<Serie> top5Series = repositorio.findTop5ByOrderByAvaliacaoDesc();
        System.out.println(top5Series.size());
        top5Series.forEach(s ->
            System.out.println(
                s.getTitulo() + ", Avaliação: " + s.getAvaliacao()
            )
        );
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Deseja buscar series de que categoria/genero: ");
        var nomeGenero = leitura.nextLine();

        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);

        System.out.println("Séries da categoria " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesPorTemporadaEAvaliação() {
        System.out.println("Filtrar séries até quantas temporadas");
        var numeroTemporadas = leitura.nextInt();
        leitura.nextLine();

        System.out.println("Com avaliação a partir de qual valor? : ");
        var numeroAvaliacao = leitura.nextDouble();
        leitura.nextLine();

        List<Serie> seriesFiltradas = repositorio.seriesPorTemporadaEAvaliacao(
            numeroTemporadas,
            numeroAvaliacao
        );

        System.out.println(
            """
            ----------------------------------------------------------------
                                    Séries filtradas
            ----------------------------------------------------------------
            """
        );

        seriesFiltradas.forEach(s ->
            System.out.println(
                s.getTitulo() + ", avaliação: " + s.getAvaliacao()
            )
        );
    }

    private void buscarEpisodioPorTrecho(){
        System.out.println("Digite o nome do episódio para busca: ");
        var trechoEpisodio = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(trechoEpisodio);

        episodiosEncontrados.forEach(episodio -> System.out.printf(
                "Série: %s, temporada: %s, episódio %s - %s\n",
                episodio.getSerie().getTitulo(), episodio.getTemporada(),
                episodio.getNumeroEpisodio(), episodio.getTitulo()
        ));
    }

    private void top5EpisodiosPorSerie(){
        buscarSeriePorNome();

        if(serieBusca.isPresent()){
            Serie serie = serieBusca.get();
            List<Episodio> top5Episodios = repositorio.top5EpisodiosPorSerie(serie);
            top5Episodios.forEach(episodio -> System.out.printf(
                    "Série: %s, temporada: %s, episódio %s - %s, nota: %.1f\n",
                    episodio.getSerie().getTitulo(), episodio.getTemporada(),
                    episodio.getNumeroEpisodio(), episodio.getTitulo(), episodio.getAvaliacao()
            ));
        }
    }

    private void buscarEpisodiosAPartirDeData(){
        buscarSeriePorNome();
        if (serieBusca.isPresent()){
            System.out.println("Digite a partir de qual ano deseja filtrar: ");
            var anoDeBusca = leitura.nextInt();
            leitura.nextLine();

            List<Episodio> episodiosAno = repositorio.episodiosPorSerieEAno(serieBusca.get(),anoDeBusca);

            episodiosAno.forEach(System.out::println);
        }
    }
}
