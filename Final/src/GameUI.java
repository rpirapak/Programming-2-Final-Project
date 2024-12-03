import javax.swing.*;

public class GameUI extends JFrame {
    private JPanel Panel;
    public GameUI(){
        setTitle("Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        Panel panel = new Panel(600,600);
        add(panel);
        pack();
        setLocationRelativeTo(null);
    }
}
