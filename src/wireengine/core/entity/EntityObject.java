package wireengine.core.entity;

import wireengine.core.physics.collision.Collider;
import wireengine.core.rendering.geometry.GLMesh;
import wireengine.core.rendering.geometry.MeshBuilder;
import wireengine.core.rendering.geometry.Transformation;

/**
 * @author Kelan
 */
public class EntityObject extends Entity
{
    public EntityObject(String name, float mass, Collider collider, MeshBuilder meshBuilder, Transformation transformation)
    {
        super(name, mass, collider, meshBuilder, transformation);
    }

    public EntityObject(String name, float mass, Collider collider, GLMesh mesh, Transformation transformation)
    {
        super(name, mass, collider, mesh, transformation);
    }

    public EntityObject(String name, float mass, MeshBuilder mesh, Transformation transformation)
    {
        super(name, mass, mesh, transformation);
    }
}
