package ar.edu.itba.paw.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.PatientDetailDao;
import ar.edu.itba.paw.models.BloodTypeEnum;
import ar.edu.itba.paw.models.PatientDetail;

@Repository
public class PatientDetailJdbcDao implements PatientDetailDao{

    private static final RowMapper<PatientDetail> ROW_MAPPER = (rs, rowNum) -> new PatientDetail(rs.getLong("patient_id"), rs.getInt("patient_age"), BloodTypeEnum.fromString(rs.getString("patient_blood_type")), rs.getDouble("patient_height"), rs.getDouble("patient_weight"), rs.getBoolean("patient_smokes"), rs.getBoolean("patient_drinks"), rs.getString("patient_meds"), rs.getString("patient_conditions"), rs.getString("patient_allergies"), rs.getString("patient_diet"), rs.getString("patient_hobbies"), rs.getString("patient_job"));

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public PatientDetailJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("patient_details");
    }

    @Override
    public PatientDetail create(long patientId, Integer age, BloodTypeEnum bloodType, Double height, Double weight,
            Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet,
            String hobbies, String job) {
        final Map<String, Object> args = new HashMap<>();
        args.put("patient_id", patientId);
        args.put("patient_age", age);
        args.put("patient_blood_type", bloodType != null ? bloodType.getName() : null);
        args.put("patient_height", height);
        args.put("patient_weight", weight);
        args.put("patient_smokes", smokes);
        args.put("patient_drinks", drinks);
        args.put("patient_meds", meds);
        args.put("patient_conditions", conditions);
        args.put("patient_allergies", allergies);
        args.put("patient_diet", diet);
        args.put("patient_hobbies", hobbies);
        args.put("patient_job", job);
        jdbcInsert.execute(args);
        return new PatientDetail(patientId, age, bloodType, height, weight, smokes, drinks, meds, conditions, allergies, diet, hobbies, job);
    }

    @Override
    public Optional<PatientDetail> getDetailByPatientId(long patientId) {
        return jdbcTemplate.query("SELECT * FROM patient_details WHERE doctor_id = ?", new Object[]  {patientId},
            new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }
    
}
