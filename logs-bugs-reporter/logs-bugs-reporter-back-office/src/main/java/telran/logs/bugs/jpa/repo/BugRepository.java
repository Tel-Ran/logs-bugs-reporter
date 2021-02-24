package telran.logs.bugs.jpa.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.logs.bugs.dto.BugStatus;
import telran.logs.bugs.dto.EmailBugsCount;
import telran.logs.bugs.jpa.entities.Bug;

public interface BugRepository extends JpaRepository<Bug, Long> {

	List<Bug> findByProgrammerId(long programmerId);

	List<Bug> findByStatus(BugStatus status);

	List<Bug> findByStatusNotAndDateOpenBefore(BugStatus closed, LocalDate dateOpen);
@Query("select programmer.email as email, count(b) as count from Bug b right join b.programmer programmer  group by programmer.email"
		+ " order by count(b) desc")
	List<EmailBugsCount> emailBugsCounts();

}
