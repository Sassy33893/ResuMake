//stores personal contact and header information
//there is only one header object at a time, new header objects overwrite the old one
public class Header {
    //header variables
    private String email;
    private String location;
    private String phoneNum;
    private String name;

    //build header from input fields
    public Header(String email, String location,String phoneNum, String name){
        this.email= email;
        this.location = location;
        this.phoneNum=phoneNum;
        this.name = name;
    }

    //rebuild header from .dat file
    public Header(String data){
        String[] dataArr = data.split("\\|");
        email = dataArr[0];
        location = dataArr[1];
        phoneNum = dataArr[2];
        name = dataArr[3];
    }

    //convert header to string for file storage
    public String toString(){
        String data="";
        data+=email+"|"+location+"|"+phoneNum+"|"+name;
        return data;
    }
    
    //getters
    public String getPhoneNum() {
        return phoneNum;
    }
    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    public String getLocation() {
        return location;
    }
}
