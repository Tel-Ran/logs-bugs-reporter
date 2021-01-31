package telran.logs.bugs;

import org.springframework.data.jpa.repository.JpaRepository;

import telran.logs.bugs.jpa.entities.Bug;

public interface BugsRepo extends JpaRepository<Bug, Long> {

}
