package itp341.pai.sonali.finalprojectfrontend.model;

import android.location.Location;
import android.media.Image;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sonali Pai on 11/8/2017.
 */

public class Toilet implements Serializable{

    //data members
    private long bathroomId;
    private String name;
    private String address;
    private boolean hasDisabilityAccomodations;
    private boolean requiresKey;
    private int points;
    private double longitude;
    private double latitude;

    private boolean upArrowSelected;
    private boolean downArrowSelected;

    public List<String> getComments() {
        return comments;
    }

    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}
    public void addComments(String comment) {comments.add(comment);}

    public void setLatitude(double latitude)
    {this.latitude = latitude;}
    public void setLongitude(double longitude)
    {this.longitude = longitude;}
    private List<String> comments;

    public Toilet(String nameOfLocation, String address, boolean hadDisabilityAccomodations, boolean requiresKey) {
        this.name = nameOfLocation;
        this.address = address;
        this.hasDisabilityAccomodations = hadDisabilityAccomodations;
        this.requiresKey = requiresKey;
        points = 0;
        upArrowSelected=false;
        downArrowSelected=false;
        comments = new ArrayList<String>();
    }

    public boolean isUpArrowSelected() {
        return upArrowSelected;
    }

    public void setUpArrowSelected(boolean upArrowSelected) {
        this.upArrowSelected = upArrowSelected;
    }

    public boolean isDownArrowSelected() {
        return downArrowSelected;
    }

    public void setDownArrowSelected(boolean downArrowSelected) {
        this.downArrowSelected = downArrowSelected;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }


    public boolean isHasDisabilityAccomodations() {
        return hasDisabilityAccomodations;
    }

    public void setHasDisabilityAccomodations(boolean hasDisabilityAccomodations) {
        this.hasDisabilityAccomodations = hasDisabilityAccomodations;
    }

    public boolean isRequiresKey() {
        return requiresKey;
    }

    public void setRequiresKey(boolean requiresKey) {
        this.requiresKey = requiresKey;
    }

    public long getBathroomId() {
        return bathroomId;
    }

    public void setBathroomId(long bathroomId) {
        this.bathroomId = bathroomId;
    }

    public String getNameOfLocation() {
        return name;
    }

    public boolean getDisabilityAccomodations() {
        return hasDisabilityAccomodations;
    }

    public void setNameOfLocation(String nameOfLocation) {
        this.name = nameOfLocation;
    }

    public void setDisabilityAccomodations(boolean disabilityAccomodations) {
        this.hasDisabilityAccomodations = disabilityAccomodations;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return address;
    }
}