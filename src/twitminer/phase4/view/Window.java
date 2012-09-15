package twitminer.phase4.view;

import java.awt.Dimension;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.MaskFormatter;

import twitminer.phase2.Rule;
import twitminer.phase4.model.SearchTrendAction;

public class Window extends JFrame {
	private static final long serialVersionUID = -270238674648820299L;
	
	public enum SearchIn { RIGHT, LEFT, BOTH };
	public enum OrderBy  { CONF, FREQ };
	
	private JTextField txtTrend;
	private JTextField txtMinConf;
	private JTextField txtMinFreq;
	
	private JRadioButton rbtnRight;
	private JRadioButton rbtnLeft;
	private JRadioButton rbtnBoth;
	private JRadioButton rbtnConf;
	private JRadioButton rbtnFreq;
	
	private ButtonGroup sideGroup;
	private ButtonGroup minsGroup;
	
	public Window() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(new Dimension(800, 600));
		setTitle("TwitMiner");
		
		JPanel panelPrincipal = new JPanel();
		getContentPane().add(panelPrincipal);
		
		Box boxPrincipal = Box.createVerticalBox();
		panelPrincipal.add(boxPrincipal);
		
		JLabel lblTrend = new JLabel("Tendance");
		boxPrincipal.add(lblTrend);
		
		txtTrend = new JTextField();
		txtTrend.setColumns(10);
		boxPrincipal.add(txtTrend);
		
		JPanel panelSide = new JPanel();
		boxPrincipal.add(panelSide);
		
		panelSide.setBorder(new TitledBorder("Coté à rechrecher"));
		
		rbtnLeft = new JRadioButton("Gauche");
		rbtnLeft.setSelected(true);
		panelSide.add(rbtnLeft);
		
		rbtnRight = new JRadioButton("Droit");
		panelSide.add(rbtnRight);
		
		rbtnBoth = new JRadioButton("Les deux");
		panelSide.add(rbtnBoth);
		
		sideGroup = new ButtonGroup();
		sideGroup.add(rbtnRight);
		sideGroup.add(rbtnLeft);
		sideGroup.add(rbtnBoth);
		
		JPanel panelOrder = new JPanel();
		boxPrincipal.add(panelOrder);
		
		panelOrder.setBorder(new TitledBorder("Ordonner par"));

		rbtnConf = new JRadioButton("Confiance");
		rbtnConf.setSelected(true);
		panelOrder.add(rbtnConf);
		
		rbtnFreq = new JRadioButton("Fréquence");
		panelOrder.add(rbtnFreq);
		
		ButtonGroup orderGroup = new ButtonGroup();
		orderGroup.add(rbtnConf);
		orderGroup.add(rbtnFreq);
		
		JPanel panelMins = new JPanel();
		boxPrincipal.add(panelMins);
		
		panelMins.setBorder(new TitledBorder("Seuils minimaux"));

		JLabel lblMinConf = new JLabel("Confiance");
		panelMins.add(lblMinConf);
		
		MaskFormatter formatConf;
		MaskFormatter formatFreq;
		try {
			formatConf = new MaskFormatter("#.##");
			formatFreq = new MaskFormatter("#.##");
		} catch (ParseException e) { return; }
		
		txtMinConf = new JFormattedTextField(formatConf);
		txtMinConf.setColumns(5);
		panelMins.add(txtMinConf);
		
		JLabel lblMinFreq = new JLabel("Fréquence");
		panelMins.add(lblMinFreq);
		
		txtMinFreq = new JFormattedTextField(formatFreq);
		txtMinFreq.setColumns(5);
		panelMins.add(txtMinFreq);
		
		minsGroup = new ButtonGroup();
		minsGroup.add(rbtnConf);
		minsGroup.add(rbtnFreq);
		
		JButton btnSearch = new JButton("Rechercher");
		btnSearch.addActionListener(new SearchTrendAction(this));
		boxPrincipal.add(btnSearch);
		
	}
	
	public void showRules(List<Rule> rules) {
		
		JDialog dialogRules = new JDialog(this, true);
		dialogRules.setTitle("Règles d'associations");
		dialogRules.setSize(new Dimension(600, 400));
		
		int nbCol = 3;
		
		Vector<String> header = new Vector<String>(nbCol);
		header.add("Règle");
		header.add("Confiance");
		header.add("Fréquence");
		
		Vector<Vector<String>> rowData = new Vector<Vector<String>>();
		for (Rule rule : rules) {
			Vector<String> oneRow = new Vector<String>(nbCol);
			
			oneRow.add(rule.toString().substring(
								0, rule.toString().lastIndexOf(" :")));
			oneRow.add(""+rule.getConf()*100 + " %");
			oneRow.add(""+rule.getFreq());
			
			rowData.add(oneRow);
		}

		
		JTable tableRules = new JTable(rowData, header);
		
		tableRules.setEnabled(false);
		tableRules.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableRules.getColumnModel().getColumn(0).setPreferredWidth(400);
		
		JScrollPane scrollPane =
				new JScrollPane(tableRules,
								JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
								JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		dialogRules.add(scrollPane);
		
		scrollPane.setVisible(true);
		dialogRules.setVisible(true);
		
	}
	
	public SearchIn getSearchSide() {
		if (rbtnLeft.isSelected())
			return SearchIn.LEFT;
		else if (rbtnRight.isSelected())
			return SearchIn.RIGHT;
		else if (rbtnBoth.isSelected())
			return SearchIn.BOTH;
		else return null;
	}
	
	public OrderBy getOrder() {
		if (rbtnConf.isSelected())
			return OrderBy.CONF;
		else if (rbtnFreq.isSelected())
			return OrderBy.FREQ;
		else return null;
	}

	public JTextField getTxtTrend() {
		return txtTrend;
	}

	public JTextField getTxtMinConf() {
		return txtMinConf;
	}

	public JTextField getTxtMinFreq() {
		return txtMinFreq;
	}

}
