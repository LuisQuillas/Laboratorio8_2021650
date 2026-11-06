package sw2.lab5.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sw2.lab5.Entity.Amistad;
import sw2.lab5.Entity.EstadoAmistad;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmistadRepository extends JpaRepository<Amistad, Integer> {

    @Query("select a from Amistad a where " +
            "(a.solicitante.id_user = :idA and a.receptor.id_user = :idB) or " +
            "(a.solicitante.id_user = :idB and a.receptor.id_user = :idA)")
    Optional<Amistad> findEntreUsuarios(@Param("idA") int idA, @Param("idB") int idB);

    @Query("select a from Amistad a where a.receptor.id_user = :idReceptor and a.estado = :estado")
    List<Amistad> findByReceptor_Id_userAndEstado(@Param("idReceptor") int idReceptor,
                                                    @Param("estado") EstadoAmistad estado);

    @Query("select a from Amistad a where a.solicitante.id_user = :idSolicitante and a.estado = :estado")
    List<Amistad> findBySolicitante_Id_userAndEstado(@Param("idSolicitante") int idSolicitante,
                                                        @Param("estado") EstadoAmistad estado);

    @Query("select a from Amistad a where a.estado = sw2.lab5.Entity.EstadoAmistad.ACEPTADA " +
            "and (a.solicitante.id_user = :idUser or a.receptor.id_user = :idUser)")
    List<Amistad> findAmigosDe(@Param("idUser") int idUser);
}
