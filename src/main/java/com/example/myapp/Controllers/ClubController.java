package com.example.myapp.Controllers;

import com.example.myapp.entities.Club;
import com.example.myapp.services.ClubService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    private final ClubService clubService;
    private final Path fileStoragePath;
    private final String fileStorageLocation = "uploads/images/clubs";

    @Autowired
    public ClubController(ClubService clubService) {
        this.clubService = clubService;
        this.fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStoragePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @GetMapping
    public ResponseEntity<List<Club>> getAllClubs() {
        List<Club> clubs = clubService.getAllClubs();
        return new ResponseEntity<>(clubs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Club> getClubById(@PathVariable Long id) {
        return clubService.getClubById(id)
                .map(club -> new ResponseEntity<>(club, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<Club> getClubByNom(@PathVariable String nom) {
        Club club = clubService.getClubByNom(nom);
        return club != null ?
                new ResponseEntity<>(club, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Club> createClub(
            @RequestPart("club") @Valid Club club,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {

        if (logo != null && !logo.isEmpty()) {
            String logoPath = saveLogo(logo);
            club.setLogopath(logoPath);
        }

        Club savedClub = clubService.saveClub(club);
        return new ResponseEntity<>(savedClub, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Club> updateClub(
            @PathVariable Long id,
            @RequestPart("club") @Valid Club club,
            @RequestPart(value = "logo", required = false) MultipartFile logo) {

        return clubService.getClubById(id)
                .map(existingClub -> {
                    club.setId(id);

                    if (logo != null && !logo.isEmpty()) {
                        // Delete old logo if exists
                        if (existingClub.getLogopath() != null) {
                            deleteLogoFile(existingClub.getLogopath());
                        }
                        String logoPath = saveLogo(logo);
                        club.setLogopath(logoPath);
                    } else {
                        // Keep the existing logo if no new logo is provided
                        club.setLogopath(existingClub.getLogopath());
                    }

                    Club updatedClub = clubService.saveClub(club);
                    return new ResponseEntity<>(updatedClub, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        return clubService.getClubById(id)
                .map(club -> {
                    // Delete associated logo if exists
                    if (club.getLogopath() != null) {
                        deleteLogoFile(club.getLogopath());
                    }
                    clubService.deleteClub(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/logos/{filename:.+}")
    public ResponseEntity<Resource> getLogo(@PathVariable String filename) {
        try {
            Path filePath = this.fileStoragePath.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String saveLogo(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            Path targetLocation = this.fileStoragePath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/clubs/logos/")
                    .path(newFilename)
                    .toUriString();

            return fileDownloadUri;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    private void deleteLogoFile(String logoUrl) {
        try {
            String filename = logoUrl.substring(logoUrl.lastIndexOf('/') + 1);
            Path filePath = this.fileStoragePath.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            System.err.println("Error deleting logo file: " + ex.getMessage());
        }
    }
}