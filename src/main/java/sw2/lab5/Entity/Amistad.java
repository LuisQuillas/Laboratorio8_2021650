package sw2.lab5.Entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Representa una solicitud de amistad entre dos usuarios.
 * "solicitante" es quien envia la solicitud, "receptor" quien la recibe.
 */
@Entity
@Table(name = "amistad", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"solicitante_id", "receptor_id"})
})
public class Amistad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_amistad;

    @ManyToOne(optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "receptor_id", nullable = false)
    private Usuario receptor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAmistad estado = EstadoAmistad.PENDIENTE;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    public int getId_amistad() {
        return id_amistad;
    }

    public void setId_amistad(int id_amistad) {
        this.id_amistad = id_amistad;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public Usuario getReceptor() {
        return receptor;
    }

    public void setReceptor(Usuario receptor) {
        this.receptor = receptor;
    }

    public EstadoAmistad getEstado() {
        return estado;
    }

    public void setEstado(EstadoAmistad estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
}
