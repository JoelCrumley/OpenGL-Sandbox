package joel.opengl.entity;

import joel.opengl.util.Bag;

import java.util.HashMap;

public class EntityHandler {

    private final HashMap<Class<? extends Component>, Bag<Component>> components = new HashMap<>();

    private int entityCounter = 0;

    public EntityHandler() {
    }

    public Bag<Component> createBag(Class<? extends Component> type) {
        Bag<Component> bag = new Bag<>();
        components.put(type, bag);
        return bag;
    }

    public Bag<Component> getBag(Class<? extends Component> type) {
        Bag<Component> bag = components.get(type);
        return bag == null ? createBag(type) : bag;
    }

    public Component[] getComponents(int entity, Class<? extends Component>... types) {
        Component[] components = new Component[types.length];
        for (int i = 0; i < types.length; i++) components[i] = getComponent(entity, types[i]);
        return components;
    }

    public <T extends Component> T getComponent(int entity, Class<T> type) {
        Bag<Component> bag = components.get(type);
        return (bag == null || entity > bag.size()) ? null : (T) bag.get(entity);
    }

    public void setComponent(int entity, Component... components) {
        for (Component component : components) {
            Bag<Component> bag = getBag(component.getClass());
            Component oldComponent = bag.safeGet(entity);
            if (oldComponent != null) oldComponent.onComponentRemoved(this, entity);
            bag.set(entity, component);
            component.onComponentAdded(this, entity);
        }
    }

    public <T extends Component> void removeComponents(int entity, Class<T>... types) {
        for (Class<T> type : types) {
            Bag<Component> bag = getBag(type);
            if (entity > bag.size()) continue;
            Component component = bag.get(entity);
            if (component == null) continue;
            component.onComponentRemoved(this, entity);
            bag.set(entity, null);
        }
    }

    public void deleteEntity(int entity) {
        for (Bag<Component> bag : components.values()) {
            if (entity > bag.size()) continue;
            Component component = bag.get(entity);
            if (component == null) continue;
            component.onComponentRemoved(this, entity);
            bag.set(entity, null);
        }
    }

    public int createEntity() {
        return entityCounter++;
    }

    public int getEntityCount() {
        return entityCounter;
    }

}
