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
import ar.edu.itba.paw.models.AccessLevelEnum;
import ar.edu.itba.paw.models.BloodTypeEnum;
import ar.edu.itba.paw.models.PatientDetail;
import ar.edu.itba.paw.models.PatientView;

@Repository
public class PatientDetailJdbcDao implements PatientDetailDao{

    private static final RowMapper<PatientDetail> ROW_MAPPER = (rs, rowNum) -> 
    new PatientDetail(rs.getLong("patient_id"), rs.getObject("patient_age") != null ? rs.getInt("patient_age") : null, 
    rs.getObject("patient_blood_type") != null ? BloodTypeEnum.fromInt(rs.getInt("patient_blood_type")) : null,
    rs.getObject("patient_height") != null ? rs.getDouble("patient_height") : null, 
    rs.getObject("patient_weight") != null ? rs.getDouble("patient_weight") : null, 
    rs.getObject("patient_smokes") != null ? rs.getBoolean("patient_smokes") : null, 
    rs.getObject("patient_drinks") != null ? rs.getBoolean("patient_drinks") : null,
    rs.getString("patient_meds"), rs.getString("patient_conditions"), rs.getString("patient_allergies"), 
    rs.getString("patient_diet"), rs.getString("patient_hobbies"), rs.getString("patient_job"));

    private final RowMapper<PatientView> PV_ROW_MAPPER = (rs, rowNum) -> {
        PatientView pv = new PatientView(
            rs.getLong("patient_id"), 
            rs.getString("user_email"), 
            rs.getString("user_name"), 
            rs.getString("user_telephone"), 
            rs.getLong("picture_id"),
            rs.getObject("patient_age") != null ? rs.getInt("patient_age") : null,
            rs.getObject("patient_blood_type") != null ? BloodTypeEnum.fromInt(rs.getInt("patient_blood_type")) : null,
            rs.getObject("patient_height") != null ? rs.getDouble("patient_height") : null,
            rs.getObject("patient_weight") != null ? rs.getDouble("patient_weight") : null
        );

        switch(AccessLevelEnum.fromInt(rs.getInt("access_level"))){
            case VIEW_MEDICAL -> {
                String meds = rs.getString("patient_meds");
                String conditions = rs.getString("patient_conditions");
                String allergies = rs.getString("patient_allergies");
                pv.setViewMedical(meds, conditions, allergies);
            }
            case VIEW_HABITS -> {
                Boolean smokes = rs.getObject("patient_smokes") != null ? rs.getBoolean("patient_smokes") : null;
                Boolean drinks = rs.getObject("patient_drinks") != null ? rs.getBoolean("patient_drinks") : null;
                String diet = rs.getString("patient_diet");
                pv.setViewHabits(smokes, drinks, diet);
            }
            case VIEW_SOCIAL -> {
                String hobbies = rs.getString("patient_hobbies");
                String job = rs.getString("patient_job");
                pv.setViewSocial(hobbies, job);
            }
            case VIEW_BASIC -> {}
        }

        return pv;
    };

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
        return jdbcTemplate.query("SELECT * FROM patient_details WHERE patient_id = ?", new Object[]  {patientId},
            new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public void updatePatientDetails(long patientId, Integer age, BloodTypeEnum bloodType, Double height, Double weight,
            Boolean smokes, Boolean drinks, String meds, String conditions, String allergies, String diet,
            String hobbies, String job) {
        String query = "UPDATE patient_details SET patient_age = ?, patient_blood_type = ?, patient_height = ?, patient_weight = ?, patient_smokes = ?, patient_drinks = ?, patient_meds = ?, patient_conditions = ?, patient_allergies = ?, patient_diet = ?, patient_hobbies = ?, patient_job = ? WHERE patient_id = ?";
        jdbcTemplate.update(query, age, 
            bloodType != null ? bloodType.ordinal() : null, 
            height, weight, smokes, drinks, meds, conditions, allergies, diet, hobbies, job, patientId);
    }
    
}
