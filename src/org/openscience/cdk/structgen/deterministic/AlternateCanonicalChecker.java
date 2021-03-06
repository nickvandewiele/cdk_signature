package org.openscience.cdk.structgen.deterministic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.signature.MoleculeSignature;

import signature.AbstractVertexSignature;

public class AlternateCanonicalChecker {
    
    public static boolean isCanonicalByVisitor(IAtomContainer atomContainer) {
        return listInOrder(getLabels(atomContainer));
    }
    
    public static List<AbstractVertexSignature> getSignatureList(
            IAtomContainer container) {
        List<AbstractVertexSignature> signatureList = 
            new ArrayList<AbstractVertexSignature>();
        MoleculeSignature signature = new MoleculeSignature(container);
        int n = container.getAtomCount();
        List<List<Integer>> visitedLists = new ArrayList<List<Integer>>();
        for (int i = 0; i < n; i++) {
            if (inVisitedLists(i, visitedLists) 
                || container.getConnectedAtomsCount(
                        container.getAtom(i)) == 0) {
                continue;
            } else {
                AbstractVertexSignature avs = signature.signatureForVertex(i);
                visitedLists.add(getVisited(n, avs));
                signatureList.add(avs);
            }
            System.out.println("i = " + i + " visited = " + visitedLists);
        }
        return signatureList;
    }
    
    public static boolean listsInOrder(List<List<Integer>> lists) {
        // a single list is always in order
        if (lists.size() <= 1) return true;
        
        // 
        for (int i = 0; i < lists.size(); i++) {
            
        }
        return true;
    }
    
    public static boolean inVisitedLists(int i, List<List<Integer>> lists) {
        for (List<Integer> visitedList : lists) {
            if (visitedList.contains(i)) {
                return true;
            }
        }
        return false;
    }
    
    // TODO : convert this to an AbstractVertexSignature method
    public static List<Integer> getVisited(int n, AbstractVertexSignature avs) {
        List<Integer> visited = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            int originalIndex = avs.getOriginalVertexIndex(i); 
            if (originalIndex != -1) {
                visited.add(originalIndex);
            }
        }
        return visited;
    }

    public static int[] getLabels(IAtomContainer graph) {
//        int[] labels = new MoleculeSignature(graph).getCanonicalLabels();
        MoleculeSignature molSig = new MoleculeSignature(graph);
        int n = graph.getAtomCount();
        AbstractVertexSignature canonicalSignature = null;
        String canonicalSignatureString = null;
        for (int i = 0; i < n; i++) {
            AbstractVertexSignature signatureForVertexI = 
                molSig.signatureForVertex(i);
            String signatureString = signatureForVertexI.toCanonicalString();
            if (canonicalSignature == null ||
                    signatureString.compareTo(canonicalSignatureString) > 0) {
                canonicalSignature = signatureForVertexI;
                canonicalSignatureString = signatureString;
            }
        }
        int[] labels = canonicalSignature.getCanonicalLabelling(n);
        System.out.println(Arrays.toString(labels));
        return labels;
    }
    
    public static boolean listInOrder(int[] intList) {
        if (intList.length < 2) return true;
        int prev = intList[0];
        for (int i = 1; i < intList.length; i++) {
            if (intList[i] == -1) continue;
            if (intList[i] < prev) return false;
            prev = intList[i];
        }
        return true;
    }
}
