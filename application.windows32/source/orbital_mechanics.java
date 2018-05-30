import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class orbital_mechanics extends PApplet {

class Mass {
  public Mass(float v0[], float x, float y) {
    this.x = x; this.y = y;
    vx = v0[0]; vy = v0[1];
  }
  public float x,y;
  public float vx, vy;
  public void move() {
    x += vx; y += vy;
  }
}
abstract class GUIComp {
  public String id = "";
  public GUIComp(float x, float y, float w, float h) {
    this.x = x; this.y = y; this.w = w; this.h = h;
  }
  public float w,h;
  public float x,y;
  public void hoverResponse() {}
  public void unHover() {}
  public boolean hovered() {
    boolean xBounds = mouseX > x && mouseX < (x+w);
    boolean yBounds = mouseY > y && mouseY < (y+h);
    if (xBounds && yBounds) {
      hoverResponse();
      return true;
    }
    // if not hovered
    unHover();
    return false;
  }
  public void clickResponse() {}
  public void unClick() {}
  public void notClick() {}
  public boolean clicked() {
    boolean xBounds = mouseX > x && mouseX < (x+w);
    boolean yBounds = mouseY > y && mouseY < (y+h);
    if (xBounds && yBounds) {
      clickResponse();
      return true;
    }
    notClick();
    return false;
  }
  public void dragResponse() {}
  public void notDrag() {}
  public boolean dragged() {
    boolean xBounds = pmouseX > x && pmouseX < (x+w);
    boolean yBounds = pmouseY > y && pmouseY < (y+h);
    if (xBounds && yBounds) {
      dragResponse();
      return true;
    }
    notDrag();
    return false;
  }
}
class Button extends GUIComp {
  public Button(float x, float y, float w, float h) {
    super(x,y,w,h);
    id = "Button";
  }
  public int colour[] = {0, 200, 0};
  public String text = "Start Over";
  public int txtSize = 13;
  public void hoverResponse() {
    colour[0] = 0; colour[1] = 255; colour[2] = 0;
  }
  public void unHover() {
    colour[0] = 0; colour[1] = 200; colour[2] = 0;
  }
  public void clickResponse() {
    // search gui list for slider value for initial velocity
    for (GUIComp comp : gui) {
      if (comp.id == "Slider" && ((Slider)comp).sliderID == "InitialVX") {
        v0[0] = 2 * sliderMax * ( ((Slider)comp).value - 0.5f );
      } else if (comp.id == "Slider" && ((Slider)comp).sliderID == "InitialVY") {
        v0[1] = 2 * sliderMax * ( ((Slider)comp).value - 0.5f );
      }
    }
    mass.x = x0; mass.y = y0;
    mass.vx = v0[0]; mass.vy = v0[1];
    displayStart = false;
  }
}
class Reset extends Button {
  public Reset(float x, float y, float w, float h) {
    super(x,y,w,h);
    id = "Button";
    text = "Set 0";
  }
  public void clickResponse() {
    for (GUIComp comp : gui) {
      if (comp.id == "Slider") {
        ((Slider)comp).setValue(0.5f);
      }
    }
  }
}
class ResetX extends Button {
  public ResetX(float x, float y, float w, float h) {
    super(x,y,w,h);
    id = "Button";
    text = "Set X 0";
  }
  public void clickResponse() {
    for (GUIComp comp : gui) {
      if (comp.id == "Slider" && ((Slider)comp).sliderID == "InitialVX") {
        ((Slider)comp).setValue(0.5f);
        break;
      }
    }
  }
}
class ResetY extends Button {
  public ResetY(float x, float y, float w, float h) {
    super(x,y,w,h);
    id = "Button";
    text = "Set Y 0";
  }
  public void clickResponse() {
    for (GUIComp comp : gui) {
      if (comp.id == "Slider" && ((Slider)comp).sliderID == "InitialVY") {
        ((Slider)comp).setValue(0.5f);
        break;
      }
    }
  }
}
class Slider extends GUIComp {
  public String sliderID;
  public SliderBlock block;
  public char direction;
  public Slider(float x, float y, float w, float h, char direction) {
    super(x,y,w,h);
    id = "Slider";
    sliderID = "InitialVX";
    this.direction = direction;
  }
  boolean sliderBlock = false;
  public void addSlider(ArrayList<GUIComp> gui) {
    if (sliderBlock) {
      return;
    }
    float xb = 0; float yb = 0;
    float wb = 0; float hb = 0;
    if (direction == 'x') {
      yb = y - (30 / 2) + h / 2;
      xb = x;
      wb = 10; hb = 30;
    } else if (direction == 'y') {
      yb = y;
      xb = x - (30 / 2) + w / 2;
      wb = 30; hb = 10;
    }
    block = new SliderBlock(xb, yb, wb, hb, this);
    gui.add(block);
    sliderBlock = true;
  }
  public void setValue(float val) {
    if (val < 0 || val > 1) {
      return;
    }
    value = val;
    if (direction == 'x') {
      block.x = (w - block.w) * val + x;
    } else  {
      block.y = (h - block.h) * val + y;
    }    
  }
  public float value;
}
class SliderBlock extends GUIComp {
  public Slider parent;
  public SliderBlock(float x, float y, float w, float h, Slider parent) {
    super(x,y,w,h);
    id = "SliderBlock"; this.parent = parent;
  }
  public void dragResponse() {
    // dont do anything if dragged out of bounds
    if (parent.direction == 'x') {
      boolean bounds = mouseX < parent.x || mouseX > (parent.x+parent.w);
      if (bounds) {
        return;
      }
      x += mouseX - pmouseX;
      parent.value = (x - parent.x) / (parent.w - w);
    } else {
      boolean bounds = mouseY < parent.y || mouseY > (parent.y+parent.h);
      if (bounds) {
        return;
      }
      y += mouseY - pmouseY;
      parent.value = (y - parent.y) / (parent.h - h);
    }
  }
}
ArrayList<GUIComp> gui;

Mass mass;
float v0[] = {4.6f, 0};
float x0 = 500; float y0 = 250;
float sliderMax = 20;
public void setup() {
  
  mass = new Mass(v0, x0, y0);
  surface.setTitle("Simple Orbit Sim");
  
  gui = new ArrayList();
  gui.add(new Button(920, 100, 80, 50));
  gui.add(new Slider(20, 550, 300, 10, 'x'));
  ((Slider)gui.get(1)).addSlider(gui);
  ((Slider)gui.get(1)).setValue(0.5f);
  
  Slider scroller = new Slider(935, 205, 10, 300, 'y');
  scroller.sliderID = "InitialVY";
  gui.add(scroller);
  scroller.addSlider(gui);
  scroller.setValue(0.5f);
  
  gui.add(new Reset(920, 550, 80, 50));
  gui.add(new ResetY(840, 550, 80, 50));
  gui.add(new ResetX(760, 550, 80, 50));
}
public void mouseMoved() {
  for (GUIComp comp : gui) {
    comp.hovered();
  }
}
public void mousePressed() {
  boolean clicked = false;
  for (GUIComp comp : gui) {
    clicked |= comp.clicked();
  }
  if (!clicked) {
    displayStart = true;
    x0 = mouseX; y0 = mouseY;
  }
}
public void mouseDragged() {
  for (GUIComp comp : gui) {
    comp.dragged();
  }
}
public void drawCent() {
  ellipse(500, 300, 50, 50);
}
float centerMass = 10000000;
public void execShape() {
  float deltaX = mass.x - 500; float deltaY = mass.y - 300;
  float accX = (float)(-6.67f * Math.pow(10.0f, -5) * centerMass * deltaX / Math.pow(deltaX * deltaX + deltaY * deltaY, 3.0f/2.0f));
  float accY = (float)(-6.67f * Math.pow(10.0f, -5) * centerMass * deltaY / Math.pow(deltaX * deltaX + deltaY * deltaY, 3.0f/2.0f));
  mass.vx += accX; mass.vy += accY;
  mass.move();
}
// true if user clicks a new spot so start point should be shown
boolean displayStart = false;
public void guiDraw() {
  for (GUIComp comp : gui) {
    if (comp.id == "Button") {
      Button butt = (Button)comp;
      fill(butt.colour[0], butt.colour[1], butt.colour[2]);
      strokeWeight(2);
      rect(butt.x,butt.y,butt.w,butt.h);
      fill(0);
      textSize(butt.txtSize);
      text(butt.text,butt.x + 4 , butt.y + 25);
    } 
    if (comp.id == "Slider") {
      fill(0, 100, 0);
      strokeWeight(2);
      rect(comp.x, comp.y, comp.w, comp.h);
    } 
    if (comp.id == "SliderBlock") {
      fill(0, 0, 100);
      strokeWeight(2);
      rect(comp.x, comp.y, comp.w, comp.h);
    }
  }
  if (displayStart) {
    stroke(255,0,0);
    point(x0, y0);
  }
}
public void draw() {
  background(5, 5, 20);
  guiDraw();
  fill(200, 210, 170);
  stroke(30,30,60);
  strokeWeight(10);
  drawCent();
  execShape();
  ellipse(mass.x, mass.y, 20, 20);
}
  public void settings() {  size(1000,600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "orbital_mechanics" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
