package app;

import java.awt.Dimension;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;

import org.openscience.cdk.structgen.deterministic.Graph;

public class GraphThumbViewer extends JPanel {
    
    public int numberOfPanels;
    
    public int thumbWidth;
    
    public int thumbHeight;
    
    private JScrollPane scrollPane;
    
    private JList list;
    
    public GraphThumbViewer(int w, int h) {
        numberOfPanels = 0;
        thumbWidth = 300;
        thumbHeight = 150;
        list = new JList(new DefaultListModel());
        list.setCellRenderer(new GraphListCellRenderer(thumbWidth, thumbHeight));
        scrollPane = new JScrollPane(list, 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane);
        scrollPane.setPreferredSize(new Dimension(w, h));
    }
    
    public void addSelectionListener(ListSelectionListener listener) {
        list.addListSelectionListener(listener);
    }
    
    public Graph getSelected() {
        Object o = list.getSelectedValue();
        if (o == null) {
            return null;
        } else {
            if (o instanceof Graph) {
                return (Graph) o;
            } else {
                return null;
            }
        }
    }
    
    public void addGraph(Graph graph) {
        ((DefaultListModel)list.getModel()).addElement(graph);
        repaint();
    }

    public void clear() {
        ((DefaultListModel)list.getModel()).clear();
        repaint();
    }

    public void setGraphs(List<Graph> selected) {
        clear();
        DefaultListModel model = (DefaultListModel) list.getModel();
        for (Graph g : selected) {
            model.addElement(g);
        }
        repaint();
    }

}
