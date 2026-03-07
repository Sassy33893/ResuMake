import java.util.ArrayList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import java.io.File;
import java.io.IOException;

//creates and manages resume objects with PDF generation
public class Resume {
    //resume identity and saved sections
    private String name;
    private ArrayList<Experience> experiences = new ArrayList<Experience>();
    private Header header;
    private ArrayList<Skill> skills = new ArrayList<Skill>();
    private ArrayList<Education> education = new ArrayList<Education>();
    private boolean submitted = false;
    private boolean admitted = false;
    
    //build resume from selected sections
    public Resume(String name, ArrayList<Experience> experiences, Header header, ArrayList<Skill> skills, ArrayList<Education> education){
        this.name = name;
        this.experiences = experiences;
        this.header = header;
        this.skills = skills;
        this.education = education;
        this.submitted = false;
        this.admitted = false;
    }

    //getters
    public String getName() {
        return name;
    }
    public Header getHeader() {
        return header;
    }
    public ArrayList<Skill> getSkills() {
        return skills;
    }
    public boolean isSubmitted() {
        return submitted;
    }
    public boolean isAdmitted() {
        return admitted;
    }
    public ArrayList<Experience> getExperiences() {
        return experiences;
    }
    public ArrayList<Education> getEducation() {
        return education;
    }

    //setters
    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }
    public void setAdmitted(boolean admitted) {
        this.admitted = admitted;
    }
    
    //replace non-alphanumeric chars in name to prevent file name issues and handle name collisions
    public void checkName(){
        char[] nameChars = this.name.toCharArray();
        for (int i = 0; i < nameChars.length; i++) {
            if (!Character.isLetterOrDigit(nameChars[i]) && nameChars[i] != '_') {
                nameChars[i] = '_';
            }
        }
        String newName = new String(nameChars);
        
        //check for name collisions and add numbers to the end of names if needed
        File dataFile = new File("Data/resume_" + newName + ".dat");
        if (dataFile.exists()) {
            int counter = 2;
            while (new File("Data/resume_" + newName + "_" + counter + ".dat").exists()) {
                counter++;
            }
            newName = newName + "_" + counter;
        }
        
        this.name = newName;
    }

    //prepare text for pdf rendering to prevent crashes from special characters
    private String textForPDF(String text) {
        if (text == null) return "";
        
        //replace problematic characters with safe alternatives
        return text.replace("\n", " ")
                   .replace("\r", " ")
                   .replace("\t", " ")
                   .replaceAll("[^\\x20-\\x7E]", "");
    }

    //generate pdf file from resume data
    public void generatePDF(){
        try {
            //create resumes folder if it doesn't exist
            File resumesFolder = new File("Resumes");
            if (!resumesFolder.exists()) {
                resumesFolder.mkdir();
            }

            //define fonts
            PDType1Font regularFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
            PDType1Font boldFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float yPosition = 750;
            float lineHeight = 15;

            //header section (name, email, phone, location) - centered with null checks and text preparation
            String nameText = textForPDF(header.getName() != null ? header.getName() : "Unknown");
            
            //build contact text with only non-empty values
            ArrayList<String> contactParts = new ArrayList<String>();
            String phone = header.getPhoneNum() != null ? header.getPhoneNum() : "";
            String email = header.getEmail() != null ? header.getEmail() : "";
            String location = header.getLocation() != null ? header.getLocation() : "";
            if (!phone.isEmpty()) contactParts.add(phone);
            if (!email.isEmpty()) contactParts.add(email);
            if (!location.isEmpty()) contactParts.add(location);
            
            String contactText = textForPDF(String.join(" | ", contactParts));
            
            //center and display name
            contentStream.beginText();
            contentStream.setFont(boldFont, 20);
            float nameWidth = boldFont.getStringWidth(nameText) / 1000 * 20;
            float nameCenterX = (612 - nameWidth) / 2;
            contentStream.newLineAtOffset(nameCenterX, yPosition);
            contentStream.showText(nameText);
            contentStream.endText();

            yPosition -= lineHeight + 3;

            //center and display contact info
            contentStream.beginText();
            contentStream.setFont(regularFont, 11);
            float contactWidth = regularFont.getStringWidth(contactText) / 1000 * 11;
            float contactCenterX = (612 - contactWidth) / 2;
            contentStream.newLineAtOffset(contactCenterX, yPosition);
            contentStream.showText(contactText);
            contentStream.endText();

            yPosition -= lineHeight + 12;

            //education section
            contentStream.beginText();
            contentStream.setFont(boldFont, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("EDUCATION");
            contentStream.endText();

            yPosition -= lineHeight;

            contentStream.setLineWidth(0.5f);
            contentStream.moveTo(50, yPosition);
            contentStream.lineTo(560, yPosition);
            contentStream.stroke();

            yPosition -= lineHeight;

            //education section with details
            for (Education edu : education) {
                contentStream.beginText();
                contentStream.setFont(regularFont, 10);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(edu.getProgram() + " - " + edu.getSchool());
                contentStream.endText();
                yPosition -= lineHeight;

                contentStream.beginText();
                contentStream.setFont(regularFont, 9);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(edu.getLocation() + " | " + edu.getDates());
                contentStream.endText();
                yPosition -= lineHeight;
                
                //display each detail line with indent
                for (String detail : edu.getDetails()) {
                    contentStream.beginText();
                    contentStream.setFont(regularFont, 9);
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("• " + detail);
                    contentStream.endText();
                    yPosition -= lineHeight;
                }
                
                //add space before next education entry
                yPosition -= 5;
            }

            yPosition -= 5;

            //experience section
            contentStream.beginText();
            contentStream.setFont(boldFont, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("EXPERIENCE");
            contentStream.endText();

            yPosition -= lineHeight;

            contentStream.setLineWidth(0.5f);
            contentStream.moveTo(50, yPosition);
            contentStream.lineTo(560, yPosition);
            contentStream.stroke();

            yPosition -= lineHeight;

            //experience section with descriptions
            for (Experience exp : experiences) {
                contentStream.beginText();
                contentStream.setFont(regularFont, 10);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(exp.getName() + " - " + exp.getOrganization());
                contentStream.endText();
                yPosition -= lineHeight;

                contentStream.beginText();
                contentStream.setFont(regularFont, 9);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(exp.getLocation() + " | " + exp.getDates());
                contentStream.endText();
                yPosition -= lineHeight;
                
                //display each description line with indent
                for (String description : exp.getDescriptions()) {
                    contentStream.beginText();
                    contentStream.setFont(regularFont, 9);
                    contentStream.newLineAtOffset(70, yPosition);
                    contentStream.showText("• " + description);
                    contentStream.endText();
                    yPosition -= lineHeight;
                }
                
                //add space before next experience entry
                yPosition -= 5;
            }

            yPosition -= 5;

            //skills section
            contentStream.beginText();
            contentStream.setFont(boldFont, 12);
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("SKILLS");
            contentStream.endText();

            yPosition -= lineHeight;

            contentStream.setLineWidth(0.5f);
            contentStream.moveTo(50, yPosition);
            contentStream.lineTo(560, yPosition);
            contentStream.stroke();

            yPosition -= lineHeight;

            contentStream.beginText();
            contentStream.setFont(regularFont, 10);
            for (Skill skill : skills) {
                contentStream.newLineAtOffset(50, yPosition);
                //join the skill list into a single string for display
                String joinedSkills = "";
                ArrayList<String> skillStrings = skill.getSkills();
                for (int j = 0; j < skillStrings.size(); j++) {
                    joinedSkills += skillStrings.get(j);
                    if (j < skillStrings.size() - 1) {
                        joinedSkills += ", ";
                    }
                }
                contentStream.showText("• " + skill.getCategory() + ": " + joinedSkills);
                contentStream.endText();
                yPosition -= lineHeight;
                contentStream.beginText();
                contentStream.setFont(regularFont, 10);
            }
            contentStream.endText();

            contentStream.close();

            //save pdf using custom resume name
            String fileName = "Resumes/" + name.replace(" ", "_") + "_Resume.pdf";
            document.save(fileName);
            document.close();

            System.out.println("Resume generated successfully: " + fileName);
        } catch (IOException e) {
            System.out.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
