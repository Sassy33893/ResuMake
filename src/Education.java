import java.util.ArrayList;

//stores education information for resume
public class Education {

    //education fields and extra details
    private String program;
    private String school;
    private String location;
    private String dates;
    private ArrayList<String> details = new ArrayList<String>();

    //build education object from form input
    public Education(String program, String school,String location, String dates,ArrayList<String> details){
        this.program = program;
        this.school = school;
        this.location = location;
        this.dates = dates;
        this.details = details;
    }

    //rebuild education object from .dat file
    public Education(String data){
        String[] dataArr = data.split("\\|");
        program = dataArr[0];
        school = dataArr[1];
        location = dataArr[2];
        dates = dataArr[3];
        for(int i=4;i<=dataArr.length-1;i++){
            details.add(dataArr[i]);
        }
    }

    //getters
    public String getProgram() {
        return program;
    }
    public String getSchool() {
        return school;
    } 
    public String getLocation() {
        return location;
    }
    public String getDates() {
        return dates;
    }
    public ArrayList<String> getDetails() {
        return details;
    }

    //convert education object to a string to save to the .dat file
    public String toString(){
        String data="";
        data+=program+"|"+school+"|"+location+"|"+dates+"|";
        for(String detail: details){
            data+=detail+"|";
        }
        return data.replaceAll("\\|$", "");
    }
}
