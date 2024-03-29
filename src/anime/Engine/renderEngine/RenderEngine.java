package anime.Engine.renderEngine;

import anime.Animation.renderer.AnimatedModelRenderer;
import anime.Engine.scene.Scene;
import anime.Engine.skybox.SkyboxRenderer;
import renderEngine.DisplayManager;

/**
 * This class represents the entire render engine.
 * 
 * @author Karl
 *
 */
public class RenderEngine {

	private MasterRenderer renderer;

	private RenderEngine(MasterRenderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Updates the display.
	 */
	public void update() {
		DisplayManager.updateDisplay();
	}

	/**
	 * Renders the scene to the screen.
	 * 
	 * @param scene
	 *            - the game scene.
	 */
	public void renderScene(Scene scene) {
		renderer.renderScene(scene);
	}

	/**
	 * Cleans up the renderers and closes the display.
	 */
	public void close() {
		renderer.cleanUp();
		DisplayManager.closeDisplay();
	}

	/**
	 * Initializes a new render engine. Creates the display and inits the
	 * renderers.
	 * 
	 * @return
	 */
	public static RenderEngine init() {
		DisplayManager.createDisplay();
		SkyboxRenderer skyRenderer = new SkyboxRenderer();
		//AnimatedModelRenderer entityRenderer = new AnimatedModelRenderer();
		//MasterRenderer renderer = new MasterRenderer(entityRenderer, skyRenderer);
		//return new RenderEngine(renderer);
		return null;
	}

}
