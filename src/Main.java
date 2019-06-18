import com.oocourse.uml2.interact.AppRunner;
import model.MyUmlGeneralInteraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;

public class Main {
    // TODO: turn off
    private static boolean INPUT_REDIR = false;
    
    public static void main(String[] args) throws Exception {
        if (INPUT_REDIR) {
            System.setIn(new FileInputStream(new File("test\\middle_4.txt")));
            System.setOut(new PrintStream(new File("test\\out.txt")));
        }
        AppRunner appRunner =
                AppRunner.newInstance(MyUmlGeneralInteraction.class);
        appRunner.run(args);
    }
}