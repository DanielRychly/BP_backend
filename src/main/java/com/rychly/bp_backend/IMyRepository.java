package com.rychly.bp_backend;

import com.rychly.bp_backend.model.PetriNet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMyRepository extends CrudRepository<PetriNet,Long> {

}
