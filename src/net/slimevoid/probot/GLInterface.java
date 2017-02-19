package net.slimevoid.probot;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public class GLInterface {
 
	private static long window;
	
	private static boolean started = false;
	private static int windowWidth, windowHeight;
	private static String windowTitle;
	private static boolean windowsResizable;
	
	public static void start(int w, int h, String title, boolean resizable) {
		if(started) throw new RuntimeException("GL already started");
		started = true;
		windowWidth = w;
		windowHeight = h;
		windowTitle = title;
		windowsResizable = resizable;
		
		System.out.println("Starting LWJGL " + Version.getVersion());
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				init();
				loop();
				free();
			}
		}).start();
	}

	private static void init() {
		GLFWErrorCallback.createPrint(System.err).set();
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, windowsResizable ? GLFW_TRUE : GLFW_FALSE);

		window = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); //TODO keyboard / mouse managment
		});

		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1);
			IntBuffer pHeight = stack.mallocInt(1);
			glfwGetWindowSize(window, pWidth, pHeight);
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}

		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		glfwShowWindow(window);
	}

	private static void loop() {
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.8f, 0.8f, 0.9f, 0.0f);
		
		//TEST
		int vertShad = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertShad, "#version 330 core\n layout (location = 0) in vec3 position; void main() { gl_Position = vec4(position.x, position.y, position.z, 1.0f); }");
		glCompileShader(vertShad);
		System.out.println("vert: "+glGetShaderi(vertShad, GL_COMPILE_STATUS));
		int fragShad = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragShad, "#version 330 core\n out vec4 color; void main() { color = vec4(1.0f, 0.5f, 0.2f, 1.0f); }");
		glCompileShader(fragShad);
		System.out.println("frag: "+glGetShaderi(fragShad, GL_COMPILE_STATUS));
		int shad = glCreateProgram();
		glAttachShader(shad, vertShad);
		glAttachShader(shad, fragShad);
		glLinkProgram(shad);
		System.out.println("prog: "+glGetProgrami(shad, GL_LINK_STATUS));
		if(glGetProgrami(shad, GL_LINK_STATUS) == 0) System.out.println(glGetProgramInfoLog(shad));
		
		int vao = glGenVertexArrays();
		int vbo = glGenBuffers();
		glBindVertexArray(vao);
			glBindBuffer(GL_ARRAY_BUFFER, vbo);
			glBufferData(GL_ARRAY_BUFFER, new float[]{
				    -0.5f, -0.5f, 0.0f,
				     0.5f, -0.5f, 0.0f,
				     0.0f,  0.5f, 0.0f
				}, GL_STATIC_DRAW);
			glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			glEnableVertexAttribArray(0);
		glBindVertexArray(0);
		//END TEST

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while ( !glfwWindowShouldClose(window) ) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

			//TEST
			glUseProgram(shad);
			glBindVertexArray(vao);
			glDrawArrays(GL_TRIANGLES, 0, 3);
			glBindVertexArray(0);
			//END TEST
			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}
	
	private static void free() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}
}