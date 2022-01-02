package com.rychly.bp_frontend;

import com.rychly.bp_frontend.model.PetriNet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMyRepository extends CrudRepository<PetriNet,Long> {

}
