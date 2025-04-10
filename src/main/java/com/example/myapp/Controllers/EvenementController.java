package com.example.myapp.Controllers;

import com.example.myapp.entities.Evenement;
import com.example.myapp.enums.EventType;
import com.example.myapp.services.EvenementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    private final EvenementService evenementService;
    private final Path fileStoragePath;
    private final String fileStorageLocation = "uploads/images/evenements";

    @Autowired
    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
        this.fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

        // Create directory if it doesn't exist
        try {
            Files.createDirectories(this.fileStoragePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @GetMapping
    public ResponseEntity<List<Evenement>> getAllEvenements() {
        List<Evenement> evenements = evenementService.getAllEvenements();
        return new ResponseEntity<>(evenements, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getEvenementById(@PathVariable Long id) {
        return evenementService.getEvenementById(id)
                .map(evenement -> new ResponseEntity<>(evenement, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/club/{clubId}")
    public ResponseEntity<List<Evenement>> getEvenementsByClubId(@PathVariable Long clubId) {
        List<Evenement> evenements = evenementService.getEvenementsByClubId(clubId);
        return new ResponseEntity<>(evenements, HttpStatus.OK);
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<List<Evenement>> getEvenementsByType(@PathVariable EventType eventType) {
        List<Evenement> evenements = evenementService.getEvenementsByType(eventType);
        return new ResponseEntity<>(evenements, HttpStatus.OK);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<Evenement>> getUpcomingEvenements(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        List<Evenement> evenements = evenementService.getUpcomingEvenements(date);
        return new ResponseEntity<>(evenements, HttpStatus.OK);
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Evenement> createEvenement(
            @RequestPart("evenement") @Valid Evenement evenement,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        if (image != null && !image.isEmpty()) {
            String imagePath = saveImage(image);
            evenement.setImageurl(imagePath);
        }

        Evenement savedEvenement = evenementService.saveEvenement(evenement);
        return new ResponseEntity<>(savedEvenement, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Evenement> updateEvenement(
            @PathVariable Long id,
            @RequestPart("evenement") @Valid Evenement evenement,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return evenementService.getEvenementById(id)
                .map(existingEvenement -> {
                    evenement.setId(id);

                    if (image != null && !image.isEmpty()) {
                        if (existingEvenement.getImageurl() != null) {
                            deleteImageFile(existingEvenement.getImageurl());
                        }
                        String imagePath = saveImage(image);
                        evenement.setImageurl(imagePath);
                    } else {
                        evenement.setImageurl(existingEvenement.getImageurl());
                    }

                    evenementService.updateEvenement(evenement);
                    return new ResponseEntity<>(evenement, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        return evenementService.getEvenementById(id)
                .map(evenement -> {
                    // Delete associated image if exists
                    if (evenement.getImageurl() != null) {
                        deleteImageFile(evenement.getImageurl());
                    }
                    evenementService.deleteEvenement(id);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
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

    // Helper methods for image handling
    private String saveImage(MultipartFile file) {
        try {
            // Generate unique filename to avoid conflicts
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            Path targetLocation = this.fileStoragePath.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/evenements/images/")
                    .path(newFilename)
                    .toUriString();

            return fileDownloadUri;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    private void deleteImageFile(String imageUrl) {
        try {
            String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            Path filePath = this.fileStoragePath.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            // Log the error but don't throw exception
            System.err.println("Error deleting image file: " + ex.getMessage());
        }
    }
}