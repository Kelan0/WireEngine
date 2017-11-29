package wireengine.core.entity;

import wireengine.core.physics.Tensor;
import wireengine.core.physics.collision.Collider;
import wireengine.core.rendering.geometry.MeshData;
import wireengine.core.rendering.geometry.Transformation;

/**
 * @author Kelan
 */
public class EntityObject extends Entity
{
    public EntityObject(String name, float mass, Collider collider, MeshData mesh, Tensor tensor, Transformation transformation)
    {
        super(name, mass, collider, mesh, tensor, transformation);
    }

    public EntityObject(String name, float mass, MeshData mesh, Tensor tensor, Transformation transformation)
    {
        super(name, mass, mesh, tensor, transformation);
    }

    public EntityObject(String name, Transformation transformation)
    {
        super(name, transformation);
    }
}
