package Test;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import edu.isi.karma.cleaning.Correctness.TransRecord;

public class ClusterVisualizer {
	public static int iter_no = 0;
	private HashMap<String, ArrayList<String[]>> data = new HashMap<String, ArrayList<String[]>>();
	public ClusterVisualizer()
	{
		
	}
	public void createAndShowGUI() {
		iter_no++;
		// Create and set up the window.
		JFrame frame = new JFrame("Iteration: "+iter_no);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setSize(700, 600);

		HashMap<String, String> display = process();
		for (String k : display.keySet()) {
			
			JLabel label = new JLabel(display.get(k));
			label.setAlignmentX(JLabel.TOP_ALIGNMENT);
			/*JTextPane label = new JTextPane();
			label.setContentType("text/html");
			label.setText(display.get(k));*/

			JScrollPane sp = new JScrollPane(label);
			sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			sp.setAlignmentY(JScrollPane.TOP_ALIGNMENT);
			frame.getContentPane().add(sp);			
		}
		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public HashMap<String, String> process() {
		HashMap<String, String> res = new HashMap<String, String>();
		for (String key : data.keySet()) {
			String cxt = "<html>";
			for (String[] e : data.get(key)) {
				String elem = "";
				if (e[1].compareTo("1") == 0) {
					elem= String.format("<font color='red'>%s</font>",e[0]);
				} else {
					elem= String.format("%s", e[0]);
				}
				if (e[2].compareTo("f") == 0) {
					elem = String.format("<p><u>%s</u></p>",elem);
				} else {
					elem = String.format("<p>%s</>",elem);
				}
				cxt += elem;
			}
			cxt += "/<html>";
			res.put(key, cxt);
		}
		return res;
	}

	public void addData(TransRecord rec, String lab) {
		String c = rec.label;
		if (data.containsKey(c)) {
			String[] x = { rec.org, lab, rec.correct };
			data.get(c).add(x);
		} else {
			ArrayList<String[]> tres = new ArrayList<String[]>();
			String[] x = { rec.org, lab, rec.correct };
			tres.add(x);
			data.put(c, tres);
		}
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		ClusterVisualizer clusterVisualizer = new ClusterVisualizer();
		clusterVisualizer.createAndShowGUI();
		/*javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});*/
	}

}
