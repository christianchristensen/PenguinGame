import java.io.IOException;
import java.util.Random;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class PenguinCanvas extends GameCanvas implements Runnable {

  private static final int MAXbullet = 5;   // difficulty (decrease=hard)
  private int bulletCount;
  private static final int MAXwindow = 5;   // difficulty (increase=hard)
  private int windowCount;
  private boolean mTrucking;
  
  private LayerManager mLayerManager;
  
  private TiledLayer mAtmosphere;
  private TiledLayer mBackground;
  private int mAnimatedIndex;
  
  private Sprite mPenguin;
  private Sprite[] mBullet;
  private boolean[] mBulletActive;
  private Sprite[] mWindow;
  private boolean[] mWindowActive;
  private int mState, mDirection;
  
  private static final int kStanding = 1;
  private static final int kShooting = 2;
  
  private static final int kLeft = 1;
  private static final int kRight = 2;
  
  private static final int[] kBulletSequence = { 7, 8, 9 };
  private static final int[] kShootingSequence = { 2, 0, 1 };
  private static final int[] kStandingSequence = { 3 };
  
   public PenguinCanvas(String penguinImageName,
      String atmosphereImageName, String backgroundImageName,
      String bulletImageName, String windowImageName ) throws IOException {
    super(true);
    int i = 0;
    // Create a LayerManager.
    mLayerManager = new LayerManager();
    int w = getWidth();
    int h = getHeight();
    mLayerManager.setViewWindow(0, 0, w, h);
    createBackground(backgroundImageName);
    //createAtmosphere(atmosphereImageName);
    createPenguin(penguinImageName);
    mBullet = new Sprite[MAXbullet];
    mBulletActive = new boolean[MAXbullet];
    for( i = 0; i<mBulletActive.length; i++ ){
        mBulletActive[i] = false;
    }
    bulletCount = 0;
    Image bulletImage = Image.createImage(bulletImageName);
    for( i = 0; i<mBullet.length; i++ ){
        mBullet[i] = new Sprite(bulletImage, 48, 48);
    }

    mWindow = new Sprite[MAXwindow];
    mWindowActive = new boolean[MAXwindow];
    for( i = 0; i<mWindowActive.length; i++ ){
        mWindowActive[i] = false;
    }
    windowCount = 0;
    Image windowImage = Image.createImage(windowImageName);
    for( i = 0; i<mWindow.length; i++ ){
        mWindow[i] = new Sprite(windowImage, 33, 33);
    }

   }
  
  private void createBackground(String backgroundImageName)
      throws IOException {
    // Create the tiled layer.
    Image backgroundImage = Image.createImage(backgroundImageName);
    int[] map = {
      1, 1, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 1, 1, 1, 1,
      1, 1, 1, 1, 2, 1, 1, 1,
/*      0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, 0, 0,
*/      6, 6, 6, 6, 6, 6, 6, 6
    };
    mBackground = new TiledLayer(8, 6, backgroundImage, 48, 48);
    mBackground.setPosition(0, 0);
    for (int i = 0; i < map.length; i++) {
      int column = i % 8;
      int row = (i - column) / 8;
      mBackground.setCell(column, row, map[i]);
    }
    
    //mAnimatedIndex = mBackground.createAnimatedTile(8);
    //mBackground.setCell(3, 0, mAnimatedIndex);
    //mBackground.setCell(5, 0, mAnimatedIndex);
    mLayerManager.append(mBackground); 
  }
/*    
  private void createAtmosphere(String atmosphereImageName)
      throws IOException {
    // Create the atmosphere layer
    Image atmosphereImage = Image.createImage(atmosphereImageName);
    mAtmosphere = new TiledLayer(8, 1, atmosphereImage,
        atmosphereImage.getWidth(), atmosphereImage.getHeight());
    mAtmosphere.fillCells(0, 0, 8, 1, 1);
    mAtmosphere.setPosition(0,192 );
    
    mLayerManager.insert(mAtmosphere, 0);
  }
*/
  private void createPenguin(String penguinImageName)
      throws IOException {
    // Create the sprite.
    Image penguinImage = Image.createImage(penguinImageName);
    mPenguin = new Sprite(penguinImage, 48, 48);
    //mPenguin.setPosition(96 + (getWidth() - 48) / 2, 192);
    mPenguin.setPosition((getWidth() - 48) / 2, 192);
    mPenguin.defineReferencePixel(24, 24);
    setDirection(kLeft);
    setState(kStanding);
    mLayerManager.insert(mPenguin, 0);
  }

  public void start() {
    mTrucking = true;
    Thread t = new Thread(this);
    t.start();
  }
  
  public void run() {
    int w = getWidth();
    int h = getHeight();
    Graphics g = getGraphics();
    int frameCount = 0;
    int factor = 2;
    int animatedDelta = 0;
    Random random = new Random();
    
    while (mTrucking) {
      if (isShown()) {
        int keyStates = getKeyStates();
        if ((keyStates & LEFT_PRESSED) != 0) {
          setState(kStanding);
          setDirection(kLeft);
          if( mPenguin.getX() > 0 )
             mPenguin.move(-7, 0);
          //mBackground.move(3, 0);
          //mAtmosphere.move(3, 0);
          mPenguin.nextFrame();
        }
        else if ((keyStates & RIGHT_PRESSED) != 0) {
          setState(kStanding);
          setDirection(kRight);
          if( mPenguin.getX() < getWidth()-mPenguin.getWidth()+5 ) // 96 + (getWidth() - 48) / 2
            mPenguin.move(7, 0);
          //mBackground.move(-3, 0);
          //mAtmosphere.move(-3, 0);
        }
        else if (((keyStates&FIRE_PRESSED)!=0)||((keyStates&UP_PRESSED)!= 0)) {
          setState(kShooting);
          if( mPenguin.getFrame() == 2 )
            incrementBullets( true ); 
            // put bullet sprite here moving in upward direction from getX()
          //Put spawn of bullet here so there can be an independent sprite
          mPenguin.nextFrame();
        }
        else {
          setState(kStanding);
        }
        
/*        frameCount++;
        if (frameCount % factor == 0) {
          int delta = 1;
          if (frameCount / factor < 10){
              delta = -1;
          }
          mAtmosphere.move(delta, 0);
          if (frameCount / factor == 20) frameCount = 0;

          //mBackground.setAnimatedTile(mAnimatedIndex, 8 + animatedDelta++);
          //if (animatedDelta == 3) animatedDelta = 0;
        }*/
        incrementBullets( false );
        // random generation of windows
        if( random.nextInt(100) > 80 )  // how fast do the windows appear?
            generateWindows( true );
        else
            generateWindows( false );

        g.setColor(0xFFFFFF);
        g.fillRect(0, 0, w, h);
        
        mLayerManager.paint(g, 0, 0);
        
        flushGraphics();
      }
      
      try { Thread.sleep(40); }
      catch (InterruptedException ie) {}
    }
  }
  
  public void stop() {
    mTrucking = false;
  }

  private boolean collisionDetection( int index ){
      int i = 0;
      for( i = 0; i < mBullet.length; i++ )
      if( mBulletActive[i]){
          if( mWindow[index].collidesWith(mBullet[i], true ) ){
              mBullet[i].move(-999, -999);
              return true;
          }
      }
      return false;
  }

  private void generateWindows( boolean insertWindow ) {
     int i = 0;
     boolean loop = true;
     if( insertWindow && (windowCount < MAXwindow) ){ // watch the bullets for mutex
        for( i = 0; (i<mWindow.length)&&(loop); i++ ){
        if( !mWindowActive[i] ){
            //mWindow[i].setFrameSequence(kBulletSequence);
            // random window coordinate
            Random random = new Random();
            mWindow[i].setPosition(random.nextInt(getWidth()-mWindow[i].getWidth()), 0);
            //mWindow[i].defineReferencePixel(24, 24);
            mLayerManager.insert(mWindow[i], 1);
            mWindowActive[i] = true;
            mWindow[i].setVisible(true);
            loop = false;
        }
        }
        windowCount++;  // semaphore for ensuring concurrency (mutex)
        //System.out.println(bulletCount);
     }
     else{
         for( i = 0; i<mWindow.length; i++ ){
             if( mWindowActive[i] ){
                 mWindow[i].move(0, +2);    // move the window
                 //if( mWindow[i].getFrame() == 9 ) mWindow[i].setFrame(7); // spin
                 //mWindow[i].nextFrame();    // spin the window
                 if( collisionDetection(i) )
                     mWindow[i].move(999,999);
             }
             if( (mWindow[i].getY() > getHeight()-80)&&mWindowActive[i] ){  // check if the bullet is off the screen
                 // window destruction and scoring will go here
                 mWindowActive[i] = false;
                 mWindow[i].setVisible(false);
                 windowCount--;  // semaphore for ensuring concurrency
                 //System.out.println(bulletCount);
             }
         }
     }
  }

  private void incrementBullets( boolean insertBullet ) {
     int i = 0;
     boolean loop = true;
     if( insertBullet && (bulletCount < MAXbullet) ){ // watch the bullets for mutex
        for( i = 0; (i<mBullet.length)&&(loop); i++ ){
        if( !mBulletActive[i] ){
            mBullet[i].setFrameSequence(kBulletSequence);
            mBullet[i].setPosition(mPenguin.getX(), mPenguin.getY()-48);
            mBullet[i].defineReferencePixel(24, 24);
            //mBullet[bulletCount] = temp;
            mLayerManager.insert(mBullet[i], 1);
            mBulletActive[i] = true;
            loop = false;
        }
        }
        bulletCount++;  // semaphore for ensuring concurrency (mutex)
        //System.out.println(bulletCount);
     }
     else{
         for( i = 0; i<mBullet.length; i++ ){
             if( mBulletActive[i] ){
                 mBullet[i].move(0, -2);    // move the bullet
                 if( mBullet[i].getFrame() == 9 ) mBullet[i].setFrame(7); // spin
                 mBullet[i].nextFrame();    // spin the bullet
             }
             if( (mBullet[i].getY() < -40)&&mBulletActive[i] ){  // check if the bullet is off the screen
                 // window destruction and scoring will go here
                 mBulletActive[i] = false;
                 bulletCount--;  // semaphore for ensuring concurrency
                 //System.out.println(bulletCount);
             }
         }
     }
  }
  
  public void setVisible(int layerIndex, boolean show) {
    Layer layer = mLayerManager.getLayerAt(layerIndex);
    layer.setVisible(show);
  }
  
  public boolean isVisible(int layerIndex) {
    Layer layer = mLayerManager.getLayerAt(layerIndex);
    return layer.isVisible();
  }
  
  private void setDirection(int newDirection) {
    if (newDirection == mDirection) return;
    if (mDirection == kLeft)
      mPenguin.setTransform(Sprite.TRANS_MIRROR);
    else if (mDirection == kRight)
      mPenguin.setTransform(Sprite.TRANS_NONE);
    mDirection = newDirection;
  }
  
  private void setState(int newState) {
    if (newState == mState) return;
    switch (newState) {
      case kStanding:
        mPenguin.setFrameSequence(kStandingSequence);
        mPenguin.setFrame(0);
        break;
      case kShooting:
        mPenguin.setFrameSequence(kShootingSequence);
        break;
      default:
        break;
    }
    mState = newState;
  }
}

