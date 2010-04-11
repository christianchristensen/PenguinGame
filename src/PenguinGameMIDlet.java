import java.io.IOException;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

public class PenguinGameMIDlet extends MIDlet implements CommandListener {
  private Display mDisplay;
  
  private PenguinCanvas mPenguinCanvas;
  private Form mShowForm;
  private Command mExitCommand, mShowCommand, mOkCommand;
  
  public void startApp() {
    if (mPenguinCanvas == null) {
      try {
        mPenguinCanvas = new PenguinCanvas("/penguin.png",
            "/atmosphere.png", "/background_tiles.png",
            "/background_tiles.png", "/win.png");
        mPenguinCanvas.start();
        mExitCommand = new Command("Exit", Command.EXIT, 0);
        mShowCommand = new Command("Show/Hide", Command.SCREEN, 0);
        mOkCommand = new Command("OK", Command.OK, 0);
        mPenguinCanvas.addCommand(mExitCommand);
        mPenguinCanvas.addCommand(mShowCommand);
        mPenguinCanvas.setCommandListener(this);
      }
      catch (IOException ioe) {
        System.out.println(ioe);
      }
    }
    
    mDisplay = Display.getDisplay(this);
    mDisplay.setCurrent(mPenguinCanvas);
  }
  
  public void pauseApp() {}
  
  public void destroyApp(boolean unconditional) {
    mPenguinCanvas.stop();
  }
  
  public void commandAction(Command c, Displayable s) {
    if (c.getCommandType() == Command.EXIT) {
      destroyApp(true);
      notifyDestroyed();
    }
    else if (c == mShowCommand) {
      if (mShowForm == null) {
        mShowForm = new Form("Show/Hide");
        ChoiceGroup cg = new ChoiceGroup("Layers", Choice.MULTIPLE);
        cg.append("Fog", null);
        cg.append("Dr. Quatsch", null);
        cg.append("Background", null);
        mShowForm.append(cg);
        mShowForm.addCommand(mOkCommand);
        mShowForm.setCommandListener(this);
      }
      ChoiceGroup cg = (ChoiceGroup)mShowForm.get(0);
      cg.setSelectedIndex(0, mPenguinCanvas.isVisible(0));
      cg.setSelectedIndex(1, mPenguinCanvas.isVisible(1));
      cg.setSelectedIndex(2, mPenguinCanvas.isVisible(2));
      mDisplay.setCurrent(mShowForm);
    }
    else if (c == mOkCommand) {
      ChoiceGroup cg = (ChoiceGroup)mShowForm.get(0);
      mPenguinCanvas.setVisible(0, cg.isSelected(0));
      mPenguinCanvas.setVisible(1, cg.isSelected(1));
      mPenguinCanvas.setVisible(2, cg.isSelected(2));
      mDisplay.setCurrent(mPenguinCanvas);
    }
  }
}
