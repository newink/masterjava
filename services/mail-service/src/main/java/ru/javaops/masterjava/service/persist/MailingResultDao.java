package ru.javaops.masterjava.service.persist;

import com.bertoncelj.jdbi.entitymapper.EntityMapperFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import ru.javaops.masterjava.persist.dao.AbstractDao;

import java.util.List;

@RegisterMapperFactory(EntityMapperFactory.class)
public abstract class MailingResultDao implements AbstractDao {

    @Override
    @SqlUpdate("TRUNCATE mailing_results")
    public void clean() {
    }

    @SqlUpdate("INSERT INTO mailing_results (result, reason, email)  VALUES (CAST(:result AS RESULT_TYPE), :reason, :email)")
    @GetGeneratedKeys
    public abstract int insertGeneratedId(@BindBean MailingResult mailingResult);

    @SqlQuery("SELECT * FROM mailing_results LIMIT :it")
    public abstract List<MailingResult> getAllWithLimit(@Bind Integer it);

    public void insert(MailingResult mailingResult) {
        int id = insertGeneratedId(mailingResult);
        mailingResult.setId(id);
    }
}
