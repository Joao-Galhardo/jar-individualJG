import com.github.britooo.looca.api.core.Looca;
import org.example.Db.Db;
import org.example.Utilitarios;

import java.io.Console;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Scanner;

import static org.example.Fucionalidades.limparConsole;


public class Main {
    public static void main(String[] args) throws SQLException, InterruptedException {
        Utilitarios utils = new Utilitarios();
        Scanner sc = new Scanner(System.in);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        boolean isAuthenticated = false;

        // Limpa o console e exibe as mensagens iniciais
        limparConsole();
        utils.exibirLogo();
        utils.exibirMenu();
        utils.exibirMensagem();

        // Constantes para consultas SQL
        final String authQuery = """
            SELECT funcionario_id, nome_funcionario FROM funcionario
            WHERE (email_funcionario = ? AND senha_acesso = ?) OR
            (login_acesso = ? AND senha_acesso = ?);
        """;

        final String insertAccessQuery = "INSERT INTO acesso_usuario (fkAcessoUsuario, data_entrada) VALUES (?, now())";

        final String getAccessIdQuery = """
            SELECT IdAcessoUsuario FROM acesso_usuario 
            WHERE fkAcessoUsuario = ? AND data_saida IS NULL 
            ORDER BY data_entrada DESC LIMIT 1;
        """;

        final String updateExitQuery = """
            UPDATE acesso_usuario SET data_saida = now() 
            WHERE fkAcessoUsuario = ? AND IdAcessoUsuario = ?;
        """;

        do {

            utils.centralizaTelaHorizontal(22);
            System.out.println("Email:");
            utils.centralizaTelaHorizontal(22);
            String email = sc.next();
            System.out.println();

            utils.centralizaTelaHorizontal(22);
            System.out.println("Senha:");
            utils.centralizaTelaHorizontal(22);
            String senha = sc.next();

            try (

                    Connection conn = Db.getConection();
                    PreparedStatement pstmtAuth = conn.prepareStatement(authQuery);
                    PreparedStatement pstmtInsertAccess = conn.prepareStatement(insertAccessQuery);
                    PreparedStatement pstmtGetAccessId = conn.prepareStatement(getAccessIdQuery);
                    PreparedStatement pstmtUpdateExit = conn.prepareStatement(updateExitQuery);
            ) {

                pstmtAuth.setString(1, email);
                pstmtAuth.setString(2, senha);
                pstmtAuth.setString(3, email);
                pstmtAuth.setString(4, senha);

                try (ResultSet rt = pstmtAuth.executeQuery()) {
                    if (rt.next()) {
                        isAuthenticated = true;
                        System.out.println("Usuário válido.");
                        limparConsole();
                        utils.mensagemInformativa();

                        int funcionarioId = rt.getInt("funcionario_id");

                        // Insere acesso do usuário
                        pstmtInsertAccess.setInt(1, funcionarioId);
                        pstmtInsertAccess.executeUpdate();

                        // Obtém o ID do acesso do usuário
                        pstmtGetAccessId.setInt(1, funcionarioId);
                        Integer acessoUsuarioId = null;
                        try (ResultSet rtGetAccessId = pstmtGetAccessId.executeQuery()) {
                            if (rtGetAccessId.next()) {
                                acessoUsuarioId = rtGetAccessId.getInt("IdAcessoUsuario");
                            }
                        }



                        // Pergunta se o usuário deseja sair
                        System.out.println("Deseja sair? (sim/não):");
                        String response = sc.next();
                        if (response.equalsIgnoreCase("sim")) {
                            // Atualiza data de saída
                            pstmtUpdateExit.setInt(1, funcionarioId);
                            pstmtUpdateExit.setInt(2, acessoUsuarioId);
                            int rowsUpdated = pstmtUpdateExit.executeUpdate();

                            if (rowsUpdated > 0) {
                                System.out.println("Data de saída atualizada com sucesso.");
                                System.out.println("Sessão encerrada. Obrigado por usar o sistema.");
                            } else {
                                System.out.println("Nenhum registro atualizado.");
                            }

                            // Encerra sessão
                            isAuthenticated = false;
                        }
                    } else {
                        System.out.println("Usuário inválido.");
                    }
                }
            } catch (SQLException e) {
                System.err.println("Erro durante a execução: " + e.getMessage());
            }
        } while (isAuthenticated);
    }
}
