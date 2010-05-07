package app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openscience.cdk.deterministic.BondCreationEvent;
import org.openscience.cdk.deterministic.BondCreationListener;
import org.openscience.cdk.deterministic.DeterministicEnumerator;
import org.openscience.cdk.deterministic.Graph;
import org.openscience.cdk.signature.TargetMolecularSignature;

import signature.AbstractVertexSignature;
import signature.display.ColoredTreePanel;

public class Debugger extends JFrame 
    implements ActionListener, BondCreationListener, ListSelectionListener, MouseListener {
    
    private DeterministicEnumerator enumerator;
    
    private GraphThumbViewer thumbViewer;
    
    private GraphPanel mainGraphPanel;
    
    private ColoredTreePanel treePanel;
    
    private ControlPanel controlPanel;
    
    public static final int THUMB_PANEL_WIDTH = 300;
    
    public static final int THUMB_PANEL_HEIGHT = 600;
    
    public static final int GRAPH_PANEL_WIDTH = 500;
    
    public static final int GRAPH_PANEL_HEIGHT = 300;
    
    public static final int TREE_PANEL_WIDTH = 500;
    
    public static final int TREE_PANEL_HEIGHT = 300;
    
    public Debugger() {
        setLayout(new BorderLayout());
        thumbViewer = new GraphThumbViewer(
                THUMB_PANEL_WIDTH, THUMB_PANEL_HEIGHT);
        thumbViewer.setBorder(BorderFactory.createEtchedBorder());
        thumbViewer.addSelectionListener(this);
        add(thumbViewer, BorderLayout.WEST);
        
        JPanel centralPanel = new JPanel(new BorderLayout());
        mainGraphPanel = new GraphPanel(
                GRAPH_PANEL_WIDTH, GRAPH_PANEL_HEIGHT, false);
        mainGraphPanel.setBorder(BorderFactory.createEtchedBorder());
        mainGraphPanel.addMouseListener(this);
        centralPanel.add(mainGraphPanel, BorderLayout.CENTER);
        
        treePanel = new ColoredTreePanel(TREE_PANEL_WIDTH, TREE_PANEL_HEIGHT);
        treePanel.setBorder(BorderFactory.createEtchedBorder());
        centralPanel.add(treePanel, BorderLayout.SOUTH);
        add(centralPanel, BorderLayout.CENTER);
        
        controlPanel = new ControlPanel();
        controlPanel.addRunListener(this);
        add(controlPanel, BorderLayout.EAST);
        
//        setPreferredSize(new Dimension(1200, 600));
        
        pack();
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        System.out.println("action performed");
        List<String> signatures = controlPanel.getSignatures();
        List<Integer> counts = controlPanel.getCounts();
        String formula = controlPanel.getCurrentFormula();
        if (signatures.size() != counts.size()) {
            System.err.println("SIGS != COUNTS");
            return;
        }
        System.out.println("formula = " + formula);
        for (int i = 0; i < signatures.size(); i++) {
            System.out.println(signatures.get(i) + " x" + counts.get(i));
        }
        
        run(formula, signatures, counts);
    }

    private void run(
            String formula, List<String> signatures, List<Integer> counts) {
        // XXX height!
        TargetMolecularSignature tms = new TargetMolecularSignature(1);
        for (int i = 0; i < signatures.size(); i++) {
            tms.add(signatures.get(i), counts.get(i));
        }
        enumerator = new DeterministicEnumerator(formula, tms);
        enumerator.setBondCreationListener(this);
        enumerator.generate();
    }

    public void bondAdded(BondCreationEvent bondCreationEvent) {
        thumbViewer.addGraph(bondCreationEvent.child);
        thumbViewer.repaint();
    }

    public void valueChanged(ListSelectionEvent e) {
        Graph selected = thumbViewer.getSelected();
        mainGraphPanel.setGraph(selected);
    }
    

    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        mainGraphPanel.select(x, y);
        int target = mainGraphPanel.getTarget();
        if (target != -1) {
            List<String> signatures = controlPanel.getSignatures();
            String selectedSignature = signatures.get(target);
            treePanel.setTree(AbstractVertexSignature.parse(selectedSignature));
        }
        repaint();
    }

    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }


    public static void main(String[] args) {
        new Debugger();
    }

}