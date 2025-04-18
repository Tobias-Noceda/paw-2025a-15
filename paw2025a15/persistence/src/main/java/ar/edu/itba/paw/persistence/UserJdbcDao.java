package ar.edu.itba.paw.persistence;

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

@Repository
public class UserJdbcDao implements UserDao{
    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(rs.getLong("user_id"), rs.getString("user_email"), rs.getString("user_password"), rs.getString("user_name"), rs.getLong("picture_id"));
   
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public UserJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("users").usingGeneratedKeyColumns("user_id");
    }

    @Override
    public User create(String email, String password, String name, long pictureId) {
        final Map<String, Object> args = new HashMap<>();
        args.put("user_email", email);
        args.put("user_password", password);
        args.put("user_name", name);
        args.put("picture_id", pictureId);
        final Number user_id = jdbcInsert.executeAndReturnKey(args);
        return new User(user_id.longValue(), email, password, name, pictureId);
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
}
