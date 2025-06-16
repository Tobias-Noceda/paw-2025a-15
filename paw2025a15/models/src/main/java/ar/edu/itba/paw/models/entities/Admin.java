package ar.edu.itba.paw.models.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "admin_details")
@PrimaryKeyJoinColumn(name = "admin_id")
@DiscriminatorValue("3")
public class Admin extends User {
    public Admin() {
        // Just for Hibernate
        super();
    }
}
