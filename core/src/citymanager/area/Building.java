package citymanager.area;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Building {
    Sprite imageSprite;

    int gridSpace;

    int positionX;
    int positionY;

    public Building(Texture tex, int occupiedGridSpace, int posX, int posY){
        imageSprite = new Sprite(tex);
        this.gridSpace = occupiedGridSpace;
        positionX = posX;
        positionY = posY;
        imageSprite.setAlpha(0.8f);
    }

    public void setSize(float width, float height){
        this.imageSprite.setSize(width,height);
    }
    public void setPosition(float x, float y){
        this.imageSprite.setPosition(x,y);
    }
    public void setOrigin(float x, float y){
        this.imageSprite.setOrigin(x,y);
    }
    public void draw(Batch batch){
        imageSprite.draw(batch);
    }
    public Texture getTexture(){
        return this.imageSprite.getTexture();
    }
}
