package org.megastage.ecs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class World {
    public static World INSTANCE;

    // stores components for each entity
    public int capacity;
    public int componentCapacity;
    public int size;
    public Object[][] population;

    // manages int ids
    protected int[] next, prev;
    protected boolean[] free;

    // manage groups (start using Bag if lot of changes)
    private Group[] groups = new Group[100];
    private int groupsSize = 0;

    // manage systems (start using Bag if lot of changes)
    private Processor[] processors = new Processor[100];
    private Map<Class<? extends Processor>, Processor> processorsMap = new HashMap<>();
;
    private int processorsSize = 0;
    
    // time management
    public long time;
    public float delta;
    protected long offset;
    
    public World() {
        this(10000, CompType.size);
    }

    public World(int entityCapacity, int componentCapacity) {
        INSTANCE = this;
        
        capacity = entityCapacity;
        this.componentCapacity = componentCapacity;
        
        int len = capacity + 2;
        population = new Object[len][];

        for (int i = 0; i < len; i++) {
            population[i] = new Object[componentCapacity];
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
    
    public void setGametime(long clocktime) {
        time = clocktime + offset;
    }

    public void synchronizeClocks(long gametime, long clocktime) {
        offset = gametime - clocktime;
    }
    
    public void tick() {
        for(int i=0; i < processorsSize; i++) {
            if(processors[i].checkProcessing()) {
                processors[i].process();
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

        return createEntity(next[1]);
    }
    
    public void ensureEntity(int eid) {
        if(free[eid]) {
            createEntity(eid);
        }
    }
    
    public int createEntity(int eid) {
        if(!free[eid]) {
            throw new RuntimeException("Entity already allocated " + eid);
        }
        
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
        if(free[eid]) {
            throw new RuntimeException("Entity already free " + eid);
        }
        
        size--;

        // remove from allocated list
        prev[next[eid]] = prev[eid];
        next[prev[eid]] = next[eid];
        free[eid] = true;
        
        // add to free list
        next[eid] = 1;
        prev[eid] = prev[1];
        next[prev[1]] = eid;
        prev[1] = eid;
        
        Arrays.fill(population[eid], null);
        updateEntityInAllGroups(eid);
    }

    public boolean hasEntity(int eid) {
        return !free[eid];
    }

    public void addComponent(int eid, int cid, Object comp) {
        population[eid][cid] = comp;
        population[eid][CompType.parent[cid]] = comp;
        updateEntityInAllGroups(eid);
    }

    public void addComponent(int eid, Object comp) {
        addComponent(eid, CompType.cid(comp.getClass().getSimpleName()), comp);
    }

    public void removeComponent(int eid, int cid) {
        population[eid][cid] = null;
        population[eid][CompType.parent[cid]] = null;
        updateEntityInAllGroups(eid);
    }

    public Object getComponent(int eid, int cid) {
        return population[eid][cid];
    }

    /**
     * @deprecated 
     */
    public <T> T getOrCreateComponent(int eid, int cid, Class<T> type) {
        //ensureEntity(eid);
        Object comp = population[eid][cid];
        if(comp == null) {
            try {
                comp = population[eid][cid] = type.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return type.cast(comp);
    }
    
    public boolean hasComponent(int eid, int cid) {
        return population[eid][cid] != null;
    }
    
    public boolean hasComponent(int eid, Class type) {
        Object obj = population[eid][CompType.cid(type.getSimpleName())];
        return obj != null && type.isInstance(obj);
    }

    private void updateEntityInAllGroups(int eid) {
        for (int i = 0; i < groupsSize; i++) {
            groups[i].update(eid);
        }
    }

    // Here is the single thread iterator
    private int eidIterPos;

    public int eidIter() {
        return eidIterPos = next[0];
    }

    public int eidNext() {
        return eidIterPos = next[eidIterPos];
    }

    private int compIterEID = 0;
    private int compIterPos = 0;
    private Class compIterType;

    public Object compIter(int eid) {
        return compIter(eid, Object.class);
    }

    public <E> E compIter(int eid, Class<E> clazz) {
        compIterEID = eid;
        compIterPos = 0;
        compIterType = clazz;
        return (E) compNext();
    }
    
    public <E> E compNext() {
        while(compIterPos < componentCapacity) {
            Object comp = population[compIterEID][compIterPos];
            if(comp != null && compIterType.isInstance(comp)) {
                return (E) population[compIterEID][compIterPos++];
            }
            compIterPos++;
        }

        return null;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Starting");
        World w = new World(10,10);
        int eid = w.createEntity();
        w.addComponent(eid, 0, new Integer(0));
        w.addComponent(eid, 3, new Double(3.0));
        w.addComponent(eid, 5, new Integer(5));
        w.addComponent(eid, 7, new Double(7.0));
        w.addComponent(eid, 9, new Integer(9));
        
        System.out.println("List of all");
        for(Object comp = w.compIter(eid); comp != null; comp = w.compNext()) {
            System.out.println(comp.toString());
        }

        System.out.println("Integer");
        for(Integer comp = w.compIter(eid, Integer.class); comp != null; comp = w.compNext()) {
            System.out.println(comp.toString());
        }
    }
}
