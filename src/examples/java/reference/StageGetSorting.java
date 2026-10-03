package reference;
import org.openpatch.scratch.*;


public class StageGetSorting {
  public StageGetSorting() {
    Stage myStage = new Stage(600, 240);

    Sprite cactus = new Sprite("cactus", "cactus");
    myStage.add(cactus);

    // added last, so without sorting the slime is always drawn on top
    Sprite slime = new Sprite("slime", "slimeBlue");
    slime.setX(30);
    myStage.add(slime);

    // whoever stands further down is drawn in front: the slime passes
    // behind the cactus on its way up and in front of it on its way down
    myStage.getSorting().byY();

    int step = 2;
    while (true) {
      slime.changeY(step);
      if (slime.getY() > 100 || slime.getY() < -100) {
        step = -step;
      }
      myStage.wait(20);
    }
  }

  public static void main(String[] args) {
    new StageGetSorting();
  }
}
