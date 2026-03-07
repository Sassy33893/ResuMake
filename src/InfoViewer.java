import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

//UI for viewing and deleting stored resume data
public class InfoViewer extends JPanel {

    ArrayList<String> display = new ArrayList<String>();
    private JPanel checkboxPanel;
    private JScrollPane scrollPane;
    private ArrayList<JCheckBox> checkboxes = new ArrayList<JCheckBox>();
    private String currentFilePath = "";
    
    public InfoViewer() {
        setLayout(new BorderLayout());
        
        //create selector menu
        String[] options = {"Skills", "Education", "Experiences"};
        JComboBox<String> selector = new JComboBox<>(options);
        
        //add listener for selector selection
        selector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selectedItem = (String) selector.getSelectedItem();
                onselectorSelected(selectedItem);
            }
        });
        
        //add selector to top of panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.pages.show(Main.container, "homePage");
            }
        });
        topPanel.add(backButton);
        topPanel.add(new JLabel("  View:"));
        topPanel.add(selector);
        add(topPanel, BorderLayout.NORTH);
        
        //create checkbox panel for displaying entries
        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(checkboxPanel);
        add(scrollPane, BorderLayout.CENTER);
        
        //create bottom panel with delete button
        JPanel bottomPanel = new JPanel();
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteSelected();
            }
        });
        bottomPanel.add(deleteButton);
        add(bottomPanel, BorderLayout.SOUTH);
        
        //load Skills by default when panel is shown so that it doesent appear empty
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                onselectorSelected("Skills");
                selector.setSelectedItem("Skills");
            }
        });
    }
    
    //rebuild and refresh the checkbox list from current display data
    private void updateListDisplay() {
        checkboxPanel.removeAll();
        checkboxes.clear();
        
        for (String item : display) {
            JCheckBox checkbox = new JCheckBox(item);
            checkbox.setAlignmentX(Component.LEFT_ALIGNMENT);
            checkboxes.add(checkbox);
            checkboxPanel.add(checkbox);
        }
        
        checkboxPanel.revalidate();
        checkboxPanel.repaint();
    }
  
    //switch file source based on dropdown selection and then display its data
    private void onselectorSelected(String selection) {
        if(selection.equals("Skills")) {
            currentFilePath = "Data/Skills.dat";
            display = DataManager.readData(currentFilePath);
        }
        else if(selection.equals("Education")) {
            currentFilePath = "Data/Education.dat";
            display = DataManager.readData(currentFilePath);
        }
        else if(selection.equals("Experiences")) {
            currentFilePath = "Data/Experience.dat";
            display = DataManager.readData(currentFilePath);
        }
        updateListDisplay();
    }
    

    //a maethod to delete selected entries by rewriting the file with unchecked entries 
    private void deleteSelected() {
        ArrayList<String> itemsToKeep = new ArrayList<String>();
        
        //collect unchecked items 
        for (int i = 0; i < checkboxes.size(); i++) {
            if (!checkboxes.get(i).isSelected()) {
                itemsToKeep.add(display.get(i));
            }
        }
        
        //write back only the items we want to keep
        DataManager.writeData(currentFilePath, itemsToKeep);
        
        //reload the display
        display = DataManager.readData(currentFilePath);
        updateListDisplay();
        
        JOptionPane.showMessageDialog(this, "Selected items deleted successfully!");
    }
}
