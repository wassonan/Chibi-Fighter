
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class MovementTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MovementTest board = new MovementTest();
		board.start();
	}
	
	Controller[] ca;
	int gp=-1; //to store controller number of the gamepad
	    
	ArrayList<MovementHitboxTest> HitboxEntities = new ArrayList<MovementHitboxTest>();
	
	
	
	boolean quitting = false;
	long lastFrame;
	int fps;
	long lastFPS;
	
	int FULLSCREEN=0;
	int BorderlessWindowed=1;
	int windowed=2;
	
	int res1920x1080=0;
	int res1600x900=1;
	int res1280x720=2;
	
	int resolution=res1280x720;
	int mode=windowed;
	
	boolean VSYNC=true;
	
	
	public void start(){
		Mouse.setGrabbed(true);
		ca = ControllerEnvironment.getDefaultEnvironment().getControllers();    
		  //it does the controlling with the controllerssss
		  for(int i =0;i<ca.length;i++){

		        /* Get the name of the controller */
		        System.out.println(ca[i].getName());
		        System.out.println("Type: "+ca[i].getType().toString());                
		        if(ca[i].getType().toString().indexOf("Gamepad")>-1){
		           gp=i;
		           

		           /* Get this controllers components (buttons and axis) */
		           Component[] components = ca[i].getComponents();
		           System.out.println("Component Count: "+components.length);
		           for(int j=0;j<components.length;j++){
		               
		               /* Get the components name */
		               System.out.println("Component "+j+": "+components[j].getName());
		               System.out.println("    Identifier: "+ components[j].getIdentifier().getName());
		               System.out.print("    ComponentType: ");
		               if (components[j].isRelative()) {
		                   System.out.print("Relative");
		               } else {
		                   System.out.print("Absolute");
		               }
		               if (components[j].isAnalog()) {
		                   System.out.print(" Analog");
		               } else {
		                   System.out.print(" Digital");
		               }
		           }
		           System.out.println("");
		        }
		   }
		//look, I just wanna walk the dinosaur
		
		try{
			if(resolution==res1920x1080)Display.setDisplayMode(new DisplayMode(1920,1080));
			else if(resolution==res1600x900)Display.setDisplayMode(new DisplayMode(1600,900));
			else if(resolution==res1280x720)Display.setDisplayMode(new DisplayMode(1280,720));
			else{
				System.out.println("Invalid Resolution");
				System.exit(0);
			}
			Display.create();
		}catch (LWJGLException e){
			e.printStackTrace();
			Display.destroy();
			System.exit(0);
		}
		
		initGL();
		getDelta();
		lastFPS = getTime();
		
		boolean fullscreen = true;
		if(mode!=FULLSCREEN) fullscreen = false;
		
		if(resolution==res1920x1080)setDisplayMode(1920,1080,fullscreen);
		else if(resolution==res1600x900)setDisplayMode(1600,900,fullscreen);
		else if(resolution==res1280x720)setDisplayMode(1280,720,fullscreen);
		Display.setVSyncEnabled(VSYNC);
		
		/**SETS UP WORLD**/
		setUpWorld();
		while(!Display.isCloseRequested()&&!quitting){
			int delta = getDelta();
			update(delta);
			
			if(!quitting){
				renderGL();
				Display.update();
				Display.sync(60);
			}
		}
		
		Display.destroy();
	}
	
	public void initGL() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, 1920, 1080, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL_TEXTURE_2D);
		GL11.glEnable(GL_BLEND);
	}
	
	public int getDelta() {
	    long time = getTime();
	    int delta = (int) (time - lastFrame);
	    lastFrame = time;
 
	    return delta;
	}
	
	public long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public void setDisplayMode(int width, int height, boolean fullscreen) {

		// return if requested DisplayMode is already set
                if ((Display.getDisplayMode().getWidth() == width) && 
			(Display.getDisplayMode().getHeight() == height) && 
			(Display.isFullscreen() == fullscreen)) {
			return;
		}
		
		try {
			DisplayMode targetDisplayMode = null;
			
			if (fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;
				
				for (int i=0;i<modes.length;i++) {
					DisplayMode current = modes[i];
					
					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against the 
						// original display mode then it's probably best to go for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
						    (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width,height);
			}
			
			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);
			
		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
		}
	}
	
	public void updateFPS(){
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	public void renderGL() {
		// Clear The Screen And The Depth Buffer
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glColor3f(0.5f,0.5f,1.0f);
			//draw everything
		/**GL11.glBegin(GL11.GL_QUADS);
	    	GL11.glVertex2f(100,100);
	    	GL11.glVertex2f(100+200,100);
	    	GL11.glVertex2f(100+200,100+200);
	    	GL11.glVertex2f(100,100+200);
	    GL11.glEnd();**/
		for(int i = 0; i < HitboxEntities.size(); i++){
			HitboxEntities.get(i).draw();
		}
			
	}
	
	public void update(int delta){
		if(!quitting){
			//take inputs and let AI make decisions
			//http://www.youtube.com/watch?v=ZKJC2cloIqc @11:41
			
			
			//TAKE MORE INPUT DOE
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				try {
					Display.setFullscreen(false);
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
				quitting=true;
	        }
			pollInput();
			if(!quitting){
				//update the world, view, etc
				for(int i = 0; i < HitboxEntities.size(); i++){
					HitboxEntities.get(i).update(HitboxEntities);
				}
				
			}
			
			
		}
	}
	
	public void pollInput(){
		ca[gp].poll();
	       EventQueue queue = ca[gp].getEventQueue();
	          Event event = new Event();

	          while(queue.getNextEvent(event)) {
	             StringBuffer buffer = new StringBuffer(ca[gp].getName());
	           buffer.append(" at ");
	           buffer.append(event.getNanos()).append(", ");
	           Component comp = event.getComponent();
	           if(comp.getName().equals("X Axis")){
	        	   for(int i = 0; i < HitboxEntities.size(); i++){
						if(HitboxEntities.get(i).port==1){
							HitboxEntities.get(i).setHspeed((int)(event.getValue()*100));
						}
					}
	           }
	           /**
	           if(comp.getName().equals("Y Axis")){
	        	   for(int i = 0; i < HitboxEntities.size(); i++){
						if(HitboxEntities.get(i).port==1){
							HitboxEntities.get(i).setVspeed((int)(event.getValue()*100));
						}
					}
	        	   
	           }
	           **/
	           if(comp.getName().equals("Button 0")){
	        	   for(int i = 0; i < HitboxEntities.size(); i++){
						if(HitboxEntities.get(i).port==1&&event.getValue()==1){
							HitboxEntities.get(i).jump();
						}
					}
	        	   
	           }
	           buffer.append(comp.getName()).append(" changed to ");
	           float value = event.getValue(); 
	           buffer.append(value);
	           System.out.println(buffer.toString());
	          }
	}
	
	
	
	public void setUpWorld(){
		MovementHitboxTest JeffTheSquare = new MovementHitboxTest(0,640,640,32,32,1);
		MovementHitboxTest MortimerTheRectangle = new MovementHitboxTest(1,640,320,64,32,false,0);
		MovementHitboxTest SterranceTheRectangle = new MovementHitboxTest(2,640,960,640,16,true,0);
		MovementHitboxTest AlgiersTheRectangle = new MovementHitboxTest(3,32,32,32,640,true,0);
		MovementHitboxTest ThaddeusTheRectangle = new MovementHitboxTest(4,960,320,64,128,true,0);
		
		MortimerTheRectangle.setFallAccelleration(0);
		SterranceTheRectangle.setFallAccelleration(0);
		AlgiersTheRectangle.setFallAccelleration(0);
		ThaddeusTheRectangle.setFallAccelleration(0);
		
		HitboxEntities.add(JeffTheSquare);
		HitboxEntities.add(MortimerTheRectangle);
		HitboxEntities.add(SterranceTheRectangle);
		HitboxEntities.add(AlgiersTheRectangle);
		HitboxEntities.add(ThaddeusTheRectangle);
	}
}

