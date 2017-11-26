package wireengine.core.entity;

import wireengine.core.WireEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kelan
 */
public class EntityManager
{
    private List<Entity> renderInitializationList = new ArrayList<>();
    private List<Entity> physicsInitializationList = new ArrayList<>();

    public void update()
    {
        if (WireEngine.isRenderThread())
        {
            this.updateRender();
        } else if (WireEngine.isPhysicsThread())
        {
            this.updatePhysics();
        }
    }

    private void updateRender()
    {
        for (Entity entity : renderInitializationList)
        {
            entity.initRenderable();
        }

        this.renderInitializationList.clear();
    }

    private void updatePhysics()
    {
        for (Entity entity : physicsInitializationList)
        {
            entity.initTickable();
        }
        this.physicsInitializationList.clear();
    }
}
