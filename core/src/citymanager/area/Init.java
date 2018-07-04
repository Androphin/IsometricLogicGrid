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
	boolean trueIsometric;

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
	Matrix4 areaRotation = new Matrix4();

	@Override
	public void create(){
		screenEmulator = new ScreenEmulator();

		trueIsometric = false;
		projectionType = "oblique";

		//resolutionFileResolver = new ResolutionFileResolver();
		terrain = new Sprite(new Texture(Gdx.files.internal("supertile_grass.png")));
		if( projectionType == "oblique" ) {
			building = new Building(new Texture(Gdx.files.internal("box_oblique.png")), 3, 1, 1);
		}else {
			if( trueIsometric ){
				building = new Building(new Texture(Gdx.files.internal("box_trueIsometric.png")), 3, 1, 1);
			}else{
				building = new Building(new Texture(Gdx.files.internal("box_isometric.png")), 3, 1, 1);
			}
		}

		batch = new SpriteBatch();
		batchRotated = new SpriteBatch();
		font = new BitmapFont();
		sr = new ShapeRenderer();

		//TILE setup
		TILES_X = 20;
		TILES_Y = 20;
		TILE_WIDTH  = 12;
		TILE_HEIGHT = 12;
		TILE_WIDTH_DIAGONALE = (float)Math.sqrt( (AREA_WIDTH*AREA_WIDTH)+(AREA_HEIGHT*AREA_HEIGHT) );

		//AREA setup
		//units: 1u = 1meter
		AREA_WIDTH = TILES_X*TILE_WIDTH;
		AREA_HEIGHT = TILES_Y*TILE_HEIGHT;
		AREA_WIDTH_DIAGONALE = (float)Math.sqrt( (AREA_WIDTH*AREA_WIDTH)+(AREA_HEIGHT*AREA_HEIGHT) );
		float angle = 0;
		float offsetY = 0;
		//OBLIQUE
		if( projectionType == "oblique" ) {
			//AREA_HEIGHT_DIAGONALE = AREA_WIDTH_DIAGONALE/1.4f;
			AREA_HEIGHT_DIAGONALE = AREA_WIDTH_DIAGONALE/1.33f;
			//calculate angle for oblique
			angle = (float)Math.atan( (AREA_HEIGHT_DIAGONALE/2)/(AREA_WIDTH_DIAGONALE/2) );
			angle = (float)Math.toDegrees(angle);
			angle = (90-angle)*-1;
			angle = 41.26f*-1;
			areaRotation.setToRotation(new Vector3(1,0,0), angle);
			//calculate position Y offset, that happens through rotation
			//HEIGHT_DIAGONALE minus distance from rotation
			offsetY = (AREA_HEIGHT_DIAGONALE*(1.33f/2))-AREA_HEIGHT_DIAGONALE;
		}else{
		//ISOMETRIC
			AREA_HEIGHT_DIAGONALE = AREA_WIDTH_DIAGONALE/2;
			if( trueIsometric ){
				areaRotation.setToRotation(new Vector3(1,0,0), -60);
			}else{
				angle = (float)Math.atan( (AREA_HEIGHT_DIAGONALE/2)/(AREA_WIDTH_DIAGONALE/2) );
				angle = (float)Math.floor( Math.toDegrees(angle) );
				angle = (90-angle)*-1;
				angle = 60*-1;
				areaRotation.setToRotation(new Vector3(1,0,0), angle);
			}
		}
		System.out.println("Area WidthDiag: "+AREA_WIDTH_DIAGONALE+"\n"
                +"Area HeightDiag: "+AREA_HEIGHT_DIAGONALE+"\n"
                +"Area WDiag/2: "+AREA_WIDTH_DIAGONALE/2+"\n"
                +"Area HDiag/2: "+AREA_HEIGHT_DIAGONALE/2+"\n"
				+"Angle: "+angle+"\n"
                +"offsetY: "+offsetY+"\n"
        );

		//WORLD setup
		WORLD_WIDTH = AREA_WIDTH_DIAGONALE;
		WORLD_HEIGHT = AREA_HEIGHT_DIAGONALE;

		terrain.setOrigin(0,0);
		terrain.setPosition(0, AREA_HEIGHT_DIAGONALE+offsetY);
		terrain.setSize(AREA_WIDTH, AREA_HEIGHT);
		terrain.rotate(-45);

		if( projectionType == "oblique") {
			building.setPosition(13.21f * TILE_WIDTH, 0f);
		}else{
			building.setPosition(13.14f*TILE_WIDTH, 0f );
		}
		building.setSize( 2*TILE_WIDTH,2*TILE_HEIGHT);
        //building.setPosition(0,0);
        //building.setSize(AREA_WIDTH_DIAGONALE, AREA_WIDTH_DIAGONALE);
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
		batchRotated.setTransformMatrix(areaRotation);
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
//						"PPI-x: " + Gdx.graphics.getPpiY() + "\n" +
//						"FPS: " + Gdx.graphics.getFPS() + "\n" +
//						"Projection: " + projectionType + "\n" +
//						""
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
