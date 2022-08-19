package joel.opengl.shaders;

import joel.opengl.maths.Mat4f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL46.*;

public abstract class ShaderProgram {

    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    public ShaderProgram(String vertexFile,String fragmentFile) {
        vertexShaderID = loadShader(vertexFile, GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL_FRAGMENT_SHADER);
        programID = glCreateProgram();
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        glLinkProgram(programID);
        glValidateProgram(programID);
        getAllUniformLocations();
    }

    protected abstract void getAllUniformLocations();

    protected int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programID, uniformName);
    }

    protected void pushInt(int location, int value) {
        glUniform1i(location, value);
    }

    protected void pushFloat(int location, float value) {
        glUniform1f(location, value);
    }

    protected void pushVector(int location, float x, float y) {
        glUniform2f(location, x, y);
    }

    protected void pushVector(int location, float x, float y, float z) {
        glUniform3f(location, x, y, z);
    }

    protected void pushVector(int location, float x, float y, float z, float w) {
        glUniform4f(location, x, y, z, w);
    }

    protected void pushBoolean(int location, boolean value) {
        glUniform1i(location, value ? GL_TRUE : GL_FALSE);
    }

    protected void pushDouble(int location, double value) {
        glUniform1d(location, value);
    }

    protected void pushVectord(int location, double x, double y) {
        glUniform2d(location, x, y);
    }

    protected void pushMat4f(int location, Mat4f matrix) {
        glUniformMatrix4fv(location, matrix.rowMajor, matrix.data);
    }

    public void bind(){
        glUseProgram(programID);
    }

    public void unbind(){
        glUseProgram(0);
    }

    public void cleanUp(){
        unbind();
        glDetachShader(programID, vertexShaderID);
        glDetachShader(programID, fragmentShaderID);
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        glDeleteProgram(programID);
    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName){
        glBindAttribLocation(programID, attribute, variableName);
    }

    private static int loadShader(String file, int type){
        StringBuilder shaderSource = new StringBuilder();
        try{
//            BufferedReader reader = new BufferedReader(new FileReader(file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(ShaderProgram.class.getResourceAsStream("/shaders/" + file)));
            String line;
            while((line = reader.readLine())!=null){
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, shaderSource);
        glCompileShader(shaderID);
        if(glGetShaderi(shaderID, GL_COMPILE_STATUS )== GL_FALSE){
            System.out.println(glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        return shaderID;
    }

}
