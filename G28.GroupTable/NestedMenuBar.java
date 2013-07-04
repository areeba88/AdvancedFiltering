import java.awt.BorderLayout;
import javax.swing.*;
 
public class NestedMenuBar {
 
  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
 
      @Override
      public void run() {
        new NestedMenuBar().makeUI();
      }
    });
  }
 
  public void makeUI() {
    JMenu menu = new JMenu("Menu");
    menu.add(new JMenuItem("Item"));
    JMenuBar innerBar = new JMenuBar();
    innerBar.add(menu);
 
    JMenuBar outerBar = new JMenuBar();
    outerBar.setLayout(new BorderLayout());
    outerBar.add(Box.createVerticalStrut(20), BorderLayout.NORTH);
    outerBar.add(innerBar, BorderLayout.SOUTH);
 
    JFrame frame = new JFrame();
    frame.setJMenuBar(outerBar);
 
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(400, 400);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}