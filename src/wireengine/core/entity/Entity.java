package wireengine.core.entity;

import wireengine.core.WireEngine;
import wireengine.core.physics.ITickable;
import wireengine.core.physics.PhysicsObject;
import wireengine.core.physics.collision.Collider;
import wireengine.core.rendering.IRenderable;
import wireengine.core.rendering.renderer.ShaderProgram;
import wireengine.core.rendering.geometry.MeshData;
import wireengine.core.rendering.geometry.Model;
import wireengine.core.rendering.geometry.Transformation;

/**
 * @author Kelan
 */
public abstract class Entity implements ITickable, IRenderable
{
    public String name;
    public PhysicsObject physicsObject; // The physics object for this entity.
    public Transformation transformation; // The transformation of this entity in the world.
    public Model model; // The model that this entity renders.
    public boolean exists; // Whether this entity exists in the world currently.
    public boolean valid = true; // Whether this is a valid entity. Used for construction and destruction.
    public boolean needsRenderInitialization;
    public boolean needsPhysicsInitialization;

    public Entity(String name, float mass, Collider collider, MeshData mesh, Transformation transformation)
    {
//        System.out.println("Entity " + name + " has " + (collider == null ? ("no collider") : ("a collider with " + collider.getTriangles().radius() + " triangles")));
        this.name = name;

        if (transformation == null)
        {
            transformation = new Transformation();
        }

        if (collider != null)
        {
            collider.getTransformation().set(transformation);
        }

        this.physicsObject = new PhysicsObject(collider, mass);
        this.transformation = transformation;
        this.model = new Model(mesh, transformation);
    }

    public Entity(String name, float mass, MeshData mesh, Transformation transformation)
    {
        this(name, mass, mesh != null ? new Collider(mesh, transformation) : null, mesh, transformation);
    }

    public Entity(String name, Transformation transformation)
    {
        this(name, 1.0F, null, transformation);
    }

    public Entity(String name)
    {
        this(name, new Transformation());
    }

    @Override
    public void initTickable()
    {
        this.needsPhysicsInitialization = false;
    }

    @Override
    public void initRenderable()
    {
        this.model.initRenderable();
        this.needsRenderInitialization = false;
    }

    @Override
    public void tick(double delta)
    {
        if (valid)
        {
            if (this.physicsObject != null)
            {
                this.physicsObject.tick(delta);
            }
        }
    }

    public void render(double delta, ShaderProgram shaderProgram)
    {
        if (valid)
        {
            if (this.model != null)
            {
                this.model.render(delta, shaderProgram);
            }

//            if (this.physicsObject != null)
//            {
//                this.physicsObject.renderDebug(shaderProgram, new Vector4f(1.0F, 1.0F, 0.0F, 1.0F), GL_LINES);
//            }
        }
    }

    public void markForInitialization(boolean render, boolean physics)
    {
        this.needsRenderInitialization |= render;
        this.needsPhysicsInitialization |= physics;
    }

    public boolean doesNeedInitialization()
    {
        if (WireEngine.isRenderThread())
        {
            return needsRenderInitialization;
        } else if (WireEngine.isPhysicsThread())
        {
            return needsPhysicsInitialization;
        } else
        {
            return needsRenderInitialization || needsPhysicsInitialization;
        }
    }

    @Override
    public PhysicsObject getPhysicsObject()
    {
        return physicsObject;
    }

    public void setPhysicsObject(PhysicsObject physicsObject)
    {
        this.physicsObject = physicsObject;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Model getModel()
    {
        return model;
    }

    public void setModel(Model model)
    {
        this.model = model;
    }

    public void validate()
    {
        this.valid = true;
    }

    public void invalidate()
    {
        this.valid = false;
    }

    public boolean isValid()
    {
        return this.valid;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        if (name != null ? !name.equals(entity.name) : entity.name != null) return false;
        if (physicsObject != null ? !physicsObject.equals(entity.physicsObject) : entity.physicsObject != null)
            return false;
        return model != null ? model.equals(entity.model) : entity.model == null;
    }

    @Override
    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (physicsObject != null ? physicsObject.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        return result;
    }
}
