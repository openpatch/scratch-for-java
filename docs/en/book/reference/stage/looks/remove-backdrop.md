---
name: removeBackdrop()
---

# removeBackdrop()

Removes a backdrop from the stage.

## Examples

![](/assets/documentation/StageRemoveBackdrop.gif)

::::tabs

:::tab{title="Default Class"}

```java
import org.openpatch.scratch.Stage;

public class StageRemoveBackdrop {
    public StageRemoveBackdrop() {
        Stage myStage = new Stage(254,100);
        myStage.addBackdrop("forest", "examples/java/assets/background_forest.png");
        myStage.addBackdrop("sea", "examples/java/assets/background_sea.png");
        myStage.wait(3000);
        myStage.removeBackdrop("forest");
    }

    public static void main(String[] args) {
        new StageRemoveBackdrop();
    }

}
```

:::

:::tab{title="Custom Class"}

```java
import org.openpatch.scratch.Stage;

class CustomStage extends Stage {
    public CustomStage() {
        super(254, 100);
        this.addBackdrop("forest", "examples/java/assets/background_forest.png");
        this.addBackdrop("sea", "examples/java/assets/background_sea.png");
        this.wait(3000);
        this.removeBackdrop("forest");
    }
}

public class StageSwitchBackdrop {
    public static void main(String[] args) {
        new CustomStage();
    }
}
```

:::

::::

## Syntax

```java
stage.removeBackdrop(name)
```

## Parameters

| Name | Data Type | Description          |
| ---- | --------- | -------------------- |
| name | String    | Name of the backdrop |

## Return

void

## Related

- [addBackdrop()](/documentation/stage/looks/add-backdrop)
- [switchBackdrop()](/documentation/stage/looks/switch-backdrop)
- [nextBackdrop()](/documentation/stage/looks/next-backdrop)
