
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.util.ResourceLoader;

import static org.lwjgl.util.glu.GLU.*;
import static org.lwjgl.opengl.GL11.*;
/**
 * Test for loading models
 * @author ZL
 *
 */
public class Modelviewer {
	private float horangle=0;
	private float verangle=0;
	private float dX = 0, dY=0, dZ=0;
//	private Font awtFont2;
//	private TrueTypeFont myfont;
	private Text text;
	private FloatBuffer projectionWorld, projectionHUD;
	private FloatBuffer lightPosition1;
	
	public void start() throws LWJGLException{
		File path = null;
		try {
			path = choosefile();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "No input, application wil close now","No input",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
				
		String folderpath = path.getParent();
		String filepath = path.getName();
	
		
		Display.setDisplayMode(Display.getDesktopDisplayMode());
		Display.setFullscreen(false);
		Display.setVSyncEnabled(true);
		Display.create();
		
		Model m = null;
		text = new Text();
		try {
			m = Model.loadModel(folderpath+"\\",filepath);				// Specify model .obj file

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int drawlist1 = m.generateDList();
		//m=null;																// Drop the memory used for the model
		
		initGL();
//		initFont();
		while(!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
			inputpoll();
			
			// Start draw
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			// Change projection and view to first person view
			changeToFPV();
			
			// Reposition light if needed (else light will be relative to player coordinates!!!!)
			
			glEnable(GL_LIGHTING);
			glEnable(GL_DEPTH_TEST);
			glDisable(GL_BLEND);
			
			// Push matrix
			glPushMatrix();
			
			// Edit temporary model matrix
			glTranslatef(dX, dY, dZ);
			glRotatef(horangle, 0, 1, 0);
			glRotatef(verangle, 1, 0, 0);
			
			glCallList(drawlist1); TextureImpl.unbind();
			
			// Pop back to view matrix
			glPopMatrix();
			
			// Change projection and view to HUD
			changeToHUD();
			
			glDisable(GL_DEPTH_TEST);
			glEnable(GL_BLEND);
			glDisable(GL_LIGHTING);
			
//			myfont.drawString(20, 50, "BB");
			text.draw(0, 0, 300, "test");
			
			Display.update();
			Display.sync(60);
		}
	}
	
	public void changeToHUD() {
		glMatrixMode(GL_PROJECTION); // change to projection matrix
		glLoadMatrix(projectionHUD);
		glMatrixMode(GL_MODELVIEW); // change to modelview matrix
		glLoadIdentity(); // load identity
	}
	
	public void changeToFPV() {
		glMatrixMode(GL_PROJECTION);
		glLoadMatrix(projectionWorld);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		// Set view matrix to (inverse) player location/rotation
		glRotatef(180, 0, 1, 0);
		glTranslatef(0, 0, 10);
	}
	
	public void initFont(){
		try(InputStream in = ResourceLoader.getResourceAsStream("CALIBRI.TTF")) {
			Font calibri = Font.createFont(Font.TRUETYPE_FONT, in);
			Font calibri60 = calibri.deriveFont(60f);
//			myfont = new TrueTypeFont(calibri60, true);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Poll for Keyinput
	 */
	public void inputpoll(){
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){horangle+=.5;}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){horangle-=.5;}
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)){verangle +=.5;}
		if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){verangle -=.5;}
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){dZ -=.1;}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){dZ +=.1;}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){dX -=.1;}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){dX += .1;}
	}
	/**
	 * Initialize openGL
	 */
	public void initGL(){
		// Initialize projection buffers
		projectionWorld = BufferUtils.createFloatBuffer(16);
		projectionHUD = BufferUtils.createFloatBuffer(16);
		
		// Switch to projection matrix
		glMatrixMode(GL_PROJECTION);
		
		// Initialize player projection matrix
		glLoadIdentity();
		gluPerspective(60, (float)Display.getWidth()/Display.getHeight(), 0.001f, 100f);
		glGetFloat(GL_PROJECTION_MATRIX, projectionWorld);
		
		// Initialize heads up display matrix
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1);
		glGetFloat(GL_PROJECTION_MATRIX, projectionHUD);
		
		// Switch to modelview
		glMatrixMode(GL_MODELVIEW);
		
		// Cullface
		glCullFace(GL_BACK);
		glEnable(GL_CULL_FACE);
		
		//depth
		glEnable(GL_DEPTH_TEST);
		
		glEnable(GL_LIGHT0);
		glEnable(GL_LIGHTING);
		
		glEnable(GL_TEXTURE_2D);
		
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		lightPosition1 =  (FloatBuffer) BufferUtils.createFloatBuffer(4).put(0).put(0).put(0).put(1).flip();
		
		glLight(GL_LIGHT0, GL_POSITION, lightPosition1);
	}
	
	public File choosefile() throws IOException{
		JFileChooser jfc = new JFileChooser(System.getProperty("user.dir"));		
		jfc.setMultiSelectionEnabled(false);
		jfc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("OBJ Files", "obj");
		jfc.addChoosableFileFilter(filter);
		int res = jfc.showOpenDialog(null);
		
		if(res == JFileChooser.APPROVE_OPTION){
			File file = jfc.getSelectedFile();
			return file;
			
			
		}
		throw new IOException("You pressed cancel or X");
	}
	
	/**
	 * Main program 
	 * @param args
	 */
	public static void main(String[] args){
		try {
			new Modelviewer().start();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
