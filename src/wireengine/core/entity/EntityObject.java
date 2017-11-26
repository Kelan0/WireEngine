package wireengine.core.entity;

import wireengine.core.physics.collision.Collider;
import wireengine.core.rendering.geometry.MeshData;
import wireengine.core.rendering.geometry.Transformation;

/**
 * @author Kelan
 */
public class EntityObject extends Entity
{
    public EntityObject(String name, float mass, Collider collider, MeshData mesh, Transformation transformation)
    {
        super(name, mass, collider, mesh, transformation);
    }

    public EntityObject(String name, float mass, MeshData mesh, Transformation transformation)
    {
        super(name, mass, mesh, transformation);
    }

    public EntityObject(String name, Transformation transformation)
    {
        super(name, transformation);
    }
}
