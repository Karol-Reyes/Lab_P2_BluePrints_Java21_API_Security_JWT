package co.edu.eci.blueprints.api;

import java.util.List;
import co.edu.eci.blueprints.model.Point;

public record NewBlueprintRequest(String author, String name, List<Point> points) {

}