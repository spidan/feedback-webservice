package de.dfki.feedback_service.feedback_webservice.models;

//@ApiModel(description = "Address of the Feedback Model")
public class Location {
    //    @ApiModelProperty(notes = "Name of the Address", example = "Waldhausweg 17, 66123 Saarbrucken")
    private String name;
    //    @ApiModelProperty(notes = "Latitude of the Address", example = "49.244710")
    private double lat;
    //    @ApiModelProperty(notes = "Longitude of the Address", example = "7.021740")
    private double lng;

    public Location() {
    }

    public Location(Location location) {
        this.name = location.getName();
        this.lat = location.getLat();
        this.lng = location.getLng();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void cleanUp() {
        this.name = "";
        this.lat = 0;
        this.lng = 0;
    }
}
