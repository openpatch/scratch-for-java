---
name: switchBackdrop()
---

# switchBackdrop()

Switches to a specific backdrop of the stage.

## Examples

![](/assets/documentation/StageSwitchBackdrop.gif)

::::tabs

:::tab{title="Default Class"}

```java
import org.openpatch.scratch.Stage;

public class StageSwitchBackdrop {
    public StageSwitchBackdrop() {
        Stage myStage = new Stage(254,100);
        myStage.addBackdrop("forest", "examples/java/assets/background_forest.png");
        myStage.addBackdrop("sea", "examples/java/assets/background_sea.png");
        myStage.wait(3000);
        myStage.switchBackdrop("sea");
    }
    public static void main(String[] args) {
        new StageSwitchBackdrop();
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
        this.switchBackdrop("sea");
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
stage.switchBackdrop(name)
```

## Parameters

| Name | Data Type | Description          |
| --------- | --------- | -------------------- |
| name      | String    | Name of the backdrop |

## Return

void

## Related

- [addBackdrop()](/documentation/stage/looks/add-backdrop)
- [removeBackdrop()](/documentation/stage/looks/remove-backdrop)
- [nextBackdrop()](/documentation/stage/looks/next-backdrop)
