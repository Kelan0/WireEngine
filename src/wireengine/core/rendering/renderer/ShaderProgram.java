package wireengine.core.rendering.renderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.*;
import wireengine.core.WireEngine;
import wireengine.core.util.FileUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL43.GL_COMPUTE_SHADER;

/**
 * @author Kelan
 */
public class ShaderProgram
{
    private Map<Integer, String> rawShaders = new HashMap<>();
    private Map<Integer, String> attributes = new HashMap<>();
    private HashMap<String, Integer> uniforms = new HashMap<>();

    private List<Integer> shaders = new ArrayList<>();
    private int program;
    private String totalLog = "";

    public boolean addAttribute(int location, String name)
    {
        attributes.put(location, name);
        return true;
    }

    public boolean addShader(int type, String file) throws IOException
    {
        WireEngine.getLogger().info("Loading shader " + getShaderName(type) + " from filePath \"" + file + "\"");
        StringBuilder source = new StringBuilder();

        FileUtils.readFile(file, source);

        if (source.length() <= 0)
        {
            return false;
        } else
        {
            WireEngine.getLogger().info("Successfully loaded shader");
            rawShaders.put(type, source.toString());
            return true;
        }
    }

    public boolean createProgram()
    {
//        delete();

        if (rawShaders.size() > 0)
        {
            program = glCreateProgram();
            if (program > 0)
            {
                for (Map.Entry<Integer, String> entry : rawShaders.entrySet())
                {
                    int shaderType = entry.getKey();
                    String shaderSource = entry.getValue();

                    int shader = glCreateShader(shaderType);

                    if (shader <= 0)
                    {
                        WireEngine.getLogger().warning(getProgramLog());
                    } else
                    {
                        glShaderSource(shader, shaderSource);
                        glCompileShader(shader);

                        if (checkShaderStatus(shader, GL_COMPILE_STATUS))
                        {
                            glAttachShader(program, shader);
                            shaders.add(shader);
                        }
                    }
                }

                for (Map.Entry<Integer, String> entry : attributes.entrySet())
                {
                    int location = entry.getKey();
                    String attribute = entry.getValue();

                    if (attribute != null && attribute.length() > 0)
                    {
                        glBindAttribLocation(program, location, attribute);
                    }
                }

                glLinkProgram(program);
                glValidateProgram(program);

                for (int shader : new ArrayList<>(shaders))
                {
                    deleteShader(shader);
                }

                boolean b = checkProgramStatus(GL_LINK_STATUS) && checkProgramStatus(GL_VALIDATE_STATUS);

                return b;
            }
        }

        return false;
    }

    public void useProgram(boolean use)
    {
        glUseProgram(use ? program : 0);
    }

    public int getUniformLocation(String uniform)
    {
//        return uniforms.computeIfAbsent(uniform, x -> glGetUniformLocation(program, uniform));
        return glGetUniformLocation(program, uniform);
    }

    public void setUniformVector1f(String uniform, float f)
    {
        glUniform1f(getUniformLocation(uniform), f);
    }

    public void setUniformVector2f(String uniform, float f, float f1)
    {
        glUniform2f(getUniformLocation(uniform), f, f1);
    }

    public void setUniformVector2f(String uniform, Vector2f v)
    {
        setUniformVector2f(uniform, v.x, v.y);
    }

    public void setUniformMatrix2f(String uniform, Matrix2f m)
    {
        glUniformMatrix2fv(getUniformLocation(uniform), false, storeAndFlip(m, BufferUtils.createFloatBuffer(4)));
    }

    public void setUniformVector3f(String uniform, float f, float f1, float f2)
    {
        glUniform3f(getUniformLocation(uniform), f, f1, f2);
    }

    public void setUniformVector3f(String uniform, Vector3f v)
    {
        setUniformVector3f(uniform, v.x, v.y, v.z);
    }

    public void setUniformMatrix3f(String uniform, Matrix3f m)
    {
        glUniformMatrix3fv(getUniformLocation(uniform), false, storeAndFlip(m, BufferUtils.createFloatBuffer(9)));
    }

    public void setUniformVector4f(String uniform, float f, float f1, float f2, float f3)
    {
        glUniform4f(getUniformLocation(uniform), f, f1, f2, f3);
    }

    public void setUniformVector4f(String uniform, Vector4f v)
    {
        setUniformVector4f(uniform, v.x, v.y, v.z, v.w);
    }

    public void setUniformMatrix4f(String uniform, Matrix4f m)
    {
        glUniformMatrix4fv(getUniformLocation(uniform), false, storeAndFlip(m, BufferUtils.createFloatBuffer(16)));
    }

    public void setUniformVector1i(String uniform, int i)
    {
        glUniform1i(getUniformLocation(uniform), i);
    }

    public void setUniformVector2i(String uniform, int i, int i1)
    {
        glUniform2i(getUniformLocation(uniform), i, i1);
    }

    public void setUniformVector3i(String uniform, int i, int i1, int i2)
    {
        glUniform3i(getUniformLocation(uniform), i, i1, i2);
    }

    public void setUniformVector4i(String uniform, int i, int i1, int i2, int i3)
    {
        glUniform4i(getUniformLocation(uniform), i, i1, i2, i3);
    }

    public void setUniformBoolean(String uniform, boolean b)
    {
        glUniform1i(getUniformLocation(uniform), b ? 1 : 0);
    }

    public void delete()
    {
        for (int shader : new ArrayList<>(shaders))
        {
            deleteShader(shader);
        }

        glDeleteProgram(program);

        WireEngine.getLogger().warning("Deleting shader\n" + getProgramLog());
    }

    private FloatBuffer storeAndFlip(Matrix matrix, FloatBuffer buf)
    {
        matrix.store(buf);
        buf.flip();
        return buf;
    }

    private void deleteShader(int shader)
    {
        glDetachShader(program, shader);
        glDeleteShader(shader);

        shaders.remove((Integer) shader);

    }

    private boolean checkProgramStatus(int parameter)
    {
        if (glGetProgrami(program, parameter) == GL_FALSE)
        {
            WireEngine.getLogger().warning("Failed to create shader program " + program + ". This may cause visual errors\n" + getProgramLog());
            delete();
            return false;
        }

        return true;
    }

    private boolean checkShaderStatus(int shader, int parameter)
    {
        if (glGetShaderi(shader, parameter) == GL_FALSE)
        {
            WireEngine.getLogger().warning("Failed to create shader " + shader + "\n" + getShaderLog(shader));
            deleteShader(shader);
            return false;
        }
        return true;
    }

    private String getShaderLog(int shader)
    {
        String infoLog = glGetShaderInfoLog(shader, glGetShaderi(shader, GL_INFO_LOG_LENGTH));
        if (infoLog != null && infoLog.trim().length() != 0)
        {
            String shaderTypeName = getShaderName(shader);
            totalLog += shaderTypeName + ": " + infoLog + "\n";
        }

        return totalLog;
    }

    private String getShaderName(int shader)
    {
        String shaderTypeName;
        switch (shader)
        {
            case GL_VERTEX_SHADER:
                shaderTypeName = "vertex_shader";
                break;
            case GL_GEOMETRY_SHADER:
                shaderTypeName = "geometry_shader";
                break;
            case GL_FRAGMENT_SHADER:
                shaderTypeName = "fragment_shader";
                break;
            case GL_COMPUTE_SHADER:
                shaderTypeName = "compute_shader";
                break;
            default:
                shaderTypeName = "unknown_shader";
                break;
        }

        return shaderTypeName;
    }

    private String getProgramLog()
    {
        String currentLog = glGetProgramInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH));
        if (currentLog != null && currentLog.trim().length() != 0)
        {
            totalLog += currentLog + "\n";
        }

        return totalLog;
    }
}
