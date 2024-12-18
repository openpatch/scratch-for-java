package Shader;

import org.openpatch.scratch.Window;

public class MyWindow extends Window {
    public MyWindow() {
        super(800, 400);
        this.setStage(new MyStage());
    }

    public static void main(String[] args) {
        new MyWindow();
    }
}