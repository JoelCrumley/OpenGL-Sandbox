package joel.opengl.rendering;

import joel.opengl.maths.Matf;

public abstract class Camera {

    public abstract Matf calculateMatrix();

    protected boolean changed = true; // When this is true, calculateMatrix will be called and new matrix will be pushed to shader on next draw

    public boolean hasChanged() {
        return changed;
    }

    public void setNotChanged() {
        changed = false;
    }

}
