package com.charlie.swgoh.automation.process.debug;

import com.charlie.swgoh.automation.process.AbstractProcess;
import com.charlie.swgoh.screen.ModScreen;
import com.charlie.swgoh.util.AutomationUtil;
import org.sikuli.script.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Extract extends AbstractProcess {

    private static final Logger LOG = LoggerFactory.getLogger(Extract.class);

    @Override
    protected void doProcess() throws Exception {
        NumberFormat format = new DecimalFormat("000");
        int modNumber = 0;
        for (Integer index : ModScreen.readOtherModLocations()) {
            handleKeys();
            int position = 0;
            for (Region region : ModScreen.RL_OTHER_MOD_SECONDARY_STATS) {
                BufferedImage bufferedImage = AutomationUtil.getBufferedImageFromRegion(region);
                String filename = AutomationUtil.TEMP_DIRECTORY + File.separatorChar
                        + "extract" + File.separatorChar
                        + format.format(modNumber) + "-" + position + ".png";
                saveImageFile(bufferedImage, filename);
                position++;
            }
            modNumber++;
        }
    }

    private void saveImageFile(BufferedImage bufferedImage, String imageFileName) {
        try {
            File imageFile = new File(imageFileName);
            ImageIO.write(bufferedImage, "png", imageFile);
        }
        catch (IOException e) {
            LOG.error("Exception while writing image file", e);
        }
    }

}
