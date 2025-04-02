package ar.edu.itba.paw.models;

import java.util.List;

public class Doctor {

    private final long id;
    private final String name;
    private final String email;
    private final List<String> workingEnsurances;
    private final String specialty;
    private final List<Schedule> schedules;

    public Doctor(
        final long id,
        final String name,
        final String email,
        final List<String> workingEnsurances,
        final String specialty,
        final List<Schedule> schedules
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.workingEnsurances = workingEnsurances;
        this.specialty = specialty;
        this.schedules = schedules;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getWorkingEnsurances() {
        return workingEnsurances;
    }

    public String getSpecialty() {
        return specialty;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void addWorkingEnsurance(String ensurance) {
        workingEnsurances.add(ensurance);
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }
}

