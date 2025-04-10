package com.example.myapp.entities;

import com.example.myapp.enums.DemandStatus;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "demands")
public class Demand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private DemandStatus status;

    @Column(name = "request_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;

    @Column(name = "response_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date responseDate;

    @Column(name = "comment", length = 500)
    private String comment;

    public Demand() {
        this.status = DemandStatus.PENDING;
        this.requestDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public DemandStatus getStatus() {
        return status;
    }

    public void setStatus(DemandStatus status) {
        this.status = status;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}