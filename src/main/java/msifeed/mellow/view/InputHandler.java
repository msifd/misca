package msifeed.mellow.view;

public interface InputHandler {
    interface Keyboard extends InputHandler {
        boolean onKeyboard(char c, int key);
    }

    interface MouseClick extends InputHandler {
        void onMouseClick(int mouseX, int mouseY, int button);
    }

    interface Mouse extends InputHandler {
        void onMousePress(int mouseX, int mouseY, int button);

        void onMouseMove(int mouseX, int mouseY, int button);

        void onMouseRelease(int mouseX, int mouseY, int state);
    }

    interface MouseWheel extends InputHandler {
        void onMouseWheel(int mouseX, int mouseY, int button);
    }
}
