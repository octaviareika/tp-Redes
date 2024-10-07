package InterfaceSwing;

import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Client extends JFrame {
    JTextField letraInput;
    private JTextPane chatPane;
    private JTextField chatInput;
    private JLabel estadoLabel;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;
    private JLabel imagemLabel;

    private void playSound(String filePath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            System.out.println("Erro ao tocar o som: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Client() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        setTitle("Jogo da Forca");
        setSize(1300, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
                    letraInput.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Digite uma letra para enviar.");
                }
            }
        });

        JPanel jogoPanel = new JPanel();
        jogoPanel.setLayout(new BorderLayout());
        jogoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(estadoLabel);
        JPanel letraPanel = new JPanel();
        letraPanel.add(new JLabel("Letra:"));
        letraPanel.add(letraInput);
        letraPanel.add(enviarLetra);
        topPanel.add(letraPanel);
        jogoPanel.add(topPanel, BorderLayout.NORTH);

        // Adicionar imagem
        imagemLabel = new JLabel();
        imagemLabel.setIcon(new ImageIcon(getClass().getResource("/InterfaceSwing/Imagens/forca.png")));
        jogoPanel.add(imagemLabel, BorderLayout.CENTER);

        // Painel de chat
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        StyledDocument doc = chatPane.getStyledDocument();

        Style estiloNegrito = chatPane.addStyle("Negrito", null);
        StyleConstants.setBold(estiloNegrito, true);

        Style estiloNormal = chatPane.addStyle("Normal", null);
        StyleConstants.setBold(estiloNormal, false);

        chatInput = new JTextField(20);
        JButton enviarChatButton = new JButton("Enviar Chat");
        enviarChatButton.setFont(new Font("Arial", Font.PLAIN, 14));
        enviarChatButton.setForeground(Color.RED);
        enviarChatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String chat = chatInput.getText().trim();
                if (!chat.isEmpty()) {
                    try {
                        doc.insertString(doc.getLength(), "Você: ", estiloNegrito);
                        doc.insertString(doc.getLength(), chat + "\n", estiloNormal);
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                    }
                    out.println("Chat: " + chat);
                    chatInput.setText("");
                } else {
                    JOptionPane.showMessageDialog(null, "Digite uma mensagem para enviar.");
                }
            }
        });

        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        chatPanel.add(new JScrollPane(chatPane), BorderLayout.CENTER);

        JPanel chatInputPanel = new JPanel();
        chatInputPanel.add(new JLabel("Chat:"));
        chatInputPanel.add(chatInput);
        chatInputPanel.add(enviarChatButton);
        chatPanel.add(chatInputPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jogoPanel, chatPanel);
        splitPane.setDividerLocation(700);

        add(splitPane);

        connectToServer(); // Chame o método para conectar ao servidor

        new Thread(new Runnable() {
            public void run() {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("Estado: ")) {
                            estadoLabel.setText(message);
                        } else if (message.startsWith("Chat: ")) {
                            try {
                                doc.insertString(doc.getLength(), message.substring(6) + "\n", estiloNormal);
                            } catch (BadLocationException ex) {
                                ex.printStackTrace();
                            }
                        } else if (message.startsWith("Imagem: ")) {
                            String imagePath = message.substring(8);
                            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
                            imagemLabel.setIcon(icon);
                        } else if (message.equals("TOCAR_MUSICA_VITORIA")) {
                            playSound("musicas/win.wav");
                        } else if (message.equals("TOCAR_MUSICA_DERROTA")) {
                            playSound("musicas/jogoPerdido.wav");
                        } else {
                            try {
                                doc.insertString(doc.getLength(), message + "\n", estiloNormal);
                            } catch (BadLocationException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    dispose();
                }
            }
        }).start();
    }

    private void connectToServer() {
        String ipAddress = JOptionPane.showInputDialog(this, "Digite o IP do servidor:", "Conexão ao Servidor", JOptionPane.QUESTION_MESSAGE);
        String portString = JOptionPane.showInputDialog(this, "Digite a porta do servidor:", "Conexão ao Servidor", JOptionPane.QUESTION_MESSAGE);

        if (ipAddress != null && portString != null) {
            try {
                int port = Integer.parseInt(portString);
                socket = new Socket(ipAddress, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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
                    out.println("Dificuldade: " + dificuldade);
                } else {
                    JOptionPane.showMessageDialog(this, "Nenhuma dificuldade selecionada. O jogo será encerrado.");
                    dispose();
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao conectar ao servidor: " + e.getMessage());
                dispose();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Por favor, insira um número válido para a porta.");
                dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Conexão cancelada.");
            dispose();
        }
    }

    @Override
    public void dispose() {
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
