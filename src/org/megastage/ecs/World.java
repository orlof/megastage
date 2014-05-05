package org.megastage.ecs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class World {

    // stores components for each entity
    public int capacity;
    public int componentCapacity;
    public int size;
    public Object[][] population;

    // manages int ids
    private int[] next, prev;
    private boolean[] free;

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
    
    private long offset;

    public World() {
        this(10000, CompType.size);
    }

    public World(int entityCapacity, int componentCapacity) {
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
    
    public void setOffset(long offset) {
        this.offset = offset;
    }
    
    public void setTime(long time) {
        time += offset;
        this.delta = (time - this.time) / 1000.0f;
        this.time = time;
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
        offset = System.currentTimeMillis();
    }
    
    public void addProcessor(Processor processor) {
        processors[processorsSize++] = processor;
    }
    
    public <T extends Processor> T getProcessor(Class<T> processorType) {
        return processorType.cast(processorsMap.get(processorType));
    }

    public Group createGroup(int... cid) {
        if (groupsSize == groups.length) {
            throw new RuntimeException("No space for new group");
        }
        
        Group group = new Group(this, cid);
        groups[groupsSize++] = group;
        return group;
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
        updateEntityInAllGroups(eid);
    }

    public void addComponent(int eid, Object comp) {
        population[eid][CompType.cid(comp.getClass().getSimpleName())] = comp;
        updateEntityInAllGroups(eid);
    }

    public void removeComponent(int eid, int cid) {
        population[eid][cid] = null;
        updateEntityInAllGroups(eid);
    }

    public Object getComponent(int eid, int cid) {
        return population[eid][cid];
    }

    /**
     * @deprecated 
     */
    public <E> E getComponent(int eid, Class<E> type) {
        return getComponent(eid, CompType.cid(type.getSimpleName()), type);
    }

    public <E> E getComponent(int eid, int cid, Class<E> type) {
        Object obj = population[eid][cid];
        return (E) (type.isInstance(obj) ? obj: null);
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
    private int entityIteratorCursor;

    public int entities() {
        return entityIteratorCursor = next[0];
    }

    public int nextEntity() {
        return entityIteratorCursor = next[entityIteratorCursor];
    }

    private int componentIteratorEid = 0;
    private int componentIteratorCursor = 0;
    private Class componentIteratorType;

    public Object components(int eid) {
        return components(eid, Object.class);
    }

    public <E> E components(int eid, Class<E> clazz) {
        componentIteratorEid = eid;
        componentIteratorCursor = 0;
        componentIteratorType = clazz;
        return (E) nextComponent();
    }
    
    public <E> E nextComponent() {
        while(componentIteratorCursor < componentCapacity) {
            Object comp = population[componentIteratorEid][componentIteratorCursor];
            if(comp != null && componentIteratorType.isInstance(comp)) {
                return (E) population[componentIteratorEid][componentIteratorCursor++];
            }
            componentIteratorCursor++;
        }

        return null;
    }

    // While version
    
    private Object componentIteratorItem;
    
    public void componentIteratorInit(int eid) {
        componentIteratorInit(eid, Object.class);
    }

    public void componentIteratorInit(int eid, Class type) {
        componentIteratorEid = eid;
        componentIteratorType = type;
        componentIteratorCursor = -1;
    }
    
    public boolean componentIteratorNext() {
        while((++componentIteratorCursor) < componentCapacity) {
            componentIteratorItem = population[componentIteratorEid][componentIteratorCursor];
            if(componentIteratorItem != null && componentIteratorType.isInstance(componentIteratorItem)) {
                return true;
            }
        }
        return false;
    }
    
    
    // Java iterator version
    
    public final Iterable componentIterator(int eid) {
        return componentIterator.init(eid, Object.class);
    }
    
    public final Iterable componentIterator(int eid, Class type) {
        return componentIterator.init(eid, type);
    }

    private final ComponentIterator componentIterator = new ComponentIterator(0, Object.class);
    
    public final class ComponentIterator<E> implements Iterable<E>, Iterator<E> {
        private int eid;
        private int cursor;
        private Class<E> type;

        public ComponentIterator(int eid, Class<E> type) {
            this.eid = eid;
            this.cursor = -1;
            this.type = type;
        }
        
        public ComponentIterator<E> init(int eid, Class type) {
            this.eid = eid;
            this.cursor = -1;
            this.type = type;
            return this;
        }

        @Override
        public boolean hasNext() {
            int pos = cursor;
            while((++pos) < componentCapacity) {
                Object comp = population[eid][pos];
                if(comp != null && type.isInstance(comp)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public E next() {
            while((++cursor) < componentCapacity) {
                Object comp = population[eid][cursor];
                if(comp != null && type.isInstance(comp)) {
                    return (E) comp;
                }
            }
            
            throw new NoSuchElementException("Iterated past last element");
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Iterator<E> iterator() {
            return this;
        }
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
        for(Object comp: w.componentIterator(eid)) {
            System.out.println(comp.toString());
        }

        System.out.println("List of integer");
        for(Object comp: w.componentIterator(eid, Integer.class)) {
            System.out.println(comp.toString());
        }

        System.out.println("List of integer");
        for(Integer comp=w.components(eid, Integer.class); comp != null; comp = w.nextComponent()) {
            System.out.println(comp.toString());
        }
        
        System.out.println("List of all");
        for(w.componentIteratorInit(eid); w.componentIteratorNext(); /**/ ) {
            System.out.println(w.componentIteratorItem.toString());
        }

        System.out.println("List of int");
        for(w.componentIteratorInit(eid, Integer.class); w.componentIteratorNext(); /**/ ) {
            System.out.println(w.componentIteratorItem.toString());
        }

        System.out.println("List of all");
        for(Object comp = w.components(eid); comp != null; comp = w.nextComponent()) {
            System.out.println(comp.toString());
        }

        System.out.println("List of int");
        for(Integer comp = w.components(eid, Integer.class); comp != null; comp = w.nextComponent()) {
            System.out.println(comp.toString());
        }
    }
}
