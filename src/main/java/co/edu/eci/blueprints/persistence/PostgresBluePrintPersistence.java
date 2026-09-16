package edu.eci.arsw.blueprints.persistence;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class PostgresBluePrintPersistence implements BlueprintPersistence {

    private final JdbcTemplate jdbc;

    public PostgresBluePrintPersistence(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        data();
    }

    private void data() {
        insertIfAbsent("john", "house", List.of(new Point(0,0), new Point(10,0), new Point(10,10), new Point(0,10)));
        insertIfAbsent("john", "garage", List.of(new Point(5,5), new Point(15,5), new Point(15,15)));
        insertIfAbsent("jane", "garden", List.of(new Point(2,2), new Point(3,4), new Point(6,7)));
    }

    private void insertIfAbsent(String author, String name, List<Point> points) {
        if (!exists(author, name)) {
            jdbc.update("INSERT INTO blueprints (author, name, points) VALUES (?, ?, ?)",
                    author, name, pointsToText(points));
        }
    }

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        String k = keyOf(bp);
        if (exists(bp.getAuthor(), bp.getName())) {
            throw new BlueprintPersistenceException("Blueprint already exists: " + k);
        }
        jdbc.update("INSERT INTO blueprints (author, name, points) VALUES (?, ?, ?)",
                bp.getAuthor(), bp.getName(), pointsToText(bp.getPoints()));
    }

    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        try {
            return jdbc.queryForObject(
                    "SELECT author, name, points FROM blueprints WHERE author = ? AND name = ?",
                    rowMapper, author, name);
        } catch (EmptyResultDataAccessException e) {
            throw new BlueprintNotFoundException("Blueprint not found: %s/%s".formatted(author, name));
        }
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        List<Blueprint> found = jdbc.query(
                "SELECT author, name, points FROM blueprints WHERE author = ?", rowMapper, author);
        if (found.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints for author: " + author);
        }
        return new HashSet<>(found);
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        return new HashSet<>(jdbc.query("SELECT author, name, points FROM blueprints", rowMapper));
    }

    @Override
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        Blueprint bp = getBlueprint(author, name);
        bp.addPoint(new Point(x, y));
        jdbc.update("UPDATE blueprints SET points = ? WHERE author = ? AND name = ?",
                pointsToText(bp.getPoints()), author, name);
    }

    private String keyOf(Blueprint bp) { return bp.getAuthor() + ":" + bp.getName(); }

    private boolean exists(String author, String name) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM blueprints WHERE author = ? AND name = ?", Integer.class, author, name);
        return count != null && count > 0;
    }

    private final RowMapper<Blueprint> rowMapper = (rs, rowNum) -> {
        String author = rs.getString("author");
        String name = rs.getString("name");
        List<Point> points = textToPoints(rs.getString("points"));
        return new Blueprint(author, name, points);
    };

    private String pointsToText(List<Point> points) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            if (i > 0) sb.append(";");
            sb.append(points.get(i).x()).append(",").append(points.get(i).y());
        }
        return sb.toString();
    }

    private List<Point> textToPoints(String text) {
        List<Point> points = new ArrayList<>();
        if (text == null || text.isBlank()) return points;
        for (String pair : text.split(";")) {
            String[] xy = pair.split(",");
            points.add(new Point(Integer.parseInt(xy[0]), Integer.parseInt(xy[1])));
        }
        return points;
    }
}