package org.megastage.ecs;

import org.megastage.components.DeleteFlag;
import org.megastage.components.srv.InitializeFlag;

public class Test {

    public static void main(String[] args) throws Exception {
        World wem = new World(10, CompType.size);
        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());
        printEntities(wem);
        
        wem.deleteEntity(3);
        printEntities(wem);
        
        wem.deleteEntity(5);
        printEntities(wem);
        
        wem.deleteEntity(2);
        printEntities(wem);
        
        wem.deleteEntity(4);
        printEntities(wem);

        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());

        printEntities(wem);

        wem.setComponent(6, CompType.DeleteFlag, new DeleteFlag());
        wem.setComponent(8, CompType.DeleteFlag, new DeleteFlag());
        wem.setComponent(7, CompType.DeleteFlag, new DeleteFlag());
        wem.setComponent(7, CompType.InitializeFlag, new InitializeFlag());
        wem.setComponent(8, CompType.InitializeFlag, new InitializeFlag());
        
        for(DeleteFlag pos = wem.compIter(8, DeleteFlag.class); pos != null; pos = wem.compNext()) {
            System.out.println(pos.toString() + pos.getClass().getName());
        }

        Group g1 = wem.createGroup(CompType.DeleteFlag, CompType.InitializeFlag);
        Group g2 = wem.createGroup(CompType.DeleteFlag);
        Group g3 = wem.createGroup();
        Group g4 = wem.createGroup(CompType.NONE);

        System.out.println("G1 pairs");
        for(Group g = g1.pairIterator(); g.nextPair(); /**/) {
            System.out.println(g.left + " " + g.right);
        }
        
        System.out.println("G2 pairs");
        for(Group g = g2.pairIterator(); g.nextPair(); /**/) {
            System.out.println(g.left + " " + g.right);
        }
        
        System.out.println("G3 pairs");
        for(Group g = g3.pairIterator(); g.nextPair(); /**/) {
            System.out.println(g.left + " " + g.right);
        }
        
        System.out.println("G4 pairs");
        for(Group g = g4.pairIterator(); g.nextPair(); /**/) {
            System.out.println(g.left + " " + g.right);
        }
        
        System.out.println("G1");
        for (int i = g1.iterator(); i != 0; i = g1.next()) {
            System.out.println("" + i);
        }
        System.out.println("G2");
        for (int i = g2.iterator(); i != 0; i = g2.next()) {
            System.out.println("" + i);
        }
        System.out.println("G3");
        for (int i = g3.iterator(); i != 0; i = g3.next()) {
            System.out.println("" + i);
        }
        wem.deleteEntity(8);
        System.out.println("G1");
        for (int i = g1.iterator(); i != 0; i = g1.next()) {
            System.out.println("" + i);
        }
        System.out.println("G2");
        for (int i = g2.iterator(); i != 0; i = g2.next()) {
            System.out.println("" + i);
        }
        System.out.println("G3");
        for (int i = g3.iterator(); i != 0; i = g3.next()) {
            System.out.println("" + i);
        }
        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());
        System.out.println("create " + wem.createEntity());
        
        printEntities(wem);

        System.out.println("create " + wem.createEntity());
    }

    public static void printEntities(World wem) {
        System.out.print("Entities:");
        for (int i = wem.eidIter(); i != 0; i = wem.eidNext()) {
            System.out.print(" " + i);
        }
        System.out.println();
    }
}

