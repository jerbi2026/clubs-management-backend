package com.example.myapp.entities;

import com.example.myapp.enums.EventType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
public class Evenement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le titre est requis")
    private String titre;

    @NotBlank(message = "La description est requise")
    private String description;

    @Temporal(TemporalType.DATE)
    @NotNull(message = "La date est requise")
    private Date date;

    private String imageurl;

    private EventType eventType;

    @ManyToOne
    @JoinColumn(name = "club_id")
    @NotNull(message = "Le club est requis")
    private Club club;

    @OneToMany(mappedBy = "evenement")
    private List<Participation> participations;

    @OneToOne(mappedBy = "evenement")
    private Attestation attestation;

    public Evenement() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public String getImageurl() { return imageurl; }
    public void setImageurl(String imageurl) { this.imageurl = imageurl; }
    public Club getClub() { return club; }
    public void setClub(Club club) { this.club = club; }
    public List<Participation> getParticipations() { return participations; }
    public void setParticipations(List<Participation> participations) { this.participations = participations; }
    public Attestation getAttestation() { return attestation; }
    public void setAttestation(Attestation attestation) { this.attestation = attestation; }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}