package de.hsbremen.mobile.balanceit.view.shader;

public class SkyboxShader {

	public static final String vertexShader = "                               \n"
			+ "attribute vec4 a_position;                                     \n"
			+ "uniform mat4 u_M;                                              \n"
			+ "uniform mat4 u_VP;                                             \n"
			+ "varying vec3 v_texCoord;                                       \n"
			+ "void main()                                                    \n"
			+ "{                                                              \n"
			+ "   gl_Position = u_VP * u_M * a_position;                      \n"
			+ "   v_texCoord = a_position.xyz;                                \n"
			+ "}                                                              \n";

	public static final String fragmentShader = "                             \n"
			+ "#ifdef GL_ES\n"
			+ "precision mediump float;\n"
			+ "#endif\n"
			+ "                                                               \n"
			+ "varying vec3 v_texCoord;                                       \n"
			+ "uniform samplerCube u_sampler;                                 \n"
			+ "void main()                                                    \n"
			+ "{                                                              \n"
			+ "    gl_FragColor = textureCube(u_sampler, v_texCoord);         \n"
			+ "}                                                              \n";
}