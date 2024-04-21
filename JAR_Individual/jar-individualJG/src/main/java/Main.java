import com.github.britooo.looca.api.core.Looca;
import org.example.Db.Db;
import org.example.Utilitarios;

import java.io.Console;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) throws SQLException {
        Utilitarios utils = new Utilitarios();
        Scanner sc = new Scanner(System.in);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        Boolean isAuthenticated = false;

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

            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rt = null;

            String query = """
                SELECT funcionario_id, nome_funcionario FROM funcionario
                JOIN setor ON setor.setor_id = funcionario.fk_setor
                JOIN processos_bloqueados_no_setor AS pb ON pb.fk_setor = setor.setor_id
                JOIN processos_janelas AS pj ON pj.processo_id = pb.fk_processo
                WHERE (email_funcionario = ? AND senha_acesso = ?) OR
                (login_acesso = ? AND senha_acesso = ?);
            """;

            try {
                conn = Db.getConection();
                pstmt = conn.prepareStatement(query);
                pstmt.setString(1, email);
                pstmt.setString(2, senha);
                pstmt.setString(3, email);
                pstmt.setString(4, senha);

                rt = pstmt.executeQuery();

                if (rt.next()) {
                    isAuthenticated = true;
                    System.out.println("Usuário válido.");

                    int funcionarioId = rt.getInt("funcionario_id");


                    rt.close();
                    pstmt.close();


                    String insertQuery = "INSERT INTO acesso_usuario (fkAcessoUsuario, data_entrada) VALUES (?, now())";
                    pstmt = conn.prepareStatement(insertQuery);
                    pstmt.setInt(1, funcionarioId);
                    pstmt.executeUpdate();
                    pstmt.close();


                    pstmt = conn.prepareStatement("SELECT IdAcessoUsuario FROM acesso_usuario WHERE fkAcessoUsuario = ? AND data_saida IS NULL ORDER BY data_entrada DESC LIMIT 1");
                    pstmt.setInt(1, funcionarioId);
                    rt = pstmt.executeQuery();

                    Integer acessoUsuarioId = null;
                    if (rt.next()) {
                        acessoUsuarioId = rt.getInt("IdAcessoUsuario");
                    }

                    rt.close();
                    pstmt.close();


                    System.out.println("Deseja sair? (sim/não):");
                    String response = sc.next();
                    if (response.equalsIgnoreCase("sim")) {

                        String updateQuery = "UPDATE acesso_usuario SET data_saida = now() WHERE fkAcessoUsuario = ? AND IdAcessoUsuario = ?";
                        pstmt = conn.prepareStatement(updateQuery);
                        pstmt.setInt(1, funcionarioId);
                        pstmt.setInt(2, acessoUsuarioId);

                        int rowsUpdated = pstmt.executeUpdate();

                        if (rowsUpdated > 0) {
                            System.out.println("Data de saída atualizada com sucesso.");
                        } else {
                            System.out.println("Nenhum registro atualizado.");
                        }

                        pstmt.close();
                        isAuthenticated = false;
                    }
                } else {
                    System.out.println("Usuário inválido.");
                }
            } catch (SQLException e) {
                System.err.println("Erro durante a execução: " + e.getMessage());
            } finally {

                try {
                    if (rt != null) {
                        rt.close();
                    }
                    if (pstmt != null) {
                        pstmt.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar recursos: " + e.getMessage());
                }
            }
        } while (isAuthenticated);

        System.out.println("Sessão encerrada. Obrigado por usar o sistema.");
    }
}
