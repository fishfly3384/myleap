package myleap;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Controller.PolicyFlag;

public class Main extends JPanel {
	private static final long serialVersionUID = 1L;
	public static void main(String args[]) {
		
		final MouseListener mouse = new MouseListener();
		final Controller controller = new Controller();
		controller.setPolicyFlags(PolicyFlag.POLICY_BACKGROUND_FRAMES);
		
		if (!SystemTray.isSupported()) {
			System.out.println("system tray is not supported, abort");
	        return ;
	    }
		SystemTray system = SystemTray.getSystemTray();
		Image image = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("icon.jpg"));
		PopupMenu menu = new PopupMenu();
		MenuItem start = new MenuItem("开始");
		start.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.removeListener(mouse);
				controller.config().setBool("MyLeap.Debug", false);
				controller.addListener(mouse);
				System.out.println("mouse controll start");
			}
		});
		menu.add(start);
		
		MenuItem debug = new MenuItem("调试");
		debug.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.removeListener(mouse);
				controller.config().setBool("MyLeap.Debug", true);
				controller.addListener(mouse);
				System.out.println("mouse controll debug");
			}
		});
		menu.add(debug);
		
		MenuItem stop = new MenuItem("停止");
		stop.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.removeListener(mouse);
				System.out.println("mouse controll stop");
			}
		});
		menu.add(stop);
		
		MenuItem quit = new MenuItem("退出");
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.removeListener(mouse);
				System.out.println("appliction quit");
				System.exit(0);
			}
		});
		menu.add(quit);
		
		TrayIcon icon=new TrayIcon(image, "My Leap", menu);
		icon.setImageAutoSize(true);
		try {
			System.out.println("application start");
			system.add(icon);
		}
		
		catch(Exception e) {
		}			
		
		try {
			System.in.read();
		}
		catch(Exception e) {
		}		
	}
}