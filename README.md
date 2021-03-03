# swgoh-automation
A tool made for automating tedious tasks in Star Wars: Galaxy of Heroes

## Requirements
* Star Wars: Galaxy of Heroes (obviously)
* Windows 10
* A screen resolution of at least 1920 x 1080
* The latest version of Bluestacks: 4.260.0.1032
* A working Java Development Kit (JDK), version 8 or later
* Maven version 3.6.3 or later
* Bluestacks ***must*** run in the English language, with a resolution of 2560 x 1440 at 240 DPI

## What it does
* Collect your daily bronziums automatically
* Spend ally points by collecting bronziums repeatedly until a specified threshold  is reached
* Read your unequipped mods and inject them in the Mod Optimizer
* Retrieve the Mod Optimizer's recommendations and moves the mods accordingly

## How to run it
* Build the project: `maven clean install`
* Run it: `java -jar target/swgoh-automation-<version>-SNAPSHOT.jar <command> <arguments...>` 

**IMPORTANT**: the first things the tool do are:
* put the Bluestacks window in the foreground
* move it to the top left corner of the primary screen
* resize it so the effective application window has a resolution of 1280 x 720 

## List of commands
### BronziumDaily
This command automatically collects your daily bronziums. You need to run this command while the game is in the bronzium buying screen.

It does not take any argument.

### BronziumAllyPoints
This command spends your ally points by buying bronziums. You need to run this command while the game is in the bronzium buying screen.

There is one argument:
* The ally point threshold for which the command stops. Numbers as displayed in the game are accepted (for example: 100K).

### ReadUnequippedMods
This command enriches the mods retrieved by the Mod Optimizer. You need to run this command while being in the mod screen of any of your characters.

Before running this command, you must create a progress file by using "Save my progress" in the Mod Optimizer.

There are two arguments:
* Your 9-digit ally code
* The name of the aforementioned file containing your progress 

This command reads through all your mods, then enriches the progress file you supplied with the unequipped mods. It writes a new file with the prefix "enriched", in the same directory as the original file.

Due to limitations of the mod screen, only the first 16 mods for each slot/set combination are read.

You should then import this new file by using "Restore my progress" in the Mod Optimizer. You can check the unequipped mods are loaded by exploring them in the Mod Optimizer.

### MoveMods
This command moves the mods according to the Mod Optimizer recommendations. You need to run this command while being in the "Character Mods" screen (Home > Characters > Mods).

Before running this command, you must save the recommendations in a file. This process is a little complicated, I hope to have a better interface with the Mod Optimizer tool:
* In the recommendation screen, switch "Show mods as" > "Individual mods"
* Open the developer tools of your browser
* Select "Inspection", and right-click on the root <html> element
* Select "Copy" > "Outer HTML"
* Paste the clipboard in a new text file. Give it the .html extension.

There is one argument:
* The name of the HTML file that you just extracted

This command reads through the recommendations and moves the mods accordingly.

It creates several report files so you can check what happened. These files are created in the same directory as the source HTML file:
* processedCharacters.txt: list of all characters that have been fully and correctly processed
* report-\<timestamp>.txt: detailed report for each character and mod that has been processed
* attentionCharacters.txt: list of the characters for which one or more mods could not be processed correctly

If attentionCharacters.txt is not empty, cross-check with the report to see what characters and mods had errors during the process.

The process can be run multiple times with the same recommendations. It relies on processedCharacters.txt to know which characters to process. If you want to start a new recommendation set, you need to delete the files first.

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
* A GUI
* Widen the window so that more mods can be read
* Mac support
* Various parameters to fine-tune the process; delays are critical for example
* Better integration with the Mod Optimizer tool
