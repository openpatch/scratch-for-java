---
name: addBackdrop()
---

# addBackdrop()

Adds a backdrop from the stage.

## Examples

![](/assets/documentation/StageAddBackdrop.gif)

::::tabs

:::tab{title="Default Class"}

```java
import org.openpatch.scratch.Stage;

public class StageAddBackdrop {
    public StageAddBackdrop() {
        Stage myStage = new Stage(254, 100);
        myStage.wait(3000);
        myStage.addBackdrop("forest", "examples/java/assets/background_forest.png");
    }

    public static void main(String[] args) {
        new StageAddBackdrop();
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
        this.wait(3000);
        this.addBackdrop("forest", "examples/java/assets/background_forest.png");
    }
}

public class StageAddBackdrop {
    public static void main(String[] args) {
        new CustomStage();
    }
}
```

:::

::::

## Syntax

```java
stage.addBackdrop(name)
```

## Parameters

| Name | Data Type | Description          |
| ---- | --------- | -------------------- |
| name | String    | Name of the backdrop |

## Return

void

## Related

- [removeBackdrop()](/documentation/stage/looks/remove-backdrop)
- [switchBackdrop()](/documentation/stage/looks/switch-backdrop)
- [nextBackdrop()](/documentation/stage/looks/next-backdrop)
