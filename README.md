# swgoh-automation
A tool made for automating tedious tasks in Star Wars: Galaxy of Heroes

## Requirements
* Star Wars: Galaxy of Heroes (obviously)
* Windows 10
* A screen resolution of at least 1920 x 1080
* The latest version of Bluestacks: 4.280.0.1022
* Bluestacks ***must*** run in the English language and with a resolution of 1280 x 720 at 240 DPI
* If you wish to build: Java version 17, Maven version 3.6.3 or later

## What it does
* Collect your daily bronziums automatically
* Spend ally points by repeatedly collecting bronziums until a specified threshold  is reached
* Read your unequipped mods and inject them in the Mod Optimizer
* Retrieve the Mod Optimizer's recommendations and move the mods accordingly
* Revert the previous mod move
* Revert *all* mod moves

## How to run it from the release
* Get the archive at: https://github.com/charlie1972/swgoh-automation/releases
* Extract it and run `swgoh-automation.cmd`
  
## How to build it from scratch
* Clone the project from: https://github.com/charlie1972/swgoh-automation
* Make sure the JAVA_HOME environment variable is set
* Build the project: `mvn clean package`
* Unzip the archive located in the target directory: swgoh-automation-<version>.zip
* Run it: `swgoh-automation.cmd`

## IMPORTANT
The first things the tool does when running are:
* put the Bluestacks window in the foreground
* move it to the top left corner of the primary screen
* resize it so the effective application window has a resolution of 1280 x 720

**During execution, the tool takes over the mouse and keyboard. Interfering with either will cause malfunction. See below how to interact with it during its exectution.**

## List of features
Each feature can be accessed from a tab at the top of the GUI.

Obviously, during a run the tool must not be interfered with. In particular, the Bronzium Daily feature keeps the Bronzium screen open, and this can take up to 1 1/2 hour (9 x 10 minutes). Use that feature when not working on the PC.

The tool works by "reading" the screen. There are lots of checks to minimize the risk of desynchronizations and clicking the wrong buttons.

You can tune the execution's speed. If the fastest setting creates desynchronizations, try slowing it down a little. Your mileage may vary. 

### Bronzium Tab
#### Bronzium Daily
This feature automatically collects your daily bronziums. You need to run it while the game is in the bronzium buying screen. It clicks on the "Free" button whenever it appears until there are no more free Bronziums to collect.

#### Bronzium Ally Points
This feature spends your ally points by repeatedly buying bronziums. You need to run it while the game is in the bronzium buying screen.
Its argument is the ally point threshold at which the execution stops. Numbers as displayed in the game are accepted (for example: 100K).

### Mods Tab
#### Working directory
This directory contains all files that are used for mods moving:
* JSON progress files
* HTML files that contain mods moves
* Move mods tracking
* Move mods reports

#### Progress file
This file is obtained from the Mod Optimizer. After fetching your data, click on **Save my progress**. Your browser will save a file named **modsOptimizer-\<date\>.json**. Move it to the working directory and press **Refresh** to see it appear.

#### Read Unequipped Mods
This feature enriches the mods retrieved by the Mod Optimizer, that contain only equipped mods. You must have a valid progress file in the working directory. You also need to run it while being in the mod screen of any of your characters.

This feature reads through all your mods, then enriches the progress file with the unequipped mods. It writes a new file with the prefix "enriched", in the working directory. It only reads mods that are level 15 and have 5 or 6 dots.

You should then import this new file in the Mod Optimizer by using **Restore my progress** in the Mod Optimizer. You can check the unequipped mods are loaded by exploring them in the Mod Optimizer.

Performance: reading 300 unequipped mods takes approximately 10 minutes. 

#### Move Mods
This feature moves the mods according to the Mod Optimizer recommendations. You need to run it while being in the **Character Mods** screen (Home > Characters > Mods).

Before running this feature, you must make and save recommendations. For each recommendation:
* Setup your mod configuration
* Run **Optimize my mods!**
* In the recommendation screen, make sure you have the default options:
  * Organize mods by: Assigned Character
  * Show mods as: Sets
  * Show me: All assignments
  * Show characters by tag: All
* In your browser, use **Save As...**
* Choose **Complete web page**
* Save
* Move the resulting HTML file to the working directory. An accompanying directory will follow the file, it is OK, it will be ignored.

You may do as many recommendations as you wish. For example if you need to do 5 battles for TB, you can make 5 different recommendations.

After refreshing, you will see the recommendations in the list.
To run the mods move:
* Select the recommendation you want to apply
* In the game, make sure you are in the **Character Mods** screen 
* If you're unsure if this tool will work correctly, check **Dry Run**. Everything will be done as planned but the mods moves will be reverted instead of confirmed.
* Click on **Move selected**

***

***TO BE COMPLETED***

***

Once run, this feature creates several report files so you can check what happened. These files are created in the same directory as the source HTML file:
* **move-mods-processedCharacters.txt**: list of all characters that have been fully and correctly processed
* **move-mods-report-\<timestamp>.csv**: detailed report for each character and mod that has been processed; can be imported with your favourite spreadsheet application
* **move-mods-attentionCharacters.txt**: list of the characters for which one or more mods could not be processed correctly

If **move-mods-attentionCharacters.txt** exists and is not empty, cross-check with the report to see what characters and mods had errors during the process.

The process can be run multiple times with the same recommendations. It relies on **move-mods-processedCharacters.txt** to know which characters to process. If you want to start a new recommendation set, you need to delete the files first.

Performance-wise, this process can take quite some time. On my system, it takes around 1 hour to move 400 mods across 100 characters. 

### Revert Move Mods
This feature reverts the actions done by the Move Mods feature.

It is useful if you performed a remod for a specific encounter (TB or legendary event for example). After the encounter you want to get back to the previous mod set. This feature allows to do that.

There are four arguments:
* The name of the HTML file that was used to perform the mod move
* The name of the JSON progress file that is given by the Mod Optimizer
* Your 9-digit ally code
* A switch to do a dry run

The feature will find the characters whose mods have changed during the mod move, and restore their mods from the snapshot done by the Mod Optimizer.

Aside from that, it performs the same way as the Move Mods feature: it creates the same report files (they have a different prefix: revert-move-mods), and has the same performance.  

## During the execution
Because this tool takes over the mouse and keyboard, you can't stop it by ordinary means.

There are two global hotkeys that the tool recognizes:
* Stop the process by using CTRL-SHIFT-Q
* Pause and unpause the process by using CTRL-SHIFT-SPACE

If they don't work, there probably is another application that uses the same global hotkeys. Close them before using this tool.

There might be some delay between the hotkey being pressed and the actual pause/stop. The tool does so only when at a stable state.

## Behind the scenes
* JavaFX for the GUI
* SikuliX for image matching, OCR and mouse/keyboard actuation
* fuzzywuzzy for fuzzy string matching
* JNA for accessing the Windows API

## Future plans
* Widen the window so that more mods can be read at a time
* Better integration with the Mod Optimizer tool
* Other emulators support
* Mac support
