package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.ExtendedAtomGenerator;
import org.openscience.cdk.renderer.generators.IAtomContainerGenerator;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactAtom;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.CompactShape;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator.ShowExplicitHydrogens;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.BackGroundColor;
import org.openscience.cdk.renderer.generators.ExtendedAtomGenerator.ShowImplicitHydrogens;
import org.openscience.cdk.renderer.selection.IChemObjectSelection;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.signature.MoleculeFromSignatureBuilder;
import org.openscience.cdk.structgen.deterministic.Graph;

import signature.AbstractVertexSignature;

public class MoleculePanel extends JPanel {
    
    private AtomContainerRenderer renderer;
    
    private StructureDiagramGenerator sdg;
    
    private IMolecule molecule;
    
    public int moleculeWidth;
    
    public int moleculeHeight;

    private ExtendedAtomGenerator extendedAtomGenerator;

    private NumberingGenerator numberingGenerator;
    
    public MoleculePanel(int panelWidth, int panelHeight) {
        this(panelWidth, panelHeight, new ArrayList<IAtomContainerGenerator>());
    }
    
    public MoleculePanel(int panelWidth, int panelHeight, 
            List<IAtomContainerGenerator> initialGenerators) {
        initialGenerators.addAll(getGenerators());
        renderer = new AtomContainerRenderer(
                initialGenerators, new AWTFontManager());
        setRenderingParameters();
        sdg = new StructureDiagramGenerator();
        this.setPreferredSize(new Dimension(panelWidth, panelHeight));
        this.setBackground(Color.WHITE);
    }
    
    public void toggleShowNumbers() {
        numberingGenerator.on = !numberingGenerator.on;
        repaint();
    }
    
    public void selectAtom(int atomIndex) {
        IChemObjectSelection selection = 
            new BondFlagSelection(atomIndex, molecule);
        renderer.getRenderer2DModel().setSelection(selection);
    }
    
    private void setRenderingParameters() {
        RendererModel model = renderer.getRenderer2DModel();
//        model.setDrawNumbers(true);
        model.setDrawNumbers(false);
        
        model.getRenderingParameter(CompactShape.class).setValue(
                        BasicAtomGenerator.Shape.OVAL);
        model.getRenderingParameter(CompactAtom.class).setValue(true);
//        model.getRenderingParameter(KekuleStructure.class).setValue(true);
        model.getRenderingParameter(ShowExplicitHydrogens.class).setValue(false);
        model.getRenderingParameter(ShowImplicitHydrogens.class).setValue(false);
        model.getRenderingParameter(BackGroundColor.class).setValue(Color.blue);
//        for (IGeneratorParameter p : model.getRenderingParameters()) {
//            System.out.println(p.getClass().getSimpleName() + " " + p.getValue());
//        }
//        System.out.println("IS COMPACT : " + model.getRenderingParameter(
//                BasicAtomGenerator.CompactAtom.class).getValue());
    }

    private List<IAtomContainerGenerator> getGenerators() {
        List<IAtomContainerGenerator> generators = 
            new ArrayList<IAtomContainerGenerator>();
//        generators.add(new RingGenerator());
//        generators.add(new BasicBondGenerator());
        generators.add(new BasicSceneGenerator());
        generators.add(new TmpBondGenerator());
        extendedAtomGenerator = new ExtendedAtomGenerator();
        generators.add(extendedAtomGenerator);
        
        numberingGenerator = new NumberingGenerator();
        generators.add(numberingGenerator);
        
        return generators;
    }
    
    public void setMoleculeFromSignature(String signature) {
        MoleculeFromSignatureBuilder builder = 
            new MoleculeFromSignatureBuilder(
                    NoNotificationChemObjectBuilder.getInstance());
        builder.makeFromColoredTree(AbstractVertexSignature.parse(signature));
        IAtomContainer container = builder.getAtomContainer();
        setMolecule(
                container.getBuilder().newInstance(IMolecule.class, container));
    }
    
    public void setMoleculeFromGraph(Graph graph) {
        IAtomContainer atomContainer = graph.getAtomContainer();
        
        try {
            IAtomContainer clonedContainer = 
                (IAtomContainer) atomContainer.clone();
            int i = 0;
            List<IAtom> keptAtoms = new ArrayList<IAtom>();
            for (IAtom atom : clonedContainer.atoms()) {
                if (clonedContainer.getConnectedAtomsCount(atom) == 0) {
//                    clonedContainer.removeAtom(atom);
//                    System.out.println("removing atom " + i);
                } else {
//                    System.out.println("keeping atom " + i);
                    keptAtoms.add(atom);
                }
                i++;
            }
            IAtom[] keptAtomArr = new IAtom[keptAtoms.size()];
            for (int j = 0; j < keptAtoms.size(); j++) { 
                keptAtomArr[j] = keptAtoms.get(j); 
            }
            clonedContainer.setAtoms(keptAtomArr);
//            System.out.println(new Graph(mol));
            if (ConnectivityChecker.isConnected(clonedContainer)) {
                System.out.println("CONNECTED");
                IMolecule mol = 
                    clonedContainer.getBuilder().newInstance(IMolecule.class);
                for (IAtom a : clonedContainer.atoms()) { mol.addAtom(a); }
                for (IBond b : clonedContainer.bonds()) { mol.addBond(b); }
                setMolecule(mol);
            } else {
                System.out.println("NOT CONNECTED");
                IMoleculeSet molecules = 
                    ConnectivityChecker.partitionIntoMolecules(clonedContainer);
                setMolecule(molecules.getMolecule(0));
            }
        } catch (CloneNotSupportedException cnse) {
            
        }
    }
    
    public void setMolecule(IMolecule molecule) {
        if (ConnectivityChecker.isConnected(molecule)) {
            this.molecule = diagramGenerate(molecule);
        } else {
            IMoleculeSet molecules = 
                ConnectivityChecker.partitionIntoMolecules(molecule);
            this.molecule = diagramGenerate(molecules.getMolecule(0));
        }
        for (int i = 0; i < this.molecule.getAtomCount(); i++) {
            IAtom a = this.molecule.getAtom(i);
            if (a != null) {
                a.setFormalCharge(0);
            }
        }
        System.out.println("Setting molecule " + new Graph(molecule));
        repaint();
    }

    public void setMoleculeWithoutLayout(IMolecule molecule) {
        this.molecule = molecule;
        repaint();
    }
    
    private IMolecule diagramGenerate(IMolecule molecule) {
        this.sdg.setMolecule(molecule, true);
        try {
            this.sdg.generateCoordinates();
        } catch (Exception c) {
            c.printStackTrace();
            return null;
        }
        return sdg.getMolecule();
    }
    
    public void paint(Graphics g) {
        super.paint(g);
        if (molecule != null) {
            Graphics2D g2 = (Graphics2D) g;
            try {
                Rectangle b = new Rectangle(getWidth(), getHeight());
//                renderer.setup(molecule, getBounds());
                renderer.setup(molecule, b);
                renderer.paintMolecule(
//                        molecule, new AWTDrawVisitor(g2), getBounds(), false);
                        molecule, new AWTDrawVisitor(g2), b, false);
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }

        }
    }

    public void clear() {
        this.molecule = null;
    }
    
}