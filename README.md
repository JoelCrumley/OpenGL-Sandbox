# OpenGL Sandbox
Personal project to test/learn basic OpenGL. Written in Java. Uses LWJGL to load OpenGL.

Various test project entry points are found at "src/joel/opengl/test/"

To plug and play, just run the precompiled jar file found in "out/artifacts/OpenGL_Sandbox_jar/" using the supplied batch file or your favourite method of running jars.

### Mandelbrot Zoom
Approximation of Mandelbrot set drawn in real time by a fragment shader. Can move around and change zoom/iterations in real time.

Has 2 modes, one for 32-bit floating point precision and another for 64-bit. The 64-bit mode will allow you to zoom in ~twice as much before you start to see artefacts but at the cost of reduced performance.

Controls: WASD - Up Left Down Right || Q/E - Zoom Out / In || R/F - Increase / Decrease Iterations (Note: Your GPU will quickly reach 100% load) || Space - Toggle Crosshair || 1/2/3/4 - Change Colour Mode

![Mandelbrot Showcase](https://raw.githubusercontent.com/Lammazz/OpenGL-Sandbox/master/res/images/MandelbrotShowcase.png)


### Basic Grid
Not a lot to see, essentially just testing various primitives and basic techniques.

Controls: WASD - Up Left Down Right || Q/E or Scroll - Zoom Out / In || Space - Reset Camera || Right Click - Move || Left Click - Select points to be connected by lines || Enter - Connect points || Backspace - Forget points

![Grid Showcase](https://raw.githubusercontent.com/Lammazz/OpenGL-Sandbox/master/res/images/BasicGridShowcase.png)
