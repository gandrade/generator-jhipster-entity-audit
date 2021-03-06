package <%=packageName%>.domain;

<% if (databaseType == 'sql' && auditFramework === 'custom') { %>
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;<% }%>
import java.time.ZonedDateTime;
<% if (databaseType == 'sql' && auditFramework === 'custom') { %>
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;<% } else if (databaseType === 'mongodb' && auditFramework === 'javers') {%>
import org.javers.core.metamodel.object.CdoSnapshot;
import org.joda.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;<% }%>
import java.io.Serializable;
import java.util.Objects;

<% if (databaseType == 'sql' && auditFramework === 'custom') { %>
@Entity
@Table(name = "jhi_entity_audit_event")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)<% } %>
public class EntityAuditEvent implements Serializable{

    private static final long serialVersionUID = 1L;
    <% if (databaseType == 'sql' && auditFramework === 'custom') { %>
  	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @NotNull
    @Size(max = 255)
    @Column(name = "entity_type", length = 255, nullable = false)
    private String entityType;

    @NotNull
    @Size(max=20)
    @Column(name = "action", length = 20, nullable = false)
    private String action;

    @Column(name = "entity_value")
    private String entityValue;

    @Column(name = "commit_version")
    private Integer commitVersion;

    @Size(max = 100)
    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    @NotNull
    @Column(name = "modified_date", nullable = false)
    private ZonedDateTime modifiedDate;
    <% } else if (databaseType === 'mongodb' && auditFramework === 'javers') { %>
    private String id;

    private String entityId;

    private String entityType;

    private String action;

    private String entityValue;

    private Integer commitVersion;

    private String modifiedBy;

    private ZonedDateTime modifiedDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }<% } %>

    <% if (databaseType === 'sql' && auditFramework === 'custom') { %>
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }<% } %>

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityValue() {
        return entityValue;
    }

    public void setEntityValue(String entityValue) {
        this.entityValue = entityValue;
    }

    public Integer getCommitVersion() {
        return commitVersion;
    }

    public void setCommitVersion(Integer commitVersion) {
        this.commitVersion = commitVersion;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public ZonedDateTime getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(ZonedDateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityAuditEvent entityAuditEvent = (EntityAuditEvent) o;
        return Objects.equals(id, entityAuditEvent.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "EntityAuditEvent{" +
            "id=" + id +
            ", entityId='" + entityId + "'" +
            ", entityType='" + entityType + "'" +
            ", action='" + action + "'" +
            ", entityValue='" + entityValue + "'" +
            ", commitVersion='" + commitVersion + "'" +
            ", modifiedBy='" + modifiedBy + "'" +
            ", modifiedDate='" + modifiedDate + "'" +
            '}';
    }

    <% if (databaseType === 'mongodb' && auditFramework === 'javers') { %>
    public static EntityAuditEvent fromJaversSnapshot(CdoSnapshot snapshot) {
        EntityAuditEvent entityAuditEvent = new EntityAuditEvent();

        switch (snapshot.getType()) {
            case INITIAL:
                entityAuditEvent.setAction("CREATE");
                break;
            case UPDATE:
                entityAuditEvent.setAction("UPDATE");
                break;
            case TERMINAL:
                entityAuditEvent.setAction("DELETE");
                break;
        }

        entityAuditEvent.setId(snapshot.getCommitId().toString());
        entityAuditEvent.setCommitVersion(Math.round(snapshot.getVersion()));
        entityAuditEvent.setEntityType(snapshot.getManagedType().getName());
        entityAuditEvent.setEntityId(snapshot.getGlobalId().value().split("/")[1]);
        entityAuditEvent.setModifiedBy(snapshot.getCommitMetadata().getAuthor());

        if (snapshot.getState().getProperties().size() > 0) {
            int count = 0;
            StringBuilder sb = new StringBuilder("{");

            for (String s:snapshot.getState().getProperties()) {
                count++;
                Object propertyValue = snapshot.getPropertyValue(s);
                sb.append("\"" + s + "\": \"" + propertyValue + "\"");
                if (count < snapshot.getState().getProperties().size()) {
                  sb.append(",");
                }
             }

             sb.append("}");
             entityAuditEvent.setEntityValue(sb.toString());
        }
        LocalDateTime localTime = snapshot.getCommitMetadata().getCommitDate();

        ZonedDateTime modifyDate = ZonedDateTime.of(localTime.getYear(),
          localTime.getMonthOfYear(),
          localTime.getDayOfMonth(),
          localTime.getHourOfDay(),
          localTime.getMinuteOfHour(),
          localTime.getSecondOfMinute(),
          localTime.getMillisOfSecond(),
          ZoneId.systemDefault());

        entityAuditEvent.setModifiedDate(modifyDate);

        return entityAuditEvent;

    }<% }%>

}
