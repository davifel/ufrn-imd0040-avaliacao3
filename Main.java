import java.io.UncheckedIOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner in = new Scanner(System.in);
    private static final String ARQUIVO = "bilheteria.txt";
    private static Bilheteria bilheteria;

    public static void main(String[] args) {
        try {
            bilheteria = new Bilheteria(ARQUIVO);
        } catch (UncheckedIOException e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
            return;
        }
        if (bilheteria.listarTodos().isEmpty()) carregarExemplos(); // só na primeira execução
        int op;
        do {
            System.out.println("""

                === BILHETERIA ===
                1 - Cadastrar filme
                2 - Listar todos os filmes
                3 - Listar filmes em cartaz
                4 - Buscar filme por nome
                5 - Alterar filme
                6 - Remover filme
                7 - Adicionar sala a um filme
                8 - Remover sala de um filme
                9 - Vender ingressos
                0 - Sair""");
            op = lerInt("Opção: ");
            try {
                switch (op) {
                    case 1 -> cadastrarFilme();
                    case 2 -> imprimir(bilheteria.listarTodos());
                    case 3 -> imprimir(bilheteria.listarEmCartaz());
                    case 4 -> imprimir(bilheteria.buscarPorNome(lerTexto("Nome (ou trecho): ")));
                    case 5 -> alterarFilme();
                    case 6 -> System.out.println(bilheteria.removerFilme(lerInt("ID do filme: "))
                            ? "Filme removido." : "Filme não encontrado.");
                    case 7 -> adicionarSala();
                    case 8 -> System.out.println(bilheteria.removerSala(lerInt("ID do filme: "), lerInt("ID da sala: "))
                            ? "Sala removida." : "Sala não encontrada.");
                    case 9 -> System.out.println(bilheteria.venderIngressos(
                            lerInt("ID do filme: "), lerInt("ID da sala: "), lerInt("Quantidade: "))
                            ? "Venda realizada!" : "Lugares insuficientes.");
                    case 0 -> System.out.println("Encerrando...");
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (UncheckedIOException e) {
                System.out.println("Erro de arquivo: " + e.getMessage());
            }
        } while (op != 0);
    }

    private static void cadastrarFilme() {
        Filme f = bilheteria.cadastrarFilme(
                lerTexto("Nome: "), lerGenero(), lerInt("Classificação (idade): "),
                lerInt("Duração (min): "), lerStatus());
        System.out.println("Cadastrado com ID " + f.getId());
    }

    private static void alterarFilme() {
        int id = lerInt("ID do filme: ");
        bilheteria.alterarFilme(id, lerTexto("Novo nome: "), lerGenero(), lerInt("Nova classificação: "),
                lerInt("Nova duração: "), lerStatus());
        System.out.println("Filme alterado.");
    }

    private static void adicionarSala() {
        int idFilme = lerInt("ID do filme: ");
        int tipo = lerInt("Tipo (1-Normal, 2-VIP, 3-D-BOX): ");
        int numero = lerInt("Número da sala: ");
        int lugares = lerInt("Capacidade (assentos): ");
        LocalTime horario = lerHorario();
        boolean trid = lerBool("Suporta 3D (s/n)? ");
        Sala sala = switch (tipo) {
            case 1 -> new SalaNormal(numero, lugares, horario, trid);
            case 2 -> new SalaVIP(numero, lugares, horario, trid, lerBool("Possui atendimento (s/n)? "));
            case 3 -> new SalaDBOX(numero, lugares, horario, trid);
            default -> throw new IllegalArgumentException("Tipo de sala inválido.");
        };
        bilheteria.adicionarSala(idFilme, sala);
        System.out.println("Sala adicionada: " + sala);
    }

    // ---------- utilitários de entrada ----------
    private static void imprimir(List<Filme> lista) {
        if (lista.isEmpty()) System.out.println("Nenhum filme encontrado.");
        lista.forEach(System.out::println);
    }

    private static String lerTexto(String msg) { System.out.print(msg); return in.nextLine(); }

    private static int lerInt(String msg) {
        while (true) {
            try { return Integer.parseInt(lerTexto(msg).trim()); }
            catch (NumberFormatException e) { System.out.println("Digite um número inteiro."); }
        }
    }

    private static boolean lerBool(String msg) {
        while (true) {
            String r = lerTexto(msg).trim().toLowerCase();
            if (r.equals("s")) return true;
            if (r.equals("n")) return false;
            System.out.println("Responda com s ou n.");
        }
    }

    private static LocalTime lerHorario() {
        while (true) {
            try { return LocalTime.parse(lerTexto("Horário (HH:mm): ").trim()); }
            catch (DateTimeParseException e) { System.out.println("Formato inválido. Ex.: 19:30"); }
        }
    }

    private static Genero lerGenero() {
        Genero[] generos = Genero.values();
        System.out.println("Gêneros:");
        for (int i = 0; i < generos.length; i++)
            System.out.printf("  %d - %s%s%n", i + 1, generos[i], generos[i].isIntenso() ? " (intenso)" : "");
        while (true) {
            int o = lerInt("Gênero: ");
            if (o >= 1 && o <= generos.length) return generos[o - 1];
            System.out.println("Opção inválida.");
        }
    }

    private static StsFilme lerStatus() {
        while (true) {
            int o = lerInt("Status (1-PREVENDA, 2-EXIBICAO, 3-FINALIZADO): ");
            if (o >= 1 && o <= 3) return StsFilme.values()[o - 1];
            System.out.println("Opção inválida.");
        }
    }

    private static void carregarExemplos() {
        Filme a = bilheteria.cadastrarFilme("Velozes e Furiosos", Genero.ACAO, 14, 130, StsFilme.EXIBICAO);
        bilheteria.adicionarSala(a.getId(), new SalaNormal(1, 120, LocalTime.of(18, 0), false));
        bilheteria.adicionarSala(a.getId(), new SalaDBOX(3, 40, LocalTime.of(20, 30), true));
        Filme b = bilheteria.cadastrarFilme("Divertida Mente 2", Genero.ANIMACAO, 0, 100, StsFilme.EXIBICAO);
        bilheteria.adicionarSala(b.getId(), new SalaVIP(2, 30, LocalTime.of(16, 0), true, true));
    }
}
