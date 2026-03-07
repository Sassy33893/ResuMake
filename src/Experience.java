import java.util.ArrayList;

//stores work or volunteer experience information for resume
public class Experience {

    private String name;
    private String organization;
    private String location;
    private String dates;
    private ArrayList<String> descriptions = new ArrayList<String>();

    //building the object from infoadder
    public Experience(String name, String organization, String location, String dates, ArrayList<String> descriptions){
        this.name = name;
        this.organization=organization;
        this.location=location;
        this.dates=dates;
        this.descriptions=descriptions;
    }
    //reconstructing the object from the data file
    public Experience(String data){
        String[] dataArr = data.split("\\|");
        name = dataArr[0];
        organization = dataArr[1];
        location = dataArr[2];
        dates = dataArr[3];
        for(int i=4;i<=dataArr.length-1;i++){
            descriptions.add(dataArr[i]);
        }
    }
    //getters 
    public String getName() {
        return name;
    }
    public String getOrganization() {
        return organization;
    }
    public String getLocation() {
        return location;
    }
    public String getDates() {
        return dates;
    }
    public ArrayList<String> getDescriptions() {
        return descriptions;
    }
    
    //tostring used to write to the data file 
    public String toString(){
        String data="";
        data+=name+"|"+organization+"|"+location+"|"+dates+"|";
        for(String description: descriptions){
            data+=description+"|";
        }
        return data.replaceAll("\\|$", "");
    }
}
