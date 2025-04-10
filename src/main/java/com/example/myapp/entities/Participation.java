package com.example.myapp.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Entity
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.DATE)
    @NotNull(message = "La date est requise")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "L'utilisateur est requis")
    private User user;

    @ManyToOne
    @JoinColumn(name = "evenement_id")
    @NotNull(message = "L'événement est requis")
    private Evenement evenement;

    public Participation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Evenement getEvenement() { return evenement; }
    public void setEvenement(Evenement evenement) { this.evenement = evenement; }
}