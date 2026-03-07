import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


//UI and structures for tracking pending changes and recently viewed resumes
public class HomePage extends JPanel {

    //variables and structures
    private JPanel resumeListPanel;
    private JPanel recentlyViewedPanel;
    private JScrollPane scrollPane;
    private JButton confirmButton;
    private Map<String, Boolean> pendingSubmitted = new HashMap<String, Boolean>();
    private Map<String, Boolean> pendingAdmitted = new HashMap<String, Boolean>();
    private Set<String> pendingDelete = new HashSet<String>();
    private Deque<Resume> recentlyViewed = new ArrayDeque<Resume>();

    //constructor 
    public HomePage() {
        setLayout(new BorderLayout());

        //top panel with navigation buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton resumeButton = new JButton("Go to Resume Maker");
        JButton infoButton = new JButton("Go to Info Adder");
        JButton infoViewerButton = new JButton("InfoViewer");

        topPanel.add(resumeButton);
        topPanel.add(infoButton);
        topPanel.add(infoViewerButton);
        add(topPanel, BorderLayout.NORTH);

        //buttons to switch pages
        resumeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.pages.show(Main.container, "resumeMaker");
            }
        });
        infoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.pages.show(Main.container, "infoAdder");
            }
        });
        infoViewerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.pages.show(Main.container, "infoViewer");
            }
        });

        //main panel with resume list
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Your Resumes", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        centerPanel.add(titleLabel, BorderLayout.NORTH);

        resumeListPanel = new JPanel();
        resumeListPanel.setLayout(new BoxLayout(resumeListPanel, BoxLayout.Y_AXIS));

        //adding a scrollbar 
        scrollPane = new JScrollPane(resumeListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        //seperate container to display recently viewed resumes
        JPanel recentlyViewedContainer = new JPanel(new BorderLayout());
        recentlyViewedContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        recentlyViewedContainer.setPreferredSize(new Dimension(260, 0));
        JLabel recentHeader = new JLabel("Recently Viewed", SwingConstants.CENTER);
        recentHeader.setFont(new Font("Arial", Font.BOLD, 14));
        recentlyViewedContainer.add(recentHeader, BorderLayout.NORTH);
        recentlyViewedPanel = new JPanel();
        recentlyViewedPanel.setLayout(new BoxLayout(recentlyViewedPanel, BoxLayout.Y_AXIS));
        JScrollPane recentScrollPane = new JScrollPane(recentlyViewedPanel);
        recentlyViewedContainer.add(recentScrollPane, BorderLayout.CENTER);
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(recentlyViewedContainer, BorderLayout.EAST);
        centerPanel.add(contentPanel, BorderLayout.CENTER);

        //adding a confirm button to apply changes the user selects
        JPanel confirmPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        confirmButton = new JButton("Confirm");
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyPendingChanges();
            }
        });
        confirmPanel.add(confirmButton);
        centerPanel.add(confirmPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

    
        //add listener to refresh the list whenever the home page is shown
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent e) {
                refreshResumeList();
            }
        });

        //initial load
        refreshResumeList();
    }

    //method to refresh the list after changes are made
    private void refreshResumeList() {
        //clear current resume list ui
        resumeListPanel.removeAll();

        //making sure data folder exists
        File dataFolder = new File("Data");
        if (!(dataFolder.exists())) {
            dataFolder.mkdir();
        }

        //get all saved resume data 
        File[] allFiles = dataFolder.listFiles();
        ArrayList<File> resumeFiles = new ArrayList<File>();
        if (allFiles != null) {
            for (int i = 0; i < allFiles.length; i++) {
                File file = allFiles[i];
                if (file.getName().startsWith("resume_") && file.getName().endsWith(".dat")){
                    resumeFiles.add(file);
                }
            }
        }
        File[] files = resumeFiles.toArray(new File[0]);

        if (files == null || files.length == 0) {
            //show message if no resumes exist
            JLabel noResumesLabel = new JLabel("No resumes found. Create one using Resume Maker!");
            noResumesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            resumeListPanel.add(noResumesLabel);
        } else {
            //load each resume and make a row for it
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                String resumeName = fileName.substring("resume_".length(), fileName.length() - ".dat".length());
                Resume resume = DataManager.loadResume(resumeName);
                if (resume != null) {
                    JPanel resumeItemPanel = createResumeItemPanel(resumeName, resume);
                    resumeListPanel.add(resumeItemPanel);
                    resumeListPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
        }

        //update ui
        updateConfirmButtonText();
        resumeListPanel.revalidate();
        resumeListPanel.repaint();
        refreshRecentlyViewedPanel();
    }

    //creates a row with all the resume information
    private JPanel createResumeItemPanel(String resumeName, Resume resume) {

        //create one row panel for a resume
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        //resume name
        JLabel nameLabel = new JLabel(resume.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 22));
        infoPanel.add(nameLabel);

        //status panel with colored squares
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        
        JLabel submittedLabel = new JLabel("Submitted: ");
        submittedLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusPanel.add(submittedLabel);
        
        JLabel submittedSquare = new JLabel("■");
        submittedSquare.setFont(new Font("Arial", Font.BOLD, 22));
        submittedSquare.setForeground(resume.isSubmitted() ? new Color(34, 139, 34) : new Color(220, 20, 60));
        statusPanel.add(submittedSquare);
        
        JLabel separator = new JLabel(" | ");
        separator.setFont(new Font("Arial", Font.PLAIN, 16));
        statusPanel.add(separator);
        
        JLabel admittedLabel = new JLabel("Admitted: ");
        admittedLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        statusPanel.add(admittedLabel);
        
        JLabel admittedSquare = new JLabel("■");
        admittedSquare.setFont(new Font("Arial", Font.BOLD, 22));
        admittedSquare.setForeground(resume.isAdmitted() ? new Color(34, 139, 34) : new Color(220, 20, 60));
        statusPanel.add(admittedSquare);
        
        infoPanel.add(statusPanel);

        panel.add(infoPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        //open button
        JButton openButton = new JButton("Open");
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openResumePdf(resume);
            }
        });
        buttonPanel.add(openButton);

        //toggle submitted status for confirm
        JButton submittedButton = new JButton("Submitted");
        submittedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Boolean pendingValue = pendingSubmitted.get(resumeName);
                boolean currentValue;
                if (pendingValue != null) {
                    currentValue = pendingValue;
                } else {
                    currentValue = resume.isSubmitted();
                }
                boolean toggledValue = !currentValue;
                pendingSubmitted.put(resumeName, toggledValue);
                updateConfirmButtonText();
            }
        });
        buttonPanel.add(submittedButton);

        //toggle admitted status for confirm
        JButton admittedButton = new JButton("Admitted");
        admittedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Boolean pendingValue = pendingAdmitted.get(resumeName);
                boolean currentValue;
                if (pendingValue != null) {
                    currentValue = pendingValue;
                } else {
                    currentValue = resume.isAdmitted();
                }
                boolean toggledValue = !currentValue;
                pendingAdmitted.put(resumeName, toggledValue);
                updateConfirmButtonText();
            }
        });
        buttonPanel.add(admittedButton);

        //mark or unmark resume for delete
        JButton deleteButton = new JButton("Select Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (pendingDelete.contains(resumeName)) {
                    pendingDelete.remove(resumeName);
                    deleteButton.setText("Select Delete");
                } else {
                    pendingDelete.add(resumeName);
                    deleteButton.setText("Delete ✓");
                }
                updateConfirmButtonText();
            }
        });
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    //applys all changes the user makes when confirm is hit
    private void applyPendingChanges() {
        //count how many pending updates exist
        int totalChanges = pendingSubmitted.size() + pendingAdmitted.size() + pendingDelete.size();
        if (totalChanges == 0) {
            JOptionPane.showMessageDialog(this, "No changes selected.");
            return;
        }

        //collect all resume names that need updates, use a set to prevent duplicates  
        Set<String> allTargets = new HashSet<String>();
        allTargets.addAll(pendingSubmitted.keySet());
        allTargets.addAll(pendingAdmitted.keySet());
        allTargets.addAll(pendingDelete);

        //apply delete or status changes for each target
        for (String resumeName : allTargets) {
            if (pendingDelete.contains(resumeName)) {
                deleteResumeFiles(resumeName);
                continue;
            }

            Resume resume = DataManager.loadResume(resumeName);
            if (resume == null) {
                continue;
            }

            if (pendingSubmitted.containsKey(resumeName)) {
                resume.setSubmitted(pendingSubmitted.get(resumeName));
            }
            if (pendingAdmitted.containsKey(resumeName)) {
                resume.setAdmitted(pendingAdmitted.get(resumeName));
            }

            DataManager.saveResume(resume);
        }

        //refresh after completing changes  
        pendingSubmitted.clear();
        pendingAdmitted.clear();
        pendingDelete.clear();

        JOptionPane.showMessageDialog(this, "Changes applied successfully.");
        refreshResumeList();
    }

    //delete data and pdf files for a resume
    private void deleteResumeFiles(String resumeName) {
        File dataFile = new File("Data/resume_" + resumeName + ".dat");
        Resume resume = DataManager.loadResume(resumeName);

        removeFromRecentlyViewed(resumeName);

        if (dataFile.exists()) {
            dataFile.delete();
        }

        if (resume != null) {
            String pdfFileName = resume.getName().replace(" ", "_") + "_Resume.pdf";
            File pdfFile = new File("Resumes/" + pdfFileName);
            if (pdfFile.exists()) {
                pdfFile.delete();
            }
        }
    }
   //remove deleted resumes from recently viewed list
    private void removeFromRecentlyViewed(String resumeName) {
        Iterator<Resume> iterator = recentlyViewed.iterator();
        while (iterator.hasNext()) {
            Resume existing = iterator.next();
            if (existing.getName().equals(resumeName)) {
                iterator.remove();
            }
        }
    }
    //show number of pending changes on the button
    private void updateConfirmButtonText() {
        int changeCount = pendingSubmitted.size() + pendingAdmitted.size() + pendingDelete.size();
        if (changeCount == 0) {
            confirmButton.setText("Confirm");
        } else {
            confirmButton.setText("Confirm (" + changeCount + ")");
        }
    }

    //open selected resume as a pdf and add it to recently viewed
    private void openResumePdf(Resume resume) {
        String resumeName = resume.getName();
        File file = new File("Resumes/" + resumeName.replace(" ", "_") + "_Resume.pdf");
        try {
            if (!(file.exists())) {
                JOptionPane.showMessageDialog(this,
                    "PDF not found for this resume.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Cannot open file on this system", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            addToRecentlyViewed(resume);
            refreshRecentlyViewedPanel();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error opening file: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    //refresh recently viewed ui panel
    private void refreshRecentlyViewedPanel() {
        recentlyViewedPanel.removeAll();

        if (recentlyViewed.isEmpty()) {
            JLabel emptyLabel = new JLabel("No recently viewed resumes");
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            recentlyViewedPanel.add(emptyLabel);
        } else {
            for (Resume resume : recentlyViewed) {
                JLabel resumeLabel = new JLabel("• " + resume.getName());
                resumeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                recentlyViewedPanel.add(resumeLabel);
                recentlyViewedPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        recentlyViewedPanel.revalidate();
        recentlyViewedPanel.repaint();
    }


     //move the most recently viewed resume to the front of recerntly viewed
    private void addToRecentlyViewed(Resume resume) {

        Iterator<Resume> iterator = recentlyViewed.iterator();
        while (iterator.hasNext()) {
            Resume existing = iterator.next();
            if (existing.getName().equals(resume.getName())) {
                iterator.remove();
                break;
            }
        }
        //keeping a max size of 5
        if (recentlyViewed.size() >= 5) {
            recentlyViewed.removeLast();
        }
        recentlyViewed.addFirst(resume);
    }
}

