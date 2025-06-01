package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;

import ar.edu.itba.paw.models.enums.AccessLevelEnum;

public interface AuthDoctorDao {
    public boolean hasAuthDoctor(long patientId, long doctorId);

    public boolean hasAuthDoctorWithAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel);

    public void authDoctor(long patientId, long doctorId, AccessLevelEnum accessLevel);

    public int[] authDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels);

    public void unauthDoctorAllAccessLevels(long patientId, long doctorId);

    public void unauthDoctorByAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel);

    public int[] unauthDoctorForLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels);

    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId);
}
