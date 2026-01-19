package ar.edu.itba.paw.webapp.dto.output;

import java.net.URI;
import java.util.List;

public class LinkDTO {

    private URI self;
    private URI picture;
    private URI image;
    private URI insurance;
    private URI insurances;
    private URI doctor;
    private URI doctors;
    private URI patient;
    private URI uploader;
    private URI authDoctors;
    private URI schedule;
    private List<URI> files;

    public URI getSelf() {
        return self;
    }

    public LinkDTO setSelf(URI self) {
        this.self = self;
        return this;
    }

    public URI getPicture() {
        return picture;
    }

    public LinkDTO setPicture(URI picture) {
        this.picture = picture;
        return this;
    }

    public URI getInsurance(){
        return insurance;
    }

    public LinkDTO setInsurance(URI insurance){
        this.insurance = insurance;
        return this;
    }

    public URI getDoctor(){
        return doctor;
    }

    public LinkDTO setDoctor(URI doctor){
        this.doctor = doctor;
        return this;
    }

    public URI getDoctors(){
        return doctors;
    }

    public LinkDTO setDoctors(URI doctors){
        this.doctors = doctors;
        return this;
    }

    public List<URI> getFiles(){
        return files;
    }

    public LinkDTO setFiles(List<URI> files){
        this.files = files;
        return this;
    }

    public URI getPatient(){
        return patient;
    }

    public LinkDTO setPatient(URI patient){
        this.patient = patient;
        return this;
    }

    public URI getUploader(){
        return uploader;
    }

    public LinkDTO setUploader(URI uploader){
        this.uploader = uploader;
        return this;
    }

    public URI getAuthDoctors() {
        return authDoctors;
    }

    public LinkDTO setAuthDoctors(URI authDoctors) {
        this.authDoctors = authDoctors;
        return this;
    }

    public URI getImage() {
        return image;
    }

    public LinkDTO setImage(URI image) {
        this.image = image;
        return this;
    }

    public URI getSchedule() {
        return schedule;
    }

    public LinkDTO setSchedule(URI schedule) {
        this.schedule = schedule;
        return this;
    }

    public URI getInsurances() {
        return insurances;
    }

    public LinkDTO setInsurances(URI insurances) {
        this.insurances = insurances;
        return this;
    }
}
