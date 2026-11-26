import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Persistência em arquivo de texto (uma linha por registro, campos separados por ';').
 *
 *   F;id;nome;genero;classificacao;duracao;status
 *   S;tipo;id;numero;lugares;lugaresMax;horario;trid;possAtender
 *
 * Linhas "S" pertencem ao filme "F" imediatamente anterior.
 * O nome do filme é codificado (URL-encoding) para permitir ';' e acentos com segurança.
 */
public class RepositorioFilmes {
    private final Path arquivo;

    public RepositorioFilmes(String caminho) {
        this.arquivo = Paths.get(caminho);
    }

    public void salvar(Collection<Filme> filmes) throws IOException {
        List<String> linhas = new ArrayList<>();
        for (Filme f : filmes) {
            linhas.add(String.join(";", "F",
                    String.valueOf(f.getId()),
                    URLEncoder.encode(f.getNome(), StandardCharsets.UTF_8),
                    f.getGenero().name(),
                    String.valueOf(f.getClassificacao()),
                    String.valueOf(f.getDuracao()),
                    f.getStatus().name()));
            for (Sala s : f.getSalas()) {
                boolean possAtender = s instanceof SalaVIP vip && vip.isPossAtender();
                linhas.add(String.join(";", "S",
                        tipoDe(s),
                        String.valueOf(s.getId()),
                        String.valueOf(s.getNumero()),
                        String.valueOf(s.getLugares()),
                        String.valueOf(s.getLugaresMax()),
                        s.getHorario().toString(),
                        String.valueOf(s.isTrid()),
                        String.valueOf(possAtender)));
            }
        }
        // Grava em arquivo temporário e substitui, para não corromper o original se algo falhar.
        Path tmp = arquivo.resolveSibling(arquivo.getFileName() + ".tmp");
        Files.write(tmp, linhas, StandardCharsets.UTF_8);
        Files.move(tmp, arquivo, StandardCopyOption.REPLACE_EXISTING);
    }

    public List<Filme> carregar() throws IOException {
        List<Filme> filmes = new ArrayList<>();
        if (!Files.exists(arquivo)) return filmes; // primeira execução

        Filme atual = null;
        int n = 0;
        for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
            n++;
            if (linha.isBlank()) continue;
            String[] c = linha.split(";", -1);
            try {
                switch (c[0]) {
                    case "F" -> {
                        atual = new Filme(
                                Integer.parseInt(c[1]),
                                URLDecoder.decode(c[2], StandardCharsets.UTF_8),
                                Genero.valueOf(c[3]),
                                Integer.parseInt(c[4]),
                                Integer.parseInt(c[5]),
                                StsFilme.valueOf(c[6]));
                        filmes.add(atual);
                    }
                    case "S" -> {
                        if (atual == null) throw new IllegalStateException("sala sem filme associado");
                        atual.adicionarSala(lerSala(c));
                    }
                    default -> throw new IllegalArgumentException("registro desconhecido: " + c[0]);
                }
            } catch (RuntimeException e) {
                throw new IOException("Arquivo corrompido na linha " + n + ": " + e.getMessage(), e);
            }
        }
        return filmes;
    }

    private Sala lerSala(String[] c) {
        int id = Integer.parseInt(c[2]);
        int numero = Integer.parseInt(c[3]);
        int lugares = Integer.parseInt(c[4]);
        int lugaresMax = Integer.parseInt(c[5]);
        LocalTime horario = LocalTime.parse(c[6]);
        boolean trid = Boolean.parseBoolean(c[7]);
        boolean possAtender = Boolean.parseBoolean(c[8]);

        return switch (c[1]) {
            case "NORMAL" -> new SalaNormal(id, numero, lugaresMax, lugares, horario, trid);
            case "VIP" -> new SalaVIP(id, numero, lugaresMax, lugares, horario, trid, possAtender);
            case "DBOX" -> new SalaDBOX(id, numero, lugaresMax, lugares, horario, trid);
            default -> throw new IllegalArgumentException("tipo de sala desconhecido: " + c[1]);
        };
    }

    private String tipoDe(Sala s) {
        if (s instanceof SalaVIP) return "VIP";
        if (s instanceof SalaDBOX) return "DBOX";
        return "NORMAL";
    }
}
