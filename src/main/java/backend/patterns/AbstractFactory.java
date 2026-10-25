package backend.patterns;


interface AbstractButton {
    String setButtonColour();
}

interface AbstractInputLine {
    String setInputColour();
}


interface AbstractThemeFactory {
    AbstractButton createButton();
    AbstractInputLine createInput();
}



class LightButton implements AbstractButton {
    @Override
    public String setButtonColour() {
        return "⚪ Светлая кнопка";
    }
}

class LightInputLine implements AbstractInputLine {
    @Override
    public String setInputColour() {
        return "⬜ Светлое поле ввода";
    }
}


class DarkButton implements AbstractButton {
    @Override
    public String setButtonColour() {
        return "⬛ Тёмная кнопка";
    }
}

class DarkInputLine implements AbstractInputLine {
    @Override
    public String setInputColour() {
        return "🔳 Тёмное поле ввода";
    }
}


class LightThemeFactory implements AbstractThemeFactory {
    @Override
    public AbstractButton createButton() {
        return new LightButton();
    }

    @Override
    public AbstractInputLine createInput() {
        return new LightInputLine();
    }
}

class DarkThemeFactory implements AbstractThemeFactory {
    @Override
    public AbstractButton createButton() {
        return new DarkButton();
    }

    @Override
    public AbstractInputLine createInput() {
        return new DarkInputLine();
    }
}


public class AbstractFactory {
    public static void renderUi(AbstractThemeFactory factory) {
        AbstractButton newButton = factory.createButton();
        AbstractInputLine newInput = factory.createInput();

        System.out.println("Отрисовка: " + newButton.setButtonColour() + " и " + newInput.setInputColour());
    }

    public static void main(String[] args) {
        renderUi(new LightThemeFactory());
        renderUi(new DarkThemeFactory());
    }
}
