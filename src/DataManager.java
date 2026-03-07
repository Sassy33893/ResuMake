import java.util.*;
import java.io.*;


//a class that contains methods to read and write .dat files to save and load the users data
//there are no constructors since it is not used to create objects

public class DataManager {
	
    //reading .dat file into a string arraylist to recreate objects later
    public static ArrayList<String> readData(String filePath){
        ArrayList<String> data = new ArrayList<String>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String lineCur="";
            while ((lineCur = reader.readLine()) != null) {
                data.add(lineCur);
            }
        } 
        catch (IOException e) {
            System.out.println("Error in reading the file");
        }
        return data;
    }
	//taking an object and writing it into the .dat files
    public static void writeData(String filePath, ArrayList<String> input){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))){
            for (String line: input) {
                writer.write(line);
                writer.newLine();
            }
        } 
        catch (IOException e) {
            System.out.println("Error in writing to the file");
        }
    }
    //extra method for the header class so that it overwrites instead of adds
    public static void writeHeader(String filePath, String headerData) {
        try {
            File headerFile = new File(filePath);
            File parentDir = headerFile.getParentFile();
            if ((parentDir != null)&&(!parentDir.exists())) {
                parentDir.mkdirs();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(headerFile, false))) {
                if (headerData == null) {
                    writer.write("");
                } else {
                    writer.write(headerData);
                }
                writer.newLine();
            }
        } 
        catch (IOException e) {
            System.out.println("Error in writing header to the file");
        }
    }

    //save resume object to individual file data/resume_[name].dat in text format
    public static void saveResume(Resume resume) {
        //create data folder if it doesn't exist
        File dataFolder = new File("Data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        
        String filePath = "Data/resume_" + resume.getName() + ".dat";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            //write resume name (first line)
            writer.write("NAME," + resume.getName());
            writer.newLine();
            
            //write status flags
            writer.write("SUBMITTED," + resume.isSubmitted());
            writer.newLine();
            writer.write("ADMITTED," + resume.isAdmitted());
            writer.newLine();
            
            //write header
            writer.write("HEADER," + resume.getHeader().toString().replaceAll(",$", ""));
            writer.newLine();
            
            //write skills
            for (Skill skill : resume.getSkills()) {
                writer.write("SKILL," + skill.toString().replaceAll(",$", ""));
                writer.newLine();
            }
            
            //write experiences
            for (Experience exp : resume.getExperiences()) {
                writer.write("EXPERIENCE," + exp.toString().replaceAll(",$", ""));
                writer.newLine();
            }
            
            //write education
            for (Education edu : resume.getEducation()) {
                writer.write("EDUCATION," + edu.toString().replaceAll(",$", ""));
                writer.newLine();
            }
            
            System.out.println("Resume saved successfully: " + filePath);
        } 
        catch (IOException e) {
            System.out.println("Error saving resume: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //load resume object from file data/resume_[name].dat
    public static Resume loadResume(String sanitizedName) {
        String filePath = "Data/resume_" + sanitizedName + ".dat";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String name = "";
            boolean submitted = false;
            boolean admitted = false;
            Header header = null;
            ArrayList<Skill> skills = new ArrayList<>();
            ArrayList<Experience> experiences = new ArrayList<>();
            ArrayList<Education> education = new ArrayList<>();
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("NAME,")) {
                    name = line.substring(5);
                } else if (line.startsWith("SUBMITTED,")) {
                    submitted = Boolean.parseBoolean(line.substring(10));
                } else if (line.startsWith("ADMITTED,")) {
                    admitted = Boolean.parseBoolean(line.substring(9));
                } else if (line.startsWith("HEADER,")) {
                    String headerData = line.substring(7);
                    header = new Header(headerData);
                } else if (line.startsWith("SKILL,")) {
                    String skillData = line.substring(6);
                    skills.add(new Skill(skillData));
                } else if (line.startsWith("EXPERIENCE,")) {
                    String expData = line.substring(11);
                    experiences.add(new Experience(expData));
                } else if (line.startsWith("EDUCATION,")) {
                    String eduData = line.substring(10);
                    education.add(new Education(eduData));
                }
            }
            
            if (header != null && !name.isEmpty()) {
                Resume resume = new Resume(name, experiences, header, skills, education);
                resume.setSubmitted(submitted);
                resume.setAdmitted(admitted);
                return resume;
            }
        } 
        catch (IOException e) {
            System.out.println("Error loading resume: " + e.getMessage());
        }
        return null;
    }

}
