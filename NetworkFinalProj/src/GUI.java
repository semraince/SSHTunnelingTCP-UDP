import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;


public class GUI {

	static JTextPane pane;
	static JFrame jFrame;
	//static Image image;
	static TrayIcon trayIcon;
	public GUI() {
	
		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}
		SystemTray tray = SystemTray.getSystemTray();
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		Image image=toolkit.getImage("bulb.gif");
		final PopupMenu popup = new PopupMenu();
		MenuItem aboutItem = new MenuItem("About");
		popup.add(aboutItem);
		MenuItem exitItem = new MenuItem("Exit");
		popup.add(exitItem);
		MenuItem startItem = new MenuItem("Start");
		popup.add(startItem);
		MenuItem showItem=new MenuItem("Show");
		popup.add(showItem);
		showItem.setEnabled(false);


		aboutItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(null, "Semra Ince 20150702013");

			}
		});
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);

			}
		});
		showItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				jFrame.setVisible(true);



			}
		});
		startItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				trayIcon.setImage(toolkit.getImage("working.png"));
				trayIcon.setImageAutoSize(true);



				startItem.setEnabled(false);
				showItem.setEnabled(true);
				jFrame=new JFrame();

				jFrame.setSize(700,500);
				jFrame.setLayout(new BorderLayout());


				//jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				JPanel jPanel=new JPanel();
				jPanel.setLayout(new BorderLayout());
				//jFrame.add(jPanel); 
				jPanel.setBackground(Color.yellow);

				pane = new JTextPane();
				pane.setContentType("text/html");
				pane.setText("<font color=\"red\">"+"Application is Started"+"</font>");
				pane.setEditable(false);
				pane.setBackground(Color.BLUE);
				jFrame.getContentPane().add(new JScrollPane(pane));
				jFrame.setSize(650, 450);
				jFrame.setLocationRelativeTo(null);

				Initializer initializer=new Initializer();
				initializer.start();
				// TODO Auto-generated method stub



			}
		});
		trayIcon =new TrayIcon(image,"System Tray Demo",popup);


		trayIcon.setImageAutoSize(true);

		try {
			tray.add(trayIcon);

		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}
	public synchronized static void setText(String a) {
		//jTextArea.setText(jTextArea.getText()+a);
		pane.setText(pane.getText().replaceAll("color=\"red\"", "color=\"black\"" ));
		pane.setText(pane.getText().replace("</body>","<font color=\"red\">"+"<br>"+a+"</br>"+"</font></body>"));
	}
	public static void main(String... args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				new GUI();
			}
		});
	}

}
