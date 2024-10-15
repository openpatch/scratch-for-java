import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class CatStage extends Stage {
  public CatStage() {
    super(800, 600);
    Sprite myCat = new CatSprite();
    this.add(myCat);
  }
}
