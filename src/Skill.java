import java.util.ArrayList;

//stores skill information for resumes, contains a list under a category
public class Skill {

    //skill variables
    private String category;
    private ArrayList<String> skills = new ArrayList<String>();

    //build skill object from input 
    public Skill(String category, ArrayList<String> skills){
        this.category = category;
        this.skills = skills;
    }

    //rebuild skill object from .dat file 
    public Skill(String data){
        String[] dataArr = data.split("\\|");
        category = dataArr[0];
        for(int i=1;i<=dataArr.length-1;i++){
            skills.add(dataArr[i]);
        }
    }

    //getters
    public String getCategory() {
        return category;
    }
    public ArrayList<String> getSkills() {
        return skills;
    }

    //convert skill object to string for storage 
    public String toString(){
        String data="";
        data+=category+"|";
        for(String skill: skills){
            data+=skill+"|";
        }
        return data.replaceAll("\\|$", "");
    }
}
