import java.awt.Font;
import java.io.InputStream;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

import static org.lwjgl.opengl.GL11.*;


public class textTest {
	private Font awtFont2;
	private TrueTypeFont myfont;
	
	public void start() throws LWJGLException{
		Display.setDisplayMode(Display.getDisplayMode());
		Display.create();
		initGL();
		initFont();
		while(!Display.isCloseRequested()){
			glClear(GL_COLOR_BUFFER_BIT);
			glClearColor(0f, 0f, 0f, 1f);
			
			glPushMatrix();
			glTranslatef(0, Display.getHeight(), 0);
			glScalef(1, -1, 1);
			myfont.drawString(0, 0, "test");
			glPopMatrix();
			
			Display.update();Display.sync(60);
		}
	}
	
	public void initGL(){
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), 0, Display.getHeight(), 1, -1);
		glMatrixMode(GL_MODELVIEW);
		
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_LIGHTING);
	}
	
	public void initFont(){
		try {			
			InputStream inputStream	= ResourceLoader.getResourceAsStream("CALIBRI.TTF");

			awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);		
			awtFont2 = awtFont2.deriveFont(60f); // set font size
			myfont = new TrueTypeFont(awtFont2, true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		try {
			new textTest().start();
		} catch (LWJGLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
