import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A beautiful contact manager -- features search by name and search by number.
 * Usage: run taskmgr.java after compiling with princeton cs's stdlib.jar. Click
 * on people's names to view their numbers. Type in the searchbar to search for
 * people by their names or numbers. Hit import to import your contacts. Hit
 * export to export your contacts. File format: name, newline, number, newline
 * 
 * @author PMARINA
 * @version 4/21/2017
 *
 */
public class Taskmgr extends JFrame {
	static JPanel North, South, East, West, Center;
	static RedBlackBST<String, Long> bs = new RedBlackBST<String, Long>();
	static RedBlackBST<Long, String> ls = new RedBlackBST<Long, String>();

	/**
	 * This is basically a jframe. It has 5 panels (n/s/e/w/center). They are
	 * all JPanels so if you have 1000 contacts, they will go off the screen. If
	 * you want a scroll bar go implement it yourself. Otherwise, just search
	 * for contacts/numbers that you need
	 * 
	 * @param s
	 *            the title of the screen
	 */
	@SuppressWarnings("deprecation")
	Taskmgr(String s) {
		setTitle(s);
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		North = new JPanel();
		North.setLayout(new BorderLayout());
		South = new JPanel();
		South.setLayout(new BorderLayout());
		East = new JPanel();
		East.setLayout(new BorderLayout());
		West = new JPanel();
		West.setLayout(new BorderLayout());
		Center = new JPanel();
		Center.setLayout(new BorderLayout());
		South.resize(WIDTH, 500);
		add(North, BorderLayout.NORTH);
		add(South, BorderLayout.SOUTH);
		add(East, BorderLayout.EAST);
		add(West, BorderLayout.WEST);
		add(Center, BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	/**
	 * This is basically the JFrame used
	 */
	static Taskmgr alpha;

	/**
	 * Creates the main JFrame and then revalidates the JFrame (for sizing
	 * issues).
	 * 
	 * @param args
	 *            serves no purpose
	 */
	public static void main(String[] args) {
		Taskmgr a = new Taskmgr("PMARINA ContactMgr");
		alpha = a;
		mainFrame();
		alpha.revalidate();
	}

	/**
	 * Whether or not stuff is being typed into the searchbar
	 */
	static boolean searchOn = false;

	/**
	 * Basically renders the main parts of the screen
	 */
	private static void mainFrame() {
		renderSearch();
		renderContacts();
		renderMainLowerBarButtons();
	}

	static boolean doneOnce = true;// Don't remember what I set this to do, but
									// it's probably not too important
	static JTextField querytier;
	// This is the JTextField that holds the userinput in the searchbar. It's 1
	// am, don't criticize my naming conventions

	/**
	 * Renders the searchbar and sets the global variable to it, so it's easily
	 * accessible from outside. Also, it creates a document listener to know
	 * when changes are made to the field.
	 */
	private static void renderSearch() {
		North.removeAll();
		North.setLayout(new BoxLayout(North, BoxLayout.Y_AXIS));
		JTextField query = new JTextField(20);
		querytier = query;
		query.setFont(new Font("Arial", Font.PLAIN, 20));
		query.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				if (!(query.getText().equals("")))
					searchOn = true;
				else
					searchOn = false;
				queryString = query.getText();
				renderContacts();

			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if (!(query.getText().equals("")))
					searchOn = true;
				else
					searchOn = false;
				queryString = query.getText();
				renderContacts();

			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				if (!(query.getText().equals("")))
					searchOn = true;
				else
					searchOn = false;
				queryString = query.getText();
				renderContacts();

			}

		});
		// North.add(new JButton("WORK FOR FS SAKE"));
		query.setPreferredSize(new Dimension(400, 50));
		query.setMinimumSize(new Dimension(400, 50));
		query.setMaximumSize(new Dimension(400, 50));
		query.setEnabled(true);
		query.setVisible(true);
		query.setHorizontalAlignment(JTextField.CENTER);
		queryString = query.getText();
		query.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (searchOn) {
					queryString = query.getText();
					// System.out.println(query.getText());
					renderSearch();
				}

			}

		});
		North.add(query, JPanel.CENTER_ALIGNMENT);
		North.setAlignmentX(Component.CENTER_ALIGNMENT);
		North.setVisible(true);
		North.validate();
		alpha.validate();

	}

	/**
	 * Render all contacts present. It uses the name-based tree to do this. It's
	 * just a bunch of buttons, one after another, placed through the use of a
	 * for loop. The contacts don't need to be ordered because the bst takes
	 * care of it.
	 */
	private static void renderContacts() {
		if (searchOn) {
			renderSearchContacts();
			Center.repaint();
			return;
		} else {
			Center.removeAll();
			Center.setLayout(new BoxLayout(Center, BoxLayout.Y_AXIS));
			Iterable<String> todos = bs.keys(); // TODO GET AN IRITABLE
												// THINGAMAJIG
			// Should NOT have to sort
			if (bs.isEmpty()) {
				JLabel a = new JLabel("No Contacts. Click Add Contact to add one.");
				a.setFont(new Font("Comic Sans", Font.ITALIC, 20));
				a.setForeground(Color.DARK_GRAY);
				a.setAlignmentX(Component.CENTER_ALIGNMENT);
				Center.add(a, BorderLayout.CENTER);
			} else {
				for (String t : todos) {
					JButton b = new JButton(t);
					b.setToolTipText(Long.toString(bs.get(t)));
					b.addActionListener(new ActionListener() {
						boolean bar = false;

						@Override
						public void actionPerformed(ActionEvent e) {
							if (bar) {
								b.setText(t);
								b.setFont(new Font("Arial", Font.PLAIN, 30));
								bar = !bar;
							} else {
								b.setText(Long.toString(bs.get(t)));
								b.setFont(new Font("Arial", Font.ITALIC, 30));
								bar = !bar;
							}
						}
					});
					b.addMouseListener(new java.awt.event.MouseAdapter() {
						public void mouseEntered(java.awt.event.MouseEvent evt) {
							b.setBackground(Color.GREEN);
						}

						public void mouseExited(java.awt.event.MouseEvent evt) {
							b.setBackground(UIManager.getColor("control"));
						}
					});
					b.setFont(new Font("Arial", Font.PLAIN, 30));
					b.setPreferredSize(new Dimension(500, 50));
					Center.setSize(new Dimension(500, 1000));
					b.setAlignmentX(Component.CENTER_ALIGNMENT);
					Center.add(b, BorderLayout.CENTER);
					// Center.validate();
					alpha.validate();
					// Center.repaint();
				}
			}
		}
		Center.repaint();
	}

	static String queryString = "";

	/**
	 * This is the rendering method that I'm realllly proud of. It's basically
	 * the one copy-pasted from above. Then, I added a for loop behind the
	 * copy-pasted part that removes contacts that don't show up in the search.
	 * Then, I copy-pasted what I had in this method into itself, so I had one
	 * for name-based searches, and one for number-based searches.
	 */
	private static void renderSearchContacts() {
		// System.out.println("I GOT TRIGERED");
		Center.removeAll();
		Center.setLayout(new BoxLayout(Center, BoxLayout.Y_AXIS));
		Iterable<String> todos = bs.keys(); // TODO GET AN IRITABLE THINGAMAJIG
		Iterable<Long> adios = ls.keys();
		if (bs.isEmpty()) {
			JLabel a = new JLabel("No Contacts. Click Add Contact to add one.");
			a.setFont(new Font("Comic Sans", Font.ITALIC, 20));
			a.setForeground(Color.DARK_GRAY);
			a.setAlignmentX(Component.CENTER_ALIGNMENT);
			Center.add(a, BorderLayout.CENTER);
		} else {
			boolean integer = true;
			try {
				Integer.parseInt(queryString);
			} catch (Exception e) {
				integer = false;
			}
			if (!integer) {
				List<String> list = new ArrayList<String>();
				todos.iterator().forEachRemaining(list::add);
				Comparator<Contact> c = Contact.getAutocomplete();
				for (String t : todos) {
					String query = queryString;
					if (t.toLowerCase().contains(query.toLowerCase())) {
						// System.out.println("Query: " + query + "\nt: " + t);
						;// do nothing
					} else {
						// System.out.println(t + " : " + query);
						list.remove(t);
					}
				}
				for (String t : list) {
					// System.out.println(t);
					JButton b = new JButton(t);
					b.setToolTipText(Long.toString(bs.get(t)));
					b.addActionListener(new ActionListener() {
						boolean bar = false;

						@Override
						public void actionPerformed(ActionEvent e) {
							if (bar) {
								b.setText(t);
								b.setFont(new Font("Arial", Font.PLAIN, 30));
								bar = !bar;
							} else {
								b.setText(Long.toString(bs.get(t)));
								b.setFont(new Font("Arial", Font.ITALIC, 30));
								bar = !bar;
							}
						}
					});
					b.addMouseListener(new java.awt.event.MouseAdapter() {
						public void mouseEntered(java.awt.event.MouseEvent evt) {
							b.setBackground(Color.GREEN);
						}

						public void mouseExited(java.awt.event.MouseEvent evt) {
							b.setBackground(UIManager.getColor("control"));
						}
					});
					b.setFont(new Font("Arial", Font.PLAIN, 30));
					b.setPreferredSize(new Dimension(500, 50));
					Center.setSize(new Dimension(500, 1000));
					b.setAlignmentX(Component.CENTER_ALIGNMENT);
					Center.add(b, BorderLayout.CENTER);
					// Center.validate();
					alpha.validate();
					// Center.repaint();
				}
				if (list.size() == 0) {
					Center.removeAll();
					Center.setLayout(new BoxLayout(Center, BoxLayout.Y_AXIS));
					JLabel a = new JLabel("No Contacts Found. Click Add Contact to add one.");
					a.setFont(new Font("Comic Sans", Font.ITALIC, 20));
					a.setForeground(Color.DARK_GRAY);
					a.setAlignmentX(Component.CENTER_ALIGNMENT);
					Center.add(a, BorderLayout.CENTER);
				}
			} else {
				List<Long> list = new ArrayList<Long>();
				adios.iterator().forEachRemaining(list::add);
				for (Long t : adios) {
					if (Long.toString(t).contains(Long.toString(Long.parseLong(queryString)))) {
						// Dont do anything
						;
					} else {
						list.remove(t);
					}
				}
				for (Long t : list) {
					// System.out.println(t);
					JButton b = new JButton(ls.get(t));
					b.setToolTipText(Long.toString(t));
					b.addActionListener(new ActionListener() {
						boolean bar = false;

						@Override
						public void actionPerformed(ActionEvent e) {
							if (bar) {
								b.setText(ls.get(t));
								b.setFont(new Font("Arial", Font.PLAIN, 30));
								bar = !bar;
							} else {
								b.setText(Long.toString(t));
								b.setFont(new Font("Arial", Font.ITALIC, 30));
								bar = !bar;
							}
						}
					});
					b.addMouseListener(new java.awt.event.MouseAdapter() {
						public void mouseEntered(java.awt.event.MouseEvent evt) {
							b.setBackground(Color.GREEN);
						}

						public void mouseExited(java.awt.event.MouseEvent evt) {
							b.setBackground(UIManager.getColor("control"));
						}
					});
					b.setFont(new Font("Arial", Font.PLAIN, 30));
					b.setPreferredSize(new Dimension(500, 50));
					Center.setSize(new Dimension(500, 1000));
					b.setAlignmentX(Component.CENTER_ALIGNMENT);
					Center.add(b, BorderLayout.CENTER);
					// Center.validate();
					alpha.validate();
					// Center.repaint();
				}
				if (list.size() == 0) {
					Center.removeAll();
					Center.setLayout(new BoxLayout(Center, BoxLayout.Y_AXIS));
					JLabel a = new JLabel("No Contacts Found. Click Add Contact to add one.");
					a.setFont(new Font("Comic Sans", Font.ITALIC, 20));
					a.setForeground(Color.DARK_GRAY);
					a.setAlignmentX(Component.CENTER_ALIGNMENT);
					Center.add(a, BorderLayout.CENTER);
				}
			}

		}
	}

	/**
	 * Basically the three buttons below everything else. One for adding
	 * contacts, one for importing contacts, and one for exporting contacts and
	 * quitting
	 */
	private static void renderMainLowerBarButtons() {
		JButton a = new JButton("Add Contact");
		a.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addContact();
			}
		});
		JButton b = new JButton("Import");
		b.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				File f = new File("contacts.txt");
				if (f.exists()) {
					try {
						Scanner sc = new Scanner(f);
						while (sc.hasNextLine()) {
							add(new Contact(sc.nextLine(), Long.parseLong(sc.nextLine())));
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				renderContacts();
			}
		});
		JButton c = new JButton("Save&Close");
		c.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Iterable<String> a = bs.keys();
				FileWriter f;
				try {
					f = new FileWriter(new File("contacts.txt"));
					for (String i : a) {
						f.write(i + "\n");
						f.write(Long.toString(bs.get(i)) + "\n");
					}
					f.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(1);
			}
		});
		a.setFont(new Font("Arial", Font.PLAIN, 40));
		b.setFont(new Font("Arial", Font.PLAIN, 40));
		c.setFont(new Font("Arial", Font.PLAIN, 40));
		South.setLayout(new FlowLayout());
		South.add(a);
		South.add(b);
		South.add(c);
	}

	/**
	 * This is the graphical thing for adding a contact, it's a custom dialog,
	 * where you basically have two fields, one for name and one for number, and
	 * it just creates a contact and sends it into the add(contact c) method.
	 * Cool thing is, I don't need to do input validation because the main
	 * method ends long before the program is done loading. You see, the action
	 * listeners trigger functions, not the main method, so you can give all the
	 * string longs, and its parselong() will not cause the program to fail. It
	 * just won't add the contact :-)
	 */
	private static void addContact() {
		JTextField name = new JTextField();
		name.setFont(new Font("Arial", Font.PLAIN, 20));
		JTextField number = new JTextField();
		number.setFont(new Font("Arial", Font.ITALIC, 20));
		JLabel namel = new JLabel("Name:");
		namel.setFont(new Font("Arial", Font.PLAIN, 25));
		JLabel numberl = new JLabel("Number:");
		numberl.setFont(new Font("Arial", Font.PLAIN, 25));
		final JComponent[] inputs = new JComponent[] { namel, name, numberl, number };
		int buttonPress = JOptionPane.showConfirmDialog(null, inputs, "ADD CONTACT", JOptionPane.PLAIN_MESSAGE);
		if (buttonPress == JOptionPane.OK_OPTION) {
			add(new Contact(name.getText(), Long.parseLong(number.getText())));
			renderContacts();
		} else {
			return;// Cancelled. Maybe add error message if have time
		}
	}

	/**
	 * I am not documenting this method
	 * 
	 * @param contact
	 *            the contact to be added
	 */
	private static void add(Contact contact) {
		bs.put(contact.s, new Long(contact.t));
		ls.put(new Long(contact.t), contact.s);
	}
}