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

    private JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert jdbcInsert;

    private final static RowMapper<User> ROW_MAPPER =
             (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("email"), rs.getString("password"));

    @Autowired
    public UserJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        this.jdbcInsert = new SimpleJdbcInsert(ds)
                 .usingGeneratedKeyColumns("id")
                 .withTableName("users");
    }

    @Override
    public Optional<User> findById(long id) {
        final List<User> list = jdbcTemplate.query("SELECT * FROM users WHERE id = ?", ROW_MAPPER, id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public User create(String email, String password){
        final Map<String, Object> args = new HashMap<>();
        args.put("email", email);
        args.put("password", password);
        final Number userId = jdbcInsert.executeAndReturnKey(args);
        return new User(userId.longValue(), email, password);
    }

}
