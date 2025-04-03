package ar.edu.itba.paw.models;

import java.util.List;

public class Doctor {

    private  long id;
    private  String name;
    private  String email;
    private  List<String> workingEnsurances;
    private  String specialty;
    private  Schedule schedules;
    private String address;

    public Doctor(long id, String name, String email, List<String> workingEnsurances, String specialty, Schedule schedules) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.workingEnsurances = workingEnsurances;
        this.specialty = specialty;
        this.schedules = schedules;
        this.address = name;

    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getWorkingEnsurances() {
        return workingEnsurances;
    }

    public void setWorkingEnsurances(List<String> workingEnsurances) {
        this.workingEnsurances = workingEnsurances;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Schedule getSchedules() {
        return schedules;
    }

    public void setSchedules(Schedule schedules) {
        this.schedules = schedules;
    }
}

