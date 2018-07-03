package citymanager.area;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.ResolutionFileResolver;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.*;

public class Init extends ApplicationAdapter {

	//emulated screen size
	public ScreenEmulator screenEmulator;
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 480;
//	public static final int SCREEN_WIDTH = 1920;
//	public static final int SCREEN_HEIGHT = 1080;
//	public static final int SCREEN_WIDTH = 320;
//	public static final int SCREEN_HEIGHT = 240;
// 	public static final int SCREEN_WIDTH = 2650;
//	public static final int SCREEN_HEIGHT = 1440;


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
	float TILE_WIDTH_DIAGONALE;

	String projectionType;

	float devicePixelAspectRatio;
	float deviceScreenAspectRatio;
	Viewport viewport;

	OrthographicCamera cam;
	CamController camCtrl;

	ResolutionFileResolver resolutionFileResolver;
	Sprite terrain;
	Building building;
	SpriteBatch batch;
	SpriteBatch batchRotated;
	BitmapFont font;
	ShapeRenderer sr;

	//matrix to rotate the grid from topdown to iso/dimetric
	Matrix4 areaRotateXMatrix = new Matrix4();

	@Override
	public void create(){
		screenEmulator = new ScreenEmulator();

		projectionType = "oblique";

		//resolutionFileResolver = new ResolutionFileResolver();
		terrain = new Sprite(new Texture(Gdx.files.internal("tile_grass.png")));
		if( projectionType == "oblique" ) {
			building = new Building(new Texture(Gdx.files.internal("box_oblique.png")), 3, 1, 1);
		}else {
			building = new Building(new Texture(Gdx.files.internal("box_isometric.png")), 3, 1, 1);
		}

		batch = new SpriteBatch();
		batchRotated = new SpriteBatch();
		font = new BitmapFont();
		sr = new ShapeRenderer();

		//TILE setup
		TILES_X = 20;
		TILES_Y = 20;
		TILE_WIDTH  = 10;
		TILE_HEIGHT = 10;
		TILE_WIDTH_DIAGONALE = (float)Math.sqrt( (AREA_WIDTH*AREA_WIDTH)+(AREA_HEIGHT*AREA_HEIGHT) );

		//AREA setup
		//units: 1u = 1meter
		AREA_WIDTH = TILES_X*TILE_WIDTH;
		AREA_HEIGHT = TILES_Y*TILE_HEIGHT;
		AREA_WIDTH_DIAGONALE = (float)Math.sqrt( (AREA_WIDTH*AREA_WIDTH)+(AREA_HEIGHT*AREA_HEIGHT) );
		if( projectionType == "oblique" ) {
			//AREA_HEIGHT_DIAGONALE = AREA_WIDTH_DIAGONALE/1.4f;
			AREA_HEIGHT_DIAGONALE = AREA_WIDTH_DIAGONALE/1.33f;
			//calculate angle for oblique
			//tan(phi) = (AREA_HEIGHT_DIAGONALE/2)/(AREA_WIDTH_DIAGONALE/2)
			float angle = (float)Math.atan( (AREA_HEIGHT_DIAGONALE/2)/(AREA_WIDTH_DIAGONALE/2) );
			angle = (float)Math.toDegrees(angle)*-1;
			System.out.println(angle);
			areaRotateXMatrix.setToRotation(new Vector3(1,0,0), angle);
		}else{
			AREA_HEIGHT_DIAGONALE = AREA_WIDTH_DIAGONALE/2;
			float angle = (float)Math.atan( (AREA_HEIGHT_DIAGONALE)/(AREA_WIDTH_DIAGONALE/2) );
			angle = (float)Math.toDegrees(angle)*-1;
			areaRotateXMatrix.setToRotation(new Vector3(1,0,0), -60);
			//areaRotateXMatrix.setToRotation(new Vector3(1,0,0), angle);
		}

		//WORLD setup
		WORLD_WIDTH = AREA_WIDTH_DIAGONALE;
		WORLD_HEIGHT = AREA_HEIGHT_DIAGONALE;

		terrain.setOrigin(0,0);
		terrain.setPosition(0, AREA_WIDTH_DIAGONALE/2f);
		terrain.setSize(AREA_WIDTH, AREA_HEIGHT);
		terrain.rotate(-45);

		building.setPosition(13.15f*TILE_WIDTH, 0.2f );
		building.setSize( 2*TILE_WIDTH,2*TILE_HEIGHT);
		building.setOrigin(0, 0);

		deviceScreenAspectRatio = (float)Gdx.graphics.getWidth()/(float)Gdx.graphics.getHeight();
		cam = new OrthographicCamera();
		//viewport = new StretchViewport(WORLD_WIDTH*deviceScreenAspectRatio, WORLD_HEIGHT, cam);
		//viewport = new FitViewport(WORLD_WIDTH*deviceScreenAspectRatio, WORLD_HEIGHT*deviceScreenAspectRatio, cam);
		viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, cam);
		//viewport = new FitViewport((float)pixelPerMeter*AREA_WIDTH_DIAGONALE, (float)pixelPerMeter*AREA_HEIGHT_DIAGONALE, cam);
		viewport.apply();
		cam.position.set(cam.viewportWidth/2,cam.viewportHeight/2,0);
		//needed to show the terrain north/area north
		cam.far = 3000;

		camCtrl = new CamController(cam);
		Gdx.input.setInputProcessor(camCtrl);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor( 0.154f, 0.200f, 0.184f, 1f );
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		batchRotated.setProjectionMatrix(cam.combined);
		batchRotated.setTransformMatrix(areaRotateXMatrix);
		batchRotated.begin();
		terrain.draw(batchRotated);
		batchRotated.end();

		sr.setProjectionMatrix(cam.combined);
		sr.begin(ShapeRenderer.ShapeType.Line);
		//WORLD bounds
		sr.setColor(Color.RED);
		sr.line(0, 0, WORLD_WIDTH, 0);
		sr.line(0, 0, 0, WORLD_HEIGHT);
		sr.line(0, WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);
		sr.line(WORLD_WIDTH, 0, WORLD_WIDTH, WORLD_HEIGHT);
		//AREA bounds
		sr.setColor(Color.GREEN);
		sr.line(0, AREA_HEIGHT_DIAGONALE /2, AREA_WIDTH_DIAGONALE/2, AREA_HEIGHT_DIAGONALE);
		sr.line(0, AREA_HEIGHT_DIAGONALE /2, AREA_WIDTH_DIAGONALE /2, 0);
		sr.line(AREA_WIDTH_DIAGONALE /2, AREA_HEIGHT_DIAGONALE, AREA_WIDTH_DIAGONALE, AREA_HEIGHT_DIAGONALE /2);
		sr.line(AREA_WIDTH_DIAGONALE /2, 0, AREA_WIDTH_DIAGONALE, AREA_HEIGHT_DIAGONALE /2);
		//logic GRID visual
		for(int x=0; x < TILES_X; x++){
			for(int y=0; y < TILES_Y; y++) {
				float startX = ((AREA_WIDTH_DIAGONALE / 2) / TILES_X) * x;
				float startY = (AREA_HEIGHT_DIAGONALE/2)-y*((AREA_HEIGHT_DIAGONALE/TILES_Y)/2);
				float endX = startX+(AREA_WIDTH_DIAGONALE/2);
				float endY = startY+(AREA_HEIGHT_DIAGONALE/2);
				sr.setColor(Color.PINK);
				sr.line(startX, startY, endX, endY);
				sr.setColor(Color.PURPLE);
				sr.line(startX, startY+y*(AREA_HEIGHT_DIAGONALE/TILES_Y), endX, y*((AREA_HEIGHT_DIAGONALE/2)/TILES_Y) );
				x = x+1;
			}
		}
		sr.end();

		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		building.draw(batch);
		batch.end();

//		batch.begin();
//		font.draw(batch,
//				"Density: " + Gdx.graphics.getDensity() + "\n" +
//						"Screen width: " + Gdx.graphics.getPpcX() + "\n" +
//						"Screen height: " + Gdx.graphics.getPpcX() + "\n" +
//						"AspectRatio: " + Gdx.graphics.getPpcX() + "\n" +
//						"Viewport width: " + Gdx.graphics.getPpcX() + "\n" +
//						"Viewport height: " + Gdx.graphics.getPpcX() + "\n" +
//						"PPC-x: " + Gdx.graphics.getPpcX() + "\n" +
//						"PPC-y: " + Gdx.graphics.getPpcY() + "\n" +
//						"PPI-x: " + Gdx.graphics.getPpiX() + "\n" +
//						"PPI-y: " + Gdx.graphics.getPpiY()
//				, 0, viewport.getWorldHeight());
//		batch.end();

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
		terrain.getTexture().dispose();
		building.getTexture().dispose();
		sr.dispose();
		batch.dispose();
	}
}
