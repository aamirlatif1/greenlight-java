package com.rev.repository.jooq;

import com.rev.jooq.tables.records.MovieRecord;
import com.rev.model.Movie;
import com.rev.repository.MovieRepository;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;

import java.util.List;
import java.util.Optional;

import static com.rev.jooq.Tables.MOVIE;

public class JooqMovieRepository implements MovieRepository {

    private static final RecordMapper<MovieRecord, Movie> TO_MOVIE = r -> new Movie(
            r.getId(), r.getTitle(), r.getYear(), r.getRuntime(), List.of(r.getGenres()), r.getVersion());

    private final DSLContext dsl;

    public JooqMovieRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public List<Movie> findAll() {
        return dsl.selectFrom(MOVIE).fetch(TO_MOVIE);
    }

    @Override
    public Optional<Movie> findById(long id) {
        return dsl.selectFrom(MOVIE)
                .where(MOVIE.ID.eq(id))
                .fetchOptional(TO_MOVIE);
    }

    @Override
    public Movie save(Movie movie) {
        return dsl.insertInto(MOVIE, MOVIE.TITLE, MOVIE.YEAR, MOVIE.RUNTIME, MOVIE.GENRES)
                .values(movie.title(), movie.year(), movie.runtime(), movie.genres().toArray(new String[0]))
                .returning(MOVIE.ID, MOVIE.TITLE, MOVIE.YEAR, MOVIE.RUNTIME, MOVIE.GENRES, MOVIE.VERSION)
                .fetchOne(TO_MOVIE);
    }

    @Override
    public Optional<Movie> update(Movie movie) {
        return dsl.update(MOVIE)
                .set(MOVIE.TITLE, movie.title())
                .set(MOVIE.YEAR, movie.year())
                .set(MOVIE.RUNTIME, movie.runtime())
                .set(MOVIE.GENRES, movie.genres().toArray(new String[0]))
                .set(MOVIE.VERSION, movie.version() + 1)
                .where(MOVIE.ID.eq(movie.id()).and(MOVIE.VERSION.eq(movie.version())))
                .returning(MOVIE.ID, MOVIE.TITLE, MOVIE.YEAR, MOVIE.RUNTIME, MOVIE.GENRES, MOVIE.VERSION)
                .fetchOptional(TO_MOVIE);
    }

    @Override
    public boolean deleteById(long id) {
        return dsl.deleteFrom(MOVIE).where(MOVIE.ID.eq(id)).execute() > 0;
    }
}
