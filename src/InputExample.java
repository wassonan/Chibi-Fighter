import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import net.java.games.input.*;

public class InputExample {
   
   Controller[] ca;
   int gp=-1; //to store controller number of the gamepad
    
    public void start() {
        try {
         Display.setDisplayMode(new DisplayMode(800,600));
         Display.setLocation(43, 12);         
         Display.create();
          ca = ControllerEnvironment.getDefaultEnvironment().getControllers();    
          
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
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        
        // init OpenGL here
        
        while (!Display.isCloseRequested()) {
            
            // render OpenGL here
            
            pollInput();
            Display.update();
        }
        
        Display.destroy();
    }
    
    public void pollInput() {
       ca[gp].poll();
       EventQueue queue = ca[gp].getEventQueue();
          Event event = new Event();

          while(queue.getNextEvent(event)) {
             StringBuffer buffer = new StringBuffer(ca[gp].getName());
           buffer.append(" at ");
           buffer.append(event.getNanos()).append(", ");
           Component comp = event.getComponent();
           buffer.append(comp.getName()).append(" changed to ");
           float value = event.getValue(); 
           buffer.append(value);
           System.out.println(buffer.toString());
          }
          
          /**
          ca[gp-1].poll();
          queue = ca[gp-1].getEventQueue();
             event = new Event();

             while(queue.getNextEvent(event)) {
                StringBuffer buffer = new StringBuffer(ca[gp-1].getName());
              buffer.append(" #2 at ");
              buffer.append(event.getNanos()).append(", ");
              Component comp = event.getComponent();
              buffer.append(comp.getName()).append(" changed to ");
              float value = event.getValue(); 
              buffer.append(value);
              System.out.println(buffer.toString());
             }
          **/
          
       if (Mouse.isButtonDown(0)) {
            int x = Mouse.getX();
            int y = Mouse.getY();
            
            System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
        }
        
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            System.out.println("SPACE KEY IS DOWN");
        }
                
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_A) {
                    System.out.println("A Key Pressed");
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_S) {
                    System.out.println("S Key Pressed");
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_D) {
                    System.out.println("D Key Pressed");
                }
                } else {
                if (Keyboard.getEventKey() == Keyboard.KEY_A) {
                    System.out.println("A Key Released");
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_S) {
                    System.out.println("S Key Released");
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_D) {
                    System.out.println("D Key Released");
                }
            }
        }
    }
    
    public static void main(String[] argv) {
        InputExample inputExample = new InputExample();
        inputExample.start();
    }
}