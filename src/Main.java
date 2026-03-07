import java.awt.*;
import javax.swing.*;

//main application window and page management
public class Main extends JFrame {

    //card layout to switch between pages
    static CardLayout pages;
    //main container for all pages
    static Container container;
 

    public static HomePage homePage;
    public static ResumeMaker resumeMaker;
    public static InfoAdder infoAdder;
    public static InfoViewer infoViewer;


    //create and register all app pages
    public Main(){
        super("ResuMake");
        pages = new CardLayout();

        container = getContentPane();
        container.setLayout(pages);

        homePage = new HomePage();
        resumeMaker = new ResumeMaker();
        infoAdder = new InfoAdder();
        infoViewer = new InfoViewer();

        container.add(homePage,"homePage");
        container.add(resumeMaker,"resumeMaker");
        container.add(infoAdder,"infoAdder");
        container.add(infoViewer,"infoViewer");
    }

    //start the desktop app window
    public static void main(String[] args) {
        Main frame = new Main();
        frame.setSize(1000, 600);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
