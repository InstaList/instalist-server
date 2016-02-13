package org.noorganization.instalist.server.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "units")
public class Unit {
    private int         mId;
    private UUID        mUUID;
    private String      mName;
    private Instant     mUpdated;
    private DeviceGroup mGroup;

    private Set<Product> mProducts;

    public Unit() {
        mUpdated = Instant.now();
    }

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return mId;
    }

    public void setId(int _id) {
        mId = _id;
    }

    public Unit withId(int _id) {
        setId(_id);
        return this;
    }

    @Column(name = "uuid", columnDefinition = "BINARY(16)", nullable = false)
    public UUID getUUID() {
        return mUUID;
    }

    public void setUUID(UUID _UUID) {
        mUUID = _UUID;
    }

    public Unit withUUID(UUID _uuid) {
        setUUID(_uuid);
        return this;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return mName;
    }

    public void setName(String _name) {
        mName = _name;
    }

    public Unit withName(String _name) {
        setName(_name);
        return this;
    }

    @Column(name = "updated", columnDefinition = "TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3)",
            nullable = false)
    public Instant getUpdated() {
        return mUpdated;
    }

    public void setUpdated(Instant _updated) {
        mUpdated = _updated;
    }

    public Unit withUpdated(Instant _updated) {
        setUpdated(_updated);
        return this;
    }

    @ManyToOne
    @JoinColumn(name = "devicegroup_id", nullable = false)
    public DeviceGroup getGroup() {
        return mGroup;
    }

    public void setGroup(DeviceGroup _group) {
        mGroup = _group;
    }

    public Unit withGroup(DeviceGroup _group) {
        setGroup(_group);
        return this;
    }

    @OneToMany(mappedBy = "unit")
    public Set<Product> getProducts() {
        return mProducts;
    }

    public void setProducts(
            Set<Product> _products) {
        mProducts = _products;
    }
}
