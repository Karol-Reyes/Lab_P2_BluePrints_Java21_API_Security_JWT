package co.edu.eci.blueprints.api;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;
import co.edu.eci.blueprints.persistence.BlueprintNotFoundException;
import co.edu.eci.blueprints.persistence.BlueprintPersistenceException;
import co.edu.eci.blueprints.services.BlueprintsServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/blueprints")
@Tag(name = "Blueprints", description = "Endpoints de negocio protegidos por scopes")
@SecurityRequirement(name = "bearer-jwt")
public class BlueprintController {

    private final BlueprintsServices services;

    public BlueprintController(BlueprintsServices services) {
        this.services = services;
    }

    @Operation(summary = "Lista todos los blueprints", description = "Requiere el scope blueprints.read")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta exitosa"),
        @ApiResponse(responseCode = "403", description = "Token sin el scope blueprints.read")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public Set<Blueprint> list() {
        return services.getAllBlueprints();
    }

    @Operation(summary = "Lista los blueprints de un autor", description = "Requiere el scope blueprints.read")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta exitosa"),
        @ApiResponse(responseCode = "403", description = "Token sin el scope blueprints.read"),
        @ApiResponse(responseCode = "404", description = "Autor sin blueprints registrados")
    })
    // traido del anterior lab
    @GetMapping("/{author}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public Set<Blueprint> byAuthor(@PathVariable String author) {
        try {
            return services.getBlueprintsByAuthor(author);
        } catch (BlueprintNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Obtiene un blueprint específico", description = "Requiere el scope blueprints.read")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta exitosa"),
        @ApiResponse(responseCode = "403", description = "Token sin el scope blueprints.read"),
        @ApiResponse(responseCode = "404", description = "Blueprint no encontrado")
    })
    // traido del anterior lab
    @GetMapping("/{author}/{name}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public Blueprint byAuthorAndName(@PathVariable String author, @PathVariable String name) {
        try {
            return services.getBlueprint(author, name);
        } catch (BlueprintNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Crea un nuevo blueprint", description = "Requiere el scope blueprints.write")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Blueprint creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o blueprint duplicado"),
        @ApiResponse(responseCode = "403", description = "Token sin el scope blueprints.write")
    })
    // traido del anterior lab
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    public Blueprint create(@RequestBody NewBlueprintRequest req) {
        try {
            Blueprint bp = new Blueprint(req.author(), req.name(), req.points());
            services.addNewBlueprint(bp);
            return bp;
        } catch (BlueprintPersistenceException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Agrega un punto a un blueprint existente", description = "Requiere el scope blueprints.write")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Punto agregado"),
        @ApiResponse(responseCode = "403", description = "Token sin el scope blueprints.write"),
        @ApiResponse(responseCode = "404", description = "Blueprint no encontrado")
    })
    // traido del anterior lab
    @PostMapping ("/{author}/{name}/points")
    @PreAuthorize ("hasAuthority('SCOPE_blueprints.write')")
    public void addPoint(@PathVariable String author, @PathVariable String name, @RequestBody Point p) {
        try {
            services.addPoint(author, name, p.x(), p.y());
        } catch (BlueprintNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
