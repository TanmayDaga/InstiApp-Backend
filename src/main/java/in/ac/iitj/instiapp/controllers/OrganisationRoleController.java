package in.ac.iitj.instiapp.controllers;

import in.ac.iitj.instiapp.config.JwtProvider;
import in.ac.iitj.instiapp.payload.User.Organisation.OrganisationRoleDto;
import in.ac.iitj.instiapp.payload.User.UserBaseDto;
import in.ac.iitj.instiapp.payload.common.ApiResponse;
import in.ac.iitj.instiapp.services.OrganisationRoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organisation-roles")
public class OrganisationRoleController {    private final OrganisationRoleService organisationRoleService;
    private final JwtProvider jwtProvider;

    @Autowired
    public OrganisationRoleController(OrganisationRoleService organisationRoleService,
                                    JwtProvider jwtProvider) {
        this.organisationRoleService = organisationRoleService;
        this.jwtProvider = jwtProvider;
    }

    /*--------------------------------------------------------ORGANISATION ROLES---------------------------------------------------*/

    @PostMapping("/{organisationUsername}")
    public ResponseEntity<ApiResponse<Void>> saveOrganisationRole(@RequestHeader("Authorization") String jwt,
                                                                 @PathVariable String organisationUsername,
                                                                 @Valid @RequestBody OrganisationRoleDto organisationRoleDto) {
        String currentUser = jwtProvider.getUsernameFromToken(jwt);
        
        // Check if current user is authorized to add roles to this organisation
        if (!currentUser.equals(organisationUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            HttpStatus.FORBIDDEN.value(),
                            "FORBIDDEN",
                            "You are not authorized to add roles to this organisation",
                            null,
                            null
                    ));
        }

        try {
            organisationRoleService.saveOrganisationRole(organisationRoleDto, organisationUsername);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            HttpStatus.CREATED.value(),
                            null,
                            "Organisation role saved successfully",
                            null,
                            null
                    ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(
                            HttpStatus.CONFLICT.value(),
                            "ROLE_CONFLICT",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "ORGANISATION_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "ROLE_SAVE_ERROR",
                            "Error saving organisation role: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @GetMapping("/{organisationUsername}")
    public ResponseEntity<ApiResponse<List<OrganisationRoleDto>>> getOrganisationRoles(@PathVariable String organisationUsername,
                                                                                      Pageable pageable) {
        try {
            List<OrganisationRoleDto> roles = organisationRoleService.getOrganisationRoles(organisationUsername, pageable);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisation roles fetched successfully",
                            roles,
                            null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "ORGANISATION_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "ROLES_FETCH_ERROR",
                            "Error fetching organisation roles: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @PutMapping("/{organisationUsername}/{oldRoleName}")
    public ResponseEntity<ApiResponse<Void>> updateOrganisationRole(@RequestHeader("Authorization") String jwt,
                                                                   @PathVariable String organisationUsername,
                                                                   @PathVariable String oldRoleName,
                                                                   @Valid @RequestBody OrganisationRoleDto newOrganisationRoleDto) {
        String currentUser = jwtProvider.getUsernameFromToken(jwt);
        
        // Check if current user is authorized to update roles for this organisation
        if (!currentUser.equals(organisationUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            HttpStatus.FORBIDDEN.value(),
                            "FORBIDDEN",
                            "You are not authorized to update roles for this organisation",
                            null,
                            null
                    ));
        }

        try {
            organisationRoleService.updateOrganisationRole(organisationUsername, oldRoleName, newOrganisationRoleDto);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisation role updated successfully",
                            null,
                            null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "ROLE_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(
                            HttpStatus.CONFLICT.value(),
                            "ROLE_CONFLICT",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "ROLE_UPDATE_ERROR",
                            "Error updating organisation role: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @DeleteMapping("/{organisationUsername}/{roleName}")
    public ResponseEntity<ApiResponse<Void>> deleteOrganisationRole(@RequestHeader("Authorization") String jwt,
                                                                   @PathVariable String organisationUsername,
                                                                   @PathVariable String roleName,
                                                                   @RequestParam String deletedRoleName) {
        String currentUser = jwtProvider.getUsernameFromToken(jwt);
        
        // Check if current user is authorized to delete roles for this organisation
        if (!currentUser.equals(organisationUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            HttpStatus.FORBIDDEN.value(),
                            "FORBIDDEN",
                            "You are not authorized to delete roles for this organisation",
                            null,
                            null
                    ));
        }

        try {
            organisationRoleService.deleteOrganisationRole(organisationUsername, roleName, deletedRoleName);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisation role deleted successfully",
                            null,
                            null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "ROLE_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "ROLE_DELETE_ERROR",
                            "Error deleting organisation role: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    /*--------------------------------------------------------USER ROLE MANAGEMENT---------------------------------------------------*/

    @PostMapping("/{organisationUsername}/{roleName}/users")
    public ResponseEntity<ApiResponse<Void>> addUserToOrganisationRole(@RequestHeader("Authorization") String jwt,
                                                                      @PathVariable String organisationUsername,
                                                                      @PathVariable String roleName,
                                                                      @RequestBody String userUsername) {
        String currentUser = jwtProvider.getUsernameFromToken(jwt);
        
        // Check if current user is authorized to manage users for this organisation
        if (!currentUser.equals(organisationUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            HttpStatus.FORBIDDEN.value(),
                            "FORBIDDEN",
                            "You are not authorized to manage users for this organisation",
                            null,
                            null
                    ));
        }

        try {
            organisationRoleService.addUserToOrganisationRole(organisationUsername, roleName, userUsername);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(
                            HttpStatus.CREATED.value(),
                            null,
                            "User added to organisation role successfully",
                            null,
                            null
                    ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(
                            HttpStatus.CONFLICT.value(),
                            "USER_ROLE_CONFLICT",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "USER_ROLE_ADD_ERROR",
                            "Error adding user to organisation role: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @GetMapping("/{organisationUsername}/users")
    public ResponseEntity<ApiResponse<List<Map<UserBaseDto, OrganisationRoleDto>>>> getAllOrganisationRoles(@PathVariable String organisationUsername,
                                                                                                            Pageable pageable) {
        try {
            List<Map<UserBaseDto, OrganisationRoleDto>> userRoles = organisationRoleService.getAllOrganisationRoles(organisationUsername, pageable);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "Organisation user roles fetched successfully",
                            userRoles,
                            null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "ORGANISATION_NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "USER_ROLES_FETCH_ERROR",
                            "Error fetching organisation user roles: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }

    @DeleteMapping("/{organisationUsername}/{roleName}/users/{userUsername}")
    public ResponseEntity<ApiResponse<Void>> removeUserFromOrganisationRole(@RequestHeader("Authorization") String jwt,
                                                                           @PathVariable String organisationUsername,
                                                                           @PathVariable String roleName,
                                                                           @PathVariable String userUsername) {
        String currentUser = jwtProvider.getUsernameFromToken(jwt);
        
        // Check if current user is authorized to remove users from this organisation
        if (!currentUser.equals(organisationUsername)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(
                            HttpStatus.FORBIDDEN.value(),
                            "FORBIDDEN",
                            "You are not authorized to remove users from this organisation",
                            null,
                            null
                    ));
        }

        try {
            organisationRoleService.removeUserFromOrganisationRole(organisationUsername, roleName, userUsername);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            HttpStatus.OK.value(),
                            null,
                            "User removed from organisation role successfully",
                            null,
                            null
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(
                            HttpStatus.NOT_FOUND.value(),
                            "NOT_FOUND",
                            e.getMessage(),
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(
                            HttpStatus.BAD_REQUEST.value(),
                            "USER_ROLE_REMOVE_ERROR",
                            "Error removing user from organisation role: " + e.getMessage(),
                            null,
                            null
                    ));
        }
    }
}
