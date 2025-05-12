package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

@Repository
public class UserJdbcDao implements UserDao{
    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("user_id"), rs.getString("user_email"), rs.getString("user_password"), rs.getString("user_name"), rs.getString("user_telephone"), UserRoleEnum.fromInt(rs.getInt("user_role")), rs.getLong("picture_id"), rs.getDate("create_date").toLocalDate(), LocaleEnum.fromInt(rs.getInt("locale")));
   
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public UserJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("users").usingGeneratedKeyColumns("user_id");
    }

    @Override
    public User create(String email, String password, String name, String telephone, UserRoleEnum role, long pictureId, LocaleEnum locale) {
        final Map<String, Object> args = new HashMap<>();
        args.put("user_email", email);
        args.put("user_password", password);
        args.put("user_name", name);
        args.put("user_telephone", telephone);
        args.put("user_role", role.ordinal());
        args.put("picture_id", pictureId);
        LocalDate createDate = LocalDate.now();
        args.put("create_date", createDate);
        args.put("locale", locale.ordinal());
        final Number user_id = jdbcInsert.executeAndReturnKey(args);
        return new User(user_id.longValue(), email, password, name, telephone, role, pictureId, createDate, locale);
    }

    @Override
    public Optional<User> getUserById(long id) {
        return jdbcTemplate.query("SELECT * FROM users WHERE user_id = ?", new Object[]  {id},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return jdbcTemplate.query("SELECT * FROM users WHERE user_email = ?", new Object[]  {email},
          new int[] {java.sql.Types.VARCHAR}, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public List<User> searchAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize) {
        if(page <= 0 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;
        StringBuilder query = new StringBuilder(
            """
                SELECT DISTINCT u.*
                FROM auth_doctors AS ad JOIN users AS u ON ad.patient_id = u.user_id
                WHERE ad.doctor_id = ?
            """
        );
        List<Object> params = new ArrayList<>();
        List<Integer> types = new ArrayList<>();
        params.add(doctorId);
        types.add(java.sql.Types.BIGINT);
        if(name != null && !name.trim().isEmpty()) {
            query.append(" AND LOWER(u.user_name) LIKE ? ");
            params.add("%" + sanitize(name) + "%");
            types.add(java.sql.Types.VARCHAR);
        }
        query.append(" LIMIT ? OFFSET ? ");
        params.add(pageSize);
        params.add(offset);
        types.add(java.sql.Types.INTEGER);
        types.add(java.sql.Types.INTEGER);

        return (List<User>) jdbcTemplate.query(query.toString(), params.toArray(), types.stream().mapToInt(i -> i).toArray(), ROW_MAPPER);
    }

    @Override
    public int searchAuthPatientsCountByDoctorIdAndName(long doctorId, String name) {
        StringBuilder query = new StringBuilder(
            """
                SELECT COUNT(DISTINCT ad.patient_id)
                FROM auth_doctors AS ad JOIN users AS u ON ad.patient_id = u.user_id
                WHERE ad.doctor_id = ?
            """
        );
        List<Object> params = new ArrayList<>();
        List<Integer> types = new ArrayList<>();
        params.add(doctorId);
        types.add(java.sql.Types.BIGINT);
        if(name != null && !name.trim().isEmpty()) {
            query.append(" AND LOWER(u.user_name) LIKE ? ");
            params.add("%" + sanitize(name) + "%");
            types.add(java.sql.Types.VARCHAR);
        }

        return jdbcTemplate.query(query.toString(), params.toArray(), types.stream().mapToInt(i -> i).toArray(), (rs, rowNum) -> rs.getInt(1)).stream().findFirst().orElse(0);
    }

    @Override
    public void changePasswordByID(long id, String password){
        String query = "UPDATE users SET user_password = ? WHERE user_id = ?";
        jdbcTemplate.update(query, 
        new Object[] {password, id},
        new int[] {java.sql.Types.VARCHAR, java.sql.Types.BIGINT});
    }

    @Override
    public void editUser(long id, String name, String telephone, long pictureId) {
        String query = "UPDATE users SET user_name = ?, picture_id = ?, user_telephone = ? WHERE user_id = ?";
        jdbcTemplate.update(query,
        new Object[] {name, pictureId, telephone, id},
        new int[] {java.sql.Types.VARCHAR, java.sql.Types.BIGINT, java.sql.Types.VARCHAR, java.sql.Types.BIGINT});
    }

    @Override
    public void updateLocale(long userId, LocaleEnum locale) {
        String query = "UPDATE users SET locale = ? WHERE user_id = ?";
        jdbcTemplate.update(query,
        new Object[] {locale.ordinal(), userId},
        new int[] {java.sql.Types.INTEGER, java.sql.Types.BIGINT});
    }

    private String sanitize(String name) {
        if (name == null) return null;
        return name
                .replace("\\", "\\\\\\")
                .replace("%", "\\\\%")
                .replace("_", "\\\\_")
                .trim()
                .toLowerCase();
    }
}
