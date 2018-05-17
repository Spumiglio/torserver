
package torservers;

/**
 *
 * @author matti
 */

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;

import com.sun.javafx.scene.web.Debugger;
import org.slf4j.Logger;


class ServerGUI extends JFrame implements ActionListener {

    private final JLabel welcome = new JLabel("Server Chat Inizializzato");
    private final JPanel panel = new JPanel();
    private final JButton close = new JButton("Termina Sessione");
    private final JLabel time = new JLabel();
    private final JLabel log = new JLabel();
    
    

    public ServerGUI(Logger LOG) {

        super("Chat Server");
        setUpWindow(LOG);
        addEventListeners();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }

    private void addEventListeners() {
        close.addActionListener(this);
        
    }

    private void setUpWindow(Logger LOG) {
        Container c = getContentPane();
        c.add(panel);
        panel.add(welcome);
        panel.add(time);
        panel.add(log);
        panel.add(close);
        setSize(200, 200);
        setResizable(false);
        setVisible(true);
        showTime();
        showLog(LOG);
    }

    private void showTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        System.out.println(dateFormat.format(cal.getTime()));
        time.setText(dateFormat.format(cal.getTime()));
    }

    private void showLog(Logger LOG){
        System.out.println("Log");

    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == close) {

            System.exit(0);
        }
        
    }
}
