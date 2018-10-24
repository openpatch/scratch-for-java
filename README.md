# Processing Library Scratch

To ease the transition from the block-based programming environment
[Scratch](scratch.mit.edu) to [Processing](https://processing.org/) this
library was created. Therefore the core elements of Scratch are remodelled. 

## Elements

### ScratchStage

Usage: `import eu.barkmin.processing.scratch.ScratchStage`

API: https://www.barkmin.eu/processing-library-scratch/reference/eu/barkmin/processing/scratch/ScratchStage.html

Source Code: https://github.com/mikebarkmin/processing-library-scratch/blob/master/src/eu/barkmin/processing/scratch/ScratchStage.java

#### Initialization

Before you can use any of the class provided by this library be sure to
initialize the ScratchStage. You only need to place this statement
`ScratchStage.init(this)` in your setup function. A minimal example could look like this:

```java
import eu.barkmin.processing.scratch.*;

void setup() {
  ScratchStage.init(this);
}

void draw() {
}
```

#### Looks

| Scratch | Processing |
| :-: | :-: |
| ![stage switch backdrop to](web/assets/stage_switch_backdrop_to.png) | `stage.switchBackdrop("backdrop1")` |
| ![stage next backdrop](web/assets/stage_next_backdrop.png) | `stage.nextBackdrop()` |
| ![stage set color](web/assets/stage_set_color.png) | `stage.setTint(0)` |
| ![stage change color](web/assets/stage_change_color_by.png) | `stage.changeTint(25)` |
| ![stage set ghost](web/assets/stage_set_ghost.png) | `stage.setTransparency(0)` |
| ![stage change ghost](web/assets/stage_change_ghost_by.png) | `stage.changeTransparency(25)` |
| ![stage backdrop name](web/assets/stage_backdrop_name.png) | `stage.getCurrentBackdropName()` |
| ![stage backdrop index](web/assets/stage_backdrop_number.png) | `stage.getCurrentBackdropIndex()` |

To add a new backdrop call `stage.addBackdrop("newBackdrop",
"path/to/newBackdrop.png");`. Afterwards you can swith to the backdrop by calling
`stage.switchBackdrop("newBackdrop");`.


#### Sound

When you work with sound you need to install
[processing-sound](https://processing.org/reference/libraries/sound/) and need
to import the library in your main `.pde` file. `import processing.sound.*;`

| Scratch | Processing |
| :-: | :-: |
| ![sound play](web/assets/sound_play.png) | `stage.play("Meow");` |
| ![sound stop all](web/assets/sound_stop_all.png) | `stage.stopAllSounds();` |

To add a new sound call `stage.addSound("newSound", "path/to/newSound.wav");`.
Afterwards you can play the sound by calling `stage.playSound("newSound");`.

Hint: On my notebook I was not able to playback mp3 files, therefore I
converted them to wav files with [SoundConverter](http://soundconverter.org/).

### ScratchSprite

Usage: `import eu.barkmin.processing.scratch.ScratchSprite`

API: https://www.barkmin.eu/processing-library-scratch/reference/eu/barkmin/processing/scratch/ScratchSprite.html

Source Code: https://github.com/mikebarkmin/processing-library-scratch/blob/master/src/eu/barkmin/processing/scratch/ScratchSprite.java

In Scratch sprites are the main actors. Every sprite has a custom set of
costumes and sounds, which could be dynamicly changed. Most of the
functionality which sprites in Scratch have were tried to reimplement in
processing.

#### Creation

```java
import eu.barkmin.processing.scratch.*;

CatSprite myCat;

void setup() {
  size(800, 600);
  ScratchStage.init(this);
	myCat = new CatSprite();
}

void draw() {
	myCat.draw();
}

// Define a class Cat 
class CatSprite extends ScratchSprite {
	CatSprite() {
		super("cat", "sprites/cat.png");	
		this.setOnEdgeBounce(true);
	}
	void draw() {
    super.draw();
		this.move(2);
	}
}
```

#### Motion

| Scratch | Processing |
| :-: | :-: |
| ![sprite set x to](web/assets/sprite_set_x_to.png) | `sprite.setX(0);` |
| ![sprite change x by](web/assets/sprite_change_x_by.png) | `sprite.changeX(10);` |
| ![sprite set y to](web/assets/sprite_set_y_to.png) | `sprite.setY(0);` |
| ![sprite change y by](web/assets/sprite_change_y_by.png) | `sprite.changeY(10);` |
| ![sprite go to](web/assets/sprite_go_to.png) | `sprite.setPosition(0, 0);` |
| ![sprite move](web/assets/sprite_move_steps.png) | `sprite.move(10);` |
| ![sprite point in direction](web/assets/sprite_point_in_direction.png) | `sprite.setRotation(90);` |
| ![sprite turn left](web/assets/sprite_turn_left.png) | `sprite.turnLeft(15);` |
| ![sprite turn right](web/assets/sprite_turn_right.png) | `sprite.turnRight(15);` |
| ![sprite direction](web/assets/sprite_direction.png) | `sprite.getRotation();` |
| ![sprite on edge bounce](web/assets/sprite_if_on_edge_bounce.png) | `sprite.setOnEdgeBounce(true);` |
| ![sprite x](web/assets/sprite_x_positon.png) | `sprite.getX();` |
| ![sprite y](web/assets/sprite_y_position.png) | `sprite.getY();` |

#### Looks

| Scratch | Processing |
| :-: | :-: |
| ![sprite show](web/assets/sprite_show.png) | `sprite.show();` |
| ![sprite hide](web/assets/sprite_hide.png) | `sprite.hide();` |
| ![srpite switch costume](web/assets/sprite_switch_costume_to.png) | `sprite.switchCostume("costume1");` |
| ![sprite next costume](web/assets/sprite_next_costume.png) | `sprite.nextCostume();` |
| ![sprite set color](web/assets/sprite_set_color_to.png) | `sprite.setTint(0);` |
| ![sprite change color by](web/assets/sprite_change_color_by.png) | `sprite.changeTint(25);` |
| ![sprite set ghost](web/assets/sprite_set_ghost_to.png) | `sprite.setTransparency(0);` |
| ![sprite change ghost by](web/assets/sprite_change_ghost.png) | `sprite.changeTransparency(25);` |
| ![sprite set size](web/assets/sprite_set_size_to.png) | `sprite.setSize(100);` | 
| ![sprite costume name](web/assets/sprite_costume_name.png) | `sprite.getCurrentCostumeName();` |
| ![sprite costume number](web/assets/sprite_costume_number.png) | `sprite.getCurrentCostumeIndex();` |
| ![sprite size](web/assets/sprite_size.png) | `sprite.getSize();` |

To add a new costume call `sprite.addCostume("newCostume",
"path/to/newCostume.png");`. Afterwards you can swith to the costume by calling
`sprite.switchCostume("newCostume");`.

#### Sound

When you work with sound you need to install
[processing-sound](https://processing.org/reference/libraries/sound/) and need
to import the library in your main `.pde` file. `import processing.sound.*;`

| Scratch | Processing |
| :-: | :-: |
| ![sound play](web/assets/sound_play.png) | `sprite.play("Meow");` |
| ![sound stop all](web/assets/sound_stop_all.png) | `sprite.stopAllSounds();` |

To add a new sound call `sprite.addSound("newSound", "path/to/newSound.wav");`.
Afterwards you can play the sound by calling `sprite.playSound("newSound");`.

Hint: On my notebook I was not able to playback mp3 files, therefore I
converted them to wav files with [SoundConverter](http://soundconverter.org/).

### ScratchPen

Usage: `import eu.barkmin.processing.scratch.ScratchPen`

API: https://www.barkmin.eu/processing-library-scratch/reference/eu/barkmin/processing/scratch/ScratchPen.html

Source Code: https://github.com/mikebarkmin/processing-library-scratch/blob/master/src/eu/barkmin/processing/scratch/ScratchPen.java

In Scratch every sprite can use a pen to draw lines, therefore every
ScratchSprite object has an pen assoziated with it. Through a similiar api the
pen can be modified.

| Scratch | Processing |
| :-:     | :-:        |
| ![pen up](web/assets/pen_pen_up.png) | `sprite.getPen().up();` |
| ![pen down](web/assets/pen_pen_down.png) | `sprite.getPen().down();` |
| ![pen set color to](web/assets/pen_set_pen_color_to.png) | `sprite.getPen().setColor(50);` |
| ![pen change color by](web/assets/pen_change_pen_color_by.png) | `sprite.getPen().changeColor(1);` |
| ![pen set transparency to](web/assets/pen_set_pen_transparency_to.png) | `sprite.getPen().setTransparency(50);` |
| ![pen change transparency by](web/assets/pen_change_pen_transparency_by.png) | `sprite.getPen().changeTransparency(1);` |
| ![pen set size to](web/assets/pen_set_pen_size_to.png) | `sprite.getPen().setSize(50);` |
| ![pen change size by](web/assets/pen_change_pen_size_by.png) | `sprite.getPen().changeSize(1);` |
| ![pen erase all](web/assets/pen_erase_all.png) | `stage.eraseAll();` |

### ScratchSound (internally)

Usage: `import eu.barkmin.processing.scratch.ScratchSound`

API: https://www.barkmin.eu/processing-library-scratch/reference/eu/barkmin/processing/scratch/ScratchSound.html

Source Code: https://github.com/mikebarkmin/processing-library-scratch/blob/master/src/eu/barkmin/processing/scratch/ScratchSound.java

### ScratchImage (internally)

Usage: `import eu.barkmin.processing.scratch.ScratchImage`

API: https://www.barkmin.eu/processing-library-scratch/reference/eu/barkmin/processing/scratch/ScratchImage.html

Source Code: https://github.com/mikebarkmin/processing-library-scratch/blob/master/src/eu/barkmin/processing/scratch/ScratchImage.java

### ScratchColor (internally)

Usage: `import eu.barkmin.processing.scratch.ScratchColor`

API: https://www.barkmin.eu/processing-library-scratch/reference/eu/barkmin/processing/scratch/ScratchColor.html

Source Code: https://github.com/mikebarkmin/processing-library-scratch/blob/master/src/eu/barkmin/processing/scratch/ScratchColor.java

Scratch makes it easy to work with colors on the
[hsl](https://en.wikipedia.org/wiki/HSL_and_HSV) color spectrum. To achive a
similar behaviour the class ScratchColor was created.

## Examples

### Cat

Source Code: https://github.com/mikebarkmin/processing-library-scratch/tree/master/examples/Cat

An example with a simple one file setup.

![cat example](web/assets/cat.gif)

### Robot

Source Code: https://github.com/mikebarkmin/processing-library-scratch/tree/master/examples/Robot

An example with a class in another file.

![robot example](web/assets/robot.gif)

### Pipes

Source Code: https://github.com/mikebarkmin/processing-library-scratch/tree/master/examples/Pipes

An example with heavy use of the ScratchPen. It also plays an sound file in the background.

![pipes example](web/assets/pipes.gif)

## Missing

* blocks which are not listed above are currently missing
* sprites can not sense something. E.g.: a color or another sprite
* sprites can not speak or thing

## How to install Scratch

### Install with the Contribution Manager

Add contributed Libraries by selecting the menu item _Sketch_ → _Import Library..._ → _Add Library..._ This will open the Contribution Manager, where you can browse for Scratch, or any other Library you want to install.

Not all available Libraries have been converted to show up in this menu. If a Library isn't there, it will need to be installed manually by following the instructions below.

### Manual Install

Contributed Libraries may be downloaded separately and manually placed within the `libraries` folder of your Processing sketchbook. To find (and change) the Processing sketchbook location on your computer, open the Preferences window from the Processing application (PDE) and look for the "Sketchbook location" item at the top.

By default the following locations are used for your sketchbook folder: 
  * For Mac users, the sketchbook folder is located inside `~/Documents/Processing` 
  * For Windows users, the sketchbook folder is located inside `My Documents/Processing`

Download Scratch from https://github.com/mikebarkmin/processing-library-scratch

Unzip and copy the contributed Library's folder into the `libraries` folder in the Processing sketchbook. You will need to create this `libraries` folder if it does not exist.

The folder structure for Library Scratch should be as follows:

```
Processing
  libraries
    Scratch
      examples
      library
        Scratch.jar
      reference
      src
```
             
Some folders like `examples` or `src` might be missing. After Library Scratch has been successfully installed, restart the Processing application.

### Troubleshooting

If you're having trouble, have a look at the [Processing Wiki](https://github.com/processing/processing/wiki/How-to-Install-a-Contributed-Library) for more information, or contact the author [Mike Barkmin](http://barkmin.eu).
