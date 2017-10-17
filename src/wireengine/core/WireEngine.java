package wireengine.core;

import wireengine.core.event.EventHandler;
import wireengine.core.physics.PhysicsEngine;
import wireengine.core.player.Player;
import wireengine.core.rendering.RenderEngine;
import wireengine.testgame.Game;

import java.util.Random;

import static wireengine.core.TickableThread.ThreadState.*;

/**
 * TODO: 3D rendering
 * TODO: 3D Player movement, 1st and 3rd person
 * TODO: 3D model loading
 * TODO: Map / level loading from a filePath.
 * TODO: Physics and collisions
 * TODO: Sound engine
 * TODO: Networking and multiplayer
 *
 * @author Kelan
 */
public final class WireEngine
{
    private static WireEngine instance;
    private static LogHelper defaultLogger;

    private Game game;
    private GameSettings gameSettings;
    private Random random;
    private EventHandler eventHandler;
    private TickScheduler tickScheduler;
    private RenderEngine renderEngine;
    private PhysicsEngine physicsEngine;

    private WireEngine(Game game)
    {
        this.game = game;
        this.gameSettings = new GameSettings();
        this.random = new Random(System.nanoTime());
        this.eventHandler = new EventHandler();
        this.tickScheduler = new TickScheduler();
        this.renderEngine = new RenderEngine();
        this.physicsEngine = new PhysicsEngine();
    }

    public static WireEngine engine()
    {
        return instance;
    }

    public static void createEngine(Game game)
    {
        if (game == null)
        {
            throw new NullPointerException("Cannot initialize engine with null game");
        }
        if (WireEngine.instance != null)
        {
            throw new IllegalStateException("Engine has already been created.");
        }

        WireEngine.instance = new WireEngine(game);
        WireEngine.instance.getEventHandler().registerListener(game);

        WireEngine.instance.renderEngine.startThread();
        WireEngine.instance.physicsEngine.startThread();

        getLogger().info("Initializing game");
        game.preInit();
        WireEngine.instance.init();
        game.postInit();
        getLogger().info("Successfully initialized game");

        WireEngine.instance.start();
        WireEngine.instance.stop();
    }

    public void handleInput(double delta)
    {
        this.game.handleInput(delta);
    }

    public static LogHelper getLogger()
    {
        if (defaultLogger == null)
        {
            defaultLogger = new LogHelper();
        }
        return defaultLogger;
    }

    public Game getGame()
    {
        return game;
    }

    public GameSettings getGameSettings()
    {
        return gameSettings;
    }

    public Random getRandom()
    {
        return random;
    }

    public EventHandler getEventHandler()
    {
        return eventHandler;
    }

    public TickScheduler getTickScheduler()
    {
        return tickScheduler;
    }

    public RenderEngine getRenderEngine()
    {
        return renderEngine;
    }

    public PhysicsEngine getPhysicsEngine()
    {
        return physicsEngine;
    }

    private void init()
    {
        this.tickScheduler.addTickable(renderEngine);
        this.tickScheduler.addTickable(physicsEngine);

        this.renderEngine.setThreadState(INITIALISE);
        this.physicsEngine.setThreadState(INITIALISE);
    }

    private void start()
    {
        this.renderEngine.setThreadState(RUN);
        this.physicsEngine.setThreadState(RUN);
        this.tickScheduler.run();
    }

    public void stop()
    {
        WireEngine.getLogger().info("Stopping game");
        this.renderEngine.setThreadState(STOP);
        this.physicsEngine.setThreadState(STOP);
        this.tickScheduler.stop();
    }
}
