package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
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
import ar.edu.itba.paw.models.LocaleEnum;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.UserRoleEnum;

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
    public List<User> getAuthPatientsByDoctorId(long id) {
        return (List<User>) jdbcTemplate.query(
                "SELECT u.* FROM auth_doctors AS ad JOIN users AS u ON ad.patient_id = u.user_id WHERE ad.doctor_id = ?",
                new Object[]{id},
                new int[]{ java.sql.Types.BIGINT },
                ROW_MAPPER
        );
    }

    @Override
    public void changePassword(String email, String password) {
        jdbcTemplate.update("UPDATE users SET user_password = ? WHERE user_email = ?", password, email);
    }

    @Override
    public void changePasswordByID(long id, String password){
        jdbcTemplate.update("UPDATE users SET user_password = ? WHERE user_id = ?", password, id);
    }

    @Override
    public void UpdatePhoneNumber(long id, String number) {
        jdbcTemplate.update("UPDATE users SET user_telephone = ? WHERE user_id = ?", number, id);
    }

    @Override
    public void editUser(long id, String name, String telephone, long pictureId) {
        jdbcTemplate.update("UPDATE users SET user_name = ?, picture_id = ?, user_telephone = ? WHERE user_id = ?", name, pictureId, telephone, id);
    }


    @Override
    public List<User> searchAuthPatientsByDoctorIdAndName(long doctorId, String name) {//TODO añadir validación input,no se si aca o en el service, de que solo sean chars alfanumericos por sqlinjection
        if(name == null || name.trim().isEmpty()) return Collections.emptyList();
        if(name.contains(";") || name.contains("--") || name.contains("'")) return Collections.emptyList();//TODO hotfix prevention, should be changed
        return (List<User>) jdbcTemplate.query(
                "SELECT u.* FROM auth_doctors AS ad JOIN users AS u ON ad.patient_id = u.user_id WHERE ad.doctor_id = ? AND u.user_name LIKE ?",
                new Object[]{doctorId, "%" + name.trim() + "%"},
                new int[]{ java.sql.Types.BIGINT, java.sql.Types.VARCHAR},
                ROW_MAPPER
        );
    }
}
