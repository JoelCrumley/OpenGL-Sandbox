package joel.opengl.oldRendering;

public class FullscreenQuad2D extends Quad2D {

    private static final float[] vertices = {
            -1.0f, 1.0f,	    //V0
            -1.0f,-1.0f,	//V1
             1.0f,-1.0f,	//V2
             1.0f, 1.0f		//V3
    };

    public FullscreenQuad2D() {
        super(vertices);
    }

    @Override
    public boolean shouldAlwaysRender() {
        return true;
    }

    //    public RawModel loadToVAO(float[] positions,int[] indices){
//        int vaoID = createVAO();
//        bindIndicesBuffer(indices);
//        storeDataInAttributeList(0,positions);
//        unbindVAO();
//        return new RawModel(vaoID,indices.length);
//    }

}
