/*import java.awt.BorderLayout;

 

import javax.swing.*;

public class AddingJMenuToJPanel extends JFrame{
  public AddingJMenuToJPanel(){

        JPanel p = new JPanel();

        this.add(p);

        JMenuBar b = new JMenuBar();

        JMenu menu = new JMenu("Menu");

        b.add(menu);

        JMenuItem item = new JMenuItem("Menu Item");

        menu.add(item);

        p.setLayout(new BorderLayout());

        p.add(b, BorderLayout.NORTH);

        this.setSize(800,800);

        setVisible(true);  

    }
   public static void main (String [] args) {
     new AddingJMenuToJPanel();

    }

}
*/
import java.awt.*;
import javax.swing.*;

public class AddingJMenuToJPanel extends JMenuBar{



//This is the JMenu that is shown
private JMenu menu;

public AddingJMenuToJPanel(String title) {
    super();
    menu = new JMenu(title);
    super.add(menu);
}

@Override
public Component add(Component comp) {
    //You add the the JMenu instead of the JMenuBar
    return menu.add(comp);
}

@Override
public JMenu add(JMenu c) {
    //You add the the JMenu instead of the JMenuBar
    return (JMenu) menu.add(c);
}

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    //Include these two lines to remove the button look
    //Or remove this method to keep the button look
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());
}

@Override
protected void paintBorder(Graphics g) {
    //Remove this line to remove the underline look
    //when you remove the button look
    //An alternative is to you setBorderPainted(false);
    //when you create the object or in the constructor
    //Or remove this method to keep the border
    //super.paintBorder(g);
	
	
}
}