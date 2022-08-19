package joel.opengl.entity;

public abstract class Component {
    public abstract void onComponentAdded(EntityHandler entityHandler, int entity);
    public abstract void onComponentRemoved(EntityHandler entityHandler, int entity);
}
