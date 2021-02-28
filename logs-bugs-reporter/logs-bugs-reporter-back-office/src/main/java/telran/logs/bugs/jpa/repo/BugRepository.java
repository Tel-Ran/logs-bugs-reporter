package telran.logs.bugs.jpa.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import telran.logs.bugs.dto.*;
import telran.logs.bugs.jpa.entities.Bug;

public interface BugRepository extends JpaRepository<Bug, Long> {

	List<Bug> findByProgrammerId(long programmerId);

	List<Bug> findByStatus(BugStatus status);

	List<Bug> findByStatusNotAndDateOpenBefore(BugStatus closed, LocalDate dateOpen);
	
@Query("select programmer.email as email, count(b) as count from Bug b right join b.programmer programmer  group by programmer.email"
		+ " order by count(b) desc")
	List<EmailBugsCount> emailBugsCounts();

@Query(value = "select p.email from bugs b join programmers p on programmer_id = p.id "
		+ "group by p.email order by count(b.id) desc limit :n_programmers", nativeQuery = true)
List<String> prgrammersMostBugs(@Param ("n_programmers") int nProgrammers);

@Query(value = "select p.email from bugs b right join programmers p on b.programmer_id = p.id "
		+ "group by p.email order by count(b.id) limit :n_programmers", nativeQuery = true)
List<String> programmersLeastBugs(@Param ("n_programmers") int nProgrammers);

@Query(value = "select seriousness from bugs  "
		+ "group by seriousness order by count(*) desc limit :n_types", nativeQuery = true)
List<Seriousness> seriousnessMostBugs(@Param ("n_types") int nTypes);

long countBySeriousness(Seriousness s); //returns count of bugs of the given seriousness

}
