

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
/**
 * 
 * @author ZL   -  Learnt from tutorial of Oskar Veerhoek from the Coding Universe
 *
 */
public class Model {
	private List<Vector3f> vertices = new ArrayList<Vector3f>();
	private List<Vector3f> normals = new ArrayList<Vector3f>();
	private List<Vector2f> texcoords = new ArrayList<Vector2f>();
	private List<Face> faces = new ArrayList<Face>();
	public List<Material> mtl = new ArrayList<Material>();



	public Model() {}
	/**
	 * Parsing the model (vertices, texcoords, normals, faces)
	 * @param f
	 * @return a Model
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Model loadModel(String folder, String file) throws FileNotFoundException, IOException {
		String mtlpath= null;
		BufferedReader reader = new BufferedReader(new FileReader(new File(folder+file)));
		Model m = new Model();
		String line;
		String currentMTL = null;
		
		while ((line = reader.readLine()) != null) { // goes through the file line by line
			if(line.startsWith("mtllib ")){
				mtlpath = line.split(" ")[1];
				try{
					m.setMaterials(Material.loadMTL(folder,mtlpath));
				}catch(Exception e){}
			} else if (line.startsWith("v ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.vertices.add(new Vector3f(x, y, z));
			} else if (line.startsWith("vn ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				m.normals.add(new Vector3f(x, y, z));
			} else if (line.startsWith("vt ")) {
			    float x = Float.valueOf(line.split(" ")[1]);
			    float y = Float.valueOf(line.split(" ")[2]);
			    m.texcoords.add(new Vector2f(x,y));
			} else if(line.startsWith("usemtl ")){
				currentMTL = line.split(" ")[1];
			} else if (line.startsWith("f ")) {
				
				Vector3f vertexIndices = new Vector3f(
						Float.valueOf(line.split(" ")[1].split("/")[0]) - 1, 
						Float.valueOf(line.split(" ")[2].split("/")[0]) - 1, 
						Float.valueOf(line.split(" ")[3].split("/")[0]) - 1);
				
				Vector3f textureIndices = null;
				try{
						textureIndices = new Vector3f(
						Float.valueOf(line.split(" ")[1].split("/")[1]) - 1, 
						Float.valueOf(line.split(" ")[2].split("/")[1]) - 1, 
						Float.valueOf(line.split(" ")[3].split("/")[1]) - 1);
				}catch(Exception e){}
				Vector3f normalIndices = new Vector3f(
						Float.valueOf(line.split(" ")[1].split("/")[2]) - 1, 
						Float.valueOf(line.split(" ")[2].split("/")[2]) - 1, 
						Float.valueOf(line.split(" ")[3].split("/")[2]) - 1);
				m.faces.add(new Face(vertexIndices, normalIndices, textureIndices, currentMTL));
			}
		}
		reader.close();
		return m;
	}
	/**
	 * Generate the display list
	 * @return the display list pointer
	 */
	public int generateDList(){
		String currentMTL = null;
		int objectDisplayList = glGenLists(1);
		
		// Force texture unbind to guarantee texture bind call in display list.
		TextureImpl.unbind();
		
		// Start display list
		glNewList(objectDisplayList, GL_COMPILE);
		
		/*
		 * Draw all faces
		 */
        for (Face face : getFaces()) {
        	/*
        	 * Check if the material has been changed, if yes set the new Material
        	 */
        	if(!face.getMTL().equals(currentMTL)){
        		for(Material mat:mtl){
        			if(mat.getName().equals(face.getMTL())){
        				currentMTL = face.getMTL();
        				mat.useMtl();
        			}
        		}
        	}
        	
        	glBegin(GL_TRIANGLES);
        	/*
        	 * Vertex 1
        	 */
        	Vector3f n1 = getNormals().get((int) face.getNormal().x);					// Normal
        	glNormal3f(n1.x, n1.y, n1.z);												
        	if(face.getTexture()!=null){												// Checks if there are texcoords, if yes
        		Vector2f t1 = getTexcoords().get((int) face.getTexture().x);			// Set the coordinates
        		glTexCoord2f(t1.x, 1-t1.y);
        	}
        	Vector3f v1 = getVertices().get((int) face.getVertex().x);					// Vertex
        	glVertex3f(v1.x, v1.y, v1.z);
        	
           	/*
        	 * vertex 2
        	 */
        	Vector3f n2 = getNormals().get((int) face.getNormal().y);					// Normal
        	glNormal3f(n2.x, n2.y, n2.z);
        	if(face.getTexture()!=null){												// Check if there are texcoords, if yes
	        	Vector2f t2 = getTexcoords().get((int) face.getTexture().y);			// set the coordinates
	        	glTexCoord2f(t2.x, 1-t2.y);
        	}
        	
        	Vector3f v2 = getVertices().get((int) face.getVertex().y);					// Vertex
        	glVertex3f(v2.x, v2.y, v2.z);
        	/*
        	 * vertex 3
        	 */
        	Vector3f n3 = getNormals().get((int) face.getNormal().z);					// Normal
        	glNormal3f(n3.x, n3.y, n3.z);
        	if(face.getTexture()!=null){												// Check if there are texcoords, if yes
	        	Vector2f t3 = getTexcoords().get((int) face.getTexture().z);			// set the coordinates
	        	glTexCoord2f(t3.x, 1-t3.y);
        	}
        	Vector3f v3 = getVertices().get((int) face.getVertex().z);					// Vertex
        	glVertex3f(v3.x, v3.y, v3.z);
        	glEnd();
        }
        
        glEndList();
        
        return objectDisplayList;
	}
	
	/*
	 * Getters and setters
	 */
	public List<Vector3f> getVertices() {return vertices;}
	public void setVertices(List<Vector3f> vertices) {this.vertices = vertices;}

	public List<Vector3f> getNormals() {return normals;}
	public void setNormals(List<Vector3f> normals) {this.normals = normals;}
	
	public List<Vector2f> getTexcoords() {return texcoords;}
	public void setTexCoords(List<Vector2f> texcoords){this.texcoords = texcoords;}
	
	public List<Face> getFaces() {return faces;}
	public void setFaces(List<Face> faces) {this.faces = faces;}

	
	public void setMaterials(ArrayList<Material> mtllist){
		this.mtl = mtllist;
	}
}
/**
 * Face class used to combine the vertices, vertex normals and textures per face
 * @author ZL
 *
 */
class Face {
	private Vector3f vertex = new Vector3f(); // three indices, not vertices or normals
	private Vector3f normal = new Vector3f();
	private Vector3f texture = new Vector3f();
	private String mtlName = null;

	public Face(Vector3f vertex, Vector3f normal, Vector3f texture, String mtlname) {
		this.vertex = vertex;
		this.normal = normal;
		this.texture = texture;
		this.mtlName = mtlname;
	}

	public Vector3f getVertex() {return vertex;}
	public void setVertex(Vector3f vertex) {this.vertex = vertex;}

	public Vector3f getNormal() {return normal;}
	public void setNormal(Vector3f normal) {this.normal = normal;}
	
	public Vector3f getTexture() {return texture;}
	public void setTexture(Vector3f texture) {this.texture = texture;}
	
	public String getMTL(){return mtlName;}
}
/**
 * Material class
 * @author ZL
 *
 */
class Material{
	public String name = null;
	public Vector3f ambient = new Vector3f();
	public Vector3f diffuse = new Vector3f();
	public Vector3f specular = new Vector3f();
	public float alpha =0;
	public float shininess = 0;
	public Texture tex;
	
	public Material(String name){
		setName(name);
	}
	public Vector3f getAmbient() {return ambient;}
	public void setAmbient(Vector3f ambient) {this.ambient = ambient;}

	public Vector3f getDiffuse() {return diffuse;}
	public void setDiffuse(Vector3f diffuse) {this.diffuse = diffuse;}
	
	public Vector3f getSpecular() {return specular;}
	public void setSpecular(Vector3f specular) {this.specular = specular;}
	
	public float getAlpha(){ return alpha;}
	public void setAlpha(float alpha){this.alpha = alpha;}
	
	public float getShininess(){ return shininess;}
	public void setShininess(float shininess){this.shininess = shininess;}
	
	public String getName(){ return name;}
	public void setName(String name){ this.name = name;}
	
	public Texture getTexture(){ return tex;}
	public void setTexture(String texpath) throws IOException{tex = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(texpath));}
	
	public void useMtl(){
		FloatBuffer ambientB = (FloatBuffer) BufferUtils.createFloatBuffer(4). put(ambient.x). put(ambient.y). put(ambient.z).put(alpha).flip();
		FloatBuffer diffuseB = (FloatBuffer) BufferUtils.createFloatBuffer(4). put(diffuse.x). put(diffuse.y). put(diffuse.z).put(alpha).flip();
		FloatBuffer specularB = (FloatBuffer) BufferUtils.createFloatBuffer(4). put(specular.x). put(specular.y). put(specular.z).put(alpha).flip();
		
		glMaterial(GL_FRONT, GL_AMBIENT, ambientB);
		glMaterial(GL_FRONT, GL_DIFFUSE, diffuseB);
		glMaterial(GL_FRONT, GL_SPECULAR, specularB);
		glMaterialf(GL_FRONT, GL_SHININESS, shininess);
		if(tex!=null){
			tex.bind();
		}
		System.out.println(name);
	}
	/**
	 * Parse the materials
	 * @param mtlpath
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<Material> loadMTL(String folder,String mtlpath) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(folder+mtlpath)));
		ArrayList<Material> mtllist = new ArrayList<Material>();
		String line;
		int counter =-1;
				
		while ((line = reader.readLine()) != null) { // goes through the file line by line
			
			if(line.startsWith("newmtl")){
				mtllist.add(new Material(line.split(" ")[1]));
				counter++;
				
			}else if(line.startsWith("Ns ") && counter>=0){
				mtllist.get(counter).setShininess(Float.valueOf(line.split(" ")[1]));
				
			}else if(line.startsWith("Ka ")&& counter>=0){
				mtllist.get(counter).setAmbient(new Vector3f(
						Float.valueOf(line.split(" ")[1]),
						Float.valueOf(line.split(" ")[2]), 
						Float.valueOf(line.split(" ")[3])));
				
			}else if(line.startsWith("Kd ")&& counter>=0){
				mtllist.get(counter).setDiffuse(new Vector3f(
						Float.valueOf(line.split(" ")[1]),
						Float.valueOf(line.split(" ")[2]), 
						Float.valueOf(line.split(" ")[3])));
				
			}else if(line.startsWith("Ks ")&& counter>=0){
				mtllist.get(counter).setSpecular(new Vector3f(
						Float.valueOf(line.split(" ")[1]),
						Float.valueOf(line.split(" ")[2]), 
						Float.valueOf(line.split(" ")[3])));
				
			}else if(line.startsWith("d ")&& counter>=0){
				mtllist.get(counter).setAlpha(Float.valueOf(line.split(" ")[1]));
				
			}else if(line.startsWith("map_Kd ")&& counter>=0){
				try{
					mtllist.get(counter).setTexture(folder+line.split(" ")[1]);
				}catch(Exception e){}
			}
		}
		reader.close();
		return mtllist;
	}
}