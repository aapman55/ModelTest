import java.awt.Font;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;

import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

public class Text{
	private Font awtFont;
	private TrueTypeFont myfont;
	private float baseFontSize = 60;
	
	public Text(){
		initFont();
	}
	
	public void draw(int x , int y , float FontSize, String text){
		float derivedFont = FontSize/baseFontSize;
		glPushMatrix();
		glTranslatef(x, y, 0);
		glScalef(derivedFont, derivedFont, derivedFont);
		myfont.drawString(0, 0, text);
		glPopMatrix();
	}
	
	public void initFont(){
		try {			
			InputStream inputStream	= ResourceLoader.getResourceAsStream("CALIBRI.TTF");

			awtFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);		
			awtFont = awtFont.deriveFont(baseFontSize); // set font size
			myfont = new TrueTypeFont(awtFont, true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}