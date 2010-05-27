package test_deterministic;

import junit.framework.Assert;

import org.junit.Test;
import org.openscience.cdk.graph.AtomContainerAtomPermutor;
import org.openscience.cdk.group.Permutation;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.structgen.deterministic.AlternateCanonicalChecker;
import org.openscience.cdk.structgen.deterministic.CanonicalChecker;

public class CanonicalCheckerTest {
    
    private IChemObjectBuilder builder = 
        NoNotificationChemObjectBuilder.getInstance();
    
    public boolean canonical(IAtomContainer ac) {
//        return CanonicalChecker.isCanonical(ac);
        return AlternateCanonicalChecker.isCanonicalByVisitor(ac);
    }
    
    public IAtomContainer makeDisconnectedSeparateBonds() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(2, 3, IBond.Order.SINGLE);
        return ac;
    }
    
    public IAtomContainer makeDisconnectedNestedBonds() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addBond(0, 3, IBond.Order.SINGLE);
        ac.addBond(1, 2, IBond.Order.SINGLE);
        return ac;
    }
    
    public IAtomContainer makeDisconnectedOverlappingBonds() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addBond(0, 2, IBond.Order.SINGLE);
        ac.addBond(1, 3, IBond.Order.SINGLE);
        return ac;
    }
    
    @Test
    public void getSignatureListDisconnectedOverlappingTest() {
        IAtomContainer ac = makeDisconnectedOverlappingBonds();
        Assert.assertEquals(2, 
                AlternateCanonicalChecker.getSignatureList(ac).size());
    }
    
    @Test
    public void getSignatureListDisconnectedSeparateTest() {
        IAtomContainer ac = makeDisconnectedSeparateBonds();
        Assert.assertEquals(2, 
                AlternateCanonicalChecker.getSignatureList(ac).size());
    }
    
    @Test
    public void getSignatureListDisconnectedNestedTest() {
        IAtomContainer ac = makeDisconnectedNestedBonds();
        Assert.assertEquals(2, 
                AlternateCanonicalChecker.getSignatureList(ac).size());
    }
    
    @Test
    public void trivialTest() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        Assert.assertEquals(true, canonical(ac));
    }
    
    @Test
    public void disconnectedSeparateBondsTest() {
        IAtomContainer ac = makeDisconnectedSeparateBonds();
        Assert.assertEquals(true, canonical(ac));
    }
    
    @Test
    public void disconnectedNestedBondsTest() {
        IAtomContainer ac = makeDisconnectedNestedBonds();
        Assert.assertEquals(true, canonical(ac));
    }

    @Test
    public void disconnectedOverlappingBondsTest() {
        IAtomContainer ac = makeDisconnectedOverlappingBonds();
        Assert.assertEquals(true, canonical(ac));
    }
    
    @Test
    public void testFourCycle() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addAtom(builder.newInstance(IAtom.class,"C"));
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        ac.addBond(1, 3, IBond.Order.SINGLE);
        ac.addBond(2, 3, IBond.Order.SINGLE);
        Assert.assertEquals(true, canonical(ac));
    }
    
    @Test
    public void cycloButeneMethyl1ene() {
        IAtomContainer ac = builder.newInstance(IAtomContainer.class);
        for (int i = 0; i < 4; i++) {
            ac.addAtom(builder.newInstance(IAtom.class,"C"));
        }
        
        for (int i = 0; i < 4; i++) {
            ac.addAtom(builder.newInstance(IAtom.class,"H"));
        }
        ac.addBond(0, 1, IBond.Order.SINGLE);
        ac.addBond(0, 2, IBond.Order.SINGLE);
        ac.addBond(0, 3, IBond.Order.DOUBLE);
        ac.addBond(1, 2, IBond.Order.DOUBLE);
        ac.addBond(1, 4, IBond.Order.SINGLE);
        ac.addBond(2, 5, IBond.Order.SINGLE);
        ac.addBond(3, 6, IBond.Order.SINGLE);
        ac.addBond(3, 7, IBond.Order.SINGLE);
        findCanonical(ac);
        Assert.assertEquals(true, 
//                CanonicalChecker.isCanonicalWithColorPartition(ac));
                canonical(ac));
    }
    
    public void findCanonical(IAtomContainer container) {
        AtomContainerAtomPermutor permutor = 
            new AtomContainerAtomPermutor(container);
        while (permutor.hasNext()) {
            IAtomContainer permutation = permutor.next(); 
            if (canonical(permutation)) {
                int n = container.getAtomCount();
                String edgestring = 
                    CanonicalChecker.edgeString(permutation, new Permutation(n));
                System.out.println("YES " + edgestring);
                return;
            }
        }
        System.out.println("NO");
    }

}
