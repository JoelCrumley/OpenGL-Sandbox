package joel.opengl.newRendering;

import joel.opengl.entity.Component;

public abstract class MeshComponent extends Component {

    public final Renderer renderer;
    protected MeshComponent(Renderer renderer) {
        this.renderer = renderer;
    }

}
