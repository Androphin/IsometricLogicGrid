package citymanager.area;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class old_logicgrid {

    public class Init extends ApplicationAdapter {

        //emulated screen size
        public ScreenEmulator screenEmulator;
        public static final int SCREEN_WIDTH = 800;
        public static final int SCREEN_HEIGHT = 480;

        int AREA_WIDTH;
        int AREA_HEIGHT;
        float AREA_WIDTH_DIAGONALE;
        float AREA_HEIGHT_DIAGONALE;

        float WORLD_WIDTH;
        float WORLD_HEIGHT;

        int TILES_X;
        int TILES_Y;
        int TILE_WIDTH;
        int TILE_HEIGHT;

        double pixelPerMeter;
        double pixelPerInch;
        float devicePixelAspectRatio;
        float deviceScreenAspectRatio;
        Viewport viewport;

        OrthographicCamera cam;
        CamController camCtrl;

        Texture terrain;
        Building building;
        Texture tex;
        SpriteBatch batch;
        BitmapFont font;

        int LAYERS = 1;
        SpriteCache[] caches = new SpriteCache[LAYERS];
        int layers[] = new int[LAYERS];
        Matrix4 gridRotateXMatrix = new Matrix4();

        Sprite[][] tiles;
        int colY = 1;
        boolean maxNotReached = true;

        @Override
        public void create(){
            screenEmulator = new ScreenEmulator();

            terrain = new Texture(Gdx.files.internal("tile_grass.png"));
            //building = );
            building = new Building(new Texture(Gdx.files.internal("box_bounds.png")), 3, 1, 1);

            Pixmap pxm = new Pixmap(2,2, Pixmap.Format.RGBA8888);
            pxm.setColor(0f, 1f, 0.5f, 0.5f);
            pxm.fill();
            tex = new Texture(pxm);
            batch = new SpriteBatch();
            font = new BitmapFont();

            //AREA setup
            //units: 1u = 1meter
            AREA_WIDTH = 2000;
            AREA_HEIGHT = 2000;
            AREA_WIDTH_DIAGONALE = (float)Math.sqrt( (AREA_WIDTH*AREA_WIDTH)+(AREA_HEIGHT*AREA_HEIGHT) );
            AREA_HEIGHT_DIAGONALE = AREA_WIDTH_DIAGONALE/1.4f; //Dimetric
            AREA_HEIGHT_DIAGONALE = AREA_WIDTH_DIAGONALE/2f; //Isometric
            System.out.println(AREA_WIDTH_DIAGONALE+" "+AREA_HEIGHT_DIAGONALE);
            //WORLD setup
            WORLD_WIDTH = AREA_WIDTH_DIAGONALE;
            WORLD_HEIGHT = AREA_HEIGHT_DIAGONALE;
            pixelPerMeter = Gdx.graphics.getWidth()/WORLD_WIDTH;
            //TILE setup
            TILES_X = 20;
            TILES_Y = 20;
            TILE_WIDTH  = 20;
            TILE_HEIGHT = 20;

            //building.setPosition((float)1.5*(AREA_WIDTH_DIAGONALE/TILES_X),1*(AREA_HEIGHT_DIAGONALE/TILES_Y)-((AREA_HEIGHT_DIAGONALE/TILES_Y)/2)+(AREA_HEIGHT_DIAGONALE/2) );
            //building.setPosition( (1*(AREA_WIDTH_DIAGONALE/TILES_X)),1*(AREA_HEIGHT_DIAGONALE/TILES_Y)-((AREA_HEIGHT_DIAGONALE/TILES_Y)/2)+(AREA_HEIGHT_DIAGONALE/2) );
            //building.setSize( 2*(AREA_WIDTH_DIAGONALE/TILES_X),2*(AREA_WIDTH_DIAGONALE/TILES_Y) );

            deviceScreenAspectRatio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
            cam = new OrthographicCamera();
            //viewport = new StretchViewport(WORLD_WIDTH*deviceScreenAspectRatio, WORLD_HEIGHT, cam);
            //viewport = new FitViewport(WORLD_WIDTH*deviceScreenAspectRatio, WORLD_HEIGHT*deviceScreenAspectRatio, cam);
            viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, cam);
            //viewport = new FitViewport((float)pixelPerMeter*AREA_WIDTH_DIAGONALE, (float)pixelPerMeter*AREA_HEIGHT_DIAGONALE, cam);
            viewport.apply();
            cam.position.set(cam.viewportWidth/2,cam.viewportHeight/2,0);

            camCtrl = new CamController(cam);
            Gdx.input.setInputProcessor(camCtrl);


            int tileDiagonale = (int)Math.sqrt(TILE_WIDTH*TILE_WIDTH + TILE_HEIGHT*TILE_HEIGHT);
            int gridWidth  = TILES_X * tileDiagonale;
            int gridHeight = TILES_Y * tileDiagonale;

            //logic grid
            tiles = new Sprite[TILES_X][TILES_Y];
            for(int x = 0; x < TILES_X; x++){
                for(int y = 0; y < colY; y++) {
                    int posX = (tileDiagonale/2*x)+tileDiagonale/2;
                    int posY = (tileDiagonale/2)* colY -(tileDiagonale*y)+(gridHeight/2);
                    tiles[x][y] = new Sprite(tex);
                    tiles[x][y].setSize(TILE_WIDTH, TILE_HEIGHT);
                    tiles[x][y].setPosition(posX, posY);
                    tiles[x][y].rotate(45);

                }
                if( colY <= 5 && maxNotReached) {
                    colY = colY + 1;
                }else{
                    maxNotReached = false;
                    colY = colY - 1;
                }
            }
            //set to true, because needed in render loop again
            maxNotReached = true;
            colY = 1;

            //matrix to rotate the grid from topdown to iso
            gridRotateXMatrix.setToRotation(new Vector3(1,0,0), -45);
        }

        @Override
        public void render() {
            Gdx.gl.glClearColor( 0.154f, 0.200f, 0.184f, 1f );
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

            batch.setProjectionMatrix(cam.combined);
            //batch.setTransformMatrix(gridRotateXMatrix);
            batch.begin();
            for (int tx = 0; tx < TILES_X; tx++) {
                for(int ty = 0; ty < colY; ty++) {
                    tiles[tx][ty].draw(batch);
                }
                if( colY <= 5  && maxNotReached) {
                    colY = colY + 1;
                }else{
                    maxNotReached = false;
                    colY = colY - 1;
                }
            }
            maxNotReached = true;
            colY = 1;
            batch.end();

            batch.setProjectionMatrix(cam.combined);
            batch.begin();
            building.draw(batch);
            batch.end();

            cam.update();
        }

        @Override
        public void resize(int width, int height) {
            viewport.update(width, height);
            cam.position.set(cam.viewportWidth/2, cam.viewportHeight/2, 0);
        }

        @Override
        public void pause() {

        }

        @Override
        public void resume() {

        }

        @Override
        public void dispose() {
            terrain.dispose();
            building.getTexture().dispose();
            tex.dispose();
            batch.dispose();
        }
    }

}
