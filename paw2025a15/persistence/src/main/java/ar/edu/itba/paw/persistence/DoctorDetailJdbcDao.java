package ar.edu.itba.paw.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

@Repository
public class DoctorDetailJdbcDao implements DoctorDetailDao {

    private static final RowMapper<DoctorDetail> ROW_MAPPER = (rs, rowNum) -> new DoctorDetail(rs.getLong("doctor_id"), rs.getString("doctor_licence"), SpecialtyEnum.fromInt(rs.getInt("doctor_specialty")));

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
    public DoctorDetailJdbcDao(final DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("doctor_details");
    }

    @Override
    public DoctorDetail create(long doctorId, String doctorLicense, SpecialtyEnum specialty) {
        final Map<String, Object> args = new HashMap<>();
        args.put("doctor_id", doctorId);
        args.put("doctor_licence", doctorLicense);
        args.put("doctor_specialty", specialty.ordinal());
        jdbcInsert.execute(args);
        return new DoctorDetail(doctorId, doctorLicense, specialty);
    }

    @Override
    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId) {
        return jdbcTemplate.query("SELECT * FROM doctor_details WHERE doctor_id = ?", new Object[] {doctorId},
                new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public void addDoctorCoverage(long doctorId, long insuranceId) {
        String sql = "INSERT INTO doctor_coverages (doctor_id, insurance_id) VALUES (?, ?)";
        jdbcTemplate.update(sql,
        new Object[] {doctorId, insuranceId},
        new int[] {java.sql.Types.BIGINT, java.sql.Types.BIGINT});
    }

    @Override
    public int[] addDoctorCoverages(long doctorId, List<Long> insurancesIds) {
        String sql = "INSERT INTO doctor_coverages (doctor_id, insurance_id) VALUES (?, ?) ";

        List<Object[]> batchArgs = insurancesIds.stream()
                .map(insuranceId -> new Object[]{doctorId, insuranceId})
                .toList();

        return jdbcTemplate.batchUpdate(sql, batchArgs, new int[] {java.sql.Types.BIGINT, java.sql.Types.BIGINT});
    }

    @Override
    public void removeAllCoveragesForDoctorId(long doctorId) {
        String sql = "DELETE FROM doctor_coverages WHERE doctor_id = ?";
        jdbcTemplate.update(sql, new Object[] {doctorId}, new int[] {java.sql.Types.BIGINT});
    }

    @Override
    public void removeDoctorCoverages(long doctorId, List<Long> toRemove) {
        String sql = "DELETE FROM doctor_coverages WHERE doctor_id = ? AND insurance_id = ?";
        List<Object[]> batchArgs = toRemove.stream()
            .map(insuranceId -> new Object[]{doctorId, insuranceId})
            .toList();
        jdbcTemplate.batchUpdate(sql, batchArgs, new int[] {java.sql.Types.BIGINT, java.sql.Types.BIGINT});
    }

    @Override
    public List<Insurance> getDoctorInsurancesById(long doctorId) {
        String sql = "SELECT insurances.* from insurances JOIN doctor_coverages ON doctor_coverages.insurance_id = insurances.insurance_id WHERE doctor_coverages.doctor_id = ?";
        return jdbcTemplate.query(sql, new Object[]{doctorId}, new int[]{java.sql.Types.BIGINT}, (rs, rowNum) -> new Insurance(rs.getLong("insurance_id"), rs.getString("insurance_name"), rs.getLong("picture_id")));
    }

    @Override
    public List<DoctorView> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize) {
        if (page < 1 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;

        StringBuilder query = new StringBuilder(
                """
                    SELECT dd.doctor_id, u.user_name, dd.doctor_specialty, u.picture_id
                    FROM doctor_details AS dd JOIN users AS u ON dd.doctor_id = u.user_id
                """
        );

        List<Object> params = new ArrayList<>();
        List<Integer> types = new ArrayList<>();

        addFiltersToQuery(query, params, types, specialty, insurance, weekday);

        if (name != null && !name.trim().isEmpty()) {
            query.append(" AND u.user_name LIKE ? ");
            params.add("%" + sanitize(name) + "%");
            types.add(java.sql.Types.VARCHAR);
        }
        if(orderBy!=null) {
            switch (orderBy) {
                case M_RECENT -> query.append(" ORDER BY u.create_date ASC ");
                case L_RECENT -> query.append(" ORDER BY u.create_date DESC ");
                case M_POPULAR -> query.append(
                        """
                            LEFT JOIN (
                                SELECT ds.doctor_id, COUNT(*) AS reserved_appointments
                                FROM appointments a
                                JOIN doctor_shifts ds ON a.shift_id = ds.shift_id
                                GROUP BY ds.doctor_id
                            ) AS app_counts ON dd.doctor_id = app_counts.doctor_id
                            ORDER BY COALESCE(app_counts.reserved_appointments, 0) DESC
                        """
                );
                default -> query.append(
                        """
                            LEFT JOIN (
                                SELECT ds.doctor_id, COUNT(*) AS reserved_appointments
                                FROM appointments a
                                JOIN doctor_shifts ds ON a.shift_id = ds.shift_id
                                GROUP BY ds.doctor_id
                            ) AS app_counts ON dd.doctor_id = app_counts.doctor_id
                            ORDER BY COALESCE(app_counts.reserved_appointments, 0) ASC
                        """
                );
            }
        }

        query.append(" LIMIT ? OFFSET ? ");
        params.add(pageSize);
        types.add(java.sql.Types.INTEGER);
        params.add(offset);
        types.add(java.sql.Types.INTEGER);

        return (List<DoctorView>) jdbcTemplate.query(
                query.toString(),
                params.toArray(),
                types.stream().mapToInt(i -> i).toArray(),
                DV_ROW_MAPPER
        );
    }

    @Override
    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday) {
        StringBuilder query = new StringBuilder(
                """
                    SELECT COUNT(*)
                    FROM doctor_details AS dd JOIN users AS u ON dd.doctor_id = u.user_id
                """
        );
        List<Object> params = new ArrayList<>();
        List<Integer> types = new ArrayList<>();
        addFiltersToQuery(query, params, types, specialty, insurance, weekday);
        if(name != null && !name.trim().isEmpty()) {
            query.append(" AND u.user_name LIKE ? ");
            params.add("%" + sanitize(name) + "%");
            types.add(java.sql.Types.VARCHAR);
        }

        return jdbcTemplate.query(query.toString(), params.toArray(), types.stream().mapToInt(i -> i).toArray(), (rs, rowNum) -> rs.getInt(1)).stream().findFirst().orElse(0);
    }

    private List<WeekdayEnum> getWeekdaysById(long doctorId) {
        String sql = "SELECT DISTINCT shift_weekday FROM doctor_shifts WHERE doctor_id = ?";
        return jdbcTemplate.query(sql, new Object[]{doctorId}, new int[]{java.sql.Types.BIGINT}, (rs, rowNum) -> WeekdayEnum.fromInt(rs.getInt("shift_weekday")));
    }

    private void addFiltersToQuery(StringBuilder query, List<Object> params, List<Integer> types, SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday) {
        if(specialty != null){
            query.append(" AND dd.doctor_specialty = ? ");
            params.add(specialty.ordinal());
            types.add(java.sql.Types.INTEGER);
        }
        if(insurance != null){
            query.append(" AND u.user_id IN (SELECT dc.doctor_id FROM doctor_coverages AS dc WHERE dc.insurance_id = ?)");
            params.add(insurance.getId());
            types.add(java.sql.Types.BIGINT);
        }
        if(weekday != null){
            query.append(" AND u.user_id IN (SELECT ds.doctor_id FROM doctor_shifts AS ds WHERE ds.shift_weekday = ?) ");
            params.add(weekday.ordinal());
            types.add(java.sql.Types.INTEGER);
        }
    }

    private String sanitize(String name) {
        if (name == null) return null;
        return name
                .replace("\\", "\\\\\\")
                .replace("%", "\\\\%")
                .replace("_", "\\\\_")
                .trim();
    }
}