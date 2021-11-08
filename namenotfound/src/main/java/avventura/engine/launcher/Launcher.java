package engine.launcher;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import engine.Engine;
import engine.GameDescription;

public class Launcher extends JFrame {

	private JPanel contentPane;
	public static Locale locale = Locale.getDefault();
	private JList<String> list;
	private Map<String, File> games = new HashMap<String, File>();
	ResourceBundle bundle;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Launcher frame = new Launcher();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Launcher() {
		initGames();
		setTitle("Launcher");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);

		try {
			bundle = ResourceBundle.getBundle("LauncherGUI", locale);
		}catch (MissingResourceException e) {
			bundle = ResourceBundle.getBundle("LauncherGUI", Locale.ENGLISH);
		}

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu(bundle.getString("Lbl_lang"));
		menuBar.add(mnNewMenu);

		JRadioButton ItalianRadioButton = new JRadioButton(bundle.getString("launcher.ItalianRadioButton.text")); //$NON-NLS-1$
		ItalianRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				locale = Locale.ITALIAN;
				dispose();
				main(new String[] {});
			}
		});
		mnNewMenu.add(ItalianRadioButton);
		JRadioButton EnglishRadioButton = new JRadioButton(bundle.getString("launcher.EnglishRadioButton.text")); //$NON-NLS-1$
		EnglishRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				locale = Locale.ENGLISH;
				dispose();
				main(new String[] {});
			}
		});
		mnNewMenu.add(EnglishRadioButton);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JLabel lblNewLabel = new JLabel(bundle.getString("Lbl_choose"));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblNewLabel, BorderLayout.NORTH);

		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setModel(new DefaultListModel<String>());
		list.setBorder(UIManager.getBorder("FileChooser.listViewBorder"));
		contentPane.add(list, BorderLayout.CENTER);

		JButton launchButton = new JButton(bundle.getString("launcher.launchButton.text")); //$NON-NLS-1$
		launchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					launchButtonActionPerformed();
				}catch (LoaderException ex) {
					ErrorDialog err = new ErrorDialog(ex.getMessage());
					err.setVisible(true);
				}catch (Exception ex ) {
					ErrorDialog err = new ErrorDialog(ex.getMessage());
					err.setVisible(true);
				}
			}
		});
		contentPane.add(launchButton, BorderLayout.SOUTH);
		DefaultListModel<String> m = (DefaultListModel) list.getModel();
		for(String g : this.games.keySet()) {
			m.addElement(g);
		}
	}

	private void initGames() {
		File games = new File("./games");
		FileNameExtensionFilter filter = new FileNameExtensionFilter(null, "jar");
		for(File g : games.listFiles()) {
			if(filter.accept(g) && g.isFile()) {
				this.games.put(g.getName().substring(0, g.getName().indexOf(".jar")), g);
			}
		}
	}

	private void launchButtonActionPerformed() throws LoaderException, Exception {
		String selected = list.getSelectedValue();
		URLClassLoader loader = URLClassLoader.newInstance(new URL[] {games.get(selected).toURI().toURL()});
		try {
			Class c = loader.loadClass("game."+selected);
			if(!c.getGenericSuperclass().equals(GameDescription.class)) {
				throw new LoaderException(bundle.getString("nogamefound"));
			}
			final GameDescription g = (GameDescription) c.getConstructor().newInstance();
			Thread t = new Thread( new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Engine engine = new Engine(g);
					try {
						engine.run();
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			});
			t.start();
		}catch (ClassNotFoundException e) {
			throw new LoaderException(bundle.getString("nogamefound"));
		}
	}

}
