package citymanager.area;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class CamController extends InputAdapter {
    final OrthographicCamera cam;
    final Vector3 lastPosition = new Vector3(-1, -1, -1);
    Vector3 currPosition = new Vector3();
    Vector3 deltaPosition = new Vector3();
    int zoomLevel = 1;
    //max bounds = area/gameworld


    public CamController(OrthographicCamera cam){
        this.cam = cam;
        System.out.println(" Cam: "+cam.position.x+" "+cam.position.y+" "+cam.position.z);
    }
    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
//			currPosition.set(screenX, screenY, 0);
//			if( !(lastPosition.x == -1 && lastPosition.y == -1 && lastPosition.z == -1) ) {
//				//deltaPosition = this.cam.unproject( new Vector3(lastPosition.x, lastPosition.y, 0) );
//				deltaPosition = this.cam.getPickRay(lastPosition.x, lastPosition.y);
//				deltaPosition.sub(currPosition);
//				cam.position.add(deltaPosition.x, deltaPosition.y, 0);
//			}
//			lastPosition.set(screenX, screenY, 0);

        //length from first touch to last
        currPosition.set(screenX, screenY, 0);
        if( !(lastPosition.x == -1 && lastPosition.y == -1 && lastPosition.z == -1) ) {
            deltaPosition = currPosition;
            deltaPosition.sub(lastPosition);
            cam.position.add(deltaPosition.x, deltaPosition.y, 0);
        }
        lastPosition.set(screenX, screenY, 0);

        //System.out.println("Curr: "+screenX+" "+screenY+" Last: "+lastPosition.x+" "+lastPosition.y+" Delta: "+deltaPosition.x+" "+deltaPosition.y+" Cam: "+cam.position.x+" "+cam.position.y+" ");

        return false;
    }
    @Override public boolean touchUp(int x, int y, int pointer, int button) {
        lastPosition.set(-1, -1, -1);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        //zoom out // scroll down
        if( amount == 1 ){
            if( zoomLevel <= 10 && zoomLevel > 1 ){
                zoomLevel = zoomLevel-amount;
            }
        }
        //zoom in // scroll up
        if( amount == -1 ){
            if( zoomLevel < 10 && zoomLevel >= 1 ){
                zoomLevel = zoomLevel-amount;
            }
        }

        this.cam.zoom = 1-(zoomLevel*0.1f);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.SPACE){

        }
        return false;
    }
}
