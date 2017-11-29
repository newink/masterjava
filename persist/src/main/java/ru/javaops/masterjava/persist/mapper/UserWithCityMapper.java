package ru.javaops.masterjava.persist.mapper;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import ru.javaops.masterjava.persist.model.City;
import ru.javaops.masterjava.persist.model.User;
import ru.javaops.masterjava.persist.model.UserFlag;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserWithCityMapper implements ResultSetMapper<User> {
    @Override
    public User map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        User user = new User(r.getInt("user_id"), r.getString("full_name"), r.getString("email"),
                UserFlag.valueOf(r.getString("flag")), r.getInt("city_id"));
        City city = new City(r.getInt("city_id"), r.getString("city_mnemonic"), r.getString("city_name"));
        user.setCity(city);
        return user;
    }
}
