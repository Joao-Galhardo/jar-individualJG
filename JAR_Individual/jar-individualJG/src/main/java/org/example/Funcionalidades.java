package org.example;

import com.github.britooo.looca.api.core.Looca;
import org.example.Db.Db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.DecimalFormat;

public class Funcionalidades {
    Looca looca = new Looca();
    Utilitarios utilitarios = new Utilitarios();

    public static void matarProcessos() {
        Utilitarios utilitarios = new Utilitarios();
        try {
            while (true) {
                String os = System.getProperty("os.name");
                if (os.contains("Windows")) {
                    // Se for Windows, mantenha o código existente
                    limparConsole();
                    System.out.println("""
                                  
                                               Monitoramento ativo!
                                               
                            Este computador é monitorado em tempo real, incluindo o hardware, para
                            assegurar conformidade com as políticas da empresa.
                            Todas as atividades serão verificadas e, se necessário, medidas serão
                            tomadas automaticamente pelo sistema.
                                                            
                            """);
                    if (isProcessRunning("whatsApp.exe")) {
                        Thread.sleep(3000);
                        limparConsole();
                        utilitarios.centralizaTelaVertical(5);
                        utilitarios.centralizaTelaHorizontal(15);
                        System.out.println("Programa indevido localizado");
                        utilitarios.centralizaTelaHorizontal(15);
                        System.out.println("Encerrando programa indevido!");
                        Thread.sleep(3000);
                        limparConsole();
                        utilitarios.barraLoad(1);
                        new ProcessBuilder("cmd", "/c", "taskkill", "/f", "/im", "whatsApp.exe").inheritIO().start().waitFor();
                        Thread.sleep(3000);
                        limparConsole();
                        utilitarios.centralizaTelaVertical(2);
                        utilitarios.centralizaTelaHorizontal(7);
                        System.out.println("Programa indevido foi encerrado com sucesso!");
                    }
                } else {
                    // Se for Linux, execute o comando para matar o processo
                    try {
                        Process process = Runtime.getRuntime().exec("ps -e");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.contains("whatsApp")) {
                                String[] parts = line.trim().split("\\s+");
                                String pid = parts[0];
                                Process killProcess = Runtime.getRuntime().exec("kill -9 " + pid);
                                killProcess.waitFor();
                                // Adicione aqui a lógica para notificar que o processo foi encerrado
                                if (killProcess.waitFor() == 0) {
                                    System.out.println("Processo encerrado com sucesso!");
                                } else {
                                    System.out.println("Erro ao encerrar o processo!");
                                }
                            }
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Thread.sleep(5000);
            }
        } catch (final Exception exception) {
            System.out.println("Erro ao Limpar o console!");
        }
    }

    public static void limparConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (final Exception exception) {
            System.out.println("Erro ao Limpar o console!");
        }
    }

    public static boolean isProcessRunning(String processName) throws IOException {
        Process process = new ProcessBuilder("tasklist", "/fi", "imagename eq " + processName).start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(processName)) {
                    return true;
                }
            }
        }
        return false;
    }


    public void encerraProcesso(Integer pid) {
        try {
            Runtime.getRuntime().exec("taskkill /F /PID " + pid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void usoHardware() {

        Double porcentagemRam = ((double) looca.getMemoria().getEmUso() / looca.getMemoria().getTotal()) * 100;
        Double porcentagemProcessador = (looca.getProcessador().getUso() * 100.0) / 100;


        utilitarios.centralizaTelaHorizontal(15);
        System.out.println("Memoria RAM em uso: %.2f  GB".formatted(Math.round((double) looca.getMemoria().getEmUso() / Math.pow(1024, 3) * 100.0) / 100.0));
        utilitarios.centralizaTelaHorizontal(15);
        System.out.println("Porcentagem de uso da Memoria RAM: %.2f".formatted(porcentagemRam) + "%");
        utilitarios.centralizaTelaHorizontal(15);
        System.out.println("Porcentagem de uso do Processador: %.2f".formatted(porcentagemProcessador) + "%");
        utilitarios.centralizaTelaHorizontal(15);
        System.out.println("Volume de Disco em uso: %.2f  GB".formatted(Math.round((double) looca.getGrupoDeDiscos().getDiscos().get(0).getTamanho() / Math.pow(1024, 3) * 100.0) / 100.0));
        utilitarios.centralizaTelaHorizontal(15);
        System.out.println();


    }

    public void informaçoesHardware() {
        System.out.println();
        utilitarios.centralizaTelaHorizontal(15);
        System.out.println("Minha Máquina");
        utilitarios.centralizaTelaHorizontal(15);
        System.out.println("Sistema Operacional: " + System.getProperty("os.name"));
        utilitarios.centralizaTelaHorizontal(15);
        System.out.println("Arquitetura do sistema: " + System.getProperty("os.arch"));
        utilitarios.centralizaTelaHorizontal(15);
        System.out.println("Processador: " + looca.getProcessador().getNome());
        utilitarios.centralizaTelaHorizontal(15);
        System.out.println("Volume Total da Ram: " + Math.round((double) looca.getMemoria().getTotal() / Math.pow(1024, 3) * 100.0) / 100.0 + "GB");
        utilitarios.centralizaTelaHorizontal(15);
        System.out.println("Modelo do disco: " + looca.getGrupoDeDiscos().getDiscos().get(0).getModelo());
        utilitarios.centralizaTelaHorizontal(15);
        System.out.println("Volume Total do disco: " + Math.round((double) looca.getGrupoDeDiscos().getDiscos().get(0).getTamanho() / Math.pow(1024, 3) * 100.0) / 100.0 + "GB");
    }

}
