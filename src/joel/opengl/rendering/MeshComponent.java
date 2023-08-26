package joel.opengl.rendering;

import joel.opengl.entity.Component;

public abstract class MeshComponent extends Component {

    public final Renderer3D renderer;
    protected MeshComponent(Renderer3D renderer) {
        this.renderer = renderer;
    }

}
