package ar.edu.itba.paw.interfaces.services;

import java.util.List;

import ar.edu.itba.paw.models.enums.AccessLevelEnum;

public interface AuthDoctorService {
    public void toggleAuthDoctor(long patientId, long doctorId);

    public void updateAuthDoctor(long patientId, long doctorId, List<AccessLevelEnum> accessLevels);

    public boolean hasAuthDoctor(long patientId, long doctorId);

    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId);
}
