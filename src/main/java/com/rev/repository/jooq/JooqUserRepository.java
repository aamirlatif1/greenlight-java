package com.rev.repository.jooq;

import com.rev.jooq.tables.records.UsersRecord;
import com.rev.model.User;
import com.rev.repository.DuplicateEmailException;
import com.rev.repository.UserRepository;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.jooq.exception.DataAccessException;

import java.sql.SQLException;
import java.util.Optional;

import static com.rev.jooq.Tables.USERS;

public class JooqUserRepository implements UserRepository {

    private static final RecordMapper<UsersRecord, User> TO_USER = r -> new User(
            r.getId(), r.getCreatedAt(), r.getName(), r.getEmail(), r.getPasswordHash(), r.getActivated(), r.getVersion());

    private final DSLContext dsl;

    public JooqUserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return dsl.selectFrom(USERS)
                .where(USERS.EMAIL.eq(email))
                .fetchOptional(TO_USER);
    }

    @Override
    public User save(User user) {
        try {
            return dsl.insertInto(USERS, USERS.NAME, USERS.EMAIL, USERS.PASSWORD_HASH, USERS.ACTIVATED)
                    .values(user.name(), user.email(), user.passwordHash(), user.activated())
                    .returning(USERS.ID, USERS.CREATED_AT, USERS.NAME, USERS.EMAIL, USERS.PASSWORD_HASH, USERS.ACTIVATED, USERS.VERSION)
                    .fetchOne(TO_USER);
        } catch (DataAccessException e) {
            if (isUniqueViolation(e)) {
                throw new DuplicateEmailException();
            }
            throw e;
        }
    }

    private static boolean isUniqueViolation(DataAccessException e) {
        return e.getCause() instanceof SQLException sqlException && "23505".equals(sqlException.getSQLState());
    }

    @Override
    public Optional<User> update(User user) {
        return dsl.update(USERS)
                .set(USERS.NAME, user.name())
                .set(USERS.EMAIL, user.email())
                .set(USERS.PASSWORD_HASH, user.passwordHash())
                .set(USERS.ACTIVATED, user.activated())
                .set(USERS.VERSION, user.version() + 1)
                .where(USERS.ID.eq(user.id()).and(USERS.VERSION.eq(user.version())))
                .returning(USERS.ID, USERS.CREATED_AT, USERS.NAME, USERS.EMAIL, USERS.PASSWORD_HASH, USERS.ACTIVATED, USERS.VERSION)
                .fetchOptional(TO_USER);
    }
}
