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

import ar.edu.itba.paw.interfaces.persistence.InsuranceDao;
import ar.edu.itba.paw.models.Insurance;

@Repository
public class InsuranceJdbcDao implements InsuranceDao{

    private static final RowMapper<Insurance> ROW_MAPPER = (rs, rowNum) -> new Insurance(rs.getLong("insurance_id"), rs.getString("insurance_name"), rs.getLong("picture_id"));
   
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public InsuranceJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("insurances").usingGeneratedKeyColumns("insurance_id");
    }

    @Override
    public Insurance create(String name, long pictureId) {
        final Map<String, Object> args = new HashMap<>();
        args.put("insurance_name", name);
        args.put("picture_id", pictureId);
        final Number insurance_id = jdbcInsert.executeAndReturnKey(args);
        return new Insurance(insurance_id.longValue(), name, pictureId);
    }

    @Override//TODO este tytpe check tiene que estar en todas las querys, si no es aca hay otros archivos donde no esta
    public void edit(long id, String name, long pictureId) {
        String sql = "UPDATE insurances SET insurance_name = ?, picture_id = ? WHERE insurance_id = ?";
        jdbcTemplate.update(sql,
        new Object[] {name, pictureId, id},
        new int[] {java.sql.Types.VARCHAR, java.sql.Types.BIGINT, java.sql.Types.BIGINT});
    }

    @Override
    public Optional<Insurance> getInsuranceById(long id) {
        return jdbcTemplate.query("SELECT * FROM insurances WHERE insurance_id = ?", new Object[]  {id},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public List<Insurance> getAllInsurances() {
        String sql = "SELECT * from insurances";
        return jdbcTemplate.query(sql, ROW_MAPPER);
    }

    @Override
    public Optional<Insurance> getInsuranceByName(String name) {
        return jdbcTemplate.query("SELECT * FROM insurances WHERE insurance_name = ?", new Object[]  {sanitize(name)},
            new int[] {java.sql.Types.VARCHAR}, ROW_MAPPER).stream().findFirst();
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
