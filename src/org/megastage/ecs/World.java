package org.megastage.ecs;

import org.megastage.util.Log;
import java.util.HashMap;
import java.util.Map;

public class World {
    public transient static World INSTANCE;

    // stores components for each entity
    public transient int capacity;
    public transient int componentCapacity;
    public int size;
    public BaseComponent[][] population;

    // manages int ids
    public int[] next, prev;
    public boolean[] free;

    // manage groups (start using Bag if lot of changes)
    private transient Group[] groups = new Group[100];
    private transient int groupsSize = 0;

    // manage systems (start using Bag if lot of changes)
    private transient Processor[] processors = new Processor[100];
    private transient Map<Class<? extends Processor>, Processor> processorsMap = new HashMap<>();
    
    private transient int processorsSize = 0;
    
    // time management
    public long tickCount = 0;
    public long time = 0;
    public float delta = 0.0f;
    protected long offset = 0;
    
    public World() {
        this(10000, CompType.size);
    }

    public World(int entityCapacity, int componentCapacity) {
        // I know, this should be moved to initialize() ...
        INSTANCE = this;
        
        capacity = entityCapacity;
        this.componentCapacity = componentCapacity;
        
        int len = capacity + 2;
        population = new BaseComponent[len][];

        for (int i = 0; i < len; i++) {
            population[i] = new BaseComponent[componentCapacity];
        }

        next = new int[len];
        prev = new int[len];
        free = new boolean[len];

        // capacity = 5 (3)
        // 0: 0, 0
        // 1: 2, 4
        // 2: 3, 1
        // 3: 4, 2
        // 4: 1, 3

        int lastIndex = len - 1;

        next[1] = 2;
        prev[1] = lastIndex;
        
        for (int i = 2; i < len; i++) {
            next[i] = (i % lastIndex) + 1;
            prev[i] = i - 1;
            free[i] = true;
        }
    }
    
    public void tick(long clockTime) {
        if(tickCount++ == 0) {
            offset = -clockTime;
            Log.info("Time offset: " + offset);
        }
        
        long gameTime = clockTime + offset;
        delta = (gameTime - time) / 1000.0f;
        time = gameTime;
        
        // Log.info("Time: %s -> %s", World.INSTANCE.delta, World.INSTANCE.time);

        tick();
    }
    
    public void tick() {
        for(int i=0; i < processorsSize; i++) {
            // Log.info(processors[i].getClass().getSimpleName());
            if(processors[i].checkProcessing()) {
                try {
                    processors[i].process();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public void initialize() {
        for(int i=0; i < processorsSize; i++) {
            processors[i].initialize();
        }
    }
    
    public void addProcessor(Processor processor) {
        processors[processorsSize++] = processor;
        processorsMap.put(processor.getClass(), processor);
    }
    
    public <T extends Processor> T getProcessor(Class<T> processorType) {
        return processorType.cast(processorsMap.get(processorType));
    }

    public Group createGroup(int... cid) {
        if (groupsSize == groups.length) {
            throw new RuntimeException("No space for new group");
        }
        
        return groups[groupsSize++] = new Group(this, cid);
    }

    public int createEntity() {
        if(size == capacity) {
            throw new RuntimeException("No space for new entity");
        }

        // use free list head
        return createEntity(next[1]);
    }
    
    public void ensureEntity(int eid) {
        if(free[eid]) {
            createEntity(eid);
        }
    }
    
    public int createEntity(int eid) {
        assert free[eid]: "Entity already allocated " + eid;
        
        size++;

        // remove from free list
        prev[next[eid]] = prev[eid];
        next[prev[eid]] = next[eid];
        free[eid] = false;
        
        // add to allocated list
        next[eid] = 0;
        prev[eid] = prev[0];
        next[prev[0]] = eid;
        prev[0] = eid;

        updateEntityInAllGroups(eid);
        
        return eid;
    }

    public void deleteEntity(int eid) {
        assert !free[eid]: "No entity " + eid;
        
        // give components possibility to cleanup
        for(BaseComponent bc = compIter(eid); bc != null; bc = compNext()) {
            bc.delete(eid);
        }

        // clean all references
        for(BaseComponent bc = compIter(eid); bc != null; bc = compNext()) {
            population[eid][compIterPos] = null;
            population[eid][CompType.parent[compIterPos]] = null;
        }

        // remove eid from allocated list
        prev[next[eid]] = prev[eid];
        next[prev[eid]] = next[eid];
        free[eid] = true;
        
        // append to free list
        next[eid] = 1;
        prev[eid] = prev[1];
        next[prev[1]] = eid;
        prev[1] = eid;
        
        size--;

        updateEntityInAllGroups(eid);
    }

    public boolean hasEntity(int eid) {
        return !free[eid];
    }

    public <E extends BaseComponent> E setComponent(int eid, int cid, E comp) {
        ensureEntity(eid);

        population[eid][cid] = comp;
        population[eid][CompType.parent[cid]] = comp;
        
        updateEntityInAllGroups(eid);
        return comp;
    }

    public <E extends BaseComponent> E setComponent(int eid, E comp) {
        return setComponent(eid, CompType.cid(comp.getClass().getSimpleName()), comp);
    }

    public void removeComponent(int eid, int cid) {
        assert !free[eid]: "No entity " + eid;

        population[eid][cid].delete(eid);

        population[eid][cid] = null;
        population[eid][CompType.parent[cid]] = null;
        
        updateEntityInAllGroups(eid);
    }

    public BaseComponent getComponent(int eid, int cid) {
        if(hasEntity(eid)) {
            return population[eid][cid];
        }
        return null;
    }

    public <T extends BaseComponent> T getOrCreateComponent(int eid, int cid, Class<T> type) {
        ensureEntity(eid);
        
        BaseComponent comp = population[eid][cid];
        if(comp == null) {
            try {
                comp = population[eid][cid] = type.newInstance();
                updateEntityInAllGroups(eid);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return type.cast(comp);
    }
    
    public boolean hasComponent(int eid, int cid) {
        assert !free[eid]: "No entity " + eid;

        return population[eid][cid] != null;
    }
    
    public boolean hasComponent(int eid, Class type) {
        assert !free[eid]: "No entity " + eid;

        Object obj = population[eid][CompType.cid(type.getSimpleName())];
        return obj != null && type.isInstance(obj);
    }

    private void updateEntityInAllGroups(int eid) {
        for (int i = 0; i < groupsSize; i++) {
            groups[i].update(eid);
        }
    }
    
    public void updateAll() {
        for(int eid=eidIter(); eid > 0; eid=eidNext()) {
            updateEntityInAllGroups(eid);
        }
    }

    // Here is the single thread iterator
    private transient int eidIterPos;

    public int eidIter() {
        return eidIterPos = next[0];
    }

    public int eidNext() {
        return eidIterPos = next[eidIterPos];
    }

    private transient int compIterEID = 0;
    private transient int compIterPos = 0;
    private transient Class compIterType;

    public BaseComponent compIter(int eid) {
        return compIter(eid, BaseComponent.class);
    }

    public <E> E compIter(int eid, Class<E> clazz) {
        compIterEID = eid;
        compIterPos = 0;
        compIterType = clazz;
        return (E) compNext();
    }
    
    public <E> E compNext() {
        while((++compIterPos) < componentCapacity) {
            Object comp = population[compIterEID][compIterPos];
            if(comp != null && compIterType.isInstance(comp) && CompType.parent[compIterPos] == 0) {
                return (E) population[compIterEID][compIterPos];
            }
        }

        return null;
    }

    private static class BCInteger extends BaseComponent {
        public int val = 0;
        public BCInteger(int v) { val = v; }
    }
    
    private static class BCDouble extends BaseComponent {
        public double val = 0;
        public BCDouble(double v) { val = v; }
    }
    
    public static void main(String[] args) throws Exception {
        System.out.println("Starting");
        World w = new World(10,10);
        int eid = w.createEntity();
        w.setComponent(eid, 0, new BCInteger(0));
        w.setComponent(eid, 3, new BCDouble(3.0));
        w.setComponent(eid, 5, new BCInteger(5));
        w.setComponent(eid, 7, new BCDouble(7.0));
        w.setComponent(eid, 9, new BCInteger(9));
        
        System.out.println("List of all");
        for(Object comp = w.compIter(eid); comp != null; comp = w.compNext()) {
            System.out.println(comp.toString());
        }

        System.out.println("Integer");
        for(BaseComponent comp = w.compIter(eid); comp != null; comp = w.compNext()) {
            System.out.println(comp.toString());
        }
    }
}
