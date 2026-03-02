import javax.swing.*;
import java.awt.*;

public class MultiplayerLobby extends JPanel {

    public MultiplayerLobby(CardLayout cardLayout, JPanel mainContainer) {

        setLayout(new BorderLayout());
        setBackground(new Color(0xF7D6E0));

        JLabel title = new JLabel("Multiplayer Lobby (Coming Soon)", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 28));

        JButton backButton = new JButton("กลับเมนู");
        backButton.addActionListener(e -> cardLayout.show(mainContainer, "MENU"));

        add(title, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);
    }
}