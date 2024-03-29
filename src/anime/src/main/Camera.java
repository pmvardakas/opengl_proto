package anime.src.main;

import anime.Engine.scene.ICamera;
import anime.Engine.utils.SmoothFloat;
import lwjglUtil.vector.Matrix4f;
import lwjglUtil.vector.Vector2f;
import lwjglUtil.vector.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWScrollCallback;
import renderEngine.DisplayManager;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents the in-game camera. This class is in charge of keeping the
 * projection-view-matrix updated. It allows the user to alter the pitch and yaw
 * with the left mouse button.
 * 
 * @author Karl
 *
 */
public class Camera implements ICamera {

	private static final float PITCH_SENSITIVITY = 0.3f;
	private static final float YAW_SENSITIVITY = 0.3f;
	private static final float MAX_PITCH = 90;

	private Vector2f currentMouseWheel = new Vector2f(0, 0);

	private DoubleBuffer mouseX = BufferUtils.createDoubleBuffer(1);
	private DoubleBuffer mouseY = BufferUtils.createDoubleBuffer(1);

	private GLFWScrollCallback scrollCallback;

	private int newMouseX = -1;
	private int newMouseY = -1;

	private int mouseDx = -1;
	private int mouseDy = -1;

	private static final float FOV = 70;
	private static final float NEAR_PLANE = 0.2f;
	private static final float FAR_PLANE = 400;

	private static final float Y_OFFSET = 5;

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix = new Matrix4f();

	private Vector3f position = new Vector3f(0, 0, 0);

	private float yaw = 0;
	private SmoothFloat pitch = new SmoothFloat(10, 10);
	private SmoothFloat angleAroundPlayer = new SmoothFloat(0, 10);
	private SmoothFloat distanceFromPlayer = new SmoothFloat(10, 5);

	public Camera() {
		this.projectionMatrix = createProjectionMatrix();
		scrollCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				currentMouseWheel.x = (float) xoffset;
				currentMouseWheel.y = (float) yoffset;
			}
		};
	}

	@Override
	public void move() {
		calculatePitch();
		calculateAngleAroundPlayer();
		float horizontalDistance = calculateHorizontalDistance();
		float verticalDistance = calculateVerticalDistance();
		calculateCameraPosition(horizontalDistance, verticalDistance);
		this.yaw = 360 - angleAroundPlayer.get();
		yaw %= 360;
		updateViewMatrix();
	}

	public void input() {
		long windowId = DisplayManager.getWindow();
		glfwSetScrollCallback(windowId, scrollCallback);

		glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);

		glfwGetCursorPos(windowId, mouseX, mouseY);
		mouseX.rewind();
		mouseY.rewind();

		mouseDx = newMouseX - (int) mouseX.get(0);
		mouseDy = newMouseY - (int) mouseY.get(0);

		newMouseX = (int) mouseX.get(0);
		newMouseY = (int) mouseY.get(0);
	}

	@Override
	public Matrix4f getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	@Override
	public Matrix4f getProjectionViewMatrix() {
		return Matrix4f.mul(projectionMatrix, viewMatrix, null);
	}

	private void updateViewMatrix() {
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(pitch.get()), new Vector3f(1, 0, 0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
		Vector3f negativeCameraPos = new Vector3f(-position.x, -position.y, -position.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
	}

	private static Matrix4f createProjectionMatrix() {
		Matrix4f projectionMatrix = new Matrix4f();
		float aspectRatio = (float) DisplayManager.getWidth() / (float) DisplayManager.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
		return projectionMatrix;
	}

	private void calculateCameraPosition(float horizDistance, float verticDistance) {
		float theta = angleAroundPlayer.get();
		position.x = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
		position.y = verticDistance + Y_OFFSET;
		position.z = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
	}

	/**
	 * @return The horizontal distance of the camera from the origin.
	 */
	private float calculateHorizontalDistance() {
		return (float) (distanceFromPlayer.get() * Math.cos(Math.toRadians(pitch.get())));
	}

	/**
	 * @return The height of the camera from the aim point.
	 */
	private float calculateVerticalDistance() {
		return (float) (distanceFromPlayer.get() * Math.sin(Math.toRadians(pitch.get())));
	}

	/**
	 * Calculate the pitch and change the pitch if the user is moving the mouse
	 * up or down with the LMB pressed.
	 */
	private void calculatePitch() {
		if (glfwGetMouseButton(DisplayManager.getWindow(), GLFW_MOUSE_BUTTON_RIGHT) == 1) {
			float pitchChange = mouseDy * 0.1f;
			pitch.increaseTarget(-pitchChange);
			clampPitch();

			pitch.update(DisplayManager.getDeltaTime());
		}
	}

	/**
	 * Calculate the angle of the camera around the player (when looking down at
	 * the camera from above). Basically the yaw. Changes the yaw when the user
	 * moves the mouse horizontally with the LMB down.
	 */
	private void calculateAngleAroundPlayer() {
		if (glfwGetMouseButton(DisplayManager.getWindow(), GLFW_MOUSE_BUTTON_RIGHT) == 1) {
			float angleChange = mouseDx * 0.3f;
			angleAroundPlayer.increaseTarget(-angleChange);
		}
		angleAroundPlayer.update(DisplayManager.getDeltaTime());
	}

	/**
	 * Ensures the camera's pitch isn't too high or too low.
	 */
	private void clampPitch() {
		if (pitch.getTarget() < 0) {
			pitch.setTarget(0);
		} else if (pitch.getTarget() > MAX_PITCH) {
			pitch.setTarget(MAX_PITCH);
		}
	}

}
