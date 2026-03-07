import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class ResumeMaker extends JPanel {

    private ArrayList<JCheckBox> skillCheckboxes = new ArrayList<JCheckBox>();
    private ArrayList<JCheckBox> experienceCheckboxes = new ArrayList<JCheckBox>();
    private ArrayList<JCheckBox> educationCheckboxes = new ArrayList<JCheckBox>();

    private ArrayList<String> skillsData = new ArrayList<String>();
    private ArrayList<String> experiencesData = new ArrayList<String>();
    private ArrayList<String> educationData = new ArrayList<String>();

    private JPanel columnsPanel;


    //max selections allowed for each resume to prevent overflow on the pdf created
    private final int maxSkills = 3;
    private final int maxExperiences = 4;
    private final int maxEducation = 2;

    public ResumeMaker() {
        setLayout(new BorderLayout());

        //create top panel with back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.pages.show(Main.container, "homePage");
            }
        });
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);

        //create columns panel
        columnsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        columnsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(columnsPanel, BorderLayout.CENTER);

        //create bottom panel with create resume button
        JPanel bottomPanel = new JPanel();
        JButton createResumeButton = new JButton("Create Resume");
        createResumeButton.setPreferredSize(new Dimension(150, 40));
        createResumeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createResume();
            }
        });
        bottomPanel.add(createResumeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        //add listener to refresh when panel becomes visible
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                refreshDisplay();
            }
        });
    }

    //rebuild and refresh the display with data from files
    private void refreshDisplay() {
        //clear old checkboxes
        skillCheckboxes.clear();
        experienceCheckboxes.clear();
        educationCheckboxes.clear();
        columnsPanel.removeAll();

        //reload data from files
        skillsData = DataManager.readData("Data/Skills.dat");
        experiencesData = DataManager.readData("Data/Experience.dat");
        educationData = DataManager.readData("Data/Education.dat");

        //create skills column
        JPanel skillsPanel = createColumnPanel("Skills (Max: 3)", skillsData, skillCheckboxes, maxSkills);
        columnsPanel.add(skillsPanel);

        //create experiences column
        JPanel experiencesPanel = createColumnPanel("Experiences (Max: 4)", experiencesData, experienceCheckboxes, maxExperiences);
        columnsPanel.add(experiencesPanel);

        //create education column
        JPanel educationPanel = createColumnPanel("Education (Max: 2)", educationData, educationCheckboxes, maxEducation);
        columnsPanel.add(educationPanel);

        columnsPanel.revalidate();
        columnsPanel.repaint();
    }

    //method to create a column panel with checkboxes and selection limits
    private JPanel createColumnPanel(String title, ArrayList<String> data, ArrayList<JCheckBox> checkboxList, int maxSelections) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JPanel headerPanel = new JPanel();
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel selectionLabel = new JLabel("Selected: 0/" + maxSelections);
        selectionLabel.setFont(new Font("Arial", Font.BOLD, 12));
        headerPanel.add(selectionLabel);
        panel.add(headerPanel);

        //create checkboxes for each item
        for (String item : data) {
            JCheckBox checkbox = new JCheckBox(item);
            checkbox.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            //add listener to enforce max selections
            checkbox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int selectedCount = countSelected(checkboxList);
                    if (checkbox.isSelected() && selectedCount > maxSelections) {
                        checkbox.setSelected(false);
                        JOptionPane.showMessageDialog(ResumeMaker.this, "Maximum of " + maxSelections + " items can be selected!");
                    }
                    selectionLabel.setText("Selected: " + countSelected(checkboxList) + "/" + maxSelections);
                }
            });

            checkboxList.add(checkbox);
            panel.add(checkbox);
        }

        //add scroll in anticipation of long lists
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.add(scrollPane, BorderLayout.CENTER);
        return containerPanel;
    }

    //method to count the number of selected checkboxes
    private int countSelected(ArrayList<JCheckBox> checkboxList) {
        int count = 0;
        for (JCheckBox checkbox : checkboxList) {
            if (checkbox.isSelected()) {
                count++;
            }
        }
        return count;
    }

    //this method takes the selections and puts them into a resume object, then generates a pdf and saves the resume data to file
    private void createResume() {
        ArrayList<String> selectedSkills = getSelectedItems(skillCheckboxes, skillsData);
        ArrayList<String> selectedExperiences = getSelectedItems(experienceCheckboxes, experiencesData);
        ArrayList<String> selectedEducation = getSelectedItems(educationCheckboxes, educationData);

        if (selectedSkills.isEmpty() || selectedExperiences.isEmpty() || selectedEducation.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one item from each category!");
            return;
        }

        //check if content will overflow the pdf page
        int estimatedLines = selectedSkills.size() * 2 + selectedExperiences.size() * 3 + selectedEducation.size() * 3;
        if (estimatedLines > 35) {
            JOptionPane.showMessageDialog(this, 
                "Too much content selected! Please reduce selections to fit on one page.",
                "Content Overflow Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        //ask user for resume name and validate
        String resumeName = JOptionPane.showInputDialog(this, "Enter a name for this resume:", "My Resume");
        if (resumeName == null || resumeName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Resume name cannot be empty!");
            return;
        }
        resumeName = resumeName.trim();

        //convert selected strings to objects
        ArrayList<Skill> skillObjects = new ArrayList<Skill>();
        for (int i = 0; i < selectedSkills.size(); i++) {
            String skillStr = selectedSkills.get(i);
            skillObjects.add(new Skill(skillStr));
        }

        ArrayList<Experience> experienceObjects = new ArrayList<Experience>();
        for (int i = 0; i < selectedExperiences.size(); i++) {
            String expStr = selectedExperiences.get(i);
            experienceObjects.add(new Experience(expStr));
        }

        ArrayList<Education> educationObjects = new ArrayList<Education>();
        for (int i = 0; i < selectedEducation.size(); i++) {
            String eduStr = selectedEducation.get(i);
            educationObjects.add(new Education(eduStr));
        }

        //load header 
        ArrayList<String> headerData = DataManager.readData("Data/Header.dat");
        Header header = new Header("default@email.com", "Unknown Location", "000-000-0000", "Resume User");
        if (!headerData.isEmpty()) {
            header = new Header(headerData.get(0));
        }

        //create resume object with custom name
        Resume resume = new Resume(resumeName, experienceObjects, header, skillObjects, educationObjects);
        
        //validate and fix name for file saving and collision detection
        resume.checkName();
        
        //generate pdf
        resume.generatePDF();
        
        //save resume data to file
        DataManager.saveResume(resume);
        
        //confirmation message
        JOptionPane.showMessageDialog(this, "Resume generated successfully!");
    }

    //collect selected data entries by index
    private ArrayList<String> getSelectedItems(ArrayList<JCheckBox> checkboxList, ArrayList<String> dataList) {
        ArrayList<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < checkboxList.size(); i++) {
            if (checkboxList.get(i).isSelected()) {
                selectedItems.add(dataList.get(i));
            }
        }
        return selectedItems;
    }
}
