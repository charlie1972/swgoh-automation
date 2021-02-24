package com.charlie.swgoh.automation.process;

import com.charlie.swgoh.automation.BlueStacksApp;
import com.charlie.swgoh.automation.PauseAppKeyHandler;
import com.charlie.swgoh.automation.StopAppKeyHandler;
import org.sikuli.basics.Debug;
import org.sikuli.basics.Settings;
import org.sikuli.script.ImagePath;
import org.sikuli.script.Key;
import org.sikuli.script.KeyModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainProcess implements IProcess {

  private static final Logger LOG = LoggerFactory.getLogger(MainProcess.class);

  private String[] args;

  @Override
  public void setParameters(String[] parameters) {
    this.args = parameters;
  }

  @Override
  public void process() throws Exception {
    Debug.off();
    Settings.ActionLogs = false;
    Settings.InfoLogs = false;
    Settings.DebugLogs = false;
    Settings.MoveMouseDelay = 0.1F;

    Key.addHotkey(Key.ESC, KeyModifier.CTRL + KeyModifier.SHIFT, new StopAppKeyHandler());
    Key.addHotkey(' ', KeyModifier.CTRL + KeyModifier.SHIFT, new PauseAppKeyHandler());

    LOG.info("Started");
    LOG.info("Press CTRL-SHIFT-SPACE to pause");
    LOG.info("Press CTRL-SHIFT-ESC to abort");

    ImagePath.add("com.charlie.swgoh.main.Main/images");

    IProcess adjustWindow = new AdjustWindow();
    adjustWindow.process();

    BlueStacksApp.lockWindow();

    if (args.length == 0) {
      LOG.error("No process specified. Exiting.");
      return;
    }

    @SuppressWarnings("unchecked")
    Class<IProcess> processClass = (Class<IProcess>) Class.forName("com.charlie.swgoh.automation.process." + args[0]);
    IProcess process = processClass.newInstance();

    String[] parameters = new String[args.length - 1];
    System.arraycopy(args, 1, parameters, 0, args.length - 1);
    process.setParameters(parameters);

    process.process();

    LOG.info("Finished");
  }

}
