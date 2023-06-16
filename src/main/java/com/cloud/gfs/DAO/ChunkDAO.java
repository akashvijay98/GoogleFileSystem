package com.cloud.gfs.DAO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "Chunk")

@Getter
@Setter
public class ChunkDAO {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "id")
    private UUID id;

    String name;

    Integer size;

    ChunkDAO(){}

    ChunkDAO(UUID id, String name, Integer size){
        this.id = id;
        this.name = name;
        this.size = size;
    }


}
