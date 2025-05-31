package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;

public interface AuthDoctorDao {
    public boolean hasAuthDoctor(Patient patient, Doctor doctor);

    public boolean hasAuthDoctorWithAccessLevel(Patient patient, Doctor doctor, AccessLevelEnum accessLevel);

    public void authDoctor(Patient patient, Doctor doctor, AccessLevelEnum accessLevel);

    public int[] authDoctorWithLevels(Patient patient, Doctor doctor, List<AccessLevelEnum> accessLevels);

    public void unauthDoctorAllAccessLevels(Patient patient, Doctor doctor);

    public void unauthDoctorByAccessLevel(Patient patient, Doctor doctor, AccessLevelEnum accessLevel);

    public int[] unauthDoctorForLevels(Patient patient, Doctor doctor, List<AccessLevelEnum> accessLevels);

    public List<AccessLevelEnum> getAuthAccessLevelEnums(Patient patient, Doctor doctor);
}
