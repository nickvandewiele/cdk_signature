package test_stochastic;

import org.junit.Test;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.signature.MoleculeSignature;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.structgen.deterministic.FragmentConverter;
import org.openscience.cdk.structgen.deterministic.TargetMolecularSignature;
import org.openscience.cdk.structgen.stochastic.RandomSignatureStructureGenerator;

import test_deterministic.AbstractDeterministicTest;

public class RandomSignatureStructureGeneratorTest {
    
public static SmilesGenerator smilesGenerator = new SmilesGenerator();
    
    public static IChemObjectBuilder builder = 
        NoNotificationChemObjectBuilder.getInstance();
    
    public static String toSmiles(IAtomContainer container) {
        if (ConnectivityChecker.isConnected(container)) {
            return smilesGenerator.createSMILES(
                    builder.newInstance(IMolecule.class, container));
        } else {
            return "disconnected";
        }
    }
    
    public void genStructure(TargetMolecularSignature tms, String formulaString) {
        RandomSignatureStructureGenerator generator = 
            new RandomSignatureStructureGenerator(formulaString, tms);
        IAtomContainer container = generator.generate();
        System.out.println(toSmiles(container));
    }
    
    @Test
    public void twistaneTest() {
        TargetMolecularSignature tms = new TargetMolecularSignature(1);
        tms.add("[C]([C][C][C][H])", 4);
        tms.add("[C]([C][C][H][H])", 6);
        tms.add("[H]([C])", 16);
        String formulaString = "C10H16";
        genStructure(tms, formulaString);
    }

    @Test
    public void mixedCarbonC6H14Test() {
        TargetMolecularSignature tms = new TargetMolecularSignature(1);
        tms.add("[C]([C][C][C][H])", 1);
        tms.add("[C]([C][C][H][H])", 2);
        tms.add("[C]([C][H][H][H])", 3);
        tms.add("[H]([C])", 14);
        String formulaString = "C6H14";
        genStructure(tms, formulaString);
    }
    
    @Test
    public void mixedCarbonC7H16Test() {
        TargetMolecularSignature tms = new TargetMolecularSignature(1);
        tms.add("[C]([C][C][C][H])", 1);
        tms.add("[C]([C][C][H][H])", 3);
        tms.add("[C]([C][H][H][H])", 3);
        tms.add("[H]([C])", 16);
        String formulaString = "C7H16";
        genStructure(tms, formulaString);
    }
    
    @Test
    public void degreeThreeDodecahedraneTest() {
        IAtomContainer degreeThreeFragment = 
            AbstractDeterministicTest.makeDegreeThreeFragment();
        int count = 20;
        TargetMolecularSignature tms = 
            FragmentConverter.convert(degreeThreeFragment, count);
        System.out.println(tms);
        String formulaString = "C20H20";
        genStructure(tms, formulaString);
    }
    
    @Test
    public void degreeThreeHeight2Test() {
        TargetMolecularSignature tms = new TargetMolecularSignature(2);
        tms.add("[C]([C]([C][C][H])[C]([C][C][H])[C]([C][C][H])[H])", 12);
        tms.add("[H]([C]([C][C][C]))", 12);
        String formulaString = "C12H12";
        genStructure(tms, formulaString);
    }
    
    @Test
    public void degreeThreeCubaneCuneaneTest() {
        IAtomContainer degreeThreeFragment = 
            AbstractDeterministicTest.makeDegreeThreeFragment();
        int count = 8;
        TargetMolecularSignature tms = 
            FragmentConverter.convert(degreeThreeFragment, count);
        System.out.println(tms);
        String formulaString = "C8H8";
        genStructure(tms, formulaString);
    }
    
    @Test
    public void cubaneHeight2Test() {
        IMolecule mol = builder.newInstance(IMolecule.class);
        for (int i = 0; i < 8; i++) { mol.addAtom(builder.newInstance(IAtom.class,"C")); }
        mol.addBond(0, 1, IBond.Order.SINGLE);
        mol.addBond(0, 3, IBond.Order.SINGLE);
        mol.addBond(0, 7, IBond.Order.SINGLE);
        mol.addBond(1, 2, IBond.Order.SINGLE);
        mol.addBond(1, 6, IBond.Order.SINGLE);
        mol.addBond(2, 3, IBond.Order.SINGLE); 
        mol.addBond(2, 5, IBond.Order.SINGLE); 
        mol.addBond(3, 4, IBond.Order.SINGLE);
        mol.addBond(4, 5, IBond.Order.SINGLE);
        mol.addBond(4, 7, IBond.Order.SINGLE);
        mol.addBond(5, 6, IBond.Order.SINGLE);
        mol.addBond(6, 7, IBond.Order.SINGLE);
        for (int i = 0; i < 8; i++) { 
            mol.addAtom(builder.newInstance(IAtom.class,"H"));
            mol.addBond(i, 8 + i, IBond.Order.SINGLE);
            System.out.println("bonding " + i + " and " + 2 * (i + 1));
        }
//        System.out.println(AbstractDeterministicTest.toSmiles(mol));
        
        MoleculeSignature molSig = new MoleculeSignature(mol);
        String height2CSig = molSig.signatureStringForVertex(0, 2);
        String height2HSig = molSig.signatureStringForVertex(8, 2);
        System.out.println(height2CSig);
        System.out.println(height2HSig);
        
        TargetMolecularSignature tms = new TargetMolecularSignature(2);
        tms.add(height2CSig, 8);
        tms.add(height2HSig, 8);
        String formulaString = "C8H8";
        genStructure(tms, formulaString);
    }
    
    @Test
    public void pineneTest() {
        TargetMolecularSignature tms = new TargetMolecularSignature(1);
        tms.add("[C]([C][C][C][C])", 1);    // four carbons
        tms.add("[C]([C]=[C][H])", 1);      // double bond, carbon and hydrogen
        tms.add("[C]([C][C]=[C])", 1);      // double bond, two carbons
        tms.add("[C]([C][C][C][H])", 2);    // CH
        tms.add("[C]([C][C][H][H])", 2);    // CH2
        tms.add("[C]([C][H][H][H])", 3);    // CH3
        tms.add("[H]([C])", 16);
        String formulaString = "C10H16";
        genStructure(tms, formulaString);
    }
    
    
    @Test
    public void multipleBondedRingHeight2Test() {
        TargetMolecularSignature tms = new TargetMolecularSignature(2);
        String a = "[C](=[C]([C][H])[C](=[C][H])[H])"; 
        String b = "[C]([C]([C]=[C])=[C]([C][H])[H])";
        String c = "[C](=[C]([C][C])[C](=[C][H])[C](=[C][H]))";
        String h = "[H]([C]([C]=[C]))";
        
        tms.add(a, 4);
        tms.add(b, 4);
        tms.add(c, 4);
        tms.add(h, 8);
        String formulaString = "C10H8";
        genStructure(tms, formulaString);
    }
    
    public static void main(String[] args) {
//        new RandomSignatureStructureGeneratorTest().mixedCarbonC6H14Test();
//        new RandomSignatureStructureGeneratorTest().mixedCarbonC7H16Test();
//        new RandomSignatureStructureGeneratorTest().twistaneTest();
//        new RandomSignatureStructureGeneratorTest().degreeThreeDodecahedraneTest();
//        new RandomSignatureStructureGeneratorTest().degreeThreeHeight2Test();
//        new RandomSignatureStructureGeneratorTest().degreeThreeCubaneCuneaneTest();
        new RandomSignatureStructureGeneratorTest().cubaneHeight2Test();
//        new RandomSignatureStructureGeneratorTest().pineneTest();
//        new RandomSignatureStructureGeneratorTest().multipleBondedRingHeight2Test();
    }
}
