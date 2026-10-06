import org.openpatch.scratch.Sprite;
import org.openpatch.scratch.Stage;

public class CatStage extends Stage {

  // scratch4j:begin fields (managed by the stage designer)
  // scratch4j:end fields
  public CatStage() {
    super(800, 600);
    Sprite myCat = new CatSprite();
    this.add(myCat);

    // scratch4j:begin setup (managed by the stage designer)
    // scratch4j:end setup
  }
}
