package ar.edu.itba.paw.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.AuthDoctorDao;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

@Repository
public class AuthDoctorJdbcDao implements AuthDoctorDao{

    private final RowMapper<DoctorView> DV_ROW_MAPPER = (rs, rowNum) -> {
        DoctorView doc = new DoctorView(
            rs.getLong("doctor_id"),
            rs.getString("user_name"),
            SpecialtyEnum.fromInt(rs.getInt("doctor_specialty")),
            rs.getLong("picture_id"),
            getDoctorInsurancesById(rs.getLong("doctor_id")),
            getWeekdaysById(rs.getLong("doctor_id"))
        );
        return doc;
    };

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public AuthDoctorJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("auth_doctors");
    }

    @Override
    public void authDoctor(long patientId, long doctorId, AccessLevelEnum accessLevel) {
        if(hasAuthDoctorWithAccessLevel(patientId, doctorId, accessLevel)) return;
        if(accessLevel!=AccessLevelEnum.VIEW_BASIC && !hasAuthDoctorWithAccessLevel(patientId, doctorId, AccessLevelEnum.VIEW_BASIC)) authDoctor(patientId, doctorId, AccessLevelEnum.VIEW_BASIC);
        final Map<String, Object> args = new HashMap<>();
        args.put("doctor_id", doctorId);
        args.put("patient_id", patientId);
        args.put("access_level", accessLevel.ordinal());
        jdbcInsert.execute(args);
    }

    @Override
    public int[] authDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        String sql = "INSERT INTO auth_doctors (doctor_id, patient_id, access_level) VALUES (?, ?, ?) ";

        List<Object[]> batchArgs = accessLevels.stream()
        .map(accessLevel -> new Object[]{doctorId, patientId, accessLevel.ordinal()})
        .toList();

        return jdbcTemplate.batchUpdate(sql, batchArgs, new int[] {java.sql.Types.BIGINT, java.sql.Types.BIGINT, java.sql.Types.INTEGER});
    }

    @Override
    public List<DoctorView> getAuthDoctorsByPatientId(long id) {
        return (List<DoctorView>) jdbcTemplate.query(
                "SELECT DISTINCT dd.doctor_id, u.user_name, dd.doctor_specialty, u.picture_id FROM auth_doctors AS ad JOIN doctor_details AS dd ON ad.doctor_id = dd.doctor_id JOIN users AS u ON dd.doctor_id = u.user_id WHERE ad.patient_id = ?",
                new Object[]{id},
                new int[]{ java.sql.Types.BIGINT },
                DV_ROW_MAPPER
        );
    }

    private List<Insurance> getDoctorInsurancesById(long doctorId) {
        String sql = "SELECT insurances.* from insurances JOIN doctor_coverages ON doctor_coverages.insurance_id = insurances.insurance_id WHERE doctor_coverages.doctor_id = ?";
        return jdbcTemplate.query(sql, new Object[]{doctorId}, new int[]{java.sql.Types.BIGINT}, (rs, rowNum) -> new Insurance(rs.getLong("insurance_id"), rs.getString("insurance_name"), rs.getLong("picture_id")));
    }

    private List<WeekdayEnum> getWeekdaysById(long doctorId) {
        String sql = "SELECT DISTINCT shift_weekday FROM doctor_shifts WHERE doctor_id = ?";
        return jdbcTemplate.query(sql, new Object[]{doctorId}, new int[]{java.sql.Types.BIGINT}, (rs, rowNum) -> WeekdayEnum.fromInt(rs.getInt("shift_weekday")));
    }

    @Override
    public boolean hasAuthDoctor(long patientId, long doctorId) {
        return jdbcTemplate.query("SELECT 1 FROM auth_doctors WHERE doctor_id = ? AND patient_id = ? LIMIT 1", new Object[]{doctorId, patientId}, new int[]{java.sql.Types.BIGINT, java.sql.Types.BIGINT}, (rs, rowNum)-> rs.next()).stream().findFirst().isPresent() ;
    }

    @Override
    public boolean hasAuthDoctorWithAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel) {
        return jdbcTemplate.query("SELECT 1 FROM auth_doctors WHERE doctor_id = ? AND patient_id = ? AND access_level = ? LIMIT 1", new Object[]{doctorId, patientId, accessLevel.ordinal()}, new int[]{java.sql.Types.BIGINT, java.sql.Types.BIGINT, java.sql.Types.INTEGER}, (rs, rowNum)-> rs.next()).stream().findFirst().isPresent() ;
    }

    @Override
    public void unauthDoctorAllAccessLevels(long patientId, long doctorId) {
        if(!hasAuthDoctor(patientId, doctorId)) return;
        String query = "DELETE FROM auth_doctors WHERE patient_id = ? AND doctor_id = ?";
        jdbcTemplate.update(query, 
        new Object[] {patientId, doctorId},
        new int[] {java.sql.Types.BIGINT, java.sql.Types.BIGINT});
    }

    @Override
    public void unauthDoctorByAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel) {
        if(accessLevel!=AccessLevelEnum.VIEW_BASIC){
            String query = "DELETE FROM auth_doctors WHERE patient_id = ? AND doctor_id = ? AND access_level = ?";
            jdbcTemplate.update(query,
            new Object[] {patientId, doctorId, accessLevel.ordinal()},
            new int[] {java.sql.Types.BIGINT, java.sql.Types.BIGINT, java.sql.Types.INTEGER});
        }
        else unauthDoctorAllAccessLevels(patientId, doctorId);
    }

    @Override
    public int[] unauthDoctorForLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        String sql = "DELETE FROM auth_doctors WHERE patient_id = ? AND doctor_id = ? AND access_level = ? ";

        List<Object[]> batchArgs = accessLevels.stream()
        .map(accessLevel -> new Object[]{patientId, doctorId, accessLevel.ordinal()})
        .toList();

        return jdbcTemplate.batchUpdate(sql, batchArgs, 
        new int[] {java.sql.Types.BIGINT, java.sql.Types.BIGINT, java.sql.Types.INTEGER});
    }

    @Override
    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId) {
        String sql = "SELECT DISTINCT access_level FROM auth_doctors WHERE doctor_id = ? AND patient_id = ?";
        return jdbcTemplate.query(sql, new Object[]{doctorId, patientId}, new int[]{java.sql.Types.BIGINT, java.sql.Types.BIGINT}, (rs, rowNum) -> AccessLevelEnum.fromInt(rs.getInt("access_level")));
    }
    
}
