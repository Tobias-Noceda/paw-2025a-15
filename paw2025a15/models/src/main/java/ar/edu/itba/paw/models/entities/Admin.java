package ar.edu.itba.paw.models.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("3")
public class Admin extends User {

    public Admin() {
        super();
    }
}

