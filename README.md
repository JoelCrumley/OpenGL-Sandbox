# OpenGL Sandbox
Personal project to test/learn basic OpenGL. Written in Java. Uses LWJGL to load OpenGL.

Various test project entry points are found at "src/joel/opengl/test/"

To plug and play, just run the precompiled jar file found in "out/artifacts/OpenGL_Sandbox_jar/" using the supplied batch file or your favourite method of running jars.

### Mandelbrot Zoom
Approximation of Mandelbrot set drawn in real time by a fragment shader. Can move around and change zoom/iterations in real time.

Has 2 modes, one for 32-bit floating point precision and another for 64-bit. The 64-bit mode will allow you to zoom in ~twice as much before you start to see artefacts but at the cost of reduced performance.

Controls: WASD - Up Left Down Right || Q/E - Zoom Out / In || R/F - Increase / Decrease Iterations (Note: Your GPU will quickly reach 100% load) || Space - Toggle Crosshair || 1/2/3/4 - Change Colour Mode

![Mandelbrot Showcase](https://raw.githubusercontent.com/Lammazz/OpenGL-Sandbox/master/res/images/MandelbrotShowcase.png)


### Server / Client  Test
Testing many different things related to networking and a server/client system.

List of features (non-exhaustive & subject to change): Primitive SQL database support; Secure communication via RSA; Password hashing (single class library used for Keccak hash function); Generalised packet handling system supporting UDP and TCP packets; Task scheduler; Simple client state system.

Clients, once connected, can communicate to eachother via messages sent in the console.

Typing "secret" into the console will change the client to a state for testing UDP movement packets (note to self: heavy optimisation needed). In this state pressing R randomises the player's colour.


### 3D Test
3D Rendering Environment.

Uses own simple entity component framework. All mesh components of a given type are rendered in a single draw using instanced rendering.

WASD & Mouse movement; Q/E roll camera; F fullscreen; 1/2/3/4/5 add cubes with random transforms, number of cubes added increases by factor of 10 between keybinds.

![Grid Showcase](https://raw.githubusercontent.com/Lammazz/OpenGL-Sandbox/master/res/images/3DTestShowcase.png)


### Basic Grid
Not a lot to see, just testing various primitives and basic techniques.

Controls: WASD - Up Left Down Right || Q/E or Scroll - Zoom Out / In || Space - Reset Camera || Right Click - Move || Left Click - Select points to be connected by lines || Enter - Connect points || Backspace - Forget points

![Grid Showcase](https://raw.githubusercontent.com/Lammazz/OpenGL-Sandbox/master/res/images/BasicGridShowcase.png)