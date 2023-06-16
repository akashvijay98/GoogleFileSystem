package com.cloud.gfs.DAO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "File")

@Getter
@Setter
public class FileDAO {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id")
    private UUID id;
    private String name;
    private Integer size;

    public FileDAO(){}

    public FileDAO(UUID id, String name, Integer size){
        this.id = id;
        this.name = name;
        this.size = size;
    }
}
