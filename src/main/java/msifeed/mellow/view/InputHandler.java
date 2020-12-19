package msifeed.mellow.view;

public interface InputHandler {
    interface Keyboard {
        boolean onKeyboard(char c, int key);
    }

    interface MouseClick {
        void onMouseClick(int mouseX, int mouseY, int button);
    }

    interface MouseWheel {
        void onMouseWheel(int mouseX, int mouseY, int button);
    }
}
