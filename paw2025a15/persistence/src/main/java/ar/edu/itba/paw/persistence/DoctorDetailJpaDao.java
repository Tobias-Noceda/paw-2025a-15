package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.models.DoctorCoverage;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class DoctorDetailJpaDao implements DoctorDetailDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public DoctorDetail create(User doctor, String licence, SpecialtyEnum specialty) {
        final DoctorDetail dd = new DoctorDetail(doctor, licence, specialty);
        em.persist(dd);
        return dd;
    }

    @Override
    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId) {
        return Optional.ofNullable(em.find(DoctorDetail.class, doctorId));
    }

    @Override
    public void addDoctorCoverage(long doctorId, long insuranceId) {
        User doctor = em.find(User.class, doctorId);
        Insurance insurance = em.find(Insurance.class, insuranceId);
        final DoctorCoverage dc = new DoctorCoverage(doctor, insurance);
        em.persist(dc);
    }

    @Override
    public int[] addDoctorCoverages(long doctorId, List<Long> insurancesIds) {
        User doctor = em.find(User.class, doctorId);
        int[] results = new int[insurancesIds.size()];
        for (int i = 0; i < insurancesIds.size(); i++) {//TODO: preguntar si no hay un batch, por lo que vi no pareciera haber
            try{
                DoctorCoverage dc = new DoctorCoverage(doctor, em.getReference(Insurance.class, insurancesIds.get(i)));
                em.persist(dc);
                results[i] = 1;
                if (i % 50 == 0) {
                    em.flush();
                    em.clear();
                }
            }
            catch(Exception e){
                results[i] = 0;
            }
        }
        return results;
    }

    @Override
    public void removeAllCoveragesForDoctorId(long doctorId) {
        
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeAllCoveragesForDoctorId'");
    }

    @Override
    public void removeDoctorCoverages(long doctorId, List<Long> toRemove) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeDoctorCoverages'");
    }

    @Override
    public List<Insurance> getDoctorInsurancesById(long doctorId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDoctorInsurancesById'");
    }

    @Override
    public List<DoctorView> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Insurance insuranceId,
            WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDoctorsPageByParams'");
    }

    @Override
    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Insurance insuranceId,
            WeekdayEnum weekday) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTotalDoctorsByParams'");
    }
    
}
