import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Transform;
import org.newdawn.slick.opengl.TextureLoader;


public class MovementHitboxTest {
		String type="Rectangle";
		int id=0;
		
		
		float x=0;
		float y=0;
		float width=0;
		float height=0;
		
		float vspeed=0;
		float hspeed=0;
		
		boolean solid = true;
		int port = 0;
		//port 0 = false;
		
		Rectangle moveBox = new Rectangle(0, 0, 0, 0);
		boolean collision = false;
		
		public MovementHitboxTest(int ident){
			//default
			x=32;
			y=32;
			width=3;
			height=3;
			id=ident;
		}
		public MovementHitboxTest(int ident, float x, float y){
			this.x=x;
			this.y=y;
			id=ident;
		}
		public MovementHitboxTest(int ident, float x, float y, float w, float h){
			this.x=x;
			this.y=y;
			this.width=w;
			this.height=h;
			id=ident;
		}
		public MovementHitboxTest(int ident, float x, float y, float w, float h, boolean s){
			this.x=x;
			this.y=y;
			this.width=w;
			this.height=h;
			this.solid=s;
			id=ident;
		}
		public MovementHitboxTest(int ident, float x, float y, float w, float h, int p){
			this.x=x;
			this.y=y;
			this.width=w;
			this.height=h;
			port=p;
			id=ident;
		}
		public MovementHitboxTest(int ident, float x, float y, float w, float h, boolean s, int p){
			this.x=x;
			this.y=y;
			this.width=w;
			this.height=h;
			this.solid=s;
			port=p;
			id=ident;
		}
		
		
				
		public void setVspeed(float v){
			vspeed=v/10;
		}
		public void setHspeed(float h){
			hspeed=h/10;
			hspeed = runSpeed*(hspeed/10);
		}
		public void setSolid(boolean s){
			solid=s;
		}
		
		public float getWidth(){
			return width;
		}
		public float getHeight(){
			return height;
		}
		public float getX(){
			return x;
		}
		public float getY(){
			return y;
		}
		
		/**States**/
		boolean grounded=false;
		/**Characteristics and Stats**/
		float maxFallSpeed=10;
		float fallAccel=(float) 0.5;
		float jumpSpeed=-10;
		float runSpeed = 25;
		public void setMaxFallSpeed(float m){
			maxFallSpeed=m;
		}
		public void setFallAccelleration(float a){
			fallAccel=a;
		}
		public void setJumpSpeed(float s){
			jumpSpeed=s;
		}
		
		/**SPECIAL ACTIONS**/
		public void jump(){
			if(grounded){
				vspeed=jumpSpeed;
			}
			vspeed=jumpSpeed;
		}
		
		public void update(ArrayList<MovementHitboxTest> HitboxEntities){
			boolean v = false, h=false;
			
			//calculate gravity
			if(vspeed<maxFallSpeed)vspeed+=fallAccel;
			
			
			float vDistance=vspeed,hDistance=hspeed;
			collision=false;
			
			if(solid){
				for(int i=0; i<HitboxEntities.size(); i++){
					if(id!=HitboxEntities.get(i).id&&HitboxEntities.get(i).solid){
						if(!v&&calculateIntersect(HitboxEntities.get(i),vspeed,0)){
							v=true;
							collision = true;
							
							if(vspeed<HitboxEntities.get(i).vspeed){
								
							}
							
							/**FIX THIS LATER
							float vTemp = vDistance;
							if(vspeed<HitboxEntities.get(i).vspeed){
								//collision above
								vTemp=(HitboxEntities.get(i).getY()+HitboxEntities.get(i).getHeight() +HitboxEntities.get(i).vspeed)-(y +vspeed);
								System.out.println(id+":  X:"+x+" Y:"+y+" Height:"+height+" Width:"+width+"  other:  X:"+HitboxEntities.get(i).x+" Y:"+HitboxEntities.get(i).y+" Height:"+HitboxEntities.get(i).height+" Width:"+HitboxEntities.get(i).width);
							}else{
								//collision below
								vTemp=(HitboxEntities.get(i).getY() +HitboxEntities.get(i).vspeed)-(y+height +vspeed);
							}
							if(vTemp<vDistance){
								System.out.println("VSPEED:"+vspeed+" VTEMP:"+vTemp+" VDISTANCE:"+vDistance);
								vDistance=vTemp;
							}
							**/
							
						}
						if(!h&&calculateIntersect(HitboxEntities.get(i),0,hspeed)){
							h=true;
							collision = true;
						}
					}
				}
			}
			
			if(vspeed!=0){
				if(!v){
					y+=vDistance;
				}
				
			}
			if(hspeed!=0){
				if(!h){
					x+=hDistance;
				}
				
			}
		}
		
		public boolean calculateIntersect(MovementHitboxTest other, float v, float h){
			Rectangle tempRectangle = new Rectangle((int) (x+h), (int) (y+v), (int) width, (int) height);
			Rectangle otherRectangle = new Rectangle((int) (other.x+other.hspeed), (int) (other.y+other.vspeed), (int) other.width, (int) other.height);
			return tempRectangle.intersects(otherRectangle);
		}
		
		public boolean intersects(MovementHitboxTest other) {
			moveBox.setBounds((int) x, (int) y, (int) width, (int) height);
			Rectangle otherRectangle = new Rectangle(other.getX(),other.getY(),other.getWidth(),other.getHeight());
			return moveBox.intersects(otherRectangle);
		}
		public boolean intersects(Rectangle other) {
			return moveBox.intersects(other);
		}
		public boolean intersects(Rectangle a, Rectangle b) {
			return a.intersects(b);
		}
		
		
		public void draw(){
			drawMovementHitbox();
		}
		
		public void drawMovementHitbox() {
			
				GL11.glColor3f(0.5f,0.5f,1.0f);
				if(!solid){
					GL11.glColor3f(1.0f,0.5f,0.5f);
				}
				if(collision){
					GL11.glColor3f(0, 1.0f, 1.0f);
				}
				//draw everything
			GL11.glBegin(GL11.GL_QUADS);
	    		/**TOPLEFT**/GL11.glVertex2f(x+width,y);
		    	/**TOPRIGHT**/GL11.glVertex2f(x,y);
		    	/**BOTLEFT**/GL11.glVertex2f(x,y+height);
		    	/**BOTRIGHT**/GL11.glVertex2f(x+width,y+height);
		    GL11.glEnd();
			
			//glRectd(x,y,x+width,y+height);
		}
}
