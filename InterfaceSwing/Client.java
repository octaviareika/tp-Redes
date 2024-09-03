package InterfaceSwing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends JFrame {
    JTextField letraInput;
    private JTextArea chatArea;
    private JTextField chatInput;
    private JLabel estadoLabel;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    public Client() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Caso o Nimbus não esteja disponível, usa o padrão do sistema.
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        setTitle("Jogo da Forca");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // fecha a janela

        // Definir um tema moderno
        

        // Painel de tentativas (jogo)
        estadoLabel = new JLabel("Estado: ");
        estadoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        letraInput = new JTextField(10);
        JButton enviarLetra = new JButton("Enviar letra");
        enviarLetra.setFont(new Font("Arial", Font.PLAIN, 14));
        enviarLetra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String letra = letraInput.getText();
                if (!letra.isEmpty()) {
                    out.println("Tentativa: " + letra);
                    letraInput.setText(""); // faz ficar em branco dvnv
                } else {
                    JOptionPane.showMessageDialog(null, "Digite uma letra para enviar.");
                }
            }
        });

        JPanel jogoPanel = new JPanel();
        jogoPanel.setLayout(new BorderLayout());
        jogoPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // margem
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(estadoLabel);
        JPanel letraPanel = new JPanel();
        letraPanel.add(new JLabel("Letra:"));
        letraPanel.add(letraInput);
        letraPanel.add(enviarLetra);
        topPanel.add(letraPanel);
        jogoPanel.add(topPanel, BorderLayout.NORTH);

        // Painel de chat
        chatArea = new JTextArea(20, 30);
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatInput = new JTextField(20);
        JButton enviarChatButton = new JButton("Enviar Chat");
        enviarChatButton.setFont(new Font("Arial", Font.PLAIN, 14));
        enviarChatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String chat = chatInput.getText().trim();
                if (!chat.isEmpty()) {
                    out.println("Chat: " + chat);
                    chatArea.append("Você: " + chat + "\n");
                    chatInput.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Digite uma mensagem para enviar.");
                }
            }
        });

        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel chatInputPanel = new JPanel();
        chatInputPanel.add(new JLabel("Chat:"));
        chatInputPanel.add(chatInput);
        chatInputPanel.add(enviarChatButton);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        // Dividir a interface em duas partes
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jogoPanel, chatPanel);
        splitPane.setDividerLocation(500); // Define a posição inicial do divisor

        add(splitPane);

        try {
            socket = new Socket("localhost", 8081); // socket do cliente conecta-se com o servidor
            out = new PrintWriter(socket.getOutputStream(), true); // envia mensagens para o servidor
            in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // recebe mensagens do servidor


            String[] opcoes = {"Fácil", "Médio", "Difícil"};
            String dificuldade = (String) JOptionPane.showInputDialog(
                    this,
                    "Escolha a dificuldade:",
                    "Seleção de Dificuldade",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]   
            );

            if (dificuldade != null) {
                out.println("Dificuldade: " + dificuldade); // envia a dificuldade escolhida para o servidor
            } else {
                JOptionPane.showMessageDialog(this, "Nenhuma dificuldade selecionada. O jogo será encerrado.");
                dispose();
                return;
            }

            new Thread(new Runnable() { // thread fica lendo as mensagens do servidor
                public void run() {
                    try {
                        String message;
                        while ((message = in.readLine()) != null) {
                            if (message.startsWith("Estado: ")) { // se a mensagem que veio do servidor começa com Estado:
                                estadoLabel.setText(message); 
                            } else if (message.startsWith("Chat: ")) {
                                chatArea.append(message.substring(6) + "\n"); // pega a mensagem que o cliente digita e coloca para aparecer na tela
                            } else {
                                chatArea.append(message + "\n");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        dispose();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {
        int response = JOptionPane.showConfirmDialog(this, "Você realmente deseja sair?", "Sair", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION){
            out.println("Sair");
            super.dispose();
        }
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Client client = new Client();
        client.setVisible(true);
    }
}