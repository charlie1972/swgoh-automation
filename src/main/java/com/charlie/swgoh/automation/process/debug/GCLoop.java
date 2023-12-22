package com.charlie.swgoh.automation.process.debug;

import com.charlie.swgoh.automation.process.AbstractProcess;
import com.charlie.swgoh.screen.ModScreen;
import com.charlie.swgoh.util.AutomationUtil;
import org.sikuli.script.Location;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class GCLoop extends AbstractProcess {

    private static final Logger LOG = LoggerFactory.getLogger(GCLoop.class);

    @Override
    protected void doProcess() throws Exception {
        Location location = new Location(1240, 650);
        while (true) {
            handleKeys();
            AutomationUtil.click(location, "All-purpose location");
            AutomationUtil.waitFor(1000L);
        }
    }

}
