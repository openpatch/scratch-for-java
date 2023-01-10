---
name: nextBackdrop()
---

# nextBackdrop()

Switches to the next backdrop of the stage.

## Examples

![](/assets/documentation/StageNextBackdrop.gif)

::::tabs

:::tab{title="Default Class"}

```java
import org.openpatch.scratch.Stage;

public class StageNextBackdrop {
    public StageNextBackdrop() {
        Stage myStage = new Stage(254, 100);
        myStage.addBackdrop("forest", "examples/java/assets/background_forest.png");
        myStage.addBackdrop("sea", "examples/java/assets/background_sea.png");

        myStage.wait(3000);
        myStage.nextBackdrop();
    }

    public static void main(String[] args) {
        new StageNextBackdrop();
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
        this.nextBackdrop("forest");
    }
}

public class StageNextBackdrop {
    public static void main(String[] args) {
        new CustomStage();
    }
}
```

:::

::::

## Syntax

```java
stage.nextBackdrop(name)
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
