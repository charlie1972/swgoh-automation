# swgoh-automation
A tool made for automating tedious tasks in Star Wars: Galaxy of Heroes

## Requirements
* Star Wars: Galaxy of Heroes (obviously)
* Windows 10
* A screen resolution of at least 1920 x 1080
* The latest version of Bluestacks: 4.280.0.1022
* Bluestacks ***must*** run in the English language and with a resolution of 2560 x 1440 at 240 DPI
* For building: Java version 11, Maven version 3.6.3 or later

## What it does
* Collect your daily bronziums automatically
* Spend ally points by repeatedly collecting bronziums until a specified threshold  is reached
* Read your unequipped mods and inject them in the Mod Optimizer
* Retrieve the Mod Optimizer's recommendations and moves the mods accordingly

## How to build it
* Clone the project from: https://github.com/charlie1972/swgoh-automation
* Build the project: `maven clean package`
* Run it from the target directory: `swgoh-automation-<version>.exe` 

## How to run it
* Get the archive at: https://github.com/charlie1972/swgoh-automation/releases
* Extract it and run the executable
  
**IMPORTANT**: the first things the tool does when running are:
* put the Bluestacks window in the foreground
* move it to the top left corner of the primary screen
* resize it so the effective application window has a resolution of 1280 x 720

**The tool takes over the mouse and keyboard. Interfering with either will cause malfunction. See below how to interact with it during its exectution.**

## List of features
Each feature can be accessed from a tab at the top of the GUI.

You can tune the execution's speed. The fastest setting works on my computer. You mileage may vary. 

### Bronzium Daily
This feature automatically collects your daily bronziums. You need to run it while the game is in the bronzium buying screen.

It does not take any argument.

### Bronzium Ally Points
This feature spends your ally points by repeatedly buying bronziums. You need to run it while the game is in the bronzium buying screen.

There is one argument:
* The ally point threshold for which the command stops. Numbers as displayed in the game are accepted (for example: 100K).

### Read Unequipped Mods
This feature enriches the mods retrieved by the Mod Optimizer. You need to run it while being in the mod screen of any of your characters.

Before running this feature, you must create a progress file by using **Save my progress** in the Mod Optimizer.

There are two arguments:
* The name of the aforementioned file containing your progress 
* Your 9-digit ally code

You can browse the file system to get the file. Pressing the **Load** button tries to load the file and retrieve all ally codes in the file. 

This feature reads through all your mods, then enriches the progress file with the unequipped mods. It writes a new file with the prefix "enriched", in the same directory as the original file.

You should then import this new file in the Mod Optimizer by using **Restore my progress** in the Mod Optimizer. You can check the unequipped mods are loaded by exploring them in the Mod Optimizer.

Performance: reading 270 unequipped mods takes approximately 9 minutes. 

### Move Mods
This feature moves the mods according to the Mod Optimizer recommendations. You need to run it while being in the **Character Mods** screen (Home > Characters > Mods).

Before running this feature, you must save the recommendations in a file. This process is a little complicated, I hope to have a better interface with the Mod Optimizer tool:
* In the recommendation screen, switch **Show mods as** > **Individual mods**
* Open the developer tools of your browser
* Select **Inspection** and right-click on the root <html> element
* Select **Copy** > **Outer HTML**
* Paste the clipboard in a new text file. Give it the .html extension.

There is one argument:
* The name of the HTML file that you just extracted

You can browse the file system to get the file. Pressing the **Check** button tries to load the file and check its format.

This feature reads through the recommendations and moves the mods accordingly.

It creates several report files so you can check what happened. These files are created in the same directory as the source HTML file:
* processedCharacters.txt: list of all characters that have been fully and correctly processed
* report-\<timestamp>.txt: detailed report for each character and mod that has been processed
* attentionCharacters.txt: list of the characters for which one or more mods could not be processed correctly

If **attentionCharacters.txt** is not empty, cross-check with the report to see what characters and mods had errors during the process.

The process can be run multiple times with the same recommendations. It relies on **processedCharacters.txt** to know which characters to process. If you want to start a new recommendation set, you need to delete the files first.

Performance-wise, this process can take quite some time. On my system, it takes around 1 1/2 hour to move 480 mods across 100 characters. 

## During the execution
Because this tool takes over the mouse and keyboard, you can't stop it by ordinary means.

There are two global hotkeys that the tool recognizes:
* Stop the process by using CTRL-SHIFT-Q
* Pause and unpause the process by using CTRL-SHIFT-SPACE

## Behind the scenes
* Use of SikuliX for image matching, OCR and mouse/keyboard actuation
* Use of fuzzywuzzy for fuzzy string matching
* Use of JNA for accessing the Windows API

## Future plans
* Widen the window so that more mods can be read at a time
* Better integration with the Mod Optimizer tool
* Other emulators support
* Mac support
