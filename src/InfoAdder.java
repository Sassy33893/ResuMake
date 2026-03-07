import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;

//UI for adding and managing resume data entries
public class InfoAdder extends JPanel implements ActionListener {

    //file paths for data storage
    private static final String headerDataFile = "Data/Header.dat";
    private static final String skillsDataFile = "Data/Skills.dat";
    private static final String educationDataFile = "Data/Education.dat";
    private static final String experienceDataFile = "Data/Experience.dat";

    //UI sizing
    private final Dimension fieldSize = new Dimension(200, 25);
    private final Dimension buttonSize = new Dimension(150, 30);

    //a stack to save user actions to allow them to undo if needed
    private UndoStack undoStack = new UndoStack();

    //header fields
    private JTextField headerNameField, headerEmailField, headerPhoneField, headerLocationField;
    private JButton headerSubmitButton;

    //skills fields
    private JTextField skillCategoryField, skillListField;
    private JButton skillSaveButton;

    //education fields
    private JTextField eduProgramField, eduSchoolField, eduLocationField, eduDatesField, eduDetailsField;
    private JButton eduAddDetailButton, eduSubmitButton;
    private DefaultListModel<String> eduDetailsListModel;

    //experience fields
    private JTextField expNameField, expOrganizationField, expLocationField, expDatesField, expDetailsField;
    private JButton expAddDescButton, expSubmitButton;
    private DefaultListModel<String> expDescListModel;
    private JButton undoButton;

    //constructing the window and its components
    public InfoAdder() {
        setLayout(new BorderLayout());
        
        //create top panel with back button and undo button 
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel leftTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.pages.show(Main.container, "homePage");
            }
        });
        undoButton = new JButton("Undo Detail/Description");
        undoButton.addActionListener(this);
        undoButton.setPreferredSize(new Dimension(220, 30));
        leftTopPanel.add(backButton);
        rightTopPanel.add(undoButton);
        topPanel.add(leftTopPanel, BorderLayout.WEST);
        topPanel.add(rightTopPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        
        //main panel 
        JPanel mainPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        //header panel
        JPanel headerPanel = createPanel("Header");
        headerNameField = createField(60);
        headerEmailField = createField(60);
        headerPhoneField = createField(20);
        headerLocationField = createField(60);
        headerSubmitButton = new JButton("Save Header");
        formatButton(headerSubmitButton);
        headerSubmitButton.addActionListener(this);

        addRow(headerPanel, "Name:", headerNameField);
        addRow(headerPanel, "Email:", headerEmailField);
        addRow(headerPanel, "Phone:", headerPhoneField);
        addRow(headerPanel, "Location:", headerLocationField);
        addButton(headerPanel, headerSubmitButton);

        //skills panel
        JPanel skillPanel = createPanel("Skills");
        skillCategoryField = createField(40);
        skillListField = createField(100);
        skillSaveButton = new JButton("Save Skills");
        formatButton(skillSaveButton);
        skillSaveButton.addActionListener(this);

        addRow(skillPanel, "Category:", skillCategoryField);
        addRow(skillPanel, "Skills:", skillListField);
        skillPanel.add(Box.createRigidArea(new Dimension(0, 75)));
        addButton(skillPanel, skillSaveButton);

        //education panel
        JPanel eduPanel = createPanel("Education");
        eduProgramField = createField(60);
        eduSchoolField = createField(60);
        eduLocationField = createField(60);
        eduDatesField = createField(30);
        eduDetailsField = createField(80);
        eduAddDetailButton = new JButton("Add Detail");
        eduSubmitButton = new JButton("Save Education");
        formatButton(eduAddDetailButton);
        formatButton(eduSubmitButton);
        eduAddDetailButton.addActionListener(this);
        eduSubmitButton.addActionListener(this);

        addRow(eduPanel, "Program:", eduProgramField);
        addRow(eduPanel, "School:", eduSchoolField);
        addRow(eduPanel, "Location:", eduLocationField);
        addRow(eduPanel, "Dates:", eduDatesField);
        addRow(eduPanel, "Detail:", eduDetailsField);
        
        eduDetailsListModel = new DefaultListModel<>();
        
        JPanel eduButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        eduButtonPanel.add(eduAddDetailButton);
        eduButtonPanel.add(eduSubmitButton);
        eduPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        eduPanel.add(eduButtonPanel);

        //experience panel
        JPanel expPanel = createPanel("Experience");
        expNameField = createField(60);
        expOrganizationField = createField(60);
        expLocationField = createField(60);
        expDatesField = createField(30);
        expDetailsField = createField(80);
        expAddDescButton = new JButton("Add Description");
        expSubmitButton = new JButton("Save Experience");
        formatButton(expAddDescButton);
        formatButton(expSubmitButton);
        expAddDescButton.addActionListener(this);
        expSubmitButton.addActionListener(this);

        addRow(expPanel, "Name:", expNameField);
        addRow(expPanel, "Organization:", expOrganizationField);
        addRow(expPanel, "Location:", expLocationField);
        addRow(expPanel, "Dates:", expDatesField);
        addRow(expPanel, "Description:", expDetailsField);
        
        expDescListModel = new DefaultListModel<>();
        
        JPanel expButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        expButtonPanel.add(expAddDescButton);
        expButtonPanel.add(expSubmitButton);
        expPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        expPanel.add(expButtonPanel);

        //add all panels to main panel in 2x2 grid
        mainPanel.add(headerPanel);
        mainPanel.add(skillPanel);
        mainPanel.add(eduPanel);
        mainPanel.add(expPanel);
    }

    //helper methods for creating each panel 
    //create titled section panel
    private JPanel createPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    //create text field with character limit
    private JTextField createField(int maxLength) {
        JTextField field = new JTextField();
        field.setMaximumSize(fieldSize);
        field.setPreferredSize(fieldSize);
        
        //add document filter to limit character count
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getLength() + string.length()) <= maxLength) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                int currentLength = fb.getDocument().getLength();
                int newLength = currentLength - length + (text != null ? text.length() : 0);
                if (newLength <= maxLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        
        return field;
    }

    //add one label and field row to a panel
    private void addRow(JPanel panel, String labelText, JTextField field) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 25));
        row.add(label);
        row.add(field);
        panel.add(row);
    }

    //add button row with spacing
    private void addButton(JPanel panel, JButton button) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(buttonPanel);
    }

    //apply consistent button size
    private void formatButton(JButton button) {
        button.setPreferredSize(buttonSize);
    }

    //append one csv line to a data file
    private void appendDataLine(String filePath, String line) {
        ArrayList<String> fileData = DataManager.readData(filePath);
        fileData.add(line);
        DataManager.writeData(filePath, fileData);
    }

    //undo most recent detail or description add
    private void handleUndoAction() {
        if (undoStack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing to undo.");
            return;
        }

        Action action = undoStack.pop();
        if (action.getType().equals("EDU")) {
            removeLastMatching(eduDetailsListModel, action.getValue());
        }
        else if (action.getType().equals("EXP")) {
            removeLastMatching(expDescListModel, action.getValue());
        }
    }

    //remove latest matching item from list model
    private void removeLastMatching(DefaultListModel<String> model, String value) {
        for (int i = model.size() - 1; i >= 0; i--) {
            if (model.get(i).equals(value)) {
                model.remove(i);
                break;
            }
        }
    }

    //action handlers for all buttons
    public void actionPerformed(ActionEvent e) {
        //undo button 
        if (e.getSource() == undoButton) {
            handleUndoAction();
            return;
        }

        //header
        if (e.getSource() == headerSubmitButton) {
            //validate all header fields are filled
            String name = headerNameField.getText().trim();
            String email = headerEmailField.getText().trim();
            String phone = headerPhoneField.getText().trim();
            String location = headerLocationField.getText().trim();
            
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All header fields are required!");
                return;
            }
            
            Header header = new Header(email, location, phone, name);
            DataManager.writeHeader(headerDataFile, header.toString());
            headerNameField.setText("");
            headerEmailField.setText("");
            headerPhoneField.setText("");
            headerLocationField.setText("");
        }

        //skills
        if (e.getSource() == skillSaveButton) {
            ArrayList<String> skills = new ArrayList<String>();
            String skillsText = skillListField.getText().trim();
            if (skillsText.length() > 0) {
                String[] skillsArray = skillsText.split(",");
                for (int i = 0; i < skillsArray.length; i++) {
                    String skill = skillsArray[i];
                    String trimmedSkill = skill.trim();
                    if (trimmedSkill.length() > 0) {
                        skills.add(trimmedSkill);
                    }
                }
            }

            Skill skill = new Skill(skillCategoryField.getText().trim(), skills);
            appendDataLine(skillsDataFile, skill.toString());
            skillCategoryField.setText("");
            skillListField.setText("");
        }

        //education
        if (e.getSource() == eduAddDetailButton) {
            String detail = eduDetailsField.getText().trim();
            if (!detail.isEmpty()) {
                eduDetailsListModel.addElement(detail);
                undoStack.push("EDU", detail);
                eduDetailsField.setText("");
            }
        }
        if (e.getSource() == eduSubmitButton) {
            String pendingDetail = eduDetailsField.getText().trim();
            if (!pendingDetail.isEmpty()) {
                eduDetailsListModel.addElement(pendingDetail);
                undoStack.push("EDU", pendingDetail);
                eduDetailsField.setText("");
            }

            ArrayList<String> details = new ArrayList<String>();
            for (int i = 0; i < eduDetailsListModel.size(); i++) {
                details.add(eduDetailsListModel.get(i));
            }

            //validate required fields before saving
            String program = eduProgramField.getText().trim();
            String school = eduSchoolField.getText().trim();
            String location = eduLocationField.getText().trim();
            String dates = eduDatesField.getText().trim();
            
            if (program.isEmpty() || school.isEmpty() || location.isEmpty() || dates.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All education fields are required!");
                return;
            }
            
            Education education = new Education(program, school, location, dates, details);
            appendDataLine(educationDataFile, education.toString());
            
            //clear the list model and undo stack after successful submission
            eduDetailsListModel.clear();
            undoStack = new UndoStack();
            
            eduProgramField.setText("");
            eduSchoolField.setText("");
            eduLocationField.setText("");
            eduDatesField.setText("");
            eduDetailsField.setText("");
        }

        //experience
        if (e.getSource() == expAddDescButton) {
            String desc = expDetailsField.getText().trim();
            if (!desc.isEmpty()) {
                expDescListModel.addElement(desc);
                undoStack.push("EXP", desc);
                expDetailsField.setText("");
            }
        }
        if (e.getSource() == expSubmitButton) {
            String pendingDescription = expDetailsField.getText().trim();
            if (!pendingDescription.isEmpty()) {
                expDescListModel.addElement(pendingDescription);
                undoStack.push("EXP", pendingDescription);
                expDetailsField.setText("");
            }

            ArrayList<String> descriptions = new ArrayList<String>();
            for (int i = 0; i < expDescListModel.size(); i++) {
                descriptions.add(expDescListModel.get(i));
            }

            //validate required fields before saving
            String name = expNameField.getText().trim();
            String organization = expOrganizationField.getText().trim();
            String location = expLocationField.getText().trim();
            String dates = expDatesField.getText().trim();
            
            if (name.isEmpty() || organization.isEmpty() || location.isEmpty() || dates.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All experience fields are required!");
                return;
            }
            
            Experience experience = new Experience(name, organization, location, dates, descriptions);
            appendDataLine(experienceDataFile, experience.toString());
            
            //clear the list model and undo stack after successful submission
            expDescListModel.clear();
            undoStack = new UndoStack();
            
            expNameField.setText("");
            expOrganizationField.setText("");
            expLocationField.setText("");
            expDatesField.setText("");
            expDetailsField.setText("");
        }
    }
}
