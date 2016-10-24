class SketchCard {
  PImage thumbnail;
  String name;
  float x, y;

  SketchCard(String imagePath, float x, float y, int w, int h) {
    thumbnail = loadImage(imagePath);
    thumbnail.resize(w, h);
    name = imagePath;
    this.x = x;
    this.y = y;
  }

  void resize(int w, int h){
    thumbnail.resize(w, h);
  }

  boolean mouseOverCard(){
    return mouseDown && x < mouseX && y < mouseY && mouseX < x+thumbnail.width && mouseY < y+thumbnail.height;
  }
    
  void display(float x, float y, float s){
    boolean over = mouseOverCard();
    float dw = this.width()*s;
    float dh = this.height()*s;
    image(thumbnail, x-dw/2, y-dh/2,dw,dh);
    if(over){
      switchToSketch(name);
    }
  }

  float width(){
    return thumbnail.width;
  }

  float height(){
    return thumbnail.height;
  }
}