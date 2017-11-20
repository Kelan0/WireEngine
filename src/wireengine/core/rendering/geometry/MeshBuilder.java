package wireengine.core.rendering.geometry;

import org.lwjgl.util.vector.Vector3f;

/**
 * A class that can be passed to objects that are initialized on a non-openGL thread so that they can create
 * any meshes on the correct thread.
 *
 * @author Kelan
 */
public abstract class MeshBuilder
{
    public abstract GLMesh build();

    public static final class CuboidBuilder extends MeshBuilder
    {
        private final Vector3f size;

        public CuboidBuilder(Vector3f size)
        {
            this.size = size;
        }

        @Override
        public GLMesh build()
        {
            return MeshHelper.createCuboid(size);
        }
    }
}
