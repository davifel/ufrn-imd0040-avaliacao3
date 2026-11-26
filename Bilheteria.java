import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Gerencia o CRUD de filmes em cartaz e suas salas, com persistência em arquivo. */
public class Bilheteria {
    private final Map<Integer, Filme> filmes = new LinkedHashMap<>();
    private final RepositorioFilmes repositorio;

    /** Carrega os dados do arquivo (se existir); toda alteração é salva automaticamente. */
    public Bilheteria(String caminhoArquivo) {
        this.repositorio = new RepositorioFilmes(caminhoArquivo);
        try {
            for (Filme f : repositorio.carregar()) filmes.put(f.getId(), f);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    private void salvar() {
        try {
            repositorio.salvar(filmes.values());
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao salvar o arquivo: " + e.getMessage(), e);
        }
    }

    // ---------- CREATE ----------
    public Filme cadastrarFilme(String nome, Genero genero, int classificacao, int duracao, StsFilme status) {
        Filme f = new Filme(nome, genero, classificacao, duracao, status);
        filmes.put(f.getId(), f);
        salvar();
        return f;
    }

    public void adicionarSala(int idFilme, Sala sala) {
        Filme f = exigirFilme(idFilme);
        for (Filme outro : filmes.values()) {
            for (Sala s : outro.getSalas()) {
                if (s.getNumero() == sala.getNumero() && s.getHorario().equals(sala.getHorario()))
                    throw new IllegalStateException("A sala " + sala.getNumero() + " já está ocupada às "
                            + sala.getHorario() + " por \"" + outro.getNome() + "\".");
            }
        }
        f.adicionarSala(sala);
        salvar();
    }

    // ---------- READ ----------
    public Filme consultarPorId(int id) { return filmes.get(id); }

    public List<Filme> buscarPorNome(String trecho) {
        List<Filme> r = new ArrayList<>();
        for (Filme f : filmes.values())
            if (f.getNome().toLowerCase().contains(trecho.toLowerCase().trim())) r.add(f);
        return r;
    }

    public List<Filme> listarTodos() { return new ArrayList<>(filmes.values()); }

    public List<Filme> listarEmCartaz() {
        List<Filme> r = new ArrayList<>();
        for (Filme f : filmes.values())
            if (f.getStatus() == StsFilme.EXIBICAO) r.add(f);
        return r;
    }

    // ---------- UPDATE ----------
    public void alterarFilme(int id, String nome, Genero genero, int classificacao, int duracao, StsFilme status) {
        Filme f = exigirFilme(id);
        f.setGenero(genero); // valida primeiro (pode lançar exceção)
        f.setNome(nome);
        f.setClassificacao(classificacao);
        f.setDuracao(duracao);
        f.setStatus(status);
        salvar();
    }

    // ---------- DELETE ----------
    public boolean removerFilme(int id) {
        boolean removido = filmes.remove(id) != null;
        if (removido) salvar();
        return removido;
    }

    public boolean removerSala(int idFilme, int idSala) {
        boolean removida = exigirFilme(idFilme).removerSala(idSala);
        if (removida) salvar();
        return removida;
    }

    // ---------- Venda ----------
    public boolean venderIngressos(int idFilme, int idSala, int qtd) {
        Filme f = exigirFilme(idFilme);
        if (f.getStatus() != StsFilme.EXIBICAO)
            throw new IllegalStateException("Filme não está em exibição.");
        Sala s = f.buscarSala(idSala);
        if (s == null) throw new IllegalArgumentException("Sala não encontrada para este filme.");
        boolean ok = s.reservar(qtd);
        if (ok) salvar();
        return ok;
    }

    private Filme exigirFilme(int id) {
        Filme f = filmes.get(id);
        if (f == null) throw new IllegalArgumentException("Filme #" + id + " não encontrado.");
        return f;
    }
}
